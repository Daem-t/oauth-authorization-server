package com.daem.interfaces.rest.client;

import com.daem.application.client.ClientManagementService;
import com.daem.application.client.dto.ClientDto;
import com.daem.application.exception.ClientIdMismatchException;
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
            throw new ClientIdMismatchException("Client ID in path does not match client ID in body"); // Throws custom exception
        }
        clientManagementService.update(clientDto);
    }

    @DeleteMapping("/{clientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String clientId) {
        clientManagementService.delete(clientId);
    }
}
