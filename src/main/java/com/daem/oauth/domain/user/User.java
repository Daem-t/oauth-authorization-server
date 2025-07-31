package com.daem.oauth.domain.user;

import com.daem.oauth.application.exception.UserAlreadyActiveException;
import com.daem.oauth.interfaces.common.constants.MessageConstants;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

/**
 * User domain model representing a user in the system.
 * This class encapsulates user business logic and rules.
 */
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String status;
    private String activationToken;
    private LocalDateTime activationTokenExpiry;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<String> roles;

    // Constructors
    public User() {
        this.roles = new HashSet<>();
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.enabled = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getActivationToken() { return activationToken; }
    public void setActivationToken(String activationToken) { this.activationToken = activationToken; }

    public LocalDateTime getActivationTokenExpiry() { return activationTokenExpiry; }
    public void setActivationTokenExpiry(LocalDateTime activationTokenExpiry) { this.activationTokenExpiry = activationTokenExpiry; }

    public Boolean getAccountNonExpired() { return accountNonExpired; }
    public void setAccountNonExpired(Boolean accountNonExpired) { this.accountNonExpired = accountNonExpired; }

    public Boolean getAccountNonLocked() { return accountNonLocked; }
    public void setAccountNonLocked(Boolean accountNonLocked) { this.accountNonLocked = accountNonLocked; }

    public Boolean getCredentialsNonExpired() { return credentialsNonExpired; }
    public void setCredentialsNonExpired(Boolean credentialsNonExpired) { this.credentialsNonExpired = credentialsNonExpired; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    // Business Logic Methods

    /**
     * Activates the user account.
     * This method encapsulates the business rules for account activation.
     * @throws UserAlreadyActiveException if the user is already active.
     */
    public void activate() {
        if ("ACTIVE".equals(this.status)) {
            throw new UserAlreadyActiveException(MessageConstants.User.ALREADY_ACTIVE);
        }
        this.status = "ACTIVE";
        this.enabled = true;
        this.activationToken = null;
        this.activationTokenExpiry = null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if the activation token is valid and not expired.
     * @param token The token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean isActivationTokenValid(String token) {
        return this.activationToken != null 
            && this.activationToken.equals(token)
            && this.activationTokenExpiry != null
            && this.activationTokenExpiry.isAfter(LocalDateTime.now());
    }

    /**
     * Checks if the user account is active.
     * @return true if the user is active, false otherwise
     */
    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }

    // --- Factory Method ---

    /**
     * Creates a new user instance for registration.
     * This factory method ensures that every new user is created with a consistent initial state.
     *
     * @param username The username for the new user.
     * @param email The email for the new user.
     * @param encodedPassword The password, already encrypted.
     * @return A new User instance, ready to be saved.
     */
    public static User registerNewUser(String username, String email, String encodedPassword) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(encodedPassword);
        
        // Set initial state according to business rules
        newUser.setStatus("PENDING_ACTIVATION");
        newUser.setEnabled(false);
        newUser.setAccountNonExpired(true);
        newUser.setAccountNonLocked(true);
        newUser.setCredentialsNonExpired(true);
        
        // Generate activation token
        newUser.setActivationToken(java.util.UUID.randomUUID().toString());
        newUser.setActivationTokenExpiry(java.time.LocalDateTime.now().plusHours(24)); // Token valid for 24 hours
        
        newUser.setCreatedAt(java.time.LocalDateTime.now());
        newUser.setUpdatedAt(java.time.LocalDateTime.now());
        
        return newUser;
    }
}
 