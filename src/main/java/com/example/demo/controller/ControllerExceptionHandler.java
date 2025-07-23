package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.example.demo.model.UserInputException;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler({UserInputException.class})
    public ResponseEntity handle(UserInputException ex){
        return new ResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
