package com.mycompany.almacen.util;


import java.nio.file.Path;
import java.nio.file.Paths;

public class ValidationUtils {
    
    /**
     * Valida que la ruta esté dentro del directorio permitido
     * @param filePath Ruta del archivo a validar
     * @param allowedDirectory Directorio permitido
     * @return true si la ruta es válida, false en caso contrario
     */
    public static boolean isValidPath(String filePath, String allowedDirectory) {
        try {
            Path basePath = Paths.get(allowedDirectory).toAbsolutePath().normalize();
            Path targetPath = Paths.get(filePath).toAbsolutePath().normalize();
            
            // Verifica que la ruta objetivo esté dentro del directorio base
            return targetPath.startsWith(basePath);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Sanitiza un nombre de archivo para evitar caracteres peligrosos
     * @param fileName Nombre de archivo a sanitizar
     * @return Nombre de archivo sanitizado
     */
    public static String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return null;
        }
        
        // Elimina caracteres potencialmente peligrosos
        String sanitized = fileName.replaceAll("[^a-zA-Z0-9._\\-]", "_");
        
        // Asegura que no empiece con puntos o barras que podrían indicar directorios
        sanitized = sanitized.replaceAll("^\\.+", "").trim();
        
        return sanitized;
    }
    
    /**
     * Valida que el nombre del archivo tenga una extensión permitida
     * @param fileName Nombre del archivo
     * @param allowedExtensions Lista de extensiones permitidas (sin el punto)
     * @return true si la extensión es válida, false en caso contrario
     */
    public static boolean hasValidExtension(String fileName, String... allowedExtensions) {
        if (fileName == null || allowedExtensions == null || allowedExtensions.length == 0) {
            return false;
        }
        
        String lowerCaseFileName = fileName.toLowerCase();
        for (String ext : allowedExtensions) {
            if (lowerCaseFileName.endsWith("." + ext.toLowerCase())) {
                return true;
            }
        }
        
        return false;
    }
}