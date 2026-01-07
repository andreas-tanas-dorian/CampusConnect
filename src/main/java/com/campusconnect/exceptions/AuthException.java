package com.campusconnect.exceptions;

/**
 * Custom Exception: Thrown during login failures.
 */
public class AuthException extends Exception {
    public AuthException(String message) {
        super(message);
    }
}