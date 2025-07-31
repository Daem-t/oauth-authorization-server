package com.daem.oauth.application.client;

import com.daem.oauth.domain.client.Client;
import com.daem.oauth.domain.client.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientApplicationService {
    private final ClientRepository clientRepository;
    public ClientApplicationService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }
    public Client register(Client client) {
        return clientRepository.save(client);
    }
    public Optional<Client> findByClientId(String clientId) {
        return clientRepository.findByClientId(clientId);
    }
    public void deleteByClientId(String clientId) {
        clientRepository.deleteByClientId(clientId);
    }
    public List<Client> findAll() {
        return clientRepository.findAll();
    }
} 