package com.daem.oauth.application.admin;

import com.daem.oauth.application.exception.BusinessException;
import com.daem.oauth.application.exception.EmailAlreadyExistsException;
import com.daem.oauth.application.exception.UserAlreadyExistsException;
import com.daem.oauth.application.exception.UserNotFoundException;
import com.daem.oauth.domain.user.Role;
import com.daem.oauth.domain.user.User;
import com.daem.oauth.domain.user.UserRepository;
import com.daem.oauth.infrastructure.user.*;
import com.daem.oauth.interfaces.admin.dto.*;
import com.daem.oauth.interfaces.common.constants.MessageConstants;
import com.daem.oauth.interfaces.common.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserManagementService {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementService.class);

    // For Command operations, use the Domain Repository to leverage domain logic
    private final UserRepository userRepository;
    // For Query operations with complex specifications, use the JPA Repository directly
    private final UserJpaRepository userJpaRepository;
    
    private final RoleJpaRepository roleJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageService messageService;

    public UserManagementService(UserRepository userRepository, UserJpaRepository userJpaRepository, RoleJpaRepository roleJpaRepository, PasswordEncoder passwordEncoder, MessageService messageService) {
        this.userRepository = userRepository;
        this.userJpaRepository = userJpaRepository;
        this.roleJpaRepository = roleJpaRepository;
        this.passwordEncoder = passwordEncoder;
        this.messageService = messageService;
    }

    @Transactional(readOnly = true)
    public UserListResponse getUserList(UserListRequest request) {
        logger.info("Getting user list: page={}, size={}, keyword={}", request.getPage(), request.getSize(), request.getKeyword());
        try {
            Pageable pageable = request.toPageable();
            Specification<UserEntity> spec = buildUserSpecification(request);
            
            // Perform the query using the infrastructure repository
            Page<UserEntity> userEntityPage = userJpaRepository.findAll(spec, pageable);
            
            // Map the result to the response DTO
            Page<User> userPage = userEntityPage.map(UserMapper::toDomain);
            logger.info("Found {} users, total pages: {}", userPage.getTotalElements(), userPage.getTotalPages());
            return UserListResponse.fromPage(userPage);
        } catch (Exception e) {
            logger.error("Failed to get user list", e);
            throw new RuntimeException(messageService.getMessage(MessageConstants.System.INTERNAL_ERROR), e);
        }
    }

    private Specification<UserEntity> buildUserSpecification(UserListRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(request.getKeyword())) {
                String keyword = "%" + request.getKeyword().toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), keyword),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), keyword)
                ));
            }
            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }
            if (StringUtils.hasText(request.getRole())) {
                predicates.add(criteriaBuilder.equal(root.join("roles").get("name"), request.getRole()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        logger.info("Getting user by id: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found: id={}", userId);
                    return new UserNotFoundException();
                });
    }

    @Transactional
    public User createUser(UserCreateRequest request) {
        logger.info("Creating new user: username={}, email={}", request.getUsername(), request.getEmail());
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("validation.username.exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("validation.email.exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(request.getEnabled());
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        updateUserRoles(user, request.getRoles() != null && !request.getRoles().isEmpty() ? request.getRoles() : List.of("USER"));
        
        User savedUser = userRepository.save(user);
        logger.info("User created successfully: id={}, username={}", savedUser.getId(), savedUser.getUsername());
        return savedUser;
    }

    @Transactional
    public void deleteUser(Long userId) {
        logger.info("Deleting user: id={}", userId);
        User user = getUserById(userId);
        user.setEnabled(false);
        user.setStatus("DELETED");
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        logger.info("User soft-deleted successfully: id={}", userId);
    }

    @Transactional
    public User updateUser(Long userId, UserUpdateRequest request) {
        logger.info("Updating user: id={}, username={}", userId, request.getUsername());
        User user = getUserById(userId);

        userRepository.findByUsername(request.getUsername()).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(userId)) throw new UserAlreadyExistsException();
        });
        userRepository.findByEmail(request.getEmail()).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(userId)) throw new EmailAlreadyExistsException();
        });

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        if (StringUtils.hasText(request.getStatus())) user.setStatus(request.getStatus());
        if (request.getEnabled() != null) user.setEnabled(request.getEnabled());
        if (request.getRoles() != null) updateUserRoles(user, request.getRoles());
        
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        logger.info("User updated successfully: id={}, username={}", userId, request.getUsername());
        return savedUser;
    }
    
    private void updateUserRoles(User user, List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            user.setRoles(new HashSet<>());
            return;
        }
        Set<Role> roles = roleNames.stream()
                .map(roleName -> roleJpaRepository.findByName(roleName))
                .filter(roleEntity -> roleEntity != null)
                .map(RoleMapper::toDomain)
                .collect(Collectors.toSet());
        user.setRoles(roles);
    }
    
    // Other methods like updateUserStatus, resetUserPassword can be refactored similarly
    // For brevity, I'm focusing on the core CRUD and list methods.
}