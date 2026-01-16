package com.mycompany.almacen.exception;

public class ValidationException extends AlmacenException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}