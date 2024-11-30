package com.example.librarymanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;


public class UserBorrowedBookBeforeWithoutReturnIt extends ErrorResponseException {

    public static String DETAIL = "You can borrow a book one at a time, and you have already borrowed it without returning it.";

    /**
     * You can borrow a book one at a time, and you have already borrowed it without returning it
     */
    public UserBorrowedBookBeforeWithoutReturnIt() {
        super(
                HttpStatus.CONFLICT,
                ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, DETAIL),
                null
        );
    }

}
