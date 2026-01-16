package com.mycompany.almacen.exception;

public class AlmacenException extends Exception {
    public AlmacenException(String message) {
        super(message);
    }

    public AlmacenException(String message, Throwable cause) {
        super(message, cause);
    }
}