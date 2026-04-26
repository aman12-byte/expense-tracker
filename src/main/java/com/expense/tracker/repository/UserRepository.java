package com.expense.tracker.repository;

import com.expense.tracker.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * UserRepository - handles database operations for User.
 * Uses Spring JdbcTemplate (clean JDBC, no JPA/ORM).
 */
@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbc;

    // RowMapper - converts a DB row to a User object
    private final RowMapper<User> userMapper = (rs, row) -> {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setEmail(rs.getString("email"));
        u.setCreatedAt(rs.getString("created_at"));
        return u;
    };

    /**
     * Save a new user to the database.
     * Returns the User with the generated ID set.
     */
    public User save(User user) {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    /**
     * Find a user by their username.
     * Returns null if not found.
     */
    public User findByUsername(String username) {
        try {
            return jdbc.queryForObject(
                "SELECT * FROM users WHERE username = ?", userMapper, username);
        } catch (Exception e) {
            return null;  // User not found
        }
    }

    /**
     * Check if a username is already taken.
     */
    public boolean existsByUsername(String username) {
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM users WHERE username = ?", Integer.class, username);
        return count != null && count > 0;
    }
}
