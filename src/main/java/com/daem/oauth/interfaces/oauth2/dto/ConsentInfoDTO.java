package com.daem.oauth.interfaces.oauth2.dto;

import java.util.Set;

public class ConsentInfoDTO {
    private String clientId;
    private String clientName;
    private Set<String> scopes;
    private String state;
    private String redirectUri;

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public Set<String> getScopes() { return scopes; }
    public void setScopes(Set<String> scopes) { this.scopes = scopes; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getRedirectUri() { return redirectUri; }
    public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }
} 