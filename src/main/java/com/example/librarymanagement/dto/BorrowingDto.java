package com.example.librarymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BorrowingDto {
    private long id;
    private LocalDate borrowingDate;
    private LocalDate returnDate;
}
