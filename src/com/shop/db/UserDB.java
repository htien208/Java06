package com.shop.db;

import com.shop.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserDB {
    public static User login(String username, String password) {
        String sql = "SELECT * FROM user WHERE username = ? AND password = ? LIMIT 1";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            System.err.println("[UserDB.login] Lỗi: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lấy toàn bộ danh sách nhân viên
     */
    public static List<User> getAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user ORDER BY created_at DESC";

        try (Connection conn = DBConnect.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("[UserDB.getAll] Lỗi: " + e.getMessage());
        }
        return list;
    }

    /**
     * Thêm nhân viên mới
     */
    public static boolean add(User user) {
        String sql = "INSERT INTO user (username, password, full_name, role, created_at) "
                   + "VALUES (?, ?, ?, ?, NOW())";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getRole());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[UserDB.add] Lỗi: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cập nhật thông tin nhân viên
     */
    public static boolean update(User user) {
        String sql = "UPDATE user SET full_name = ?, role = ?, password = ? "
                   + "WHERE user_id = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getRole());
            ps.setString(3, user.getPassword());
            ps.setInt(4, user.getUserId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[UserDB.update] Lỗi: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa nhân viên theo ID
     */
    public static boolean delete(int userId) {
        String sql = "DELETE FROM user WHERE user_id = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[UserDB.delete] Lỗi: " + e.getMessage());
            return false;
        }
    }

    private static User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setFullName(rs.getString("full_name"));
        u.setRole(rs.getString("role"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) u.setCreatedAt(ts.toLocalDateTime());
        return u;
    }
}
