package com.daem.oauth.infrastructure.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionJpaRepository extends JpaRepository<PermissionEntity, Long> {
    PermissionEntity findByName(String name);
} 