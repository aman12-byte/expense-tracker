package com.expense.tracker.repository;

import com.expense.tracker.model.Expense;
import com.expense.tracker.model.Income;
import com.expense.tracker.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TransactionRepository - handles DB operations for transactions.
 *
 * Key OOP Note: The RowMapper here demonstrates POLYMORPHISM.
 * Based on the 'type' column ("INCOME" or "EXPENSE"), it creates
 * either an Income or Expense object, both returned as Transaction.
 * This is runtime polymorphism in action!
 */
@Repository
public class TransactionRepository {

    @Autowired
    private JdbcTemplate jdbc;

    /**
     * RowMapper - POLYMORPHISM demonstrated here!
     * Creates Expense or Income based on the 'type' column.
     * Both are returned as Transaction (parent class reference).
     */
    private final RowMapper<Transaction> transactionMapper = (rs, row) -> {
        String type = rs.getString("type");

        // POLYMORPHISM: decide at runtime which subclass to create
        Transaction t;
        if ("EXPENSE".equals(type)) {
            t = new Expense();
        } else {
            t = new Income();
        }

        t.setId(rs.getLong("id"));
        t.setAmount(rs.getDouble("amount"));
        t.setDescription(rs.getString("description"));
        t.setDate(rs.getDate("date").toLocalDate());
        t.setUserId(rs.getLong("user_id"));

        // category_id may be NULL
        long catId = rs.getLong("category_id");
        t.setCategoryId(rs.wasNull() ? null : catId);
        t.setCategoryName(rs.getString("category_name"));

        return t;
    };

    /**
     * Save a new transaction. Works for both Expense and Income
     * because both call getType() which returns "EXPENSE" or "INCOME".
     * This is POLYMORPHISM - same save() method handles both types.
     */
    public Transaction save(Transaction transaction) {
        String sql = "INSERT INTO transactions (type, amount, description, date, category_id, user_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, transaction.getType());       // "INCOME" or "EXPENSE"
            ps.setDouble(2, transaction.getAmount());
            ps.setString(3, transaction.getDescription());
            ps.setDate(4, Date.valueOf(transaction.getDate()));
            if (transaction.getCategoryId() != null) {
                ps.setLong(5, transaction.getCategoryId());
            } else {
                ps.setNull(5, Types.BIGINT);
            }
            ps.setLong(6, transaction.getUserId());
            return ps;
        }, keyHolder);

        transaction.setId(keyHolder.getKey().longValue());
        return transaction;
    }

    /**
     * Update an existing transaction.
     */
    public void update(Transaction transaction) {
        jdbc.update(
            "UPDATE transactions SET type=?, amount=?, description=?, date=?, category_id=? " +
            "WHERE id=? AND user_id=?",
            transaction.getType(),
            transaction.getAmount(),
            transaction.getDescription(),
            Date.valueOf(transaction.getDate()),
            transaction.getCategoryId(),
            transaction.getId(),
            transaction.getUserId()
        );
    }

    /**
     * Delete a transaction by id. Checks user_id for security.
     */
    public void delete(Long id, Long userId) {
        jdbc.update("DELETE FROM transactions WHERE id=? AND user_id=?", id, userId);
    }

    /**
     * Get all transactions for a user, ordered by date descending.
     */
    public List<Transaction> findByUserId(Long userId) {
        String sql = "SELECT t.*, c.name AS category_name " +
                     "FROM transactions t LEFT JOIN categories c ON t.category_id = c.id " +
                     "WHERE t.user_id = ? ORDER BY t.date DESC, t.id DESC";
        return jdbc.query(sql, transactionMapper, userId);
    }

    /**
     * Filtered search: supports type, categoryId, date range.
     * Uses dynamic SQL building with parameter list.
     */
    public List<Transaction> findFiltered(Long userId, String type, Long categoryId,
                                           LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder(
            "SELECT t.*, c.name AS category_name FROM transactions t " +
            "LEFT JOIN categories c ON t.category_id = c.id WHERE t.user_id=?"
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (type != null && !type.isBlank()) {
            sql.append(" AND t.type=?");
            params.add(type);
        }
        if (categoryId != null) {
            sql.append(" AND t.category_id=?");
            params.add(categoryId);
        }
        if (from != null) {
            sql.append(" AND t.date >= ?");
            params.add(Date.valueOf(from));
        }
        if (to != null) {
            sql.append(" AND t.date <= ?");
            params.add(Date.valueOf(to));
        }
        sql.append(" ORDER BY t.date DESC, t.id DESC");
        return jdbc.query(sql.toString(), transactionMapper, params.toArray());
    }

    /**
     * Sum of INCOME or EXPENSE for a specific month/year.
     */
    public double sumByType(Long userId, String type, int month, int year) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
                     "WHERE user_id=? AND type=? AND MONTH(date)=? AND YEAR(date)=?";
        Double result = jdbc.queryForObject(sql, Double.class, userId, type, month, year);
        return result != null ? result : 0.0;
    }

    /**
     * Sum of all-time INCOME or EXPENSE (for balance calculation).
     */
    public double sumAllByType(Long userId, String type) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id=? AND type=?";
        Double result = jdbc.queryForObject(sql, Double.class, userId, type);
        return result != null ? result : 0.0;
    }

    /**
     * Category-wise expense breakdown for pie chart.
     */
    public List<Map<String, Object>> getCategoryWiseExpenses(Long userId, int month, int year) {
        String sql = "SELECT c.name AS category, c.color, COALESCE(SUM(t.amount), 0) AS total " +
                     "FROM transactions t JOIN categories c ON t.category_id = c.id " +
                     "WHERE t.user_id=? AND t.type='EXPENSE' AND MONTH(t.date)=? AND YEAR(t.date)=? " +
                     "GROUP BY c.id, c.name, c.color ORDER BY total DESC";
        return jdbc.queryForList(sql, userId, month, year);
    }

    /**
     * Monthly cashflow (Income and Expense) for the dual-line chart (Last 6 months).
     */
    public List<Map<String, Object>> getMonthlyCashflow(Long userId) {
        String sql = "SELECT YEAR(date) AS yr, MONTH(date) AS mo, " +
                     "SUM(CASE WHEN type='INCOME' THEN amount ELSE 0 END) AS income, " +
                     "SUM(CASE WHEN type='EXPENSE' THEN amount ELSE 0 END) AS expense " +
                     "FROM transactions WHERE user_id=? " +
                     "GROUP BY YEAR(date), MONTH(date) ORDER BY yr DESC, mo DESC LIMIT 6";
        List<Map<String, Object>> list = jdbc.queryForList(sql, userId);
        java.util.Collections.reverse(list); // Ensure chronological order
        return list;
    }
}
