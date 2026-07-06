package com.shop.ui;

import com.shop.db.ProductDB;
import com.shop.model.Product;
import com.shop.util.SessionManager;
import com.shop.util.ExportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class ProductPanel extends JPanel {

    private JPanel prodPanel;
    private JPanel pnlToolbar;
    private JLabel lblSearch;
    private JTextField txtSearchProd;
    private JButton butSearch;
    private JButton butAdd;
    private JButton butChange;
    private JButton butDelete;
    private JButton butExportCSV;
    private JScrollPane scrollProd;
    private JTable tblProd;
    private JPanel pnlStatus;
    private JLabel lblProdStatus;

    private static final String[] COLUMNS = {
            "ID", "Tên sản phẩm", "Thương hiệu", "Size", "Màu", "Giá", "Tồn kho", "Trạng thái"
    };

    private DefaultTableModel tableModel;

    public ProductPanel() {
        setLayout(new java.awt.BorderLayout());
        add(prodPanel, java.awt.BorderLayout.CENTER);

        initTable();
        initEvents();
        loadData(ProductDB.getAll());

        if (!SessionManager.isManager()) {       //an nut "Xóa" neu kh phai admin
            butDelete.setVisible(false);
        }
    }

    private void initTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {        // không cho sửa trực tiếp trên bảng
                return false;
            }
        };
        tblProd.setModel(tableModel);
        tblProd.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblProd.getTableHeader().setReorderingAllowed(false);

        // Màu xen kẽ dòng chẵn/lẻ
        tblProd.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    setBackground(new java.awt.Color(99, 102, 241));  // tím xanh khi chọn
                    setForeground(java.awt.Color.WHITE);
                } else if (row % 2 == 0) {
                    setBackground(java.awt.Color.WHITE);              // dòng chẵn trắng
                    setForeground(java.awt.Color.BLACK);
                } else {
                    setBackground(new java.awt.Color(240, 240, 248)); // dòng lẻ xám nhạt
                    setForeground(java.awt.Color.BLACK);
                }
                return this;
            }
        });
        tblProd.setRowHeight(28); // tăng chiều cao dòng cho dễ đọc
    }

    private void initEvents() {
        // tìm kiếm khi bấm nút "Search"
        butSearch.addActionListener(e -> doSearch());
        txtSearchProd.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                doSearch();
            }
        });

        butAdd.addActionListener(e -> {
            ProdDialog dialog = new ProdDialog(null, null);
            dialog.setVisible(true);
            if (dialog.isSaved()) loadData(ProductDB.getAll());
        });

        butChange.addActionListener(e -> {
            Product selected = getSelectedProduct();
            if (selected == null) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn sản phẩm cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            ProdDialog dialog = new ProdDialog(null, selected);
            dialog.setVisible(true);
            if (dialog.isSaved()) loadData(ProductDB.getAll());
        });

        butDelete.addActionListener(e -> {
            Product selected = getSelectedProduct();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Xóa sản phẩm \"" + selected.getName() + "\"?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = ProductDB.delete(selected.getProductId());
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    loadData(ProductDB.getAll());
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại! Sản phẩm có thể đang được dùng trong hóa đơn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        butExportCSV.addActionListener(e ->
                ExportUtil.exportCSV(this, tblProd, "product"));
        // Cố định chiều cao toolbar
        pnlToolbar.setPreferredSize(new Dimension(0, 50));
        pnlToolbar.setMinimumSize(new Dimension(0, 50));
        pnlToolbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Tăng kích thước button toolbar
        Font btnFont = new Font("Segoe UI", Font.PLAIN, 18);
        txtSearchProd.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        butSearch.setFont(btnFont);
        butAdd.setFont(btnFont);
        butChange.setFont(btnFont);
        butDelete.setFont(btnFont);
        if (getComponentCount() > 0) butExportCSV.setFont(btnFont);
    }

    private void doSearch() {
        String keyword = txtSearchProd.getText().trim();
        List<Product> result = keyword.isEmpty()
                ? ProductDB.getAll()
                : ProductDB.search(keyword);
        loadData(result);
    }

    public void loadData(List<Product> list) {
        tableModel.setRowCount(0);                     // xóa dữ liệu cũ
        for (Product p : list) {
            tableModel.addRow(new Object[]{
                    p.getProductId(),
                    p.getName(),
                    p.getBrand(),
                    p.getSize(),
                    p.getColor(),
                    p.getPriceDisplay(),
                    p.getStock(),
                    p.getStatusDisplay()
            });
        }
        lblProdStatus.setText("Tổng: " + list.size() + " sản phẩm");
    }

    public void refresh() {
        doSearch(); // tự động load lại theo từ khóa hiện tại
    }

    private Product getSelectedProduct() {
        int row = tblProd.getSelectedRow();
        if (row == -1) return null;

        int productId = (int) tableModel.getValueAt(row, 0);
        String name   = (String) tableModel.getValueAt(row, 1);
        String brand  = (String) tableModel.getValueAt(row, 2);
        String size   = (String) tableModel.getValueAt(row, 3);
        String color  = (String) tableModel.getValueAt(row, 4);
        int stock     = (int) tableModel.getValueAt(row, 6);
        String status = "Đang bán".equals(tableModel.getValueAt(row, 7))
                ? "ACTIVE" : "INACTIVE";
        Product p = new Product();
        p.setProductId(productId);
        p.setName(name);
        p.setBrand(brand);
        p.setSize(size);
        p.setColor(color);
        p.setStock(stock);
        p.setStatus(status);
        return p;
    }
}