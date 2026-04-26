package com.expense.tracker.controller;

import com.expense.tracker.model.Budget;
import com.expense.tracker.service.BudgetManager;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * BudgetController - REST API for budget management.
 * Users can set monthly budgets per category and view alerts when exceeded.
 */
@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private BudgetManager budgetManager;

    /** GET /api/budgets?month=4&year=2026 - get all budgets with spending for a month */
    @GetMapping
    public ResponseEntity<?> getBudgets(
            HttpSession session,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

        LocalDate now = LocalDate.now();
        int m = (month != null) ? month : now.getMonthValue();
        int y = (year  != null) ? year  : now.getYear();

        List<Budget> budgets = budgetManager.getBudgetsWithSpending(userId, m, y);
        return ResponseEntity.ok(budgets);
    }

    /** POST /api/budgets - set a budget for a category */
    @PostMapping
    public ResponseEntity<?> setBudget(@RequestBody Budget budget, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

        if (budget.getLimitAmount() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Budget amount must be greater than 0"));
        }
        if (budget.getCategoryId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Category is required"));
        }

        budget.setUserId(userId);

        // Default to current month/year if not provided
        if (budget.getMonth() == 0) budget.setMonth(LocalDate.now().getMonthValue());
        if (budget.getYear()  == 0) budget.setYear(LocalDate.now().getYear());

        Budget saved = budgetManager.setBudget(budget);
        return ResponseEntity.ok(saved);
    }

    /** DELETE /api/budgets/{id} - delete a budget */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

        budgetManager.deleteBudget(id);
        return ResponseEntity.ok(Map.of("message", "Budget deleted"));
    }

    /** GET /api/budgets/alerts - get budgets that have been exceeded (for warnings) */
    @GetMapping("/alerts")
    public ResponseEntity<?> getAlerts(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

        LocalDate now = LocalDate.now();
        List<Budget> alerts = budgetManager.getExceededBudgets(userId, now.getMonthValue(), now.getYear());
        return ResponseEntity.ok(alerts);
    }
}
