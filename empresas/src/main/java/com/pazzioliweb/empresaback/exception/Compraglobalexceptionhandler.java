package com.pazzioliweb.empresaback.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class Compraglobalexceptionhandler {


    @ExceptionHandler(Empresaexception.class)
    public ResponseEntity<Map<String, String>> handlecompraexception(Empresaexception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.parseMediaType("application/json;charset=UTF-8")) // 👈 correcto
                .body(errorResponse);
    }

}
