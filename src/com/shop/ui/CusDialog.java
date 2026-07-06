package com.shop.ui;

import com.shop.db.CustomerDB;
import com.shop.model.Customer;

import javax.swing.*;
import java.awt.*;

public class CusDialog extends JPanel {

    private JPanel CusDialogPanel;
    private JLabel lblCusDialogTitle;
    private JLabel lblNameCus;
    private JTextField txtName;
    private JLabel lblCusSDT;
    private JTextField txtSDT;
    private JLabel lblCusAddr;
    private JTextField txtAddr;
    private JLabel lblCusID;
    private JLabel lblID;
    private JPanel pnlBut;
    private JButton butSave;
    private JButton butCancel;

    private Customer editCustomer;
    private boolean saved = false;

    public CusDialog(Customer customer) {
        this.editCustomer = customer;
        setLayout(new BorderLayout());
        add(CusDialogPanel, BorderLayout.CENTER);

        if (editCustomer != null) {
            // Chế độ Sửa
            lblCusDialogTitle.setText("Sửa thông tin khách hàng");
            lblID.setText(String.valueOf(editCustomer.getCustomerId()));
            txtName.setText(editCustomer.getFullName());
            txtSDT.setText(editCustomer.getPhone());
            txtAddr.setText(editCustomer.getAddress() != null
                    ? editCustomer.getAddress() : "");
        } else {
            // Chế độ Thêm mới
            lblCusDialogTitle.setText("Thêm khách hàng mới");
            lblID.setText("Tự động");
        }

        butSave.addActionListener(e -> doSave());
        butCancel.addActionListener(e ->
                ((JDialog) SwingUtilities.getWindowAncestor(this)).dispose());
    }

    private void doSave() {
        String name = txtName.getText().trim();
        String sdt  = txtSDT.getText().trim();
        String addr = txtAddr.getText().trim();

        // Validate
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập họ và tên khách hàng!");
            txtName.requestFocus();
            return;
        }
        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập số điện thoại!");
            txtSDT.requestFocus();
            return;
        }
        if (!sdt.matches("\\d{9,11}")) {
            JOptionPane.showMessageDialog(this,
                    "Số điện thoại không hợp lệ!\nVui lòng nhập 9-11 chữ số.");
            txtSDT.requestFocus();
            return;
        }

        boolean ok;
        if (editCustomer != null) {
            // Sửa
            editCustomer.setFullName(name);
            editCustomer.setPhone(sdt);
            editCustomer.setAddress(addr);
            ok = CustomerDB.update(editCustomer);
        } else {
            // Thêm mới
            Customer c = new Customer();
            c.setFullName(name);
            c.setPhone(sdt);
            c.setAddress(addr);
            ok = CustomerDB.add(c);
        }

        if (ok) {
            JOptionPane.showMessageDialog(this,
                    editCustomer != null
                            ? "Cập nhật thành công!"
                            : "Thêm khách hàng thành công!");
            saved = true;
            ((JDialog) SwingUtilities.getWindowAncestor(this)).dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Lưu thất bại! Kiểm tra kết nối database.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}