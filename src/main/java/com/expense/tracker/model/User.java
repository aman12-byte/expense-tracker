package com.expense.tracker.model;

/**
 * ============================================================
 * User Model - Demonstrates ENCAPSULATION
 * ============================================================
 * All fields are PRIVATE, accessible only via public getters/setters.
 * This is Encapsulation - hiding internal data and providing
 * controlled access through methods.
 *
 * Viva Q: What is Encapsulation?
 * A: Restricting direct access to class fields using 'private' and
 *    providing 'public' getters/setters for controlled access.
 */
public class User {

    // Private fields - ENCAPSULATION (data hiding)
    private Long   id;
    private String username;
    private String password;   // stored as SHA-256 hash
    private String email;
    private String createdAt;

    // Default constructor (needed by Spring/JDBC)
    public User() {}

    // Parameterized constructor
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email    = email;
    }

    // ==================== Getters & Setters ====================
    // These are the controlled access points (ENCAPSULATION)

    public Long getId()                { return id; }
    public void setId(Long id)         { this.id = id; }

    public String getUsername()              { return username; }
    public void   setUsername(String u)      { this.username = u; }

    public String getPassword()              { return password; }
    public void   setPassword(String p)      { this.password = p; }

    public String getEmail()                 { return email; }
    public void   setEmail(String e)         { this.email = e; }

    public String getCreatedAt()             { return createdAt; }
    public void   setCreatedAt(String c)     { this.createdAt = c; }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "'}";
    }
}
