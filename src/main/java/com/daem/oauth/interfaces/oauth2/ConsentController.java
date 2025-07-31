package com.daem.oauth.interfaces.oauth2;

import com.daem.oauth.interfaces.oauth2.dto.ConsentInfoDTO;
import com.daem.oauth.interfaces.oauth2.dto.ConsentDecisionDTO;
import com.daem.oauth.application.oauth2.ConsentApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oauth2/consent")
public class ConsentController {
    private final ConsentApplicationService consentApplicationService;

    public ConsentController(ConsentApplicationService consentApplicationService) {
        this.consentApplicationService = consentApplicationService;
    }

    @GetMapping
    public ResponseEntity<ConsentInfoDTO> getConsentInfo(@RequestParam String client_id, @RequestParam String scope, @RequestParam(required = false) String state, @RequestParam(required = false) String redirect_uri) {
        ConsentInfoDTO dto = consentApplicationService.getConsentInfo(client_id, scope, state, redirect_uri);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<?> handleConsent(@RequestBody ConsentDecisionDTO decision, Authentication principal) {
        if (decision.getScopes() == null || decision.getScopes().isEmpty()) {
            return ResponseEntity.badRequest().body("Scopes不能为空");
        }
        boolean result = consentApplicationService.handleConsent(decision, principal.getName());
        if (!result) {
            return ResponseEntity.badRequest().body("授权同意失败");
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deny")
    public ResponseEntity<?> handleDeny(@RequestParam String client_id, Authentication principal) {
        boolean result = consentApplicationService.handleDeny(client_id, principal.getName());
        if (!result) {
            return ResponseEntity.badRequest().body("拒绝授权失败");
        }
        return ResponseEntity.ok().build();
    }
} 