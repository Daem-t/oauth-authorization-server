package com.daem.infrastructure.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RegisteredClientRepository registeredClientRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RegisteredClientRepository registeredClientRepository, PasswordEncoder passwordEncoder) {
        this.registeredClientRepository = registeredClientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String clientId = "admin-client";
        if (registeredClientRepository.findByClientId(clientId) == null) {
            RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId(clientId)
                    .clientSecret(passwordEncoder.encode("secret"))
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .redirectUri("http://127.0.0.1:9000/login/oauth2/code/admin-client")
                    .scope(OidcScopes.OPENID)
                    .scope(OidcScopes.PROFILE)
                    .scope("server.admin")
                    .build();

            registeredClientRepository.save(registeredClient);
        }
    }
}
