package com.expense.tracker.model;

/**
 * ============================================================
 * Category Model - Demonstrates ENCAPSULATION
 * ============================================================
 * Represents expense/income categories like Food, Travel, Bills.
 * Default categories have user_id = null (system-wide).
 * Custom categories created by a user have user_id = that user's id.
 */
public class Category {

    // Private fields - ENCAPSULATION
    private Long   id;
    private String name;
    private String icon;    // emoji icon, e.g. "🍔"
    private String color;   // hex color, e.g. "#ef4444"
    private Long   userId;  // null = default/system category

    // Default constructor
    public Category() {}

    // Parameterized constructor
    public Category(String name, String icon, String color, Long userId) {
        this.name   = name;
        this.icon   = icon;
        this.color  = color;
        this.userId = userId;
    }

    /**
     * Returns true if this is a system default category (user_id is null).
     * Custom categories can be edited/deleted; default ones cannot.
     */
    public boolean isDefault() {
        return this.userId == null;
    }

    // ==================== Getters & Setters ====================

    public Long   getId()                     { return id; }
    public void   setId(Long id)              { this.id = id; }

    public String getName()                   { return name; }
    public void   setName(String name)        { this.name = name; }

    public String getIcon()                   { return icon; }
    public void   setIcon(String icon)        { this.icon = icon; }

    public String getColor()                  { return color; }
    public void   setColor(String color)      { this.color = color; }

    public Long   getUserId()                 { return userId; }
    public void   setUserId(Long userId)      { this.userId = userId; }
}
