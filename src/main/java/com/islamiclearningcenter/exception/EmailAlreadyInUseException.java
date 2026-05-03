package com.islamiclearningcenter.exception;

/**
 * Thrown when a registration request uses an email that is already registered.
 *
 * <p>TODO: Avoid putting the concrete email in the API error body — it enables account enumeration.
 * Prefer a generic client message (e.g. "Unable to register with this email") and log the detail
 * server-side if needed.
 */
public class EmailAlreadyInUseException extends RuntimeException {

  public EmailAlreadyInUseException(String email) {
    super("An account with this email already exists: " + email);
  }
}
