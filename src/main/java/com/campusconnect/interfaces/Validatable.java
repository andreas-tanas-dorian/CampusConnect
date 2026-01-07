package com.campusconnect.interfaces;
import com.campusconnect.exceptions.InvalidInputException;

public interface Validatable {
    void validate() throws InvalidInputException;
}