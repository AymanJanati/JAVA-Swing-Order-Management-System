package com.gestion.commandes.database;

import com.gestion.commandes.models.Commande;
import com.gestion.commandes.models.LigneCommande;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommandeDAO {
    // Method to add an order
    public void addCommande(Commande commande) throws SQLException {
        String query = "INSERT INTO commandes (date, idClient) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, commande.getDate());
            statement.setInt(2, commande.getIdClient());
            statement.executeUpdate();

            // Get the generated order ID
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int idCommande = generatedKeys.getInt(1);

                    // Add order lines
                    for (LigneCommande ligne : commande.getLignesCommande()) {
                        addLigneCommande(idCommande, ligne);
                    }
                }
            }
        }
    }

    // Method to add an order line
    private void addLigneCommande(int idCommande, LigneCommande ligne) throws SQLException {
        String query = "INSERT INTO lignes_commande (idCommande, idProduit, quantite, sousTotal) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idCommande);
            statement.setInt(2, ligne.getIdProduit());
            statement.setInt(3, ligne.getQuantite());
            statement.setDouble(4, ligne.getSousTotal());
            statement.executeUpdate();
        }
    }

    // Method to get all orders
    public List<Commande> getAllCommandes() throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String query = "SELECT * FROM commandes";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int idCommande = resultSet.getInt("idCommande");
                String date = resultSet.getString("date");
                int idClient = resultSet.getInt("idClient");

                // Get order lines
                List<LigneCommande> lignes = getLignesCommande(idCommande);
                commandes.add(new Commande(idCommande, date, idClient, lignes));
            }
        }
        return commandes;
    }

    // Method to get order lines
    public List<LigneCommande> getLignesCommande(int idCommande) throws SQLException {
        List<LigneCommande> lignes = new ArrayList<>();
        String query = "SELECT * FROM lignes_commande WHERE idCommande = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idCommande);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int idLigneCommande = resultSet.getInt("idLigneCommande");
                    int idProduit = resultSet.getInt("idProduit");
                    int quantite = resultSet.getInt("quantite");
                    double sousTotal = resultSet.getDouble("sousTotal");
                    lignes.add(new LigneCommande(idLigneCommande, idCommande, idProduit, quantite, sousTotal));
                }
            }
        }
        return lignes;
    }

    // Method to update an order
    public void updateCommande(Commande commande) throws SQLException {
        // Update the order
        String query = "UPDATE commandes SET date = ?, idClient = ? WHERE idCommande = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, commande.getDate());
            statement.setInt(2, commande.getIdClient());
            statement.setInt(3, commande.getIdCommande());
            statement.executeUpdate();
        }

        // Delete existing order lines
        String deleteQuery = "DELETE FROM lignes_commande WHERE idCommande = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setInt(1, commande.getIdCommande());
            statement.executeUpdate();
        }

        // Add updated order lines
        for (LigneCommande ligne : commande.getLignesCommande()) {
            addLigneCommande(commande.getIdCommande(), ligne);
        }
    }

    // Method to delete an order
    public void deleteCommande(int idCommande) throws SQLException {
        // Delete order lines first
        String deleteLignesQuery = "DELETE FROM lignes_commande WHERE idCommande = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteLignesQuery)) {
            statement.setInt(1, idCommande);
            statement.executeUpdate();
        }

        // Delete the order
        String deleteCommandeQuery = "DELETE FROM commandes WHERE idCommande = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteCommandeQuery)) {
            statement.setInt(1, idCommande);
            statement.executeUpdate();
        }
    }
}