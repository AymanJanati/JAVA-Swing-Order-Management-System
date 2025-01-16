package com.gestion.commandes.utils;

public class PasswordHasher {
    public static void main(String[] args) {
        String password = "AymanRoot"; // Plain-text password
        String hashedPassword = PasswordUtil.hashPassword(password); // Hash the password
        System.out.println("Hashed Password: " + hashedPassword);
    }
}
