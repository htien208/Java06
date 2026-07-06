package com.shop.ui;

import com.shop.db.CustomerDB;
import com.shop.db.InvoiceDB;
import com.shop.db.ProductDB;
import com.shop.model.Customer;
import com.shop.model.Invoice;
import com.shop.model.InvoiceDetail;
import com.shop.model.Product;
import com.shop.util.SessionManager;

import javax.swing.*;
import java.awt.Window;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDetailPanel extends JPanel {

    private JPanel pnlInvoiceDetail;

    private JPanel pnlAddProd;
    private JPanel pnlSearchProd;
    private JLabel lblSearchProd;
    private JButton butSearchProd;
    private JTextField txtSearchProd;

    private JPanel pnlTable;

    private JPanel pnlProd;
    private JLabel lblNameProd;
    private JLabel lblName;
    private JLabel lblSize;
    private JLabel lblPrice;
    private JTextField txtPrice;
    private JLabel lblQuantity;
    private JTextField txtQuantity;
    private JButton butDelProd;
    private JButton butAddProd;
    private JLabel lblSizeProd;

    private JPanel pnlPickCus;
    private JLabel lblCustomer;
    private JLabel lblCusName;
    private JTextField txtCusName;
    private JLabel lblSDT;
    private JTextField txtSDT;
    private JButton butEditInf;
    private JButton butAddCus;
    private JLabel lblSearchCus;
    private JTextField txtSearchCus;
    private JButton butSearchCus;

    private JPanel pnlDiscount;
    private JLabel lblTotal;
    private JTextField txtTotalPrice;
    private JButton butCancel;
    private JButton butAddInvoice;
    private JLabel lblDiscount;
    private JTextField txtDiscount;

    private JTable tblInvoice;

    private DefaultTableModel tableModel;
    private JPopupMenu searchProductPopup = new JPopupMenu();
    private JPopupMenu searchCustomerPopup = new JPopupMenu();
    private Product selectedProduct = null;
    private Customer selectedCustomer = null;
    private List<InvoiceDetail> invoiceDetails = new ArrayList<>();

    private static final String[] COLUMNS = {
            "STT", "ID SP", "Tên sản phẩm", "Đơn giá", "Số lượng", "Thành tiền"
    };

    public InvoiceDetailPanel() {
        setLayout(new BorderLayout());
        add(pnlInvoiceDetail, BorderLayout.CENTER);

        initTable();
        initEvents();
        resetForm();

    }

    private void initTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblInvoice.setModel(tableModel);
        tblInvoice.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Đặt vào JScrollPane nếu chưa có
        if (!(tblInvoice.getParent() instanceof JViewport)) {
            pnlTable.setLayout(new BorderLayout());
            pnlTable.add(new JScrollPane(tblInvoice), BorderLayout.CENTER);
        }
    }

    private void initEvents() {
        // Tìm kiếm sản phẩm
        butSearchProd.addActionListener(e -> doSearchProduct());
        txtSearchProd.addActionListener(e -> doSearchProduct());

        // Thêm sản phẩm vào bảng HĐ
        butAddProd.addActionListener(e -> doAddProduct());

        // Xóa dòng đang chọn
        butDelProd.addActionListener(e -> doDeleteRow());

        // Tìm kiếm khách hàng qua popup
        butSearchCus.addActionListener(e -> doSearchCustomer());
        txtSearchCus.addActionListener(e -> doSearchCustomer());

        // Thêm khách hàng mới
        butAddCus.addActionListener(e -> doAddCustomer());

        // Sửa thông tin KH
        butEditInf.addActionListener(e -> doEditCustomer());

        // Tính lại tổng khi chiết khấu thay đổi
        txtDiscount.addActionListener(e -> recalcTotal());

        butCancel.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this,
                    "Hủy hóa đơn hiện tại?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                resetForm();
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window != null) window.dispose();
            }
        });
        butAddInvoice.addActionListener(e -> doCreateInvoice());
    }

    private void doSearchProduct() {
        String keyword = txtSearchProd.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm!");
            return;
        }

        List<Product> results = ProductDB.search(keyword);
        searchProductPopup.removeAll();

        if (results.isEmpty()) {
            JMenuItem noResult = new JMenuItem("Không tìm thấy sản phẩm");
            noResult.setEnabled(false);
            searchProductPopup.add(noResult);
        } else {
            for (Product p : results) {
                JMenuItem item = new JMenuItem(
                        p.getName() + " | " + p.getBrand()
                                + " | Size " + p.getSize()
                                + " | " + p.getPriceDisplay()
                                + " | Tồn: " + p.getStock()
                );
                item.addActionListener(ev -> {
                    selectedProduct = p;
                    lblName.setText(p.getName());
                    lblSize.setText(p.getSize());
                    txtPrice.setText(p.getPrice().toPlainString());
                    txtQuantity.setText("1");
                    txtSearchProd.setText(p.getName());
                    searchProductPopup.setVisible(false);
                });
                searchProductPopup.add(item);
            }
        }
        searchProductPopup.show(txtSearchProd, 0, txtSearchProd.getHeight());
    }

    private void doAddProduct() {
        if (selectedProduct == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm trước!");
            return;
        }

        //check số lượng
        int quantity;
        try {
            quantity = Integer.parseInt(txtQuantity.getText().trim());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!");
                return;
            }
            if (quantity > selectedProduct.getStock()) {
                JOptionPane.showMessageDialog(this,
                        "Số lượng vượt quá tồn kho! Tồn kho hiện tại: "
                                + selectedProduct.getStock());
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên!");
            return;
        }

        // check đơn giá
        BigDecimal unitPrice;
        try {
            unitPrice = new BigDecimal(txtPrice.getText().trim().replace(",", ""));
            if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Đơn giá phải lớn hơn 0!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Đơn giá không hợp lệ!");
            return;
        }

        //cộng dồn SP nếu đã có trong bảng
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int existId = (int) tableModel.getValueAt(i, 1);
            if (existId == selectedProduct.getProductId()) {
                int existQty = (int) tableModel.getValueAt(i, 4);
                int newQty = existQty + quantity;
                if (newQty > selectedProduct.getStock()) {
                    JOptionPane.showMessageDialog(this,
                            "Tổng số lượng vượt quá tồn kho!");
                    return;
                }
                tableModel.setValueAt(newQty, i, 4);
                BigDecimal newSubtotal = unitPrice.multiply(BigDecimal.valueOf(newQty));
                tableModel.setValueAt(String.format("%,.0f ₫", newSubtotal), i, 5);
                recalcTotal();
                clearProductForm();
                return;
            }
        }

        // thêm dòng mới vào baảng
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        tableModel.addRow(new Object[]{
                tableModel.getRowCount() + 1, selectedProduct.getProductId(),
                selectedProduct.getName(),
                String.format("%,.0f ₫", unitPrice),
                quantity,
                String.format("%,.0f ₫", subtotal)
        });

        // lưu vào danh sách detail
        InvoiceDetail detail = new InvoiceDetail(
                selectedProduct.getProductId(),
                selectedProduct.getName(),
                quantity,
                unitPrice
        );
        invoiceDetails.add(detail);

        recalcTotal();
        clearProductForm();
    }

    //xóa dòng đang chọn
    private void doDeleteRow() {
        int row = tblInvoice.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa!");
            return;
        }
        tableModel.removeRow(row);
        invoiceDetails.remove(row);
        updateSTT();
        recalcTotal();
    }

    //tìm kiếm khách hàng
    private void doSearchCustomer() {
        String phone = txtSearchCus.getText().trim();
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập SĐT tìm kiếm!");
            return;
        }

        List<Customer> results = CustomerDB.findByPhone(phone);
        searchCustomerPopup.removeAll();

        if (results.isEmpty()) {
            JMenuItem noResult = new JMenuItem("Không tìm thấy — bấm Thêm KH mới");
            noResult.setEnabled(false);
            searchCustomerPopup.add(noResult);
        } else {
            for (Customer c : results) {
                JMenuItem item = new JMenuItem(c.getFullName() + " | " + c.getPhone());
                item.addActionListener(ev -> {
                    selectedCustomer = c;
                    txtCusName.setText(c.getFullName());
                    txtSDT.setText(c.getPhone());
                    searchCustomerPopup.setVisible(false);
                });
                searchCustomerPopup.add(item);
            }
        }
        searchCustomerPopup.show(txtSearchCus, 0, txtSearchCus.getHeight());
    }

    //thêm khách hàng mới
    private void doAddCustomer() {
        String name  = txtCusName.getText().trim();
        String phone = txtSDT.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập tên và SĐT khách hàng!");
            return;
        }

        Customer c = new Customer();
        c.setFullName(name);
        c.setPhone(phone);

        boolean ok = CustomerDB.add(c);
        if (ok) {
            selectedCustomer = CustomerDB.findByPhone(phone).get(0);
            JOptionPane.showMessageDialog(this, "Đã thêm khách hàng mới!");
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại!");
        }
    }

    //sửa thông tin khách hàng
    private void doEditCustomer() {
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn khách hàng trước!");
            return;
        }

        txtCusName.setEditable(true);
        txtSDT.setEditable(true);
        txtCusName.requestFocus();
        JOptionPane.showMessageDialog(this,
                "Đã mở chỉnh sửa — sửa xong bấm Thêm KH để lưu lại.");
    }

    //update STT sau khi sửa
    private void updateSTT() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(i + 1, i, 0);
        }
    }

    //tính lại tổng tiền
    private void recalcTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String subtotalStr = tableModel.getValueAt(i, 5).toString()
                    .replace("₫", "").replace(",", "").trim();
            try {
                total = total.add(new BigDecimal(subtotalStr));
            } catch (NumberFormatException ignored) {}
        }

        //tính chiết khấu
        try {
            String discountStr = txtDiscount.getText().trim().replace(",", "");
            if (!discountStr.isEmpty()) {
                BigDecimal discount = new BigDecimal(discountStr);
                total = total.subtract(discount);
                if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;
            }
        } catch (NumberFormatException ignored) {}

        txtTotalPrice.setText(String.format("%,.0f ₫", total));
    }

    //tạo hóa đơn
    private void doCreateInvoice() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng thêm ít nhất 1 sản phẩm vào hóa đơn!");
            return;
        }

        // Build Invoice object
        Invoice invoice = new Invoice();
        invoice.setUserId(SessionManager.getCurrentUser().getUserId());

        if (selectedCustomer != null) {
            invoice.setCustomerId(selectedCustomer.getCustomerId());
        }

        //tính tổng
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceDetail d : invoiceDetails) {
            total = total.add(d.getSubtotal());
        }
        // Trừ chiết khấu
        try {
            String discountStr = txtDiscount.getText().trim().replace(",", "");
            if (!discountStr.isEmpty()) {
                total = total.subtract(new BigDecimal(discountStr));
                if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;
            }
        } catch (NumberFormatException ignored) {}

        invoice.setTotal(total);
        invoice.setDetails(invoiceDetails);

        // Lưu vào DB
        boolean ok = InvoiceDB.save(invoice);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Tạo hóa đơn thành công!\nTổng tiền: "
                            + String.format("%,.0f ₫", total),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            // Đóng dialog sau khi tạo HĐ thành công
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) window.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Tạo hóa đơn thất bại! Kiểm tra kết nối database.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // reset form
    private void resetForm() {
        tableModel.setRowCount(0);
        invoiceDetails.clear();
        selectedProduct  = null;
        selectedCustomer = null;
        clearProductForm();
        txtCusName.setText("");
        txtCusName.setEditable(false);
        txtSDT.setText("");
        txtSDT.setEditable(false);
        txtSearchCus.setText("");
        txtDiscount.setText("");
        txtTotalPrice.setText("0 ₫");
    }

    //xóa form
    private void clearProductForm() {
        selectedProduct = null;
        txtSearchProd.setText("");
        lblName.setText("");
        lblSize.setText("");
        txtPrice.setText("");
        txtQuantity.setText("");
    }
}