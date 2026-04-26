# OOP Concepts Explanation — Expense Tracker
## College Viva Preparation Guide

---

## 1. ENCAPSULATION

> **Definition**: Wrapping data (fields) and the methods that operate on them into a single unit (class), and restricting direct access to the data using access modifiers.

### Where it's used:

**User.java**
```java
public class User {
    private Long   id;          // PRIVATE - cannot be accessed directly
    private String username;
    private String password;    // sensitive data - hidden!

    // Controlled access via public methods
    public String getUsername() { return username; }
    public void   setUsername(String u) { this.username = u; }
}
```

**UserService.java** — `hashPassword()` is private:
```java
// ENCAPSULATION: internal implementation is hidden from callers
private String hashPassword(String password) {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    ...
}
// Caller only sees:
public User authenticate(String username, String password) { ... }
```

**Budget.java** — business logic inside class:
```java
// Data and related behaviour are kept together
public boolean isExceeded() {
    return spentAmount > limitAmount;  // uses private fields
}
public double getPercentageUsed() {
    return (spentAmount / limitAmount) * 100;
}
```

**Viva Answer**: Encapsulation hides the internal state of an object using `private` access modifier and provides `public` getter/setter methods for controlled access. This protects data from unauthorized modification.

---

## 2. INHERITANCE

> **Definition**: A mechanism where one class (child) acquires the properties and behaviors of another class (parent) using the `extends` keyword.

### Where it's used:

```
Transaction (parent/abstract)
    ├── Expense  (child) — extends Transaction
    └── Income   (child) — extends Transaction
```

**Transaction.java** (parent):
```java
public abstract class Transaction {
    // Common fields shared by BOTH Expense and Income
    private Long      id;
    private double    amount;
    private String    description;
    private LocalDate date;
    private Long      categoryId;
    ...
}
```

**Expense.java** (child) — reuses ALL fields from Transaction:
```java
public class Expense extends Transaction {
    // No need to re-declare id, amount, description, etc.
    // They are INHERITED from Transaction!

    public Expense(double amount, String desc, LocalDate date, Long catId, Long userId) {
        super(amount, desc, date, catId, userId);  // calls parent constructor
    }
    ...
}
```

**Using Inheritance**:
```java
Expense e = new Expense(500.0, "Lunch", LocalDate.now(), 1L, 1L);
e.getAmount();       // inherited from Transaction — works!
e.getDescription();  // inherited from Transaction — works!
e.getType();         // defined in Expense itself
```

**Viva Answer**: Inheritance allows a subclass to reuse fields and methods from a superclass. `Expense` and `Income` both inherit `amount`, `date`, `description`, etc. from `Transaction`. This avoids code duplication and establishes an IS-A relationship: an Expense IS-A Transaction.

---

## 3. POLYMORPHISM

> **Definition**: The ability of a single interface (method name) to represent different behaviors depending on the object type. Two types: Compile-time (Overloading) and Runtime (Overriding).

### Where it's used (Runtime Polymorphism — Method Overriding):

**Expense.java and Income.java** override `getType()` and `getSummary()`:

```java
// In Expense:
@Override
public String getType() { return "EXPENSE"; }

@Override
public String getSummary() { return "Expense: " + getDescription() + " — " + getFormattedAmount(); }

// In Income:
@Override
public String getType() { return "INCOME"; }

@Override
public String getSummary() { return "Income: " + getDescription() + " + " + getFormattedAmount(); }
```

**TransactionService.java** — handles BOTH with ONE method:
```java
// POLYMORPHISM: same method works for both Expense and Income
public Transaction addTransaction(Transaction transaction) {
    return transactionRepository.save(transaction);
    // transaction.getType() automatically calls the correct
    // subclass method at RUNTIME!
}
```

**TransactionRepository.java** — RowMapper demonstrates polymorphism:
```java
// At runtime, decides which object to create:
if ("EXPENSE".equals(type)) {
    t = new Expense();   // Runtime Polymorphism
} else {
    t = new Income();
}
// Both returned as Transaction — same type, different behavior!
```

**TransactionController.java**:
```java
// Same addTransaction() method handles Expense and Income objects:
Transaction transaction;
if ("EXPENSE".equals(type)) {
    transaction = transactionService.createExpense(...);
} else {
    transaction = transactionService.createIncome(...);
}
Transaction saved = transactionService.addTransaction(transaction); // POLYMORPHISM
```

**Viva Answer**: Polymorphism means "many forms". When we call `transaction.getType()` on a `Transaction` reference that holds an `Expense` object, it returns `"EXPENSE"` not `"INCOME"`. Java automatically calls the correct overriding method at runtime. This is Runtime Polymorphism (Dynamic Dispatch).

---

## 4. ABSTRACTION

> **Definition**: Hiding complex implementation details and showing only what's necessary. In Java, achieved using `abstract` classes and `interfaces`.

### Where it's used:

**Transaction.java** — abstract class:
```java
public abstract class Transaction {
    // Abstract methods MUST be implemented by subclasses
    // We define WHAT, not HOW

    public abstract String getType();    // Must be in Expense/Income
    public abstract String getSummary(); // Must be in Expense/Income

    // Concrete method — common implementation for all subclasses
    public String getFormattedAmount() {
        return String.format("₹%.2f", amount);
    }
}
```

You CANNOT do:
```java
Transaction t = new Transaction();  // ERROR! abstract class cannot be instantiated
```

You CAN do:
```java
Transaction t = new Expense(...);   // OK! Expense is a concrete class
Transaction t = new Income(...);    // OK! Income is a concrete class
```

**Viva Answer**: Abstraction hides unnecessary details. `Transaction` is abstract — it defines the template (getType, getSummary) but leaves the implementation to subclasses. Users of the code work with the abstract `Transaction` type without needing to know if it's an `Expense` or `Income` underneath.

---

## Common Viva Questions & Answers

**Q1: What is the difference between Abstraction and Encapsulation?**
A: Encapsulation is about hiding DATA using access modifiers (private fields). Abstraction is about hiding IMPLEMENTATION details using abstract classes/interfaces.

**Q2: Can you instantiate Transaction?**
A: No, because it's declared `abstract`. We must use its subclasses: `Expense` or `Income`.

**Q3: What is method overriding?**
A: When a subclass provides a specific implementation for a method already defined in the parent class. `getType()` is overridden in both `Expense` and `Income`.

**Q4: What's the difference between `extends` and `implements`?**
A: `extends` is used for class inheritance (one class inherits from another). `implements` is used for interfaces. Java allows only single-class inheritance but multiple interface implementation.

**Q5: Where is the database in this project?**
A: H2 embedded database is used. It's a Java-based database that runs inside the JVM and stores data in a file (`./data/expensetracker.mv.db`). No separate MySQL installation needed.

**Q6: What is a Service layer?**
A: The Service layer contains business logic. It sits between the Controller (handles HTTP requests) and Repository (handles database). Example: `BudgetManager` checks if budgets are exceeded, `UserService` hashes passwords.

**Q7: What design pattern is shown in TransactionService?**
A: Factory-like pattern in `createExpense()` and `createIncome()` methods. Also shows polymorphism by returning both as `Transaction`.

**Q8: What is session-based authentication?**
A: When a user logs in successfully, their userId is stored in an `HttpSession` on the server. Each subsequent request checks the session to identify the user. No token needed.
