package com.shop.ui;

import com.shop.db.UserDB;
import com.shop.model.User;
import com.shop.util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SettingPanel extends JPanel {

    private JPanel SettingPanel;
    private JPanel pnlButton;
    private JButton butAddUser;
    private JButton butEditInfo;
    private JButton butChangePass;
    private JButton butDelete;
    private JPanel pnlInfo;
    private JLabel lblStaffCount;
    private JScrollPane scrollList;
    private JTable tblUser;

    private DefaultTableModel tableModel;
    private static final String[] COLUMNS = {
            "ID", "Họ và tên", "Tài khoản", "Chức vụ", "Ngày tạo"
    };

    public SettingPanel() {
        setLayout(new BorderLayout());
        add(SettingPanel, BorderLayout.CENTER);

        initTable();
        initEvents();
        loadData();

        // Ẩn nút Xóa nếu không phải MANAGER
        if (!SessionManager.isManager()) {
            butDelete.setVisible(false);
            butAddUser.setVisible(false);
        }
    }

    private void initTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblUser.setModel(tableModel);
        tblUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblUser.getTableHeader().setReorderingAllowed(false);

        // Ẩn cột ID
        tblUser.getColumnModel().getColumn(0).setMinWidth(0);
        tblUser.getColumnModel().getColumn(0).setMaxWidth(0);
        tblUser.getColumnModel().getColumn(0).setWidth(0);
    }

    private void initEvents() {
        // Thêm tài khoản mới
        butAddUser.addActionListener(e -> {
            JDialog dialog = new JDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    "Thêm tài khoản", true);
            AccountPanel panel = new AccountPanel(null);
            dialog.setContentPane(panel);
            dialog.setSize(480, 400);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            loadData();
        });

        // Sửa thông tin
        butEditInfo.addActionListener(e -> {
            User selected = getSelectedUser();
            if (selected == null) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn nhân viên cần sửa!");
                return;
            }
            JDialog dialog = new JDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    "Sửa thông tin", true);
            AccountPanel panel = new AccountPanel(selected);
            dialog.setContentPane(panel);
            dialog.setSize(480, 400);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            loadData();
        });

        // Đổi mật khẩu
        butChangePass.addActionListener(e -> {
            User selected = getSelectedUser();
            if (selected == null) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn nhân viên cần đổi mật khẩu!");
                return;
            }
            JDialog dialog = new JDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    "Đổi mật khẩu", true);
            ChangePassPanel panel = new ChangePassPanel(selected);
            dialog.setContentPane(panel);
            dialog.setSize(420, 320);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });

        // Xóa tài khoản
        butDelete.addActionListener(e -> {
            User selected = getSelectedUser();
            if (selected == null) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn nhân viên cần xóa!");
                return;
            }
            // Không cho xóa chính mình
            if (selected.getUserId() == SessionManager.getCurrentUser().getUserId()) {
                JOptionPane.showMessageDialog(this,
                        "Không thể xóa tài khoản đang đăng nhập!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Xóa tài khoản \"" + selected.getFullName() + "\"?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = UserDB.delete(selected.getUserId());
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Double-click → sửa thông tin
        tblUser.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) butEditInfo.doClick();
            }
        });

        Font btnFont = new Font("Segoe UI", Font.PLAIN, 18);
        butAddUser.setFont(btnFont);
        butEditInfo.setFont(btnFont);
        butChangePass.setFont(btnFont);
        butDelete.setFont(btnFont);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<User> list = UserDB.getAll();
        for (User u : list) {
            tableModel.addRow(new Object[]{
                    u.getUserId(),
                    u.getFullName(),
                    u.getUsername(),
                    u.getRoleDisplay(),
                    u.getCreatedAt() != null
                            ? String.format("%02d/%02d/%04d",
                            u.getCreatedAt().getDayOfMonth(),
                            u.getCreatedAt().getMonthValue(),
                            u.getCreatedAt().getYear())
                            : ""
            });
        }
        lblStaffCount.setText("Tổng: " + list.size() + " nhân viên");
    }

    private User getSelectedUser() {
        int row = tblUser.getSelectedRow();
        if (row == -1) return null;
        int userId      = (int) tableModel.getValueAt(row, 0);
        String fullName = tableModel.getValueAt(row, 1).toString();
        String username = tableModel.getValueAt(row, 2).toString();
        String role     = "Quản lý".equals(tableModel.getValueAt(row, 3).toString())
                ? "MANAGER" : "STAFF";
        User u = new User();
        u.setUserId(userId);
        u.setFullName(fullName);
        u.setUsername(username);
        u.setRole(role);
        return u;
    }
}