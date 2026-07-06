package com.shop.ui;

import com.shop.db.UserDB;
import com.shop.model.User;
import com.shop.util.SessionManager;

import javax.swing.*;
import java.awt.*;

public class ChangePassPanel extends JPanel {

    private JPanel ChangePassPanel;
    private JPanel pnlTitle;
    private JLabel lblTitle;
    private JPanel pnlChangePass;
    private JLabel lblOldPass;
    private JPasswordField txtOldPass;
    private JLabel lblNewPass;
    private JPasswordField txtNewPass;
    private JLabel lblConfPass;
    private JPasswordField txtConfPass;
    private JPanel pnlBut;
    private JButton butOK;
    private JButton butCancel;

    private User targetUser;

    public ChangePassPanel(User user) {
        this.targetUser = user;
        setLayout(new BorderLayout());
        add(ChangePassPanel, BorderLayout.CENTER);

        lblTitle.setText("Đổi mật khẩu — " + user.getFullName());

        butOK.addActionListener(e -> doChangePass());
        butCancel.addActionListener(e ->
                ((JDialog) SwingUtilities.getWindowAncestor(this)).dispose());
    }

    private void doChangePass() {
        String oldPass  = new String(txtOldPass.getPassword()).trim();
        String newPass  = new String(txtNewPass.getPassword()).trim();
        String confPass = new String(txtConfPass.getPassword()).trim();

        if (oldPass.isEmpty() || newPass.isEmpty() || confPass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ thông tin!");
            return;
        }


        boolean isManager = SessionManager.isManager();
        boolean isSelf    = targetUser.getUserId()
                == SessionManager.getCurrentUser().getUserId();

        if (isSelf || !isManager) {
            // Xác thực mật khẩu cũ
            User check = UserDB.login(targetUser.getUsername(), oldPass);
            if (check == null) {
                JOptionPane.showMessageDialog(this,
                        "Mật khẩu hiện tại không đúng!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtOldPass.setText("");
                txtOldPass.requestFocus();
                return;
            }
        }

        if (!newPass.equals(confPass)) {
            JOptionPane.showMessageDialog(this,
                    "Mật khẩu mới xác nhận không khớp!");
            txtConfPass.setText("");
            txtConfPass.requestFocus();
            return;
        }

        if (newPass.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Mật khẩu mới phải có ít nhất 6 ký tự!");
            return;
        }

        // Cập nhật mật khẩu mới
        targetUser.setPassword(newPass);
        boolean ok = UserDB.update(targetUser);

        if (ok) {
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
            ((JDialog) SwingUtilities.getWindowAncestor(this)).dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Đổi mật khẩu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}