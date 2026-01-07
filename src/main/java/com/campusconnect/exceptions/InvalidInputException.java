package com.campusconnect.exceptions;

/**
 * Custom Exception: Thrown when user data (like email or password)
 * doesn't meet requirements.
 */
public class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}