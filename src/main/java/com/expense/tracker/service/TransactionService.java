package com.expense.tracker.service;

import com.expense.tracker.model.Expense;
import com.expense.tracker.model.Income;
import com.expense.tracker.model.Transaction;
import com.expense.tracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * TransactionService - handles business logic for transactions.
 *
 * KEY OOP CONCEPT: POLYMORPHISM demonstrated here!
 * ====================================================
 * This service operates on 'Transaction' references (the parent class).
 * At runtime, these can be either Expense or Income objects.
 *
 * Example:
 *   Transaction t = new Expense(...);  // Expense stored as Transaction
 *   transactionRepository.save(t);     // save() calls t.getType()
 *                                      // which returns "EXPENSE" — POLYMORPHISM!
 *
 * The toString() / getSummary() calls on any Transaction will
 * automatically call the correct subclass version — runtime polymorphism.
 *
 * Viva Q: Where is Polymorphism used in TransactionService?
 * A: addTransaction() accepts any Transaction (Expense or Income).
 *    Inside, it calls transaction.getType() which invokes the
 *    correct subclass method at runtime — this is runtime polymorphism.
 */
@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Add a transaction.
     * POLYMORPHISM: 'transaction' can be an Expense or Income object,
     * both handled by this single method via the parent class reference.
     */
    public Transaction addTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    /**
     * Update a transaction.
     */
    public void updateTransaction(Transaction transaction) {
        transactionRepository.update(transaction);
    }

    /**
     * Delete a transaction.
     */
    public void deleteTransaction(Long id, Long userId) {
        transactionRepository.delete(id, userId);
    }

    /**
     * Get all transactions for a user.
     */
    public List<Transaction> getAllTransactions(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    /**
     * Get filtered transactions (by type, category, date range).
     */
    public List<Transaction> getFilteredTransactions(Long userId, String type,
                                                      Long categoryId,
                                                      LocalDate from, LocalDate to) {
        return transactionRepository.findFiltered(userId, type, categoryId, from, to);
    }

    /**
     * Get total income for a specific month/year.
     */
    public double getTotalIncome(Long userId, int month, int year) {
        return transactionRepository.sumByType(userId, "INCOME", month, year);
    }

    /**
     * Get total expenses for a specific month/year.
     */
    public double getTotalExpenses(Long userId, int month, int year) {
        return transactionRepository.sumByType(userId, "EXPENSE", month, year);
    }

    /**
     * Get current account balance (all-time income minus all-time expenses).
     */
    public double getBalance(Long userId) {
        double totalIncome  = transactionRepository.sumAllByType(userId, "INCOME");
        double totalExpense = transactionRepository.sumAllByType(userId, "EXPENSE");
        return totalIncome - totalExpense;
    }

    /**
     * Get expense totals grouped by category (for pie chart).
     */
    public List<Map<String, Object>> getCategoryWiseExpenses(Long userId, int month, int year) {
        return transactionRepository.getCategoryWiseExpenses(userId, month, year);
    }

    /**
     * Get monthly expense totals for last 6 months (for bar chart).
     */
    public List<Map<String, Object>> getMonthlyExpenses(Long userId) {
        return transactionRepository.getMonthlyExpenses(userId);
    }

    // ===================== Factory Methods =====================
    // These create specific subclass objects — similar to Factory Pattern

    /**
     * Create an Expense object.
     * The caller gets an Expense, but can use it as a Transaction (POLYMORPHISM).
     */
    public Expense createExpense(double amount, String description, LocalDate date,
                                  Long categoryId, Long userId) {
        return new Expense(amount, description, date, categoryId, userId);
    }

    /**
     * Create an Income object.
     */
    public Income createIncome(double amount, String description, LocalDate date,
                                Long categoryId, Long userId) {
        return new Income(amount, description, date, categoryId, userId);
    }
}
