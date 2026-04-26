# 💰 Expense Tracker — College Mini Project

**Course**: Object-Oriented Programming (Java)
**Year**: 2nd Year B.Tech 
**Tech Stack**: Java 17 + Spring Boot + H2 Database + HTML/CSS/JS

---

## 🚀 How to Run (Mac / Windows)

### Prerequisites
- Java 17+ installed (`java -version` to check)
- Maven installed (`mvn -version` to check)
  - Install on Mac: `brew install maven`

### Steps

```bash
# 1. Navigate to the project directory
cd "/Users/ranveer/expense tracker"

# 2. Run the application
mvn spring-boot:run

# 3. Open in browser
open http://localhost:8080
```

The app will:
- Auto-create the H2 database file at `./data/expensetracker.mv.db`
- Auto-seed 10 default categories (Food, Travel, Bills, etc.)
- Run on port 8080

### Database Inspector
Visit http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:file:./data/expensetracker`
- Username: `sa`, Password: (empty)

---

## 📁 Project Structure

```
expense-tracker/
│
├── pom.xml                          # Maven dependencies
│
├── src/main/java/com/expense/tracker/
│   │
│   ├── ExpenseTrackerApplication.java    # Main entry point
│   │
│   ├── model/                        # OOP Model classes
│   │   ├── User.java                 # Encapsulation
│   │   ├── Transaction.java          # Abstract (Abstraction)
│   │   ├── Expense.java              # extends Transaction (Inheritance)
│   │   ├── Income.java               # extends Transaction (Inheritance)
│   │   ├── Category.java             # Encapsulation
│   │   └── Budget.java               # with business logic methods
│   │
│   ├── repository/                   # Database layer (JDBC)
│   │   ├── UserRepository.java
│   │   ├── TransactionRepository.java
│   │   ├── CategoryRepository.java
│   │   └── BudgetRepository.java
│   │
│   ├── service/                      # Business logic layer
│   │   ├── UserService.java
│   │   ├── TransactionService.java   # Polymorphism shown here
│   │   ├── CategoryService.java
│   │   └── BudgetManager.java        # Budget alerts
│   │
│   └── controller/                   # REST API endpoints
│       ├── AuthController.java
│       ├── TransactionController.java
│       ├── CategoryController.java
│       ├── BudgetController.java
│       └── DashboardController.java
│
└── src/main/resources/
    ├── application.properties        # H2 config
    ├── schema.sql                    # Auto-run DB schema
    └── static/                       # Frontend web files
        ├── index.html                # Login / Signup
        ├── dashboard.html            # Overview + Charts
        ├── transactions.html         # Add/Edit/Delete
        ├── categories.html           # Category management
        ├── budget.html               # Budget & alerts
        └── css/style.css             # Dark theme stylesheet
```

---

## ✅ Features

| Feature | Status |
|---|---|
| User Login / Signup | ✅ |
| Add Income & Expenses | ✅ |
| Edit & Delete Transactions | ✅ |
| Custom Categories | ✅ |
| Monthly Budgets per Category | ✅ |
| Budget Exceeded Warnings | ✅ |
| Dashboard (Income / Expense / Balance) | ✅ |
| Transaction History with Filters | ✅ |
| Pie Chart (Category-wise) | ✅ |
| Bar Chart (Monthly Expenses) | ✅ |
| H2 Database (zero setup) | ✅ |

---

## 🏗️ OOP Concepts Summary

| Concept | Where Used |
|---|---|
| **Encapsulation** | All model classes: private fields + public getters/setters |
| **Inheritance** | `Expense extends Transaction`, `Income extends Transaction` |
| **Polymorphism** | `TransactionService.addTransaction(Transaction)` handles both; `getType()` and `getSummary()` are overridden in subclasses |
| **Abstraction** | `Transaction` is `abstract`; declares abstract methods `getType()` and `getSummary()` |
