package com.daem.oauth.infrastructure.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class SecurityUser implements UserDetails {
    private final UserEntity user;
    public SecurityUser(UserEntity user) {
        this.user = user;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new java.util.HashSet<>();
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role.getName()));
                if (role.getPermissions() != null) {
                    role.getPermissions().forEach(permission ->
                        authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(permission.getName()))
                    );
                }
            });
        }
        return authorities;
    }
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    @Override
    public String getUsername() {
        return user.getUsername();
    }
    @Override
    public boolean isAccountNonExpired() {
        return user.getAccountNonExpired() != null ? user.getAccountNonExpired() : true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return user.getAccountNonLocked() != null ? user.getAccountNonLocked() : true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return user.getCredentialsNonExpired() != null ? user.getCredentialsNonExpired() : true;
    }
    @Override
    public boolean isEnabled() {
        return user.getEnabled() != null ? user.getEnabled() : true;
    }
    public UserEntity getUserEntity() {
        return user;
    }
} 