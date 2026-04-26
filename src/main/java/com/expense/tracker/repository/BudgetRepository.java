package com.expense.tracker.repository;

import com.expense.tracker.model.Budget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

/**
 * BudgetRepository - handles database operations for Budget.
 * The findWithSpending query uses a correlated subquery to calculate
 * how much has been spent for each budget's category in that month.
 */
@Repository
public class BudgetRepository {

    @Autowired
    private JdbcTemplate jdbc;

    // RowMapper for Budget
    private final RowMapper<Budget> budgetMapper = (rs, row) -> {
        Budget b = new Budget();
        b.setId(rs.getLong("id"));
        b.setUserId(rs.getLong("user_id"));
        b.setCategoryId(rs.getLong("category_id"));
        b.setCategoryName(rs.getString("category_name"));
        b.setLimitAmount(rs.getDouble("limit_amount"));
        b.setSpentAmount(rs.getDouble("spent_amount"));
        b.setMonth(rs.getInt("budget_month"));
        b.setYear(rs.getInt("budget_year"));
        return b;
    };

    /**
     * Save a new budget limit for a category.
     */
    public Budget save(Budget budget) {
        String sql = "INSERT INTO budgets (user_id, category_id, limit_amount, budget_month, budget_year) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, budget.getUserId());
            ps.setLong(2, budget.getCategoryId());
            ps.setDouble(3, budget.getLimitAmount());
            ps.setInt(4, budget.getMonth());
            ps.setInt(5, budget.getYear());
            return ps;
        }, keyHolder);

        budget.setId(keyHolder.getKey().longValue());
        return budget;
    }

    /**
     * Update budget limit amount.
     */
    public void update(Budget budget) {
        jdbc.update("UPDATE budgets SET limit_amount=? WHERE id=?",
                    budget.getLimitAmount(), budget.getId());
    }

    /**
     * Delete a budget by id.
     */
    public void delete(Long id) {
        jdbc.update("DELETE FROM budgets WHERE id=?", id);
    }

    /**
     * Find budget for a specific category and month/year.
     * Returns null if not found.
     */
    public Budget findByCategoryAndPeriod(Long userId, Long categoryId, int month, int year) {
        try {
            String sql = "SELECT b.*, c.name AS category_name, 0.0 AS spent_amount " +
                         "FROM budgets b LEFT JOIN categories c ON b.category_id = c.id " +
                         "WHERE b.user_id=? AND b.category_id=? AND b.budget_month=? AND b.budget_year=?";
            return jdbc.queryForObject(sql, budgetMapper, userId, categoryId, month, year);
        } catch (Exception e) {
            return null;  // No budget set for this category+period
        }
    }

    /**
     * Get all budgets for a user in a month, WITH actual spending calculated.
     * Uses a correlated subquery to sum transactions for each budget's category.
     */
    public List<Budget> findWithSpending(Long userId, int month, int year) {
        String sql = "SELECT b.*, c.name AS category_name, " +
                     "  COALESCE((" +
                     "    SELECT SUM(t.amount) FROM transactions t " +
                     "    WHERE t.user_id = b.user_id AND t.category_id = b.category_id " +
                     "    AND t.type = 'EXPENSE' AND MONTH(t.date) = b.budget_month AND YEAR(t.date) = b.budget_year" +
                     "  ), 0) AS spent_amount " +
                     "FROM budgets b LEFT JOIN categories c ON b.category_id = c.id " +
                     "WHERE b.user_id=? AND b.budget_month=? AND b.budget_year=? ORDER BY b.id";
        return jdbc.query(sql, budgetMapper, userId, month, year);
    }

    /**
     * Get sum of all budget limits for a user in a month.
     */
    public double getTotalBudget(Long userId, int month, int year) {
        String sql = "SELECT COALESCE(SUM(limit_amount), 0) FROM budgets WHERE user_id=? AND budget_month=? AND budget_year=?";
        Double result = jdbc.queryForObject(sql, Double.class, userId, month, year);
        return result != null ? result : 0.0;
    }
}
