package com.shop.util;

import com.shop.model.User;


public class SessionManager {
    private static User currentUser = null;

    private SessionManager() {}

    public static void login(User user) {
        currentUser = user;
    }    //luu user vào session

    public static User getCurrentUser() {
        return currentUser;
    }    //tra ve user dang login, null neu chua login

    public static boolean isLoggedIn() {
        return currentUser != null;
    }    //check login

    public static boolean isManager() {    //kiem tra xem co phai QL khong
        return currentUser != null
                && "MANAGER".equalsIgnoreCase(currentUser.getRole());
    }

    public static boolean isStaff() {     //kiem tra xem co phai staff khong
        return currentUser != null
                && "STAFF".equalsIgnoreCase(currentUser.getRole());
    }

    public static void logout() {
        currentUser = null;
    }
}

