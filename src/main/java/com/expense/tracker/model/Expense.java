package com.expense.tracker.model;

import java.time.LocalDate;

/**
 * ============================================================
 * Expense - Subclass of Transaction
 * Demonstrates INHERITANCE and POLYMORPHISM
 * ============================================================
 *
 * INHERITANCE: Expense extends Transaction, inheriting all its
 * fields (amount, date, description, etc.) and methods.
 * We only add what's specific to an expense.
 *
 * POLYMORPHISM (Method Overriding): getType() and getSummary()
 * are declared abstract in Transaction. Expense provides its own
 * specific implementations using @Override.
 *
 * Viva Q: What is Polymorphism?
 * A: Same method name, different behavior based on object type.
 *    When we call transaction.getType() on an Expense object,
 *    it returns "EXPENSE" even though we're using a Transaction reference.
 *    This is Runtime (Dynamic) Polymorphism / Method Overriding.
 */
public class Expense extends Transaction {

    // Default constructor
    public Expense() {
        super();
    }

    // Parameterized constructor - calls super (parent) constructor
    public Expense(double amount, String description, LocalDate date,
                   Long categoryId, Long userId) {
        super(amount, description, date, categoryId, userId);
    }

    // ===================== POLYMORPHISM (Method Overriding) =====================

    /**
     * @Override - overrides the abstract method from Transaction.
     * Returns "EXPENSE" - identifies this object's type at runtime.
     */
    @Override
    public String getType() {
        return "EXPENSE";
    }

    /**
     * @Override - provides Expense-specific summary text.
     */
    @Override
    public String getSummary() {
        return "Expense: " + getDescription() + " — " + getFormattedAmount();
    }
}
