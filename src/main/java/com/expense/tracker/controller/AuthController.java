package com.expense.tracker.controller;

import com.expense.tracker.model.User;
import com.expense.tracker.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AuthController - handles user login, signup, and session management.
 * Uses HttpSession to track logged-in users (simple session-based auth).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * POST /api/auth/signup
     * Register a new user account.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String email    = request.getOrDefault("email", "");

        // Basic validation
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username and password are required"));
        }
        if (username.length() < 3) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username must be at least 3 characters"));
        }
        if (password.length() < 4) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password must be at least 4 characters"));
        }
        if (userService.existsByUsername(username)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username is already taken"));
        }

        User user = userService.createUser(username, password, email);
        return ResponseEntity.ok(Map.of(
            "message", "Account created successfully!",
            "userId",  user.getId()
        ));
    }

    /**
     * POST /api/auth/login
     * Authenticate user and create a session.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpSession session) {
        String username = request.get("username");
        String password = request.get("password");

        User user = userService.authenticate(username, password);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }

        // Store user info in session
        session.setAttribute("userId",   user.getId());
        session.setAttribute("username", user.getUsername());

        return ResponseEntity.ok(Map.of(
            "message",  "Login successful",
            "userId",   user.getId(),
            "username", user.getUsername()
        ));
    }

    /**
     * POST /api/auth/logout
     * Invalidate the current session.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    /**
     * GET /api/auth/me
     * Get current logged-in user info.
     * Returns 401 if not logged in (frontend uses this to guard pages).
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        Long   userId   = (Long)   session.getAttribute("userId");
        String username = (String) session.getAttribute("username");

        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        return ResponseEntity.ok(Map.of("userId", userId, "username", username));
    }
}
