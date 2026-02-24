package com.mycompany.almacen.exception;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Excepción para errores de validación con soporte para múltiples campos.
 */
public class ValidationException extends AlmacenException {
    private final Map<String, String> fieldErrors;

    public ValidationException(String message) {
        super(message);
        this.fieldErrors = new HashMap<>();
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.fieldErrors = new HashMap<>();
    }

    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.fieldErrors = new HashMap<>(fieldErrors);
    }

    /**
     * Agrega un error para un campo específico.
     */
    public void addFieldError(String fieldName, String errorMessage) {
        fieldErrors.put(fieldName, errorMessage);
    }

    /**
     * Obtiene los errores por campo.
     */
    public Map<String, String> getFieldErrors() {
        return Collections.unmodifiableMap(fieldErrors);
    }

    /**
     * Verifica si hay errores para un campo específico.
     */
    public boolean hasFieldError(String fieldName) {
        return fieldErrors.containsKey(fieldName);
    }

    /**
     * Obtiene el error de un campo específico.
     */
    public String getFieldError(String fieldName) {
        return fieldErrors.get(fieldName);
    }

    /**
     * Verifica si hay errores de validación.
     */
    public boolean hasErrors() {
        return !fieldErrors.isEmpty();
    }
}