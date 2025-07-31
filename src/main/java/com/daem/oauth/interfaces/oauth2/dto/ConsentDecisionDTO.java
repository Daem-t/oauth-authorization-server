package com.daem.oauth.interfaces.oauth2.dto;

import java.util.Set;

public class ConsentDecisionDTO {
    private String clientId;
    private Set<String> scopes;
    private String state;

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public Set<String> getScopes() { return scopes; }
    public void setScopes(Set<String> scopes) { this.scopes = scopes; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
} 