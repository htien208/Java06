package com.shop.db;

import com.shop.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDB {

    public static List<Customer> findByPhone(String phone) {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customer WHERE phone LIKE ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + phone + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("[CustomerDB.findByPhone] Lỗi: " + e.getMessage());
        }
        return list;
    }


    public static boolean add(Customer c) {
        String sql = "INSERT INTO customer (full_name, phone, address) VALUES (?, ?, ?)";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getFullName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getAddress() != null ? c.getAddress() : "");
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[CustomerDB.add] Lỗi: " + e.getMessage());
            return false;
        }
    }


    public static List<Customer> getAll() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customer ORDER BY full_name";

        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("[CustomerDB.getAll] Lỗi: " + e.getMessage());
        }
        return list;
    }


    public static boolean update(Customer c) {
        String sql = "UPDATE customer SET full_name=?, phone=?, address=? WHERE customer_id=?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getFullName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getAddress() != null ? c.getAddress() : "");
            ps.setInt(4, c.getCustomerId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[CustomerDB.update] Lỗi: " + e.getMessage());
            return false;
        }
    }

    private static Customer mapRow(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getInt("customer_id"));
        c.setFullName(rs.getString("full_name"));
        c.setPhone(rs.getString("phone"));
        c.setAddress(rs.getString("address"));
        return c;
    }
}