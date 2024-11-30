package com.example.librarymanagement.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class NotFoundResourceException extends ErrorResponseException {
    public NotFoundResourceException() {
        super(
                HttpStatus.NOT_FOUND,
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.NOT_FOUND,
                        "Not Found resource"
                ),
                null
        );
    }

}
