package com.shop.ui;

import com.shop.db.InvoiceDB;
import com.shop.model.Invoice;
import com.shop.model.InvoiceDetail;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.shop.util.ExportUtil;

public class InvoicePanel extends JPanel {

    private JPanel InvoicePanel;

    private JPanel pnlSearchByTime;
    private JLabel lblTimeFrom;
    private JTextField txtTimeFrom;
    private JLabel lblTimeTo;
    private JTextField txtTimeTo;
    private JButton butSearchByTime;
    private JButton butAllInvoice;

    private JPanel pnlInforInvoice;
    private JButton butDeleteInvoice;
    private JButton butViewInvoiceDetail;
    private JLabel lblInvoiceFromTo;
    private JLabel lblTotal;
    private JLabel lblTotalInvoice;
    private JButton butMakeInvoice;
    private JScrollPane scrollInvoice;
    private JTable tblInvoice;
    private JButton butExportCSV;

    private DefaultTableModel tableModel;
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String[] COLUMNS = {
            "Mã HĐ", "Khách hàng", "Tổng tiền", "Ngày tạo"
    };

    public InvoicePanel() {
        setLayout(new BorderLayout());
        add(InvoicePanel, BorderLayout.CENTER);

        initTable();
        initEvents();

        String today = LocalDate.now().format(DATE_FMT);
        txtTimeFrom.setText("01/01/2025");
        txtTimeTo.setText(today);

        loadData(InvoiceDB.getAll());
    }

    private void initTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblInvoice.setModel(tableModel);
        tblInvoice.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblInvoice.getTableHeader().setReorderingAllowed(false);

        // Wrap JTable vào JScrollPane — tìm panel cha của tblInvoice
        // KHÔNG đụng vào pnlInforInvoice để tránh mất các nút
        if (tblInvoice.getParent() != null
                && !(tblInvoice.getParent() instanceof JViewport)) {
            Container parent = tblInvoice.getParent();
            parent.setLayout(new BorderLayout());
            parent.add(new JScrollPane(tblInvoice), BorderLayout.CENTER);
        }
    }

    private void initEvents() {

        butSearchByTime.addActionListener(e -> doSearchByTime());


        butAllInvoice.addActionListener(e -> {
            txtTimeFrom.setText("01/01/2025");
            txtTimeTo.setText(LocalDate.now().format(DATE_FMT));
            loadData(InvoiceDB.getAll());
        });


        butMakeInvoice.addActionListener(e -> {
            JDialog dialog = new JDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    "Tạo hóa đơn mới", true);
            InvoiceDetailPanel panel = new InvoiceDetailPanel();
            dialog.setContentPane(panel);
            dialog.setSize(1000, 650);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

            loadData(InvoiceDB.getAll());    //load lại sau khi đóng dialog
        });


        butViewInvoiceDetail.addActionListener(e -> doViewDetail());

        //  xóa hóa đơn, đang phát triển
        butDeleteInvoice.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Chức năng đang phát triển!", "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE));

        tblInvoice.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) doViewDetail();
            }
        });

        butExportCSV.addActionListener(e ->
                ExportUtil.exportCSV(this, tblInvoice, "Invoice"));

        Font btnFont = new Font("Segoe UI", Font.PLAIN, 18);
        txtTimeFrom.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtTimeTo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        butSearchByTime.setFont(btnFont);
        butAllInvoice.setFont(btnFont);
        butMakeInvoice.setFont(btnFont);
        butViewInvoiceDetail.setFont(btnFont);
        butDeleteInvoice.setFont(btnFont);
    }

    private void doSearchByTime() {
        String fromStr = txtTimeFrom.getText().trim();
        String toStr   = txtTimeTo.getText().trim();

        if (fromStr.isEmpty() || toStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đủ ngày bắt đầu và kết thúc!");
            return;
        }

        try {
            LocalDate from = LocalDate.parse(fromStr, DATE_FMT);
            LocalDate to   = LocalDate.parse(toStr, DATE_FMT);

            if (from.isAfter(to)) {
                JOptionPane.showMessageDialog(this,
                        "Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc!");
                return;
            }

            lblInvoiceFromTo.setText("Đang hiển thị hóa đơn từ "
                    + fromStr + " đến " + toStr);
            loadData(InvoiceDB.getByDate(from, to));

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Định dạng ngày không hợp lệ!\nVui lòng nhập theo dạng dd/MM/yyyy",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doViewDetail() {
        int row = tblInvoice.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn hóa đơn cần xem!");
            return;
        }

        int invoiceId       = (int) tableModel.getValueAt(row, 0);
        String customerName = tableModel.getValueAt(row, 1).toString();
        String total        = tableModel.getValueAt(row, 2).toString();
        String date         = tableModel.getValueAt(row, 3).toString();

        List<InvoiceDetail> details = InvoiceDB.getDetailsByInvoiceId(invoiceId);

        StringBuilder sb = new StringBuilder();
        sb.append("  HÓA ĐƠN #").append(invoiceId).append("\n");
        sb.append("══════════════════════════════\n");
        sb.append("Khách hàng : ").append(customerName).append("\n");
        sb.append("Ngày tạo   : ").append(date).append("\n");
        sb.append("──────────────────────────────\n");

        for (InvoiceDetail d : details) {
            sb.append(String.format("%-20s x%d  %s%n",
                    d.getProductName(), d.getQuantity(), d.getSubtotalDisplay()));
        }
        sb.append("──────────────────────────────\n");
        sb.append("TỔNG TIỀN  : ").append(total).append("\n");

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setEditable(false);
        textArea.setBackground(UIManager.getColor("Panel.background"));

        JOptionPane.showMessageDialog(this,
                new JScrollPane(textArea),
                "Chi tiết hóa đơn #" + invoiceId,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void loadData(List<Invoice> list) {
        tableModel.setRowCount(0);
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Invoice inv : list) {
            tableModel.addRow(new Object[]{
                    inv.getInvoiceId(),
                    inv.getCustomerName(),
                    inv.getTotalDisplay(),
                    inv.getCreatedAtDisplay()
            });
            if (inv.getTotal() != null) {
                totalAmount = totalAmount.add(inv.getTotal());
            }
        }

        lblTotalInvoice.setText("Tổng: " + list.size() + " hóa đơn");
        lblTotal.setText("Tổng giá trị: " + String.format("%,.0f ₫", totalAmount));
    }
}