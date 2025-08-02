package com.daem.application.client.dto;

import java.util.Set;

public record ClientDto(
        String clientId,
        String clientSecret,
        Set<String> clientAuthenticationMethods,
        Set<String> authorizationGrantTypes,
        Set<String> redirectUris,
        Set<String> scopes
) {
}
