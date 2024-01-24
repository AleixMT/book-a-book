package net.unir.missi.desarrollowebfullstack.bookabook.controller;

import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.unir.missi.desarrollowebfullstack.bookabook.model.sql.Author;
import net.unir.missi.desarrollowebfullstack.bookabook.model.sql.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import net.unir.missi.desarrollowebfullstack.bookabook.model.api.AuthorRequest;
import net.unir.missi.desarrollowebfullstack.bookabook.service.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authors Controller", description = "Microservicio encargado de exponer operaciones CRUD sobre autores alojados en una base de datos.")
public class AuthorController {

    private final AuthorService service;

    private final ObjectMapper objectMapper;

    @GetMapping("/authors")
    @Operation(
            operationId = "Obtener autores",
            description = "Operacion de lectura y filtrado",
            summary = "Se devuelve una lista de todos los autores almacenados en la base de datos.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Author.class)))
    public ResponseEntity<List<AuthorRequest>> getAuthors(
            @Parameter(name = "firstName", example = "Juan")
            @RequestParam(required = false) String firstName,
            @Parameter(name = "lastName", example = "Garcia")
            @RequestParam(required = false) String lastName,
            @Parameter(name = "birthDate", example = "2024-01-20")
            @RequestParam(required = false) LocalDate birthDate,
            @Parameter(name = "nationality", example = "spanish")
            @RequestParam(required = false) String nationality,
            @Parameter(name = "email", example = "example@example.com")
            @RequestParam(required = false) String email,
            @Parameter(name = "webSite", example = "bokkabook.com")
            @RequestParam(required = false) String webSite,
            @Parameter(name = "biography")
            @RequestParam(required = false) String biography,
            @Parameter(name = "bookId")
            @RequestParam(required = false) Long bookId)
    {

        try {
            List<AuthorRequest> request = service.getAllAuthors(firstName,lastName,birthDate,nationality,email,webSite,biography,bookId);
            return ResponseEntity.ok(Objects.requireNonNullElse(request, Collections.emptyList()));
        } catch (Exception e) {
            log.error("Error getting authors list {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/authors/{idAuthor}")
    @Operation(
            operationId = "Obtener autores por su id",
            description = "Operacion de lectura y filtrado",
            summary = "Se devuelve un autor almacenados en la base de datos con un id seleccionado.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Author.class)))
    public ResponseEntity<AuthorRequest> getAuthorById(@PathVariable String idAuthor)
    {
        try {
            AuthorRequest author = service.getAuthorById(idAuthor);
            log.error("Error AUTHOR {}", author);

            if(author!=null)
                return ResponseEntity.ok(author);
            else
                return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("Error getting author {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/authors")
    @Operation(
            operationId = "Insercción de un autor.",
            description = "Operacion de escritura.",
            summary = "Se devuelve el autor insertado.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Author.class)))
    public ResponseEntity<AuthorRequest> addAuthor(@RequestBody AuthorRequest authorRequested)
    {
        try {
            if(authorRequested!=null) {
                AuthorRequest newAuthor = service.createAuthor(authorRequested);
                return ResponseEntity.status(HttpStatus.CREATED).body(newAuthor);
            }
            else
                return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            log.error("Error adding author {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/authors/{idAuthor}")
    @Operation(
            operationId = "Modificación total de un autor.",
            description = "Operacion de escritura.",
            summary = "Se devuelve el autor modificado.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Author.class)))
    public ResponseEntity<AuthorRequest> modifyAllAuthorData(@PathVariable String idAuthor, @RequestBody AuthorRequest authorData) {
        try {
            AuthorRequest tempAuthor = service.getAuthorById(idAuthor);
            //Si el autor del id que recibimos es nulo, no hacemos la modificacion
            if(tempAuthor!=null){
                    return ResponseEntity.ok(service.modifyAllAuthorData(tempAuthor, authorData));
            } else
                return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("Error modifying author {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/authors/{idAuthor}")
    @Operation(
            operationId = "Modificación parcial de un autor.",
            description = "Operacion de escritura.",
            summary = "Se devuelve el autor modificado.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Author.class)))
    public ResponseEntity<AuthorRequest> modifyAuthorData(@PathVariable String idAuthor, @RequestBody String authorData) {
        try {
            JsonMergePatch jsonMergePatch = JsonMergePatch.fromJson(objectMapper.readTree(authorData));
            JsonNode target = jsonMergePatch.apply(objectMapper.readTree(objectMapper.writeValueAsString(authorData)));
            AuthorRequest authorPatched = objectMapper.treeToValue(target, AuthorRequest.class);

            AuthorRequest tempAuthor = service.getAuthorById(idAuthor);

            if(tempAuthor!=null){
                return ResponseEntity.ok(service.modifyAuthorData(tempAuthor, authorPatched));
            } else
                return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("Error modifying author {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/authors/{idAuthor}")
    @Operation(
            operationId = "Borrado de un autor.",
            description = "Operacion de escritura.",
            summary = "Se devuelve el autor eliminado.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Author.class)))
    public ResponseEntity<AuthorRequest> deleteAuthor(@PathVariable String idAuthor) {
        try {
            AuthorRequest prev = service.getAuthorById(idAuthor);
            if(prev!=null){
                return ResponseEntity.ok(service.deleteAuthor(prev));
            } else
                return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("Error deleting author {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
