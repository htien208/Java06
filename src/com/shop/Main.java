package com.shop;

import com.formdev.flatlaf.FlatLightLaf;
import com.shop.ui.LoginForm;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 6);

        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
