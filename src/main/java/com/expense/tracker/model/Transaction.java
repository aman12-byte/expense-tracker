package com.expense.tracker.model;

import java.time.LocalDate;

/**
 * ============================================================
 * Transaction - Abstract Base Class
 * Demonstrates ABSTRACTION and is the parent for INHERITANCE
 * ============================================================
 *
 * ABSTRACTION: This class is 'abstract' - it cannot be instantiated directly.
 * It defines the common structure for all transactions and declares
 * abstract methods that MUST be implemented by subclasses.
 *
 * INHERITANCE: Expense and Income both extend this class,
 * inheriting all its fields and concrete methods.
 *
 * Viva Q: What is Abstraction?
 * A: Hiding implementation details and showing only essential features.
 *    'abstract' classes cannot be instantiated; they act as templates.
 *
 * Viva Q: What is Inheritance?
 * A: A class (child) inheriting fields and methods from another class (parent)
 *    using the 'extends' keyword.
 */
public abstract class Transaction {

    // Private fields - ENCAPSULATION
    private Long      id;
    private double    amount;
    private String    description;
    private LocalDate date;
    private Long      categoryId;
    private String    categoryName;   // populated via JOIN
    private Long      userId;

    // ===================== Constructors =====================

    public Transaction() {}

    public Transaction(double amount, String description, LocalDate date,
                       Long categoryId, Long userId) {
        this.amount      = amount;
        this.description = description;
        this.date        = date;
        this.categoryId  = categoryId;
        this.userId      = userId;
    }

    // ===================== Abstract Methods =====================
    // ABSTRACTION - subclasses (Expense, Income) MUST implement these

    /**
     * Returns the type of transaction: "INCOME" or "EXPENSE"
     * Each subclass provides its own implementation (POLYMORPHISM)
     */
    public abstract String getType();

    /**
     * Returns a human-readable summary of this transaction.
     * Demonstrates runtime POLYMORPHISM when called via Transaction reference.
     */
    public abstract String getSummary();

    // ===================== Concrete Method =====================
    // Shared by all subclasses - no need to override

    /**
     * Returns amount formatted as Indian Rupee string.
     */
    public String getFormattedAmount() {
        return String.format("₹%.2f", amount);
    }

    // ==================== Getters & Setters ====================

    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }

    public double getAmount()                   { return amount; }
    public void   setAmount(double amount)      { this.amount = amount; }

    public String getDescription()              { return description; }
    public void   setDescription(String d)      { this.description = d; }

    public LocalDate getDate()                  { return date; }
    public void      setDate(LocalDate date)    { this.date = date; }

    public Long getCategoryId()                 { return categoryId; }
    public void setCategoryId(Long cId)         { this.categoryId = cId; }

    public String getCategoryName()             { return categoryName; }
    public void   setCategoryName(String name)  { this.categoryName = name; }

    public Long getUserId()                     { return userId; }
    public void setUserId(Long userId)          { this.userId = userId; }
}
