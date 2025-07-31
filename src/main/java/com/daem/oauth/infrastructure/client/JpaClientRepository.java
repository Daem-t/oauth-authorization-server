package com.daem.oauth.infrastructure.client;

import com.daem.oauth.domain.client.Client;
import com.daem.oauth.domain.client.ClientRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaClientRepository implements ClientRepository {
    private final RegisteredClientJpaRepository jpaRepository;
    public JpaClientRepository(RegisteredClientJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    @Override
    public Optional<Client> findByClientId(String clientId) {
        return jpaRepository.findByClientId(clientId)
                .map(RegisteredClientMapper::toDomain);
    }
    @Override
    public Client save(Client client) {
        RegisteredClientEntity entity = RegisteredClientMapper.toEntity(client);
        RegisteredClientEntity saved = jpaRepository.save(entity);
        return RegisteredClientMapper.toDomain(saved);
    }
    @Override
    public void deleteByClientId(String clientId) {
        jpaRepository.findByClientId(clientId)
                .ifPresent(entity -> jpaRepository.deleteById(entity.getId()));
    }
    @Override
    public List<Client> findAll() {
        return jpaRepository.findAll().stream()
                .map(RegisteredClientMapper::toDomain)
                .collect(Collectors.toList());
    }
} 