package com.shop.model;

import java.time.LocalDateTime;

public class User {

    private int userId;
    private String username;
    private String password;
    private String fullName;
    private String role;
    private LocalDateTime createdAt;

    public User() {}

    public User(int userId, String username, String password,
                String fullName, String role, LocalDateTime createdAt) {
        this.userId    = userId;
        this.username  = username;
        this.password  = password;
        this.fullName  = fullName;
        this.role      = role;
        this.createdAt = createdAt;
    }

    public int getUserId()               { return userId; }
    public void setUserId(int userId)    { this.userId = userId; }

    public String getUsername()                  { return username; }
    public void setUsername(String username)     { this.username = username; }

    public String getPassword()                  { return password; }
    public void setPassword(String password)     { this.password = password; }

    public String getFullName()                  { return fullName; }
    public void setFullName(String fullName)     { this.fullName = fullName; }

    public String getRole()                      { return role; }
    public void setRole(String role)             { this.role = role; }

    public LocalDateTime getCreatedAt()                      { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)        { this.createdAt = createdAt; }

    // Hiển thị role tiếng Việt — dùng trong UI
    public String getRoleDisplay() {
        return "MANAGER".equalsIgnoreCase(role) ? "Quản lý" : "Nhân viên";
    }

    @Override
    public String toString() {
        return fullName + " (" + getRoleDisplay() + ")";
    }
}
