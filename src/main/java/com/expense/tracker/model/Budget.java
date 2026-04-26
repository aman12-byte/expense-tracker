package com.expense.tracker.model;

/**
 * ============================================================
 * Budget Model - Demonstrates ENCAPSULATION
 * ============================================================
 * Represents a monthly spending limit for a category.
 * Contains business logic methods (isExceeded, getPercentageUsed)
 * that operate on the private data - this is good OOP practice.
 *
 * Viva Q: What's the difference between a Model and a DTO?
 * A: A Model represents a domain entity (usually mapped to DB).
 *    spentAmount here is calculated/joined, not stored directly —
 *    making Budget also partially a DTO (Data Transfer Object).
 */
public class Budget {

    // Private fields - ENCAPSULATION
    private Long   id;
    private Long   userId;
    private Long   categoryId;
    private String categoryName;
    private double limitAmount;
    private double spentAmount;   // calculated from transactions, not stored
    private int    month;
    private int    year;

    // Default constructor
    public Budget() {}

    // Parameterized constructor
    public Budget(Long userId, Long categoryId, double limitAmount, int month, int year) {
        this.userId      = userId;
        this.categoryId  = categoryId;
        this.limitAmount = limitAmount;
        this.month       = month;
        this.year        = year;
    }

    // ===================== Business Logic Methods =====================

    /**
     * Returns true if spending has exceeded the budget limit.
     * Example of behaviour hiding internal data via method.
     */
    public boolean isExceeded() {
        return spentAmount > limitAmount;
    }

    /**
     * Returns what % of the budget has been used.
     * Used to display the progress bar in the UI.
     */
    public double getPercentageUsed() {
        if (limitAmount == 0) return 0;
        return (spentAmount / limitAmount) * 100;
    }

    /**
     * Returns remaining budget amount.
     */
    public double getRemaining() {
        return limitAmount - spentAmount;
    }

    // ==================== Getters & Setters ====================

    public Long   getId()                       { return id; }
    public void   setId(Long id)                { this.id = id; }

    public Long   getUserId()                   { return userId; }
    public void   setUserId(Long userId)        { this.userId = userId; }

    public Long   getCategoryId()               { return categoryId; }
    public void   setCategoryId(Long cId)       { this.categoryId = cId; }

    public String getCategoryName()             { return categoryName; }
    public void   setCategoryName(String name)  { this.categoryName = name; }

    public double getLimitAmount()              { return limitAmount; }
    public void   setLimitAmount(double amt)    { this.limitAmount = amt; }

    public double getSpentAmount()              { return spentAmount; }
    public void   setSpentAmount(double amt)    { this.spentAmount = amt; }

    public int    getMonth()                    { return month; }
    public void   setMonth(int month)           { this.month = month; }

    public int    getYear()                     { return year; }
    public void   setYear(int year)             { this.year = year; }
}
