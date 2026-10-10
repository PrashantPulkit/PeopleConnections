package com.connection.api.controller;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import  org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.connection.api.dto.ConnectionRequest;
import com.connection.api.dto.PathResponse;
import com.connection.api.dto.PersonRequest;
import com.connection.api.entity.Person;
import com.connection.api.service.PeopleService;
@RestController
public class PeopleController {
private final PeopleService service;

    public PeopleController(PeopleService service) {
        this.service = service;
    }

    @PostMapping("/people")
    public ResponseEntity<?> createPerson(@RequestBody(required = false) PersonRequest request) {

        if (request == null ||
                request.name() == null ||
                request.name().isBlank()) {

            return ResponseEntity.badRequest().body(Map.of("error", "name is required"));
        }

        Person person =
                service.createPerson(request.name());

        return ResponseEntity
                .status(201)
                .body(person);
    }

    @PostMapping("/knows")
    public ResponseEntity<?> createConnection(
            @RequestBody(required = false) ConnectionRequest request) {

        if (request == null ||
                request.a() == null ||
                request.b() == null) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error", "a and b are required"));
        }

        try {

            service.createConnection(
                    request.a(),
                    request.b()
            );

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (NoSuchElementException e) {

            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/people/{id}/contacts")
    public ResponseEntity<?> getContacts(
            @PathVariable String id) {

        int personId;

        try {
            personId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "invalid person id"));
        }

        try {

            List<Person> contacts =service.getContacts(personId);

            return ResponseEntity.ok(contacts);

        } catch (NoSuchElementException e) {

            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/path")
    public ResponseEntity<?> findPath(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        if (from == null || to == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "from and to are required"));
        }

        int fromId;
        int toId;

        try {

            fromId = Integer.parseInt(from);
            toId = Integer.parseInt(to);

        } catch (NumberFormatException e) {

            return ResponseEntity.badRequest().body(Map.of("error", "invalid from or to"));
        }

        try {

            PathResponse response =service.findPath(fromId, toId);

            return ResponseEntity.ok(response);

        } catch (NoSuchElementException e) {

            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
}
