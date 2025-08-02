package com.daem.application;

import com.daem.application.dto.ClientDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository; // Correct import
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClientManagementService {

    private final RegisteredClientRepository registeredClientRepository; // Correct type
    private final PasswordEncoder passwordEncoder;

    public ClientManagementService(RegisteredClientRepository registeredClientRepository, PasswordEncoder passwordEncoder) {
        this.registeredClientRepository = registeredClientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void create(ClientDto clientDto) {
        RegisteredClient registeredClient = toRegisteredClient(clientDto);
        registeredClientRepository.save(registeredClient);
    }

    @Transactional(readOnly = true)
    public List<ClientDto> findAll() {
        // This call will now correctly resolve to RegisteredClientRepositoryAdapter's findAll()
        return ((com.daem.infrastructure.repository.RegisteredClientRepositoryAdapter) registeredClientRepository).findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void update(ClientDto clientDto) {
        RegisteredClient existingClient = registeredClientRepository.findByClientId(clientDto.clientId());
        if (existingClient == null) {
            // Or throw an exception
            return;
        }
        // Create a new client with the updated details
        RegisteredClient updatedClient = toRegisteredClient(clientDto, existingClient.getId());
        registeredClientRepository.save(updatedClient);
    }

    @Transactional
    public void delete(String clientId) {
        // This call will now correctly resolve to RegisteredClientRepositoryAdapter's deleteByClientId()
        ((com.daem.infrastructure.repository.RegisteredClientRepositoryAdapter) registeredClientRepository).deleteByClientId(clientId);
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
