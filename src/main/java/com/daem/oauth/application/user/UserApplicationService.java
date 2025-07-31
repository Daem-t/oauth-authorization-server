package com.daem.oauth.application.user;

import com.daem.oauth.application.exception.*;
import com.daem.oauth.domain.user.User;
import com.daem.oauth.domain.user.UserRepository;
import com.daem.oauth.domain.user.event.UserRegisteredEvent;
import com.daem.oauth.interfaces.auth.dto.RegisterRequest;
import com.daem.oauth.interfaces.common.constants.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(UserApplicationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public UserApplicationService(UserRepository userRepository, 
                                  PasswordEncoder passwordEncoder, 
                                  ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void registerUser(RegisterRequest request, String ipAddress, String defaultRole) {
        logger.info("Registering user: {}", request.getUsername());
        
        // 1. Check for uniqueness
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException(MessageConstants.Validation.USERNAME_EXISTS);
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(MessageConstants.Validation.EMAIL_EXISTS);
        }

        // 2. Use the domain factory to create the user
        User user = User.registerNewUser(
            request.getUsername(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword())
        );
        // Note: Role assignment could also be part of the factory or a separate domain service
        
        // 3. Save the new user
        User savedUser = userRepository.save(user);
        logger.info("User {} saved with status PENDING_ACTIVATION.", savedUser.getUsername());

        // 4. Publish a domain event. The email sending is now handled by a listener.
        eventPublisher.publishEvent(new UserRegisteredEvent(savedUser));
        logger.info("UserRegisteredEvent published for user {}", savedUser.getUsername());
    }

    @Transactional
    public void activateUser(String token) {
        logger.info("Activating user with token: {}", token);

        User user = userRepository.findByActivationToken(token)
                .orElseThrow(() -> new InvalidActivationTokenException(MessageConstants.Auth.TOKEN_INVALID));

        if (user.getActivationTokenExpiry() != null && user.getActivationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ExpiredActivationTokenException(MessageConstants.Auth.TOKEN_EXPIRED);
        }

        try {
            user.activate();
        } catch (UserAlreadyActiveException e) {
            logger.warn("Attempted to activate an already active user: {}", user.getUsername());
        }

        userRepository.save(user);
        logger.info("User {} activated successfully.", user.getUsername());
    }
    
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
