-- =========================================================
-- Expense Tracker - Database Schema
-- H2 Embedded Database (runs auto on startup)
-- =========================================================

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Categories table
-- user_id = NULL means it's a default (system) category
-- user_id = some value means it's a custom user category
CREATE TABLE IF NOT EXISTS categories (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(50)  NOT NULL,
    icon    VARCHAR(10)  DEFAULT '💰',
    color   VARCHAR(20)  DEFAULT '#6366f1',
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Transactions table (stores both Income and Expense)
-- type column = 'INCOME' or 'EXPENSE' (mapped to Income/Expense subclasses in Java)
CREATE TABLE IF NOT EXISTS transactions (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    type        VARCHAR(10)    NOT NULL,
    amount      DECIMAL(12, 2) NOT NULL,
    description VARCHAR(255),
    date        DATE           NOT NULL,
    category_id BIGINT,
    user_id     BIGINT         NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (user_id)     REFERENCES users(id)      ON DELETE CASCADE
);

-- Budgets table - monthly budget limits per category
-- Note: 'month' and 'year' are H2 reserved words, using budget_month/budget_year
CREATE TABLE IF NOT EXISTS budgets (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT         NOT NULL,
    category_id  BIGINT         NOT NULL,
    limit_amount DECIMAL(12, 2) NOT NULL,
    budget_month INT            NOT NULL,
    budget_year  INT            NOT NULL,
    FOREIGN KEY (user_id)     REFERENCES users(id)      ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- =========================================================
-- Seed Default Categories
-- Using MERGE INTO KEY(id) - idempotent (safe to run repeatedly)
-- =========================================================
MERGE INTO categories (id, name, icon, color, user_id) KEY(id) VALUES (1,  'Food',          '🍔', '#ef4444', NULL);
MERGE INTO categories (id, name, icon, color, user_id) KEY(id) VALUES (2,  'Travel',        '✈️', '#3b82f6', NULL);
MERGE INTO categories (id, name, icon, color, user_id) KEY(id) VALUES (3,  'Bills',         '📄', '#f59e0b', NULL);
MERGE INTO categories (id, name, icon, color, user_id) KEY(id) VALUES (4,  'Shopping',      '🛍️', '#8b5cf6', NULL);
MERGE INTO categories (id, name, icon, color, user_id) KEY(id) VALUES (5,  'Entertainment', '🎬', '#06b6d4', NULL);
MERGE INTO categories (id, name, icon, color, user_id) KEY(id) VALUES (6,  'Health',        '💊', '#10b981', NULL);
MERGE INTO categories (id, name, icon, color, user_id) KEY(id) VALUES (7,  'Education',     '📚', '#f97316', NULL);
MERGE INTO categories (id, name, icon, color, user_id) KEY(id) VALUES (8,  'Salary',        '💼', '#22c55e', NULL);
MERGE INTO categories (id, name, icon, color, user_id) KEY(id) VALUES (9,  'Freelance',     '💻', '#a855f7', NULL);
MERGE INTO categories (id, name, icon, color, user_id) KEY(id) VALUES (10, 'Other',         '💰', '#6b7280', NULL);
