package com.daem.oauth.domain.client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    Optional<Client> findByClientId(String clientId);
    Client save(Client client);
    void deleteByClientId(String clientId);
    List<Client> findAll();
} 