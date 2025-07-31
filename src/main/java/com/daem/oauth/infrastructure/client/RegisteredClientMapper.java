package com.daem.oauth.infrastructure.client;

import com.daem.oauth.domain.client.Client;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class RegisteredClientMapper {
    // Entity -> Domain
    public static Client toDomain(RegisteredClientEntity entity) {
        if (entity == null) return null;
        return new Client(
                entity.getClientId(),
                entity.getClientSecret(),
                entity.getClientName(),
                entity.getRedirectUris(),
                splitToSet(entity.getScopes()),
                splitToSet(entity.getAuthorizationGrantTypes()),
                entity.getClientAuthenticationMethods(),
                entity.getClientIdIssuedAt(),
                entity.getClientSecretExpiresAt()
        );
    }

    // Domain -> Entity
    public static RegisteredClientEntity toEntity(Client client) {
        if (client == null) return null;
        RegisteredClientEntity entity = new RegisteredClientEntity();
        entity.setId(client.getClientId()); // 这里假设id与clientId一致，可根据实际调整
        entity.setClientId(client.getClientId());
        entity.setClientSecret(client.getClientSecret());
        entity.setClientName(client.getClientName());
        entity.setRedirectUris(client.getRedirectUri());
        entity.setScopes(joinSet(client.getScope()));
        entity.setAuthorizationGrantTypes(joinSet(client.getGrantTypes()));
        entity.setClientAuthenticationMethods(client.getClientType());
        entity.setClientIdIssuedAt(client.getCreatedAt());
        entity.setClientSecretExpiresAt(client.getUpdatedAt());
        // 其它字段可根据需要补充
        entity.setClientSettings("{}");
        entity.setTokenSettings("{}");
        return entity;
    }

    private static Set<String> splitToSet(String str) {
        if (str == null || str.isEmpty()) return new HashSet<>();
        return Arrays.stream(str.split(",")).map(String::trim).collect(Collectors.toSet());
    }
    private static String joinSet(Set<String> set) {
        if (set == null || set.isEmpty()) return "";
        return String.join(",", set);
    }
} 