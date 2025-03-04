package com.anil.blog.controllers;

import com.anil.blog.dtos.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
@Slf4j
public class ErrorController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception e){
        log.error("Caught exception",e);
        ApiErrorResponse error = ApiErrorResponse.builder().
                status(HttpStatus.INTERNAL_SERVER_ERROR.value()).
                message("An unexpected error occurred")
                .build();
        return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException e){
        log.error("Illegal Argument error",e);
        ApiErrorResponse error = ApiErrorResponse.builder().
                status(HttpStatus.BAD_REQUEST.value()).
                message("Invalid input provided.Check your request and try again.").
                build();
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalStateException(IllegalStateException e){
        log.error("Illegal Statement error",e);
        ApiErrorResponse error = ApiErrorResponse.builder().
                status(HttpStatus.CONFLICT.value()).
                message("Can't really do that.").
                build();
        return new ResponseEntity<>(error,HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(BadCredentialsException e){
        log.error("Credentials error",e);
        ApiErrorResponse error = ApiErrorResponse.builder().
                status(HttpStatus.UNAUTHORIZED.value()).
                message("Incorrect username or password").
                build();
        return new ResponseEntity<>(error,HttpStatus.UNAUTHORIZED);
    }
}
