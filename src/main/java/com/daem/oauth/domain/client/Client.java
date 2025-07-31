package com.daem.oauth.domain.client;

import java.time.LocalDateTime;
import java.util.Set;

public class Client {
    private String clientId;
    private String clientSecret;
    private String clientName;
    private String redirectUri;
    private Set<String> scope;
    private Set<String> grantTypes;
    private String clientType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Client() {}

    public Client(String clientId, String clientSecret, String clientName, String redirectUri, Set<String> scope, Set<String> grantTypes, String clientType, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.clientName = clientName;
        this.redirectUri = redirectUri;
        this.scope = scope;
        this.grantTypes = grantTypes;
        this.clientType = clientType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public String getRedirectUri() { return redirectUri; }
    public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }
    public Set<String> getScope() { return scope; }
    public void setScope(Set<String> scope) { this.scope = scope; }
    public Set<String> getGrantTypes() { return grantTypes; }
    public void setGrantTypes(Set<String> grantTypes) { this.grantTypes = grantTypes; }
    public String getClientType() { return clientType; }
    public void setClientType(String clientType) { this.clientType = clientType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
} 