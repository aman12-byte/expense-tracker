package com.expense.tracker.repository;

import com.expense.tracker.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

/**
 * CategoryRepository - handles database operations for Category.
 */
@Repository
public class CategoryRepository {

    @Autowired
    private JdbcTemplate jdbc;

    // RowMapper for Category
    private final RowMapper<Category> categoryMapper = (rs, row) -> {
        Category c = new Category();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        c.setIcon(rs.getString("icon"));
        c.setColor(rs.getString("color"));
        long uid = rs.getLong("user_id");
        c.setUserId(rs.wasNull() ? null : uid);  // null = default category
        return c;
    };

    /**
     * Get all categories visible to a user:
     * - Default categories (user_id IS NULL)
     * - Custom categories created by this user
     */
    public List<Category> findAllForUser(Long userId) {
        String sql = "SELECT * FROM categories WHERE user_id IS NULL OR user_id=? ORDER BY id";
        return jdbc.query(sql, categoryMapper, userId);
    }

    /**
     * Find a category by its ID.
     */
    public Category findById(Long id) {
        try {
            return jdbc.queryForObject("SELECT * FROM categories WHERE id=?", categoryMapper, id);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Save a new custom category.
     */
    public Category save(Category category) {
        String sql = "INSERT INTO categories (name, icon, color, user_id) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, category.getName());
            ps.setString(2, category.getIcon());
            ps.setString(3, category.getColor());
            if (category.getUserId() != null) {
                ps.setLong(4, category.getUserId());
            } else {
                ps.setNull(4, Types.BIGINT);
            }
            return ps;
        }, keyHolder);

        category.setId(keyHolder.getKey().longValue());
        return category;
    }

    /**
     * Update a custom category. Only user's own categories can be updated.
     */
    public void update(Category category) {
        jdbc.update(
            "UPDATE categories SET name=?, icon=?, color=? WHERE id=? AND user_id=?",
            category.getName(), category.getIcon(), category.getColor(),
            category.getId(), category.getUserId()
        );
    }

    /**
     * Delete a custom category. Only user's own categories can be deleted.
     */
    public void delete(Long id, Long userId) {
        jdbc.update("DELETE FROM categories WHERE id=? AND user_id=?", id, userId);
    }
}
