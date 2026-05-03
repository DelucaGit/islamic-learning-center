package com.islamiclearningcenter.exception;

/** Thrown when login email or password does not match any account (same message for both cases). */
public class InvalidCredentialsException extends RuntimeException {

  public InvalidCredentialsException() {
    super("Invalid email or password");
  }
}
