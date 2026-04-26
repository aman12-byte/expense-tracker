package com.expense.tracker.controller;

import com.expense.tracker.model.Expense;
import com.expense.tracker.model.Income;
import com.expense.tracker.model.Transaction;
import com.expense.tracker.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

/**
 * TransactionController - REST API for income and expense transactions.
 *
 * POLYMORPHISM is shown here:
 * - addTransaction() checks the "type" field and creates either
 *   an Expense or Income, but passes it as Transaction to the service.
 * - toMap() calls transaction.getSummary() which calls the correct
 *   subclass method based on runtime type — POLYMORPHISM.
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /**
     * Convert Transaction to a Map for JSON response.
     * Calls getSummary() — demonstrates POLYMORPHISM (calls Expense.getSummary()
     * or Income.getSummary() depending on actual type).
     */
    private Map<String, Object> toMap(Transaction t) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id",           t.getId());
        map.put("type",         t.getType());          // calls overridden method
        map.put("amount",       t.getAmount());
        map.put("description",  t.getDescription());
        map.put("date",         t.getDate() != null ? t.getDate().toString() : null);
        map.put("categoryId",   t.getCategoryId());
        map.put("categoryName", t.getCategoryName());
        map.put("summary",      t.getSummary());       // POLYMORPHISM: calls correct subclass getSummary()
        map.put("formatted",    t.getFormattedAmount());
        return map;
    }

    /** GET /api/transactions - get all (optionally filtered) transactions */
    @GetMapping
    public ResponseEntity<?> getTransactions(
            HttpSession session,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

        LocalDate fromDate = (from != null && !from.isBlank()) ? LocalDate.parse(from) : null;
        LocalDate toDate   = (to   != null && !to.isBlank())   ? LocalDate.parse(to)   : null;

        List<Transaction> list   = transactionService.getFilteredTransactions(userId, type, categoryId, fromDate, toDate);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Transaction t : list) result.add(toMap(t));
        return ResponseEntity.ok(result);
    }

    /** POST /api/transactions - add a new income or expense */
    @PostMapping
    public ResponseEntity<?> addTransaction(@RequestBody Map<String, Object> req, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

        try {
            String    type        = (String) req.get("type");
            double    amount      = Double.parseDouble(req.get("amount").toString());
            String    description = (String) req.getOrDefault("description", "");
            LocalDate date        = LocalDate.parse((String) req.get("date"));
            Long      categoryId  = req.get("categoryId") != null
                                    ? Long.parseLong(req.get("categoryId").toString()) : null;

            // POLYMORPHISM: create Expense or Income, then save as Transaction
            Transaction transaction;
            if ("EXPENSE".equals(type)) {
                transaction = transactionService.createExpense(amount, description, date, categoryId, userId);
            } else {
                transaction = transactionService.createIncome(amount, description, date, categoryId, userId);
            }

            Transaction saved = transactionService.addTransaction(transaction);
            return ResponseEntity.ok(toMap(saved));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid data: " + e.getMessage()));
        }
    }

    /** PUT /api/transactions/{id} - update a transaction */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(
            @PathVariable Long id,
            @RequestBody Map<String, Object> req,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

        try {
            String    type        = (String) req.get("type");
            double    amount      = Double.parseDouble(req.get("amount").toString());
            String    description = (String) req.getOrDefault("description", "");
            LocalDate date        = LocalDate.parse((String) req.get("date"));
            Long      categoryId  = req.get("categoryId") != null
                                    ? Long.parseLong(req.get("categoryId").toString()) : null;

            Transaction transaction;
            if ("EXPENSE".equals(type)) {
                transaction = new Expense(amount, description, date, categoryId, userId);
            } else {
                transaction = new Income(amount, description, date, categoryId, userId);
            }
            transaction.setId(id);

            transactionService.updateTransaction(transaction);
            return ResponseEntity.ok(toMap(transaction));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid data: " + e.getMessage()));
        }
    }

    /** DELETE /api/transactions/{id} - delete a transaction */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

        transactionService.deleteTransaction(id, userId);
        return ResponseEntity.ok(Map.of("message", "Transaction deleted successfully"));
    }
}
