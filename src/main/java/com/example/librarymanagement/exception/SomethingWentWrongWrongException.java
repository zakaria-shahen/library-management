package com.example.librarymanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;


public class SomethingWentWrongWrongException extends ErrorResponseException {

    public static String DETAIL = "Something went wrong.";

    /**
     * Something went wrong
     */
    public SomethingWentWrongWrongException() {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, DETAIL),
                null
        );
    }

}
