package com.example.librarymanagement.controller;

import com.example.librarymanagement.dto.UserDto;
import com.example.librarymanagement.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

import static com.example.librarymanagement.controller.ControllerUtils.URIRequestAndReplaceLastPath;

@AllArgsConstructor
@RestController
@RequestMapping("/patrons")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("principal.claims['sub'] == T(String).valueOf(#id) || hasAuthority('SCOPE_ADMIN')")
    public UserDto getUser(@PathVariable long id) {
        return userService.findById(id);
    }

    // registration endpoint
    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody @Validated UserDto userDto) {
        var uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/" + userService.create(userDto).getId())
                .build().toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("principal.claims['sub'] == T(String).valueOf(#id) || hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> updateUser(@PathVariable long id, @RequestBody @Validated UserDto userDto) {
        userDto.setId(id);
        Optional<UserDto> optionalUserDto = userService.update(userDto);
        return optionalUserDto.<ResponseEntity<Void>>map(it -> ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.CONTENT_LOCATION, URIRequestAndReplaceLastPath("/" + it.getId()))
                .build()
        ).orElseGet(() -> ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .header(HttpHeaders.CONTENT_LOCATION, URIRequestAndReplaceLastPath("/" + userDto.getId()))
                .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("principal.claims['sub'] == T(String).valueOf(#id) || hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
