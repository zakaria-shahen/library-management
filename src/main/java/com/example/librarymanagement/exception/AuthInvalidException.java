package com.example.librarymanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;


public class AuthInvalidException extends ErrorResponseException {

    public static String DETAIL = "Invalid credentials.";

    /**
     * Invalid credentials
     */
    public AuthInvalidException() {
        super(
                HttpStatus.UNAUTHORIZED,
                ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, DETAIL),
                null
        );
    }

}
