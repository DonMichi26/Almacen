package com.mycompany.almacen.util;

import java.io.File;

public class FileUtils {
    
    /**
     * Obtiene la ruta de la carpeta de documentos del usuario
     * @return Ruta de la carpeta de documentos
     */
    public static String getUserDocumentsFolder() {
        return System.getProperty("user.home") + File.separator + "Documents";
    }
    
    /**
     * Asegura que una carpeta exista, creándola si es necesario
     * @param folderPath Ruta de la carpeta
     */
    public static void ensureFolderExists(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }
}