package com.daem.oauth.application.security;

import com.daem.oauth.domain.user.Role;
import com.daem.oauth.domain.user.User;
import com.daem.oauth.infrastructure.user.SecurityUser;
import com.daem.oauth.infrastructure.user.UserMapper;
import com.daem.oauth.interfaces.auth.dto.LoginRequest;
import com.daem.oauth.interfaces.auth.dto.LoginResponse;
import com.daem.oauth.interfaces.common.constants.MessageConstants;
import com.daem.oauth.interfaces.common.service.MessageService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Application service responsible for handling user authentication.
 */
@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final MessageService messageService;

    public AuthenticationService(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService, MessageService messageService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.messageService = messageService;
    }

    /**
     * Authenticates a user and returns a JWT upon successful authentication.
     * This method implements the dual-token (access + refresh) strategy.
     *
     * @param loginRequest The login credentials.
     * @return A LoginResponse containing the access token, refresh token, and user info.
     */
    public LoginResponse login(LoginRequest loginRequest) {
        // 1. Perform authentication using Spring Security's AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // 2. Set the authenticated user in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Get the underlying User domain object from the principal
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        User user = UserMapper.toDomain(securityUser.getUserEntity());

        // 4. Generate both access and refresh tokens
        String accessToken = jwtTokenService.generateAccessToken(user);
        String refreshToken = jwtTokenService.generateRefreshToken(user);

        // 5. Prepare user info for the response
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getStatus(),
                user.getRoles().stream().map(role -> role.getName()).toArray(String[]::new),
                null // Locale can be set in the controller if needed
        );

        // 6. Build and return the complete LoginResponse
        return new LoginResponse(
                accessToken,
                refreshToken,
                jwtTokenService.getAccessTokenExpiration(),
                jwtTokenService.getRefreshTokenExpiration(),
                messageService.getMessage(MessageConstants.Auth.LOGIN_SUCCESS),
                userInfo
        );
    }
}