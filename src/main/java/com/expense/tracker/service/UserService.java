package com.expense.tracker.service;

import com.expense.tracker.model.User;
import com.expense.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * UserService - handles user registration and login.
 *
 * ENCAPSULATION: The password hashing logic is hidden inside this
 * service. The controller only calls createUser() and authenticate()
 * without needing to know HOW passwords are hashed.
 *
 * Viva Q: Where do you use Encapsulation in services?
 * A: The hashPassword() method is private — callers cannot access it
 *    directly. The service hides the implementation and exposes only
 *    the needed public methods. That's Encapsulation.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Register a new user. Hashes the password before saving.
     */
    public User createUser(String username, String password, String email) {
        String hashedPassword = hashPassword(password);
        User user = new User(username, hashedPassword, email);
        return userRepository.save(user);
    }

    /**
     * Authenticate a user by username and password.
     * Returns the User object if credentials are valid, null otherwise.
     */
    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) return null;

        // Compare hashed passwords
        if (!user.getPassword().equals(hashPassword(password))) return null;

        return user;
    }

    /**
     * Check if a username already exists.
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * PRIVATE method - hidden from outside (ENCAPSULATION).
     * Hashes the password using SHA-256 algorithm.
     * Note: In production, BCrypt would be used instead.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            // Should never happen with SHA-256
            return password;
        }
    }
}
