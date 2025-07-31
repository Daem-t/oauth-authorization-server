package com.daem.oauth.domain.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Domain Repository for User Aggregate.
 * It defines the contract for user persistence, free of any infrastructure details.
 */
public interface UserRepository {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByActivationToken(String token);
    Optional<User> findById(Long id);
    User save(User user);
    void deleteById(Long id);
    List<User> findAll();
    Page<User> findAll(Pageable pageable);
}