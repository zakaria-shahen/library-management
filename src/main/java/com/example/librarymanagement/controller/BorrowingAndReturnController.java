package com.example.librarymanagement.controller;

import com.example.librarymanagement.dto.BorrowingDto;
import com.example.librarymanagement.service.BorrowingAndReturnService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@AllArgsConstructor
@RestController
public class BorrowingAndReturnController {

    private final BorrowingAndReturnService borrowingAndReturnService;

    @PostMapping("/borrowing/{bookId}/user/{userId}")
    @PreAuthorize("principal.claims['sub'] == T(String).valueOf(#userId) || hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> borrowing(@PathVariable long bookId, @PathVariable long userId, @RequestBody BorrowingDto borrowingDto) {
        var borrowingId = borrowingAndReturnService.borrowing(bookId, userId, borrowingDto).getId();
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/" + borrowingId).build().toUri();

        return ResponseEntity.created(uri).build();
    }

    @PreAuthorize("principal.claims['sub'] == T(String).valueOf(#userId) || hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/return/{bookId}/user/{userId}")
    public ResponseEntity<Void> returns(@PathVariable long bookId, @PathVariable long userId) {
        borrowingAndReturnService.returns(bookId, userId);
        return ResponseEntity.noContent().build();
    }

}
