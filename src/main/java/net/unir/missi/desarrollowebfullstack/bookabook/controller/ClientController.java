package net.unir.missi.desarrollowebfullstack.bookabook.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.unir.missi.desarrollowebfullstack.bookabook.data.model.api.BookResponse;
import net.unir.missi.desarrollowebfullstack.bookabook.data.model.api.DeleteResponse;
import net.unir.missi.desarrollowebfullstack.bookabook.data.model.api.ClientDto;
import net.unir.missi.desarrollowebfullstack.bookabook.data.model.sql.Book;
import net.unir.missi.desarrollowebfullstack.bookabook.data.model.sql.Client;
import net.unir.missi.desarrollowebfullstack.bookabook.service.IClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Clients Controller")
public class ClientController {

    private final IClientService clientService;

    @GetMapping("/clients")
    @Operation(
            operationId = "Obtener clientes",
            description = "Operacion de lectura.",
            summary = "Se devuelve una lista de todos los clientes a partir de una búsqueda por filtro.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Client.class)))
    public ResponseEntity<List<Client>> getFilterClients(
            @Parameter(name = "firstName", description = "Nombre", example = "", required = false)
            @RequestParam(required = false) String firstName,
            @Parameter(name = "lastName", description = "Apellido", example = "", required = false)
            @RequestParam(required = false) String lastName,
            @Parameter(name = "address", description = "Dirección", example = "", required = false)
            @RequestParam(required = false) String address,
            @Parameter(name = "phoneNumber", description = "Teléfono", example = "", required = false)
            @RequestParam(required = false) String phoneNumber,
            @Parameter(name = "email", description = "Email", example = "", required = false)
            @RequestParam(required = false) String email) {

        List<Client> clients = clientService.getFilterClients(firstName, lastName, address, phoneNumber, email);
        return ResponseEntity.ok(Objects.requireNonNullElse(clients, Collections.emptyList()));
    }

    @GetMapping("/clients/{clientId}")
    @Operation(
            operationId = "Obtener el detalle de un cliente.",
            description = "Operacion de lectura",
            summary = "Se devuelve la información de un cliente a partir de su identificador.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Client.class)))
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "No se ha encontrado el cliente con el identificador indicado.")
    public ResponseEntity<Client> getClient(@PathVariable String clientId) {
        Client client = clientService.getClient(clientId);
        return client != null ? ResponseEntity.ok(client) : ResponseEntity.notFound().build();
    }

    @PostMapping("/clients")
    @Operation(
            operationId = "Registrar un nuevo cliente.",
            description = "Operación de escritura.",
            summary = "Se crea un cliente a partir de sus datos.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del cliente a crear.",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientDto.class))))
    @ApiResponse(
            responseCode = "201",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Client.class)))
    @ApiResponse(
            responseCode = "400",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Datos introducidos incorrectos.")
    public ResponseEntity<Client> addBook(@RequestBody ClientDto requestClient) {
        Client clientAdded = clientService.addClient(requestClient);
        return clientAdded != null ? ResponseEntity.status(HttpStatus.CREATED).body(clientAdded) : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/clients/{clientId}")
    @Operation(
            operationId = "Dar de baja un cliente.",
            description = "Operacion de escritura.",
            summary = "Se elimina un cliente a partir de su identificador.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)))
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "No se ha encontrado el cliente con el identificador indicado.")
    public ResponseEntity<DeleteResponse> deleteClient(@PathVariable String clientId) {
        Boolean clientDeleted = clientService.deleteClient(clientId);
        DeleteResponse deleteResponse = DeleteResponse.builder().message("Book deleted").build();
        return Boolean.TRUE.equals(clientDeleted) ? ResponseEntity.ok(deleteResponse) : ResponseEntity.notFound().build();
    }

    @PutMapping("/client/{clientId}")
    @Operation(
            operationId = "Modificar totalmente un cliente.",
            description = "Operación de escritura.",
            summary = "Se modifica totalmente un cliente.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del cliente a actualizar.",
                    required = true,
                    content = @Content(mediaType = "application/merge-patch+json", schema = @Schema(implementation = ClientDto.class))))
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Client.class)))
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Cliente no encontrado.")
    public ResponseEntity<Client> updateClient(@PathVariable String clientId, @RequestBody ClientDto client) {
        Client updatedClient = clientService.updateClient(clientId, client);
        return updatedClient != null ? ResponseEntity.ok(updatedClient) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/clients/{clientId}")
    @Operation(
            operationId = "Modificar parcialmente un cliente.",
            description = "RFC 7386. Operación de escritura.",
            summary = "RFC 7386. Se modifica parcialmente un cliente.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del cliente a modificar.",
                    required = true,
                    content = @Content(mediaType = "application/merge-patch+json", schema = @Schema(implementation = ClientDto.class))))
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Client.class)))
    @ApiResponse(
            responseCode = "400",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Datos de cliente introducidos incorrectos.")
    public ResponseEntity<Client> updateClientAttribute(@PathVariable String clientId, @RequestBody String requestClientAttribute) {
        Client clientModified = clientService.updateClientAttribute(clientId, requestClientAttribute);
        return clientModified != null ? ResponseEntity.ok(clientModified) : ResponseEntity.badRequest().build();
    }

}