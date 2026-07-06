package com.shop.db;

import com.shop.model.Product;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ProductDB - Thao tác với bảng `product`
 */
public class ProductDB {

    /**
     * Lấy toàn bộ danh sách sản phẩm
     */
    public static List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM product ORDER BY product_id DESC";

        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("[ProductDB.getAll] Lỗi: " + e.getMessage());
        }
        return list;
    }

    public static List<Product> search(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM product "
                + "WHERE name LIKE ? OR brand LIKE ? "
                + "ORDER BY product_id DESC";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("[ProductDB.search] Lỗi: " + e.getMessage());
        }
        return list;
    }

    /**
     * Thêm sản phẩm mới
     */
    public static boolean add(Product p) {
        String sql = "INSERT INTO product (name, brand, size, color, price, stock, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getBrand());
            ps.setString(3, p.getSize());
            ps.setString(4, p.getColor());
            ps.setBigDecimal(5, p.getPrice());
            ps.setInt(6, p.getStock());
            ps.setString(7, p.getStatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[ProductDB.add] Lỗi: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cập nhật sản phẩm
     */
    public static boolean update(Product p) {
        String sql = "UPDATE product SET name=?, brand=?, size=?, color=?, "
                + "price=?, stock=?, status=? WHERE product_id=?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getBrand());
            ps.setString(3, p.getSize());
            ps.setString(4, p.getColor());
            ps.setBigDecimal(5, p.getPrice());
            ps.setInt(6, p.getStock());
            ps.setString(7, p.getStatus());
            ps.setInt(8, p.getProductId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[ProductDB.update] Lỗi: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa sản phẩm theo ID
     */
    public static boolean delete(int productId) {
        String sql = "DELETE FROM product WHERE product_id = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[ProductDB.delete] Lỗi: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cập nhật tồn kho — dùng khi tạo hóa đơn
     */
    public static boolean updateStock(int productId, int newStock) {
        String sql = "UPDATE product SET stock = ? WHERE product_id = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newStock);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[ProductDB.updateStock] Lỗi: " + e.getMessage());
            return false;
        }
    }

    /**
     * Map ResultSet → Product object
     */
    private static Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setName(rs.getString("name"));
        p.setBrand(rs.getString("brand"));
        p.setSize(rs.getString("size"));
        p.setColor(rs.getString("color"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setStock(rs.getInt("stock"));
        p.setStatus(rs.getString("status"));
        return p;
    }
}