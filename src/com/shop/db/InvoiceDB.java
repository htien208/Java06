package com.shop.db;

import com.shop.model.Invoice;
import com.shop.model.InvoiceDetail;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDB {

    public static boolean save(Invoice invoice) {
        String sqlInvoice = "INSERT INTO invoice (user_id, customer_id, total, note, created_at) "
                + "VALUES (?, ?, ?, ?, NOW())";
        String sqlDetail  = "INSERT INTO invoice_detail (invoice_id, product_id, quantity, unit_price, subtotal) "
                + "VALUES (?, ?, ?, ?, ?)";
        String sqlStock   = "UPDATE product SET stock = stock - ? WHERE product_id = ?";

        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // Bước 1: INSERT invoice → lấy invoiceId vừa tạo
            PreparedStatement psInvoice = conn.prepareStatement(
                    sqlInvoice, Statement.RETURN_GENERATED_KEYS);
            psInvoice.setInt(1, invoice.getUserId());
            if (invoice.getCustomerId() != null) {
                psInvoice.setInt(2, invoice.getCustomerId());
            } else {
                psInvoice.setNull(2, Types.INTEGER);
            }
            psInvoice.setBigDecimal(3, invoice.getTotal());
            psInvoice.setString(4, invoice.getNote());
            psInvoice.executeUpdate();

            // Lấy invoiceId vừa insert
            ResultSet generatedKeys = psInvoice.getGeneratedKeys();
            if (!generatedKeys.next()) throw new SQLException("Không lấy được invoiceId!");
            int invoiceId = generatedKeys.getInt(1);

            // Bước 2 + 3: INSERT từng detail + trừ tồn kho
            PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
            PreparedStatement psStock  = conn.prepareStatement(sqlStock);

            for (InvoiceDetail detail : invoice.getDetails()) {
                // INSERT invoice_detail
                psDetail.setInt(1, invoiceId);
                psDetail.setInt(2, detail.getProductId());
                psDetail.setInt(3, detail.getQuantity());
                psDetail.setBigDecimal(4, detail.getUnitPrice());
                psDetail.setBigDecimal(5, detail.getSubtotal());
                psDetail.executeUpdate();

                // Trừ tồn kho
                psStock.setInt(1, detail.getQuantity());
                psStock.setInt(2, detail.getProductId());
                psStock.executeUpdate();
            }

            conn.commit(); // Tất cả OK → commit
            return true;

        } catch (SQLException e) {
            System.err.println("[InvoiceDB.save] Lỗi: " + e.getMessage());
            try {
                if (conn != null) conn.rollback(); // Lỗi → rollback
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true); // Reset về mặc định
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Invoice> getAll() {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT i.*, c.full_name AS customer_name "
                + "FROM invoice i "
                + "LEFT JOIN customer c ON i.customer_id = c.customer_id "
                + "ORDER BY i.created_at DESC";

        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("[InvoiceDB.getAll] Lỗi: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy chi tiết các dòng sản phẩm của 1 hóa đơn
     */
    public static List<InvoiceDetail> getDetailsByInvoiceId(int invoiceId) {
        List<InvoiceDetail> list = new ArrayList<>();
        String sql = "SELECT d.*, p.name AS product_name "
                + "FROM invoice_detail d "
                + "JOIN product p ON d.product_id = p.product_id "
                + "WHERE d.invoice_id = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                InvoiceDetail d = new InvoiceDetail();
                d.setDetailId(rs.getInt("detail_id"));
                d.setInvoiceId(rs.getInt("invoice_id"));
                d.setProductId(rs.getInt("product_id"));
                d.setProductName(rs.getString("product_name"));
                d.setQuantity(rs.getInt("quantity"));
                d.setUnitPrice(rs.getBigDecimal("unit_price"));
                d.setSubtotal(rs.getBigDecimal("subtotal"));
                list.add(d);
            }

        } catch (SQLException e) {
            System.err.println("[InvoiceDB.getDetailsByInvoiceId] Lỗi: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lọc hóa đơn theo khoảng ngày — dùng cho báo cáo
     */
    public static List<Invoice> getByDate(LocalDate from, LocalDate to) {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT i.*, c.full_name AS customer_name "
                + "FROM invoice i "
                + "LEFT JOIN customer c ON i.customer_id = c.customer_id "
                + "WHERE DATE(i.created_at) BETWEEN ? AND ? "
                + "ORDER BY i.created_at DESC";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("[InvoiceDB.getByDate] Lỗi: " + e.getMessage());
        }
        return list;
    }

    /**
     * Map ResultSet → Invoice object
     */
    private static Invoice mapRow(ResultSet rs) throws SQLException {
        Invoice inv = new Invoice();
        inv.setInvoiceId(rs.getInt("invoice_id"));
        inv.setUserId(rs.getInt("user_id"));

        int customerId = rs.getInt("customer_id");
        if (!rs.wasNull()) inv.setCustomerId(customerId);

        inv.setCustomerName(rs.getString("customer_name") != null
                ? rs.getString("customer_name") : "Khách lẻ");
        inv.setTotal(rs.getBigDecimal("total"));
        inv.setNote(rs.getString("note"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) inv.setCreatedAt(ts.toLocalDateTime());

        return inv;
    }
}