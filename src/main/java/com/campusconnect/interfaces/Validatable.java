package com.campusconnect.interfaces;

import com.campusconnect.exceptions.InvalidInputException;

/**
 * Interface ensuring an object can check its own data integrity.
 * Implementing this allows us to call .validate() on any entity before saving.
 */
public interface Validatable {
    void validate() throws InvalidInputException;
}