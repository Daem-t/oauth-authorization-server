package com.daem.infrastructure.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.MessageSource; // Added import
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;

import java.util.Locale; // Added import
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RegisteredClientRepository registeredClientRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource; // Added MessageSource

    public DataInitializer(RegisteredClientRepository registeredClientRepository, PasswordEncoder passwordEncoder, MessageSource messageSource) { // Added MessageSource to constructor
        this.registeredClientRepository = registeredClientRepository;
        this.passwordEncoder = passwordEncoder;
        this.messageSource = messageSource; // Injected MessageSource
    }

    @Override
    public void run(String... args) {
        String clientId = "admin-client";
        if (registeredClientRepository.findByClientId(clientId) == null) {
            RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId(clientId)
                    .clientSecret(passwordEncoder.encode("secret"))
                    .clientName(messageSource.getMessage("client.name", null, Locale.getDefault())) // Used MessageSource for client name
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
