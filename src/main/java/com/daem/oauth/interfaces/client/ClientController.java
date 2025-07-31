package com.daem.oauth.interfaces.client;

import com.daem.oauth.application.client.ClientApplicationService;
import com.daem.oauth.domain.client.Client;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    private final ClientApplicationService clientService;
    public ClientController(ClientApplicationService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<Client> register(@RequestBody Client client) {
        Client saved = clientService.register(client);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<Client> getByClientId(@PathVariable String clientId) {
        Optional<Client> client = clientService.findByClientId(clientId);
        return client.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> deleteByClientId(@PathVariable String clientId) {
        clientService.deleteByClientId(clientId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Client>> listAll() {
        return ResponseEntity.ok(clientService.findAll());
    }
} 