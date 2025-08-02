package com.daem.interfaces.rest;

import com.daem.application.ClientManagementService;
import com.daem.application.dto.ClientDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientManagementService clientManagementService;

    public ClientController(ClientManagementService clientManagementService) {
        this.clientManagementService = clientManagementService;
    }

    @GetMapping
    public List<ClientDto> getAll() {
        return clientManagementService.findAll();
    }

    @PutMapping("/{clientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable String clientId, @RequestBody ClientDto clientDto) {
        // Ensure the client ID in the path matches the one in the body
        if (!clientId.equals(clientDto.clientId())) {
            throw new IllegalArgumentException("Client ID in path does not match client ID in body");
        }
        clientManagementService.update(clientDto);
    }

    @DeleteMapping("/{clientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String clientId) {
        clientManagementService.delete(clientId);
    }
}
