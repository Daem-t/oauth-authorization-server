package com.daem.oauth.infrastructure.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, Long> {
    RoleEntity findByName(String name);
} 