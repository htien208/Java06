package com.shop.ui;

import com.shop.db.CustomerDB;
import com.shop.model.Customer;
import com.shop.util.SessionManager;
import com.shop.util.ExportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;



public class CusPanel extends JPanel {

    private JPanel CusPanel;
    private JPanel pnlToolbar;
    private JLabel lblSearchByPhone;
    private JTextField txtSearchByPhone;
    private JButton butSearch;
    private JButton butAdd;
    private JButton butEdit;
    private JButton butDelete;
    private JPanel pnlInfor;
    private JLabel lblCusCount;
    private JScrollPane scrollCus;
    private JTable tblCus;
    private JButton butExportCSV;

    private DefaultTableModel tableModel;
    private static final String[] COLUMNS = {
            "ID", "Họ và tên", "Số điện thoại", "Địa chỉ"
    };

    public CusPanel() {
        setLayout(new BorderLayout());
        add(CusPanel, BorderLayout.CENTER);

        initTable();
        initEvents();
        loadData(CustomerDB.getAll());

        // Ẩn nút Xóa nếu không phải MANAGER
        if (!SessionManager.isManager()) {
            butDelete.setVisible(false);
        }
    }

    private void initTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblCus.setModel(tableModel);
        tblCus.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCus.getTableHeader().setReorderingAllowed(false);

        // Màu xen kẽ dòng chẵn/lẻ
        tblCus.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
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
        tblCus.setRowHeight(28); // tăng chiều cao dòng cho dễ đọc
        // Ẩn cột ID
//        tblCus.getColumnModel().getColumn(0).setMinWidth(0);
//        tblCus.getColumnModel().getColumn(0).setMaxWidth(0);
//        tblCus.getColumnModel().getColumn(0).setWidth(0);
        pnlToolbar.setPreferredSize(new Dimension(0, 50));
        pnlToolbar.setMinimumSize(new Dimension(0, 50));
        pnlToolbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
    }

    private void initEvents() {
        // Tìm kiếm theo SĐT
        butSearch.addActionListener(e -> doSearch());

        // Realtime search khi gõ
        txtSearchByPhone.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { doSearch(); }
        });

        // Thêm khách hàng
        butAdd.addActionListener(e -> {
            JDialog dialog = new JDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    "Thêm khách hàng", true);
            CusDialog panel = new CusDialog(null);
            dialog.setContentPane(panel);
            dialog.setSize(420, 300);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            if (panel.isSaved()) loadData(CustomerDB.getAll());
        });

        // sửa khách hàng
        butEdit.addActionListener(e -> {
            Customer selected = getSelectedCustomer();
            if (selected == null) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn khách hàng cần sửa!");
                return;
            }
            JDialog dialog = new JDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    "Sửa thông tin khách hàng", true);
            CusDialog panel = new CusDialog(selected);
            dialog.setContentPane(panel);
            dialog.setSize(420, 300);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            if (panel.isSaved()) loadData(CustomerDB.getAll());
        });

        butDelete.addActionListener(e -> {
            Customer selected = getSelectedCustomer();
            if (selected == null) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn khách hàng cần xóa!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Xóa khách hàng \"" + selected.getFullName() + "\"?\n"
                            + "Lưu ý: Các hóa đơn liên quan vẫn được giữ lại.",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this,
                        "Chức năng đang phát triển!", "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        tblCus.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) butEdit.doClick();
            }
        });

        butExportCSV.addActionListener(e ->
                ExportUtil.exportCSV(this, tblCus, "Customer"));

        Font btnFont = new Font("Segoe UI", Font.PLAIN, 18);
        txtSearchByPhone.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        butSearch.setFont(btnFont);
        butAdd.setFont(btnFont);
        butEdit.setFont(btnFont);
        butDelete.setFont(btnFont);
        if (getComponentCount() > 0) butExportCSV.setFont(btnFont);
    }

    private void doSearch() {
        String phone = txtSearchByPhone.getText().trim();
        List<Customer> result = phone.isEmpty()
                ? CustomerDB.getAll()
                : CustomerDB.findByPhone(phone);
        loadData(result);
    }

    public void loadData(List<Customer> list) {
        tableModel.setRowCount(0);
        for (Customer c : list) {
            tableModel.addRow(new Object[]{
                    c.getCustomerId(),
                    c.getFullName(),
                    c.getPhone(),
                    c.getAddress() != null ? c.getAddress() : ""
            });
        }
        lblCusCount.setText("Tổng: " + list.size() + " khách hàng");
    }

    private Customer getSelectedCustomer() {
        int row = tblCus.getSelectedRow();
        if (row == -1) return null;
        Customer c = new Customer();
        c.setCustomerId((int) tableModel.getValueAt(row, 0));
        c.setFullName(tableModel.getValueAt(row, 1).toString());
        c.setPhone(tableModel.getValueAt(row, 2).toString());
        c.setAddress(tableModel.getValueAt(row, 3).toString());
        return c;
    }
}