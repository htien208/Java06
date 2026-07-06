package com.shop.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DATABASE = "quanlycuahang";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&allowPublicKeyRetrieval=true";

    private static Connection instance = null;  //khoi tao instance

    private DBConnect() {}     //khoi tao ket noi

    public static Connection getConnection() {
        try {
            if (instance == null || instance.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                instance = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("[DBConnect] Ket noi database thanh cong");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnect] Không tìm thấy MySQL Driver");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("[DBConnect] Lỗi kết nối database: " + e.getMessage());
            e.printStackTrace();
        }
        return instance;
    }

    public static void closeConnection() {
        if (instance != null) {
            try {
                instance.close();
                instance = null;
                System.out.println("[DBConnect] Da ngat ket noi database");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
