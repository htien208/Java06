package com.shop.ui;

import com.shop.db.UserDB;
import com.shop.model.User;
import com.shop.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginForm extends JFrame {

    private JPanel pnlLogin;
    private JLabel lblLogin;
    private JLabel lblUsername;
    private JTextField txtUsername;
    private JLabel lblPass;
    private JPasswordField txtPassword;
    private JButton butCancel;
    private JButton butLogin;
    private JLabel lblError;

    public LoginForm() {
        setContentPane(pnlLogin);
        setTitle("hamiti's Shop - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 320);
        setLocationRelativeTo(null);
        setResizable(false);

        lblError.setText(" ");
        lblError.setForeground(new Color(200, 50, 50));     //an lblError, chi hien thi khi co loi

        butLogin.addActionListener(e -> doLogin());

        butCancel.addActionListener(e -> System.exit(0));

        txtUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    txtPassword.requestFocus();
            }
        });

        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    doLogin();
            }
        });
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Vui lòng nhập đầy đủ tài khoản và mật khẩu!");
            return;
        }

        butLogin.setEnabled(false);
        butLogin.setText("Đang đăng nhập...");

        User user = UserDB.login(username, password);

        if (user != null) {
            SessionManager.login(user);
            SwingUtilities.invokeLater(() -> {
                new MainFrame().setVisible(true);
            });
            this.dispose();
        } else {
            lblError.setText("Tài khoản hoặc mật khẩu không đúng!");
            butLogin.setEnabled(true);
            butLogin.setText("Đăng nhập");
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }
}