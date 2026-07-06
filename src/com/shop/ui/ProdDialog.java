package com.shop.ui;

import com.shop.db.ProductDB;
import com.shop.model.Product;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class ProdDialog extends JDialog {

    private JPanel pnlProdDialog;
    private JLabel lblName;
    private JTextField txtName;
    private JLabel lblBrand;
    private JComboBox boxBrand;
    private JLabel lblSize;
    private JLabel lblColor;
    private JTextField txtColor;
    private JLabel lblPrice;
    private JTextField txtPrice;
    private JLabel lblStock;
    private JTextField txtStock;
    private JButton butCancel;
    private JButton butSave;
    private JComboBox boxStatus;
    private JLabel lblStatus;
    private JSlider sldSize;

    private boolean saved = false;
    private Product editProduct; // null = Thêm mới, có giá trị = Sửa

    public ProdDialog(Frame parent, Product product) {
        super(parent, true);              // modal = true: chặn MainFrame khi dialog mở
        this.editProduct = product;

        setContentPane(pnlProdDialog);
        setSize(420, 500);
        setLocationRelativeTo(parent);
        setResizable(false);

        if (editProduct != null) {
            setTitle("Sửa sản phẩm");
            fillForm(editProduct);
        } else {
            setTitle("Thêm sản phẩm mới");
        }

        butSave.addActionListener(e -> doSave());

        butCancel.addActionListener(e -> dispose());
    }

    /** Điền dữ liệu sản phẩm vào form khi Sửa */
    private void fillForm(Product p) {
        txtName.setText(p.getName());
        boxBrand.setSelectedItem(p.getBrand());
        txtColor.setText(p.getColor());
        txtPrice.setText(p.getPrice() != null ? p.getPrice().toPlainString() : "");
        txtStock.setText(String.valueOf(p.getStock()));
        boxStatus.setSelectedItem(p.getStatusDisplay());

        // Set slider về size của sản phẩm
        try {
            int size = Integer.parseInt(p.getSize());
            if (size >= 35 && size <= 45) sldSize.setValue(size);
        } catch (NumberFormatException ignored) {}
    }

    private void doSave() {
        // Validate data không được bỏ trống
        if (txtName.getText().trim().isEmpty()) {
            showError("Vui lòng nhập tên sản phẩm!");
            txtName.requestFocus();
            return;
        }
        if (boxBrand.getSelectedIndex() == 0) {
            showError("Vui lòng chọn thương hiệu!");
            return;
        }
        if (txtPrice.getText().trim().isEmpty()) {
            showError("Vui lòng nhập giá sản phẩm!");
            txtPrice.requestFocus();
            return;
        }
        if (txtStock.getText().trim().isEmpty()) {
            showError("Vui lòng nhập số lượng tồn kho!");
            txtStock.requestFocus();
            return;
        }

        // Validate giá là số
        BigDecimal price;
        try {
            price = new BigDecimal(txtPrice.getText().trim().replace(",", ""));
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                showError("Giá không được âm!");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Giá phải là số hợp lệ!");
            txtPrice.requestFocus();
            return;
        }

        // Validate tồn kho là số nguyên
        int stock;
        try {
            stock = Integer.parseInt(txtStock.getText().trim());
            if (stock < 0) {
                showError("Tồn kho không được âm!");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Tồn kho phải là số nguyên!");
            txtStock.requestFocus();
            return;
        }

        Product p = editProduct != null ? editProduct : new Product();
        p.setName(txtName.getText().trim());
        p.setBrand(boxBrand.getSelectedItem().toString());
        p.setSize(String.valueOf(sldSize.getValue()));
        p.setColor(txtColor.getText().trim());
        p.setPrice(price);
        p.setStock(stock);
        p.setStatus("Đang bán".equals(boxStatus.getSelectedItem()) ? "ACTIVE" : "INACTIVE");

        // Gọi DB
        boolean ok = editProduct != null
                ? ProductDB.update(p)
                : ProductDB.add(p);

        if (ok) {
            JOptionPane.showMessageDialog(this,
                    editProduct != null ? "Cập nhật thành công!" : "Thêm sản phẩm thành công!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            saved = true;
            dispose();
        } else {
            showError("Lưu thất bại! Kiểm tra kết nối database.");
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    /** ProductPanel dùng cái này để biết có cần reload data không */
    public boolean isSaved() {
        return saved;
    }    //productPanel dùng để biết cần reload không
}