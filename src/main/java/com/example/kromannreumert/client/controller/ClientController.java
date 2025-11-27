package com.example.kromannreumert.client.controller;

import com.example.kromannreumert.client.DTO.ClientRequestDTO;
import com.example.kromannreumert.client.DTO.UpdateClientIdPrefixDTO;
import com.example.kromannreumert.client.DTO.UpdateClientNameDTO;
import com.example.kromannreumert.client.DTO.UpdateClientUserList;
import com.example.kromannreumert.client.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/client/")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }


    @GetMapping("")
    public ResponseEntity<?> getAllClients() {
        return new ResponseEntity<>(clientService.getAllClients(), HttpStatus.OK);
    }

    @GetMapping("/{idprefix}")
    public ResponseEntity<?> getClientById(@PathVariable("idprefix") Long idprefix) {
        try {
            return new ResponseEntity<>(clientService.getClientByIdPrefix(idprefix), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Client not found by id: " + idprefix, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getclientbyname/{clientName}")
    public ResponseEntity<?> getClientByClientName(@PathVariable String clientName) {
        try {
            return new ResponseEntity<>(clientService.getClientByName(clientName), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Client not found by client name: " + clientName, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addClient(@RequestBody ClientRequestDTO clientRequestDTO) {
        try {
            return new ResponseEntity<>(clientService.addClient(clientRequestDTO), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Failed to create the client: " + clientRequestDTO.clientName(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/id")
    public ResponseEntity<?> updateClientIdPrefix(@RequestBody UpdateClientIdPrefixDTO clientIdPrefixDTO) {
        try {
            return new ResponseEntity<>(clientService.updateClientIdPrefix(clientIdPrefixDTO), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Failed to update the client: " + clientIdPrefixDTO.clientName(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/name")
    public ResponseEntity<?> updateClientName(@RequestBody UpdateClientNameDTO clientNameDTO) {
        try {
            return new ResponseEntity<>(clientService.updateClientName(clientNameDTO), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Failed to update the client: " + clientNameDTO.oldName(), HttpStatus.BAD_REQUEST);
        }
    }

   @PutMapping("/update/users")
   public ResponseEntity<?> updateClientUsers(@RequestBody UpdateClientUserList clientUserList) {
       System.out.println(clientUserList);
       try {
           return new ResponseEntity<>(clientService.updateClientUserList(clientUserList), HttpStatus.OK);
       } catch (RuntimeException e) {
           return new ResponseEntity<>("Failed to update the client: " + clientUserList, HttpStatus.BAD_REQUEST);
       }
   }

   @GetMapping("/user/{idprefix}")
    public ResponseEntity<?> getUserByClient(@PathVariable Long idprefix) {
        try {
            return new ResponseEntity<>(clientService.getUserFromClient(idprefix), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Failed to retrieve the client user list", HttpStatus.BAD_REQUEST);
        }
   }
}
