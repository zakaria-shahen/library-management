package com.example.librarymanagement.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalException {

    private final Logger logger = LoggerFactory.getLogger(GlobalException.class);

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail DataIntegrityViolationExceptionHandler(HttpServletRequest httpServletRequest, DataIntegrityViolationException ex) {
        logger.warn("User calling {} endpoint and that throw DataIntegrityViolationException with next detail: {}", httpServletRequest.getContextPath(), ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setDetail("You try to link resource with resource doesn't exists at all!");
        return problemDetail;
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ProblemDetail DuplicateKeyExceptionHandler(HttpServletRequest httpServletRequest, DuplicateKeyException ex) {
        logger.warn("User calling {} endpoint and that throw DuplicateKeyException with next detail: {}", httpServletRequest.getContextPath(), ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setDetail("some of your property should be unique");
        return problemDetail;
    }

}