package com.daem.infrastructure.persistence.jpa.repository;

import com.daem.infrastructure.persistence.jpa.entity.RegisteredClientEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegisteredClientJpaRepository extends JpaRepository<RegisteredClientEntity, String> {
    Optional<RegisteredClientEntity> findByClientId(String clientId);
    void deleteByClientId(String clientId);
}
