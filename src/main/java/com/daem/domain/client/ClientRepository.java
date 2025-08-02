package com.daem.domain.client;

import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.List;

public interface ClientRepository extends RegisteredClientRepository {
    List<RegisteredClient> findAll();
    void deleteByClientId(String clientId);
}
