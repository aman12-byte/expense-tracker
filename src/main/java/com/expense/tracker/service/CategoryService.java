package com.expense.tracker.service;

import com.expense.tracker.model.Category;
import com.expense.tracker.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CategoryService - handles category management business logic.
 * Users can create custom categories in addition to the default ones.
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Get all categories available to a user (default + their custom ones).
     */
    public List<Category> getCategoriesForUser(Long userId) {
        return categoryRepository.findAllForUser(userId);
    }

    /**
     * Add a new custom category for a user.
     */
    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    /**
     * Update a custom category. Only the owner can update.
     */
    public void updateCategory(Category category) {
        categoryRepository.update(category);
    }

    /**
     * Delete a custom category. Only the owner can delete.
     */
    public void deleteCategory(Long id, Long userId) {
        categoryRepository.delete(id, userId);
    }

    /**
     * Get a category by its ID.
     */
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
}
