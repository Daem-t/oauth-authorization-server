package com.daem.oauth.application.oauth2;

import com.daem.oauth.interfaces.oauth2.dto.ConsentDecisionDTO;
import com.daem.oauth.interfaces.oauth2.dto.ConsentInfoDTO;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class ConsentApplicationService {
    private final RegisteredClientRepository registeredClientRepository;
    private final OAuth2AuthorizationConsentService consentService;

    public ConsentApplicationService(RegisteredClientRepository registeredClientRepository,
                                     OAuth2AuthorizationConsentService consentService) {
        this.registeredClientRepository = registeredClientRepository;
        this.consentService = consentService;
    }

    public boolean handleConsent(ConsentDecisionDTO decision, String principalName) {
        RegisteredClient client = registeredClientRepository.findByClientId(decision.getClientId());
        if (client == null || decision.getScopes() == null || decision.getScopes().isEmpty()) {
            return false;
        }
        OAuth2AuthorizationConsent.Builder builder = OAuth2AuthorizationConsent.withId(client.getId(), principalName);
        decision.getScopes().forEach(builder::scope);
        OAuth2AuthorizationConsent consent = builder.build();
        consentService.save(consent);
        return true;
    }

    public boolean handleDeny(String clientId, String principalName) {
        RegisteredClient client = registeredClientRepository.findByClientId(clientId);
        if (client == null) {
            return false;
        }
        OAuth2AuthorizationConsent consent = OAuth2AuthorizationConsent.withId(client.getId(), principalName).build();
        consentService.remove(consent);
        return true;
    }

    public ConsentInfoDTO getConsentInfo(String clientId, String scope, String state, String redirectUri) {
        RegisteredClient client = registeredClientRepository.findByClientId(clientId);
        ConsentInfoDTO dto = new ConsentInfoDTO();
        dto.setClientId(clientId);
        dto.setClientName(client != null ? client.getClientName() : "");
        Set<String> scopes = new HashSet<>();
        if (scope != null && !scope.isEmpty()) {
            scopes.addAll(Arrays.asList(scope.split(" ")));
        }
        dto.setScopes(scopes);
        dto.setState(state);
        dto.setRedirectUri(redirectUri);
        return dto;
    }
} 