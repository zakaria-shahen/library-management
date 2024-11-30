package com.example.librarymanagement.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class UnavailableBookCopiesException extends ErrorResponseException {

    public static String DETAIL = "Unavailable book copies, try another time, or choose other book";

    /**
     * Unavailable book copies, try another time, or choose other book
     */
    public UnavailableBookCopiesException() {
        super(
                HttpStatus.CONFLICT,
                ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, DETAIL),
                null
        );
    }

}
