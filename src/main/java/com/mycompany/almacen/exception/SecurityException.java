package com.mycompany.almacen.exception;

public class SecurityException extends AlmacenException {
    public SecurityException(String message) {
        super(message);
    }

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}