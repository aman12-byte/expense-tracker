package com.expense.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================================
 * Expense Tracker Application - Main Entry Point
 * College Mini Project | 2nd Year OOP Assignment
 * ============================================================
 *
 * OOP Concepts Demonstrated:
 *  - Encapsulation  : All model fields are private with getters/setters
 *  - Inheritance    : Expense and Income both extend abstract Transaction
 *  - Polymorphism   : TransactionService handles both Expense and Income
 *                     via Transaction reference (runtime polymorphism)
 *  - Abstraction    : Transaction is abstract; forces subclasses to
 *                     implement getType() and getSummary()
 *
 * Tech Stack:
 *  - Java 17 + Spring Boot 3.2
 *  - H2 Embedded Database (no MySQL needed - zero setup)
 *  - HTML + CSS + JavaScript frontend
 *  - Chart.js for graphs
 *
 * Run: mvn spring-boot:run
 * Open: http://localhost:8080
 */
@SpringBootApplication
public class ExpenseTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseTrackerApplication.class, args);
        System.out.println("\n✅ Expense Tracker is running!");
        System.out.println("🌐 Open http://localhost:8080 in your browser");
        System.out.println("🗄️  H2 Console: http://localhost:8080/h2-console\n");
    }
}
