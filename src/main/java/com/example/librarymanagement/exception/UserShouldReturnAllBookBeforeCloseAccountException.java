package com.example.librarymanagement.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class UserShouldReturnAllBookBeforeCloseAccountException extends ErrorResponseException {

    public static String DETAIL = "User should return all books before close account";

    /**
     * User should return all books before close account
     */
    public UserShouldReturnAllBookBeforeCloseAccountException() {
        super(
                HttpStatus.CONFLICT,
                ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, DETAIL),
                null
        );
    }

}
