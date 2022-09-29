package com.manulife.codingtest.bookstore.config.exception;

import com.manulife.codingtest.bookstore.config.payload.MessageResponse;
import com.manulife.codingtest.bookstore.config.payload.ResponseType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<MessageResponse>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ResponseEntity
                .badRequest()
                .body(ex.getBindingResult().getFieldErrors().stream()
                        .map(x -> new MessageResponse(ResponseType.ERROR, x.getField() + " " + x.getDefaultMessage()))
                        .collect(Collectors.toList()));
    }
}
