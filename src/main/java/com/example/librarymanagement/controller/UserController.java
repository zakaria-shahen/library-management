package com.example.librarymanagement.controller;

import com.example.librarymanagement.dto.UserDto;
import com.example.librarymanagement.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

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
    public UserDto getUser(@PathVariable long id) {
        return userService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody UserDto userDto) {
        var uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/" + userService.create(userDto).getId())
                .build().toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable long id, @RequestBody UserDto userDto) {
        userDto.setId(id);
        var uri = ServletUriComponentsBuilder.fromCurrentRequestUri();
        Optional<UserDto> optionalUserDto = userService.update(userDto);
        return optionalUserDto.<ResponseEntity<Void>>map(it -> ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.CONTENT_LOCATION, uri.path("/" + it.getId()).build().toUri().toString())
                .build()
        ).orElseGet(() -> ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .header(HttpHeaders.CONTENT_LOCATION, uri.path("/" + userDto.getId()).build().toUri().toString())
                .build()
        );
    }

}
