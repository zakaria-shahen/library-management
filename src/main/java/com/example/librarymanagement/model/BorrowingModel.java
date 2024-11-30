package com.example.librarymanagement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BorrowingModel {
    private long id;
    private long bookId;
    private long userId;
    private LocalDate borrowingDate;
    private LocalDate returnDate;
    private boolean isReturned;
}
