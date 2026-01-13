package com.mycompany.almacen.util;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ThemeManager {
    
    public static void setLightTheme() {
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void setDarkTheme() {
        try {
            FlatDarkLaf.setup();
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void toggleTheme() {
        String currentLaf = UIManager.getLookAndFeel().getClass().getName();
        if (currentLaf.equals(FlatDarkLaf.class.getName())) {
            setLightTheme();
        } else {
            setDarkTheme();
        }
        
        // Actualizar todas las ventanas
        SwingUtilities.invokeLater(() -> {
            for (java.awt.Window window : java.awt.Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
            }
        });
    }
}