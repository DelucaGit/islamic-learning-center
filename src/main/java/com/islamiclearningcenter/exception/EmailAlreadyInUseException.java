package com.islamiclearningcenter.exception;

/** Thrown when a registration request uses an email that is already registered. */
public class EmailAlreadyInUseException extends RuntimeException {

  public EmailAlreadyInUseException(String email) {
    super("An account with this email already exists: " + email);
  }
}
