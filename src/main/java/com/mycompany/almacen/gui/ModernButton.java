package com.mycompany.almacen.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernButton extends JButton {
    
    private Color backgroundColor = new Color(255, 255, 255);
    private Color borderColor = new Color(200, 200, 200);
    private Color hoverBackgroundColor = new Color(240, 240, 240);
    private Color hoverBorderColor = new Color(180, 180, 180);
    private Color textColor = new Color(50, 50, 50);
    private Color textHoverColor = new Color(30, 30, 30);
    
    public ModernButton(String text) {
        super(text);
        initButton();
    }
    
    private void initButton() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(true);
        setOpaque(false);
        
        setFont(new Font("Segoe UI", Font.PLAIN, 12));
        setForeground(textColor);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverBackgroundColor);
                setForeground(textHoverColor);
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(backgroundColor);
                setForeground(textColor);
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw background
        if (getModel().isPressed()) {
            g2.setColor(hoverBackgroundColor);
        } else if (getModel().isRollover()) {
            g2.setColor(hoverBackgroundColor);
        } else {
            g2.setColor(backgroundColor);
        }
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
        
        // Draw border
        g2.setColor(getModel().isRollover() ? hoverBorderColor : borderColor);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);
        
        g2.dispose();
        
        super.paintComponent(g);
    }
    
    @Override
    protected void paintBorder(Graphics g) {
        // Border is painted in paintComponent to maintain rounded corners
    }
}