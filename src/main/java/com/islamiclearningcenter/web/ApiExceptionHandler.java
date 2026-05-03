package com.islamiclearningcenter.web;

import com.islamiclearningcenter.exception.EmailAlreadyInUseException;
import com.islamiclearningcenter.exception.InvalidCredentialsException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(EmailAlreadyInUseException.class)
  public ResponseEntity<Map<String, String>> emailTaken(EmailAlreadyInUseException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<Map<String, String>> invalidCredentials(InvalidCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> validation(MethodArgumentNotValidException ex) {
    FieldError first =
        ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
    String message =
        first != null && first.getDefaultMessage() != null
            ? first.getDefaultMessage()
            : "Invalid request";
    return ResponseEntity.badRequest().body(Map.of("error", message));
  }
}
