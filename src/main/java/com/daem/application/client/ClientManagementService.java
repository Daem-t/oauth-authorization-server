package com.daem.application.client;

import com.daem.application.client.dto.ClientDto;
import com.daem.domain.client.ClientRepository;
import com.daem.application.exception.ClientNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClientManagementService {

    private final ClientRepository clientRepository; // Changed type
    private final PasswordEncoder passwordEncoder;

    public ClientManagementService(ClientRepository clientRepository, PasswordEncoder passwordEncoder) { // Changed constructor
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void create(ClientDto clientDto) {
        RegisteredClient registeredClient = toRegisteredClient(clientDto);
        clientRepository.save(registeredClient);
    }

    @Transactional(readOnly = true)
    public List<ClientDto> findAll() {
        return clientRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void update(ClientDto clientDto) {
        RegisteredClient existingClient = clientRepository.findByClientId(clientDto.clientId());
        if (existingClient == null) {
            throw new ClientNotFoundException("Client with client ID " + clientDto.clientId() + " not found.");
        }
        // Create a new client with the updated details
        RegisteredClient updatedClient = toRegisteredClient(clientDto, existingClient.getId());
        clientRepository.save(updatedClient);
    }

    @Transactional
    public void delete(String clientId) {
        RegisteredClient existingClient = clientRepository.findByClientId(clientId);
        if (existingClient == null) {
            throw new com.daem.application.exception.ClientNotFoundException("Client with client ID " + clientId + " not found.");
        }
        clientRepository.deleteByClientId(clientId);
    }

    private RegisteredClient toRegisteredClient(ClientDto clientDto, String id) {
        RegisteredClient.Builder builder = RegisteredClient.withId(id);
        builder.clientId(clientDto.clientId())
                .clientSecret(passwordEncoder.encode(clientDto.clientSecret()))
                .clientAuthenticationMethods(methods -> methods.addAll(clientDto.clientAuthenticationMethods().stream()
                        .map(ClientAuthenticationMethod::new)
                        .collect(Collectors.toSet())))
                .authorizationGrantTypes(grantTypes -> grantTypes.addAll(clientDto.authorizationGrantTypes().stream()
                        .map(org.springframework.security.oauth2.core.AuthorizationGrantType::new)
                        .collect(Collectors.toSet())))
                .redirectUris(uris -> uris.addAll(clientDto.redirectUris()))
                .scopes(scopes -> scopes.addAll(clientDto.scopes()));
        return builder.build();
    }

    private ClientDto toDto(RegisteredClient registeredClient) {
        return new ClientDto(
                registeredClient.getClientId(),
                null, // Never expose the secret
                registeredClient.getClientAuthenticationMethods().stream().map(ClientAuthenticationMethod::getValue).collect(Collectors.toSet()),
                registeredClient.getAuthorizationGrantTypes().stream().map(org.springframework.security.oauth2.core.AuthorizationGrantType::getValue).collect(Collectors.toSet()),
                registeredClient.getRedirectUris(),
                registeredClient.getScopes()
        );
    }

    private RegisteredClient toRegisteredClient(ClientDto clientDto) {
        return toRegisteredClient(clientDto, UUID.randomUUID().toString());
    }
}
