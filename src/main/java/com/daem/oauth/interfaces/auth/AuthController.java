package com.daem.oauth.interfaces.auth;

import com.daem.oauth.application.security.AuthenticationService;
import com.daem.oauth.application.user.UserApplicationService;
import com.daem.oauth.interfaces.auth.dto.LoginRequest;
import com.daem.oauth.interfaces.auth.dto.LoginResponse;
import com.daem.oauth.interfaces.auth.dto.RegisterRequest;
import com.daem.oauth.interfaces.common.constants.MessageConstants;
import com.daem.oauth.interfaces.common.dto.MessageResponse;
import com.daem.oauth.interfaces.common.service.MessageService;
import com.daem.oauth.interfaces.common.util.LocaleUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * Handles user registration, login, and other authentication-related HTTP requests.
 * This controller is designed to be "thin", delegating all business logic to application services.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationService authenticationService;
    private final UserApplicationService userApplicationService;
    private final MessageService messageService;

    @Value("${app.auth.default-role:USER}")
    private String defaultRole;

    public AuthController(AuthenticationService authenticationService,
                          UserApplicationService userApplicationService,
                          MessageService messageService) {
        this.authenticationService = authenticationService;
        this.userApplicationService = userApplicationService;
        this.messageService = messageService;
    }

    /**
     * User Login
     * @param request Login credentials
     * @return A JWT token upon successful authentication.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Login attempt for user: {}", request.getUsername());
        LoginResponse response = authenticationService.login(request);
        logger.info("User login successful: {}", request.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * User Registration
     * @param request     Registration details
     * @param httpRequest HTTP request object to capture IP address
     * @return A success message.
     */
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        logger.info("Registration attempt for user: {}", request.getUsername());
        
        // Set locale for response messages
        LocaleContextHolder.setLocale(LocaleUtil.detectLocaleFromRequest(httpRequest));

        // Delegate registration logic to the application service
        userApplicationService.registerUser(request, httpRequest.getRemoteAddr(), defaultRole);

        String message = messageService.getMessage(MessageConstants.Auth.REGISTER_SUCCESS);
        logger.info("User registration successful: {}", request.getUsername());
        return ResponseEntity.ok(new MessageResponse(message));
    }

    /**
     * Account Activation
     * @param token Activation token
     * @return A success message.
     */
    @GetMapping("/activate")
    public ResponseEntity<MessageResponse> activate(@RequestParam("token") String token) {
        logger.info("Account activation attempt with token: {}", token);
        userApplicationService.activateUser(token);
        String message = messageService.getMessage(MessageConstants.Auth.ACTIVATE_SUCCESS);
        logger.info("Account activation successful for token: {}", token);
        return ResponseEntity.ok(new MessageResponse(message));
    }

    // Other endpoints like /refresh and /logout can be added here,
    // following the same principle of delegating logic to application services.
}