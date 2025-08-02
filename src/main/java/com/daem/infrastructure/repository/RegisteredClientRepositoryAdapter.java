package com.daem.infrastructure.repository;

import com.daem.domain.RegisteredClientEntity;
import com.daem.domain.RegisteredClientJpaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RegisteredClientRepositoryAdapter implements RegisteredClientRepository {

    private final RegisteredClientJpaRepository registeredClientJpaRepository;
    private final ObjectMapper objectMapper; // Changed to be injected

    public RegisteredClientRepositoryAdapter(RegisteredClientJpaRepository registeredClientJpaRepository, ObjectMapper objectMapper) { // Added ObjectMapper to constructor
        this.registeredClientJpaRepository = registeredClientJpaRepository;
        this.objectMapper = objectMapper; // Injected ObjectMapper
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        RegisteredClientEntity entity = toEntity(registeredClient);
        registeredClientJpaRepository.save(entity);
    }

    @Override
    public RegisteredClient findById(String id) {
        return registeredClientJpaRepository.findById(id).map(this::toObject).orElse(null);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return registeredClientJpaRepository.findByClientId(clientId).map(this::toObject).orElse(null);
    }

    public List<RegisteredClient> findAll() {
        return registeredClientJpaRepository.findAll().stream().map(this::toObject).collect(Collectors.toList());
    }

    public void deleteByClientId(String clientId) {
        registeredClientJpaRepository.deleteByClientId(clientId);
    }

    private RegisteredClientEntity toEntity(RegisteredClient registeredClient) {
        RegisteredClientEntity entity = new RegisteredClientEntity();
        entity.setId(registeredClient.getId());
        entity.setClientId(registeredClient.getClientId());
        entity.setClientIdIssuedAt(registeredClient.getClientIdIssuedAt() != null ? registeredClient.getClientIdIssuedAt() : Instant.now());
        entity.setClientSecret(registeredClient.getClientSecret());
        entity.setClientSecretExpiresAt(registeredClient.getClientSecretExpiresAt());
        entity.setClientName(registeredClient.getClientName());
        entity.setClientAuthenticationMethods(writeSet(registeredClient.getClientAuthenticationMethods(), ClientAuthenticationMethod::getValue));
        entity.setAuthorizationGrantTypes(writeSet(registeredClient.getAuthorizationGrantTypes(), AuthorizationGrantType::getValue));
        entity.setRedirectUris(writeSet(registeredClient.getRedirectUris(), String::new));
        entity.setPostLogoutRedirectUris(writeSet(registeredClient.getPostLogoutRedirectUris(), String::new)); // Added postLogoutRedirectUris
        entity.setScopes(writeSet(registeredClient.getScopes(), String::new));
        entity.setClientSettings(writeMap(registeredClient.getClientSettings().getSettings()));
        entity.setTokenSettings(writeMap(registeredClient.getTokenSettings().getSettings()));
        return entity;
    }

    private RegisteredClient toObject(RegisteredClientEntity entity) {
        Set<ClientAuthenticationMethod> clientAuthenticationMethods = parseSet(entity.getClientAuthenticationMethods(), ClientAuthenticationMethod::new);
        Set<AuthorizationGrantType> authorizationGrantTypes = parseSet(entity.getAuthorizationGrantTypes(), AuthorizationGrantType::new);
        Set<String> redirectUris = parseSet(entity.getRedirectUris(), String::new);
        Set<String> postLogoutRedirectUris = parseSet(entity.getPostLogoutRedirectUris(), String::new); // Added postLogoutRedirectUris
        Set<String> scopesFromEntity = parseSet(entity.getScopes(), String::new); // Renamed to avoid conflict
        Map<String, Object> clientSettingsMap = parseMap(entity.getClientSettings());
        Map<String, Object> tokenSettingsMap = parseMap(entity.getTokenSettings());

        RegisteredClient.Builder builder = RegisteredClient.withId(entity.getId());
        builder.clientId(entity.getClientId())
                .clientIdIssuedAt(entity.getClientIdIssuedAt())
                .clientSecret(entity.getClientSecret())
                .clientSecretExpiresAt(entity.getClientSecretExpiresAt())
                .clientName(entity.getClientName())
                .clientAuthenticationMethods(methods -> methods.addAll(clientAuthenticationMethods))
                .authorizationGrantTypes(grantTypes -> grantTypes.addAll(authorizationGrantTypes))
                .redirectUris(uris -> uris.addAll(redirectUris))
                .postLogoutRedirectUris(uris -> uris.addAll(postLogoutRedirectUris))
                .scopes(scopes -> scopes.addAll(scopesFromEntity))
                .clientSettings(ClientSettings.withSettings(clientSettingsMap).build())
                .tokenSettings(TokenSettings.withSettings(tokenSettingsMap).build());
        return builder.build();
    }

    private <T> String writeSet(Set<T> set, Function<T, String> mapper) {
        if (set == null || set.isEmpty()) {
            return null;
        }
        try {
            java.util.List<String> mappedList = set.stream().map(mapper).collect(java.util.stream.Collectors.toList());
            return objectMapper.writeValueAsString(mappedList);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String writeMap(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> Set<T> parseSet(String json, Function<String, T> mapper) {
        if (!StringUtils.hasText(json)) {
            return Collections.emptySet();
        }
        try {
            // Try to parse as a List (JSON array)
            List<String> list = objectMapper.readValue(json, new TypeReference<List<String>>() {});
            return list.stream().map(mapper).collect(Collectors.toSet());
        } catch (JsonProcessingException e) {
            // If parsing as List fails, assume it's a single string and wrap it in a Set
            Set<T> singleElementSet = new HashSet<>();
            singleElementSet.add(mapper.apply(json));
            return singleElementSet;
        }
    }

    private Map<String, Object> parseMap(String json) {
        if (!StringUtils.hasText(json)) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            // If parsing as Map fails, return an empty map
            return Collections.emptyMap();
        }
    }
}
