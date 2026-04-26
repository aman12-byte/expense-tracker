package com.expense.tracker.controller;

import com.expense.tracker.model.Category;
import com.expense.tracker.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * CategoryController - REST API for managing categories.
 * Handles both listing default categories and user-created custom ones.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /** GET /api/categories - get all categories for logged-in user */
    @GetMapping
    public ResponseEntity<?> getCategories(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

        List<Category> categories = categoryService.getCategoriesForUser(userId);
        return ResponseEntity.ok(categories);
    }

    /** POST /api/categories - add a custom category */
    @PostMapping
    public ResponseEntity<?> addCategory(@RequestBody Category category, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

        if (category.getName() == null || category.getName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Category name is required"));
        }

        category.setUserId(userId);  // Mark this as a user's custom category
        if (category.getIcon() == null || category.getIcon().isBlank()) category.setIcon("💰");
        if (category.getColor() == null || category.getColor().isBlank()) category.setColor("#6366f1");

        Category saved = categoryService.addCategory(category);
        return ResponseEntity.ok(saved);
    }

    /** PUT /api/categories/{id} - update a custom category */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @RequestBody Category category,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

        category.setId(id);
        category.setUserId(userId);
        categoryService.updateCategory(category);
        return ResponseEntity.ok(Map.of("message", "Category updated"));
    }

    /** DELETE /api/categories/{id} - delete a custom category */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

        categoryService.deleteCategory(id, userId);
        return ResponseEntity.ok(Map.of("message", "Category deleted"));
    }
}
