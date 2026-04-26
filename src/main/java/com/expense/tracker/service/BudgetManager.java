package com.expense.tracker.service;

import com.expense.tracker.model.Budget;
import com.expense.tracker.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * BudgetManager - manages budget limits and alerts.
 *
 * This is a key class suggested in the project spec.
 * It manages monthly budget limits per category and alerts
 * users when their spending exceeds the set limit.
 *
 * Viva Q: What does BudgetManager do?
 * A: It sets/updates budget limits, retrieves budgets with
 *    actual spending calculated, and filters exceeded budgets
 *    to show warnings/alerts to the user.
 */
@Service
public class BudgetManager {

    @Autowired
    private BudgetRepository budgetRepository;

    /**
     * Set or update a budget for a category.
     * If a budget already exists for that category+month+year, it updates.
     * Otherwise, it creates a new budget.
     */
    public Budget setBudget(Budget budget) {
        // Check if budget already exists for this category+period
        Budget existing = budgetRepository.findByCategoryAndPeriod(
            budget.getUserId(), budget.getCategoryId(),
            budget.getMonth(), budget.getYear()
        );

        if (existing != null) {
            // Update existing budget
            existing.setLimitAmount(budget.getLimitAmount());
            budgetRepository.update(existing);
            return existing;
        } else {
            // Create new budget
            return budgetRepository.save(budget);
        }
    }

    /**
     * Get all budgets for a user in a given month,
     * with actual spending amounts populated.
     */
    public List<Budget> getBudgetsWithSpending(Long userId, int month, int year) {
        return budgetRepository.findWithSpending(userId, month, year);
    }

    /**
     * Get only the budgets where spending has EXCEEDED the limit.
     * These are used to show warning alerts.
     * Uses Java 8 Streams to filter — demonstrates modern Java.
     */
    public List<Budget> getExceededBudgets(Long userId, int month, int year) {
        List<Budget> all = getBudgetsWithSpending(userId, month, year);
        return all.stream()
                  .filter(Budget::isExceeded)   // calls isExceeded() from Budget class
                  .collect(Collectors.toList());
    }

    /**
     * Get total sum of all category budgets for a monthly overview.
     */
    public double getTotalMonthlyBudget(Long userId, int month, int year) {
        return budgetRepository.getTotalBudget(userId, month, year);
    }

    /**
     * Delete a budget.
     */
    public void deleteBudget(Long id) {
        budgetRepository.delete(id);
    }
}
