package com.gestion.commandes;

import com.gestion.commandes.database.DatabaseConnection;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try {
            // Test the database connection
            Connection connection = DatabaseConnection.getConnection();
            if (connection != null) {
                System.out.println("Connected to the database!");
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}