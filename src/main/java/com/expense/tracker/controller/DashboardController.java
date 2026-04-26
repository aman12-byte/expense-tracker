package com.expense.tracker.controller;

import com.expense.tracker.model.Budget;
import com.expense.tracker.service.BudgetManager;
import com.expense.tracker.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DashboardController - provides aggregated data for the dashboard page.
 * Returns income, expenses, balance, budget alerts and chart data in one call.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BudgetManager budgetManager;

    /**
     * GET /api/dashboard
     * Returns all dashboard summary data:
     * - Income, Expenses, Balance, Savings for current month
     * - Budget alerts (exceeded budgets)
     * - Category-wise expense data (pie chart)
     * - Monthly expense history (bar chart)
     */
    @GetMapping
    public ResponseEntity<?> getDashboard(
            HttpSession session,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

        LocalDate now = LocalDate.now();
        int m = (month != null) ? month : now.getMonthValue();
        int y = (year  != null) ? year  : now.getYear();

        // Fetch all dashboard data
        double income      = transactionService.getTotalIncome(userId, m, y);
        double expenses    = transactionService.getTotalExpenses(userId, m, y);
        double balance     = transactionService.getBalance(userId);
        double totalBudget = budgetManager.getTotalMonthlyBudget(userId, m, y);

        List<Budget>              alerts          = budgetManager.getExceededBudgets(userId, m, y);
        List<Map<String, Object>> categoryData    = transactionService.getCategoryWiseExpenses(userId, m, y);
        List<Map<String, Object>> monthlyData     = transactionService.getMonthlyExpenses(userId);

        // Build response
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("month",           m);
        dashboard.put("year",            y);
        dashboard.put("income",          income);
        dashboard.put("expenses",        expenses);
        dashboard.put("balance",         balance);
        dashboard.put("savings",         income - expenses);
        dashboard.put("totalBudget",     totalBudget);
        dashboard.put("budgetAlerts",    alerts);
        dashboard.put("categoryExpenses", categoryData);
        dashboard.put("monthlyExpenses", monthlyData);

        return ResponseEntity.ok(dashboard);
    }
}
