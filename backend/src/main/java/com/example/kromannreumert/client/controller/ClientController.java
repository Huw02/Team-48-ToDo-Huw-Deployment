package com.example.kromannreumert.client.controller;

import com.example.kromannreumert.client.DTO.ClientRequestDTO;
import com.example.kromannreumert.client.DTO.UpdateClientIdPrefixDTO;
import com.example.kromannreumert.client.DTO.UpdateClientNameDTO;
import com.example.kromannreumert.client.DTO.UpdateClientUserList;
import com.example.kromannreumert.client.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/client/")
@CrossOrigin(origins = "*")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }


    @GetMapping("")
    public ResponseEntity<?> getAllClients(Principal principal) {
        return new ResponseEntity<>(clientService.getAllClients(principal.getName()), HttpStatus.OK);
    }

    @GetMapping("/{idprefix}")
    public ResponseEntity<?> getClientById(@PathVariable("idprefix") Long idprefix, Principal principal) {
        return new ResponseEntity<>(clientService.getClientByIdPrefix(idprefix, principal.getName()), HttpStatus.OK);
    }

    // This endpoints needs to be removed or changed as there can contain spaces in a client name.
    @GetMapping("/getclientbyname/{clientName}")
    public ResponseEntity<?> getClientByClientName(@PathVariable String clientName, Principal principal) {
            return new ResponseEntity<>(clientService.getClientByName(clientName, principal.getName()), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addClient(@RequestBody ClientRequestDTO clientRequestDTO, Principal principal) {
        return new ResponseEntity<>(clientService.addClient(clientRequestDTO, principal.getName()), HttpStatus.CREATED);
    }

    @PatchMapping("/update/id")
    public ResponseEntity<?> updateClientIdPrefix(@RequestBody UpdateClientIdPrefixDTO clientIdPrefixDTO, Principal principal) {
            return new ResponseEntity<>(clientService.updateClientIdPrefix(clientIdPrefixDTO, principal.getName()), HttpStatus.OK);
    }

    @PatchMapping("/update/name")
    public ResponseEntity<?> updateClientName(@RequestBody UpdateClientNameDTO clientNameDTO, Principal principal) {
            return new ResponseEntity<>(clientService.updateClientName(clientNameDTO, principal.getName()), HttpStatus.OK);
    }


   @PutMapping("/update/users")
   public ResponseEntity<?> updateClientUsers(@RequestBody UpdateClientUserList clientUserList, Principal principal) {
           return new ResponseEntity<>(clientService.updateClientUserList(clientUserList, principal.getName()), HttpStatus.OK);
   }

   @GetMapping("/user/{idprefix}")
    public ResponseEntity<?> getUserByClient(@PathVariable Long idprefix, Principal principal) {
            return new ResponseEntity<>(clientService.getUserFromClient(idprefix, principal.getName()), HttpStatus.OK);
   }

   @GetMapping("/size")
    public ResponseEntity<?> getClientSize(Principal principal) {
           return new ResponseEntity<>(clientService.getClientSize(principal.getName()), HttpStatus.OK);
       }

   @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable Long id, Principal principal) {
        return new ResponseEntity<>(clientService.deleteClient(id, principal.getName()), HttpStatus.OK);
   }
}
