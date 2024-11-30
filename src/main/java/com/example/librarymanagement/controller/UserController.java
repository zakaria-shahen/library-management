package com.example.librarymanagement.controller;

import com.example.librarymanagement.dto.UserDto;
import com.example.librarymanagement.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

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


}
