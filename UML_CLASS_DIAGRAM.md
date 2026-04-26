# UML Class Diagram — Expense Tracker

## Class Hierarchy

```
                    ┌─────────────────────────────────┐
                    │        <<abstract>>             │
                    │         Transaction             │
                    │─────────────────────────────────│
                    │ - id: Long                      │
                    │ - amount: double                │
                    │ - description: String           │
                    │ - date: LocalDate               │
                    │ - categoryId: Long              │
                    │ - categoryName: String          │
                    │ - userId: Long                  │
                    │─────────────────────────────────│
                    │ + getType(): String  <<abstract>>│
                    │ + getSummary(): String <<abstract>
                    │ + getFormattedAmount(): String  │
                    │ + [getters / setters]           │
                    └─────────────────────────────────┘
                               △ (extends)
              ┌────────────────┴────────────────┐
              │                                 │
   ┌──────────────────────┐        ┌──────────────────────┐
   │         Expense      │        │         Income       │
   │──────────────────────│        │──────────────────────│
   │ (inherits all fields)│        │ (inherits all fields)│
   │──────────────────────│        │──────────────────────│
   │ + getType(): String  │        │ + getType(): String  │
   │   returns "EXPENSE"  │        │   returns "INCOME"   │
   │ + getSummary(): String         │ + getSummary(): String│
   └──────────────────────┘        └──────────────────────┘


   ┌──────────────────────┐
   │         User         │
   │──────────────────────│
   │ - id: Long           │
   │ - username: String   │
   │ - password: String   │
   │ - email: String      │
   │ - createdAt: String  │
   │──────────────────────│
   │ + [getters/setters]  │
   └──────────────────────┘


   ┌──────────────────────┐
   │       Category       │
   │──────────────────────│
   │ - id: Long           │
   │ - name: String       │
   │ - icon: String       │
   │ - color: String      │
   │ - userId: Long       │
   │──────────────────────│
   │ + isDefault():boolean│
   │ + [getters/setters]  │
   └──────────────────────┘


   ┌──────────────────────────────┐
   │           Budget             │
   │──────────────────────────────│
   │ - id: Long                   │
   │ - userId: Long               │
   │ - categoryId: Long           │
   │ - categoryName: String       │
   │ - limitAmount: double        │
   │ - spentAmount: double        │
   │ - month: int                 │
   │ - year: int                  │
   │──────────────────────────────│
   │ + isExceeded(): boolean      │
   │ + getPercentageUsed(): double│
   │ + getRemaining(): double     │
   │ + [getters/setters]          │
   └──────────────────────────────┘
```

## Service Layer

```
   ┌──────────────────────────────────────────┐
   │           TransactionService             │
   │──────────────────────────────────────────│
   │ - repo: TransactionRepository            │
   │──────────────────────────────────────────│
   │ + addTransaction(t: Transaction)         │  ← POLYMORPHISM
   │ + updateTransaction(t: Transaction)      │
   │ + deleteTransaction(id, userId)          │
   │ + getAllTransactions(userId): List<Tx>   │
   │ + getFilteredTransactions(...)           │
   │ + getTotalIncome(userId, month, year)    │
   │ + getTotalExpenses(userId, month, year)  │
   │ + getBalance(userId): double             │
   │ + getCategoryWiseExpenses(...)           │
   │ + createExpense(...): Expense            │
   │ + createIncome(...): Income              │
   └──────────────────────────────────────────┘

   ┌──────────────────────────────────────────┐
   │            BudgetManager                 │
   │──────────────────────────────────────────│
   │ - repo: BudgetRepository                 │
   │──────────────────────────────────────────│
   │ + setBudget(b: Budget): Budget           │
   │ + getBudgetsWithSpending(...): List<B>   │
   │ + getExceededBudgets(...): List<Budget>  │
   │ + getTotalMonthlyBudget(...): double     │
   │ + deleteBudget(id)                       │
   └──────────────────────────────────────────┘

   ┌──────────────────────────────────────────┐
   │             UserService                  │
   │──────────────────────────────────────────│
   │ - repo: UserRepository                   │
   │──────────────────────────────────────────│
   │ + createUser(username, pwd, email): User │
   │ + authenticate(username, pwd): User      │
   │ + existsByUsername(username): boolean    │
   │ - hashPassword(pwd): String   [PRIVATE]  │ ← ENCAPSULATION
   └──────────────────────────────────────────┘
```

## Relationships

```
User          1 ──────────── * Transaction
User          1 ──────────── * Budget
User          1 ──────────── * Category (custom)
Category      1 ──────────── * Transaction (optional)
Category      1 ──────────── * Budget
Transaction   <|──────────── Expense         (Inheritance)
Transaction   <|──────────── Income          (Inheritance)
```

## OOP Principles Summary

| Principle | Class/Method |
|---|---|
| **Encapsulation** | All fields `private`; UserService.hashPassword() is `private` |
| **Inheritance** | `Expense extends Transaction`, `Income extends Transaction` |
| **Polymorphism** | `TransactionService.addTransaction(Transaction)` works for both; `getType()` / `getSummary()` are overridden |
| **Abstraction** | `Transaction` is `abstract`; defines contract via abstract methods |
