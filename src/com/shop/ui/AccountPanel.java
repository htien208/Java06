package com.shop.ui;

import com.shop.db.UserDB;
import com.shop.model.User;

import javax.swing.*;
import java.awt.*;

public class AccountPanel extends JPanel {

    private JPanel AccountPanel;
    private JPanel pnlTitle;
    private JLabel lblTitle;
    private JPanel pnlInfoUser;
    private JLabel lblID;
    private JLabel lblUsername;
    private JTextField txtUsername;
    private JLabel lblIDreadonly;
    private JLabel lblName;
    private JTextField txtName;
    private JLabel lblRole;
    private JComboBox boxRole;
    private JLabel lblPass;
    private JPasswordField txtPass;
    private JLabel lblConfPass;
    private JPasswordField txtConfPass;
    private JPanel pnlButton;
    private JButton butOK;
    private JButton butCancel;

    private User editUser; // null = thêm mới, có giá trị = sửa

    public AccountPanel(User user) {
        this.editUser = user;
        setLayout(new BorderLayout());
        add(AccountPanel, BorderLayout.CENTER);

        // Cấu hình ComboBox role
        boxRole.removeAllItems();
        boxRole.addItem("Nhân viên");
        boxRole.addItem("Quản lý");

        if (editUser != null) {
            // Chế độ Sửa
            lblTitle.setText("Sửa thông tin nhân sự");
            lblIDreadonly.setText(String.valueOf(editUser.getUserId()));
            txtUsername.setText(editUser.getUsername());
            txtUsername.setEditable(false); // không cho đổi username
            txtName.setText(editUser.getFullName());
            boxRole.setSelectedItem(editUser.getRoleDisplay());
            // Ẩn ô mật khẩu khi sửa
            lblPass.setVisible(false);
            txtPass.setVisible(false);
            lblConfPass.setVisible(false);
            txtConfPass.setVisible(false);
        } else {
            // Chế độ Thêm mới
            lblTitle.setText("Thêm tài khoản nhân sự");
            lblIDreadonly.setText("Tự động");
        }

        butOK.addActionListener(e -> doSave());
        butCancel.addActionListener(e ->
                ((JDialog) SwingUtilities.getWindowAncestor(this)).dispose());
    }

    private void doSave() {
        String username = txtUsername.getText().trim();
        String fullName = txtName.getText().trim();
        String role     = "Quản lý".equals(boxRole.getSelectedItem())
                ? "MANAGER" : "STAFF";

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên tài khoản!");
            txtUsername.requestFocus();
            return;
        }
        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ và tên!");
            txtName.requestFocus();
            return;
        }

        if (editUser == null) {
            // Thêm mới — cần validate mật khẩu
            String pass     = new String(txtPass.getPassword()).trim();
            String confPass = new String(txtConfPass.getPassword()).trim();

            if (pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu!");
                return;
            }
            if (!pass.equals(confPass)) {
                JOptionPane.showMessageDialog(this,
                        "Mật khẩu xác nhận không khớp!");
                txtConfPass.setText("");
                txtConfPass.requestFocus();
                return;
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(pass);
            newUser.setFullName(fullName);
            newUser.setRole(role);

            boolean ok = UserDB.add(newUser);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Thêm tài khoản thành công!");
                ((JDialog) SwingUtilities.getWindowAncestor(this)).dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Thêm thất bại! Tài khoản có thể đã tồn tại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Sửa thông tin
            editUser.setFullName(fullName);
            editUser.setRole(role);

            boolean ok = UserDB.update(editUser);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                ((JDialog) SwingUtilities.getWindowAncestor(this)).dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}