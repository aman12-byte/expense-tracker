package com.expense.tracker.model;

import java.time.LocalDate;

/**
 * ============================================================
 * Income - Subclass of Transaction
 * Demonstrates INHERITANCE and POLYMORPHISM
 * ============================================================
 *
 * INHERITANCE: Income extends Transaction just like Expense does.
 * Both Expense and Income share the same parent (Transaction).
 * This is the classic "IS-A" relationship:
 *   - An Expense IS-A Transaction ✓
 *   - An Income  IS-A Transaction ✓
 *
 * POLYMORPHISM: When TransactionService processes a List<Transaction>,
 * and calls transaction.getType(), the correct version (from Expense
 * or Income) is called automatically at runtime.
 * This is called Runtime Polymorphism / Dynamic Dispatch.
 */
public class Income extends Transaction {

    // Default constructor
    public Income() {
        super();
    }

    // Parameterized constructor - calls super (parent) constructor
    public Income(double amount, String description, LocalDate date,
                  Long categoryId, Long userId) {
        super(amount, description, date, categoryId, userId);
    }

    // ===================== POLYMORPHISM (Method Overriding) =====================

    /**
     * @Override - overrides the abstract method from Transaction.
     * Returns "INCOME" - identifies this object's type at runtime.
     */
    @Override
    public String getType() {
        return "INCOME";
    }

    /**
     * @Override - provides Income-specific summary text.
     */
    @Override
    public String getSummary() {
        return "Income: " + getDescription() + " + " + getFormattedAmount();
    }
}
