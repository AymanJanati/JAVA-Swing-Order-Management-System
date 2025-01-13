package com.gestion.commandes.database;

import com.gestion.commandes.models.Produit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {
    // Method to add a product
    public void addProduit(Produit produit) throws SQLException {
        String query = "INSERT INTO produits (nom, prix, quantiteEnStock) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, produit.getNom());
            statement.setDouble(2, produit.getPrix());
            statement.setInt(3, produit.getQuantiteEnStock());
            statement.executeUpdate();
        }
    }

    // Method to get all products
    public List<Produit> getAllProduits() throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT * FROM produits";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int idProduit = resultSet.getInt("idProduit");
                String nom = resultSet.getString("nom");
                double prix = resultSet.getDouble("prix");
                int quantiteEnStock = resultSet.getInt("quantiteEnStock");
                produits.add(new Produit(idProduit, nom, prix, quantiteEnStock));
            }
        }
        return produits;
    }

    // Method to update a product
    public void updateProduit(Produit produit) throws SQLException {
        String query = "UPDATE produits SET nom = ?, prix = ?, quantiteEnStock = ? WHERE idProduit = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, produit.getNom());
            statement.setDouble(2, produit.getPrix());
            statement.setInt(3, produit.getQuantiteEnStock());
            statement.setInt(4, produit.getIdProduit());
            statement.executeUpdate();
        }
    }

    // Method to delete a product
    public void deleteProduit(int idProduit) throws SQLException {
        String query = "DELETE FROM produits WHERE idProduit = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idProduit);
            statement.executeUpdate();
        }
    }

    public Produit getProduitById(int idProduit) throws SQLException {
        String query = "SELECT * FROM produits WHERE idProduit = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idProduit);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String nom = resultSet.getString("nom");
                    double prix = resultSet.getDouble("prix");
                    int quantiteEnStock = resultSet.getInt("quantiteEnStock");
                    return new Produit(idProduit, nom, prix, quantiteEnStock);
                }
            }
        }
        return null; // Product not found
    }
}