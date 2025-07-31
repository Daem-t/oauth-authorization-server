package com.daem.oauth.infrastructure.client;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RegisteredClientJpaRepository extends JpaRepository<RegisteredClientEntity, String> {
    Optional<RegisteredClientEntity> findByClientId(String clientId);
} 