package com.gestion.commandes.database;

import com.gestion.commandes.models.Facture;
import com.gestion.commandes.models.LigneFacture;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FactureDAO {
    // Method to add an invoice
    public void addFacture(Facture facture) throws SQLException {
        String query = "INSERT INTO factures (date, montantTotal, idClient) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, facture.getDate());
            statement.setDouble(2, facture.getMontantTotal());
            statement.setInt(3, facture.getIdClient());
            statement.executeUpdate();

            // Get the generated invoice ID
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int idFacture = generatedKeys.getInt(1);

                    // Add invoice lines
                    for (LigneFacture ligne : facture.getLignesFacture()) {
                        addLigneFacture(idFacture, ligne);
                    }
                }
            }
        }
    }

    // Method to add an invoice line
    private void addLigneFacture(int idFacture, LigneFacture ligne) throws SQLException {
        String query = "INSERT INTO lignes_facture (idFacture, idProduit, quantite, sousTotal) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idFacture);
            statement.setInt(2, ligne.getIdProduit());
            statement.setInt(3, ligne.getQuantite());
            statement.setDouble(4, ligne.getSousTotal());
            statement.executeUpdate();
        }
    }

    // Method to get all invoices
    public List<Facture> getAllFactures() throws SQLException {
        List<Facture> factures = new ArrayList<>();
        String query = "SELECT * FROM factures";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int idFacture = resultSet.getInt("idFacture");
                String date = resultSet.getString("date");
                double montantTotal = resultSet.getDouble("montantTotal");
                int idClient = resultSet.getInt("idClient");

                // Get invoice lines
                List<LigneFacture> lignes = getLignesFacture(idFacture);
                factures.add(new Facture(idFacture, date, montantTotal, idClient, lignes));
            }
        }
        return factures;
    }

    // Method to get invoice lines
    public List<LigneFacture> getLignesFacture(int idFacture) throws SQLException {
        List<LigneFacture> lignes = new ArrayList<>();
        String query = "SELECT * FROM lignes_facture WHERE idFacture = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idFacture);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int idLigne = resultSet.getInt("idLigne");
                    int idProduit = resultSet.getInt("idProduit");
                    int quantite = resultSet.getInt("quantite");
                    double sousTotal = resultSet.getDouble("sousTotal");
                    lignes.add(new LigneFacture(idLigne, idFacture, idProduit, quantite, sousTotal));
                }
            }
        }
        return lignes;
    }

    public void updateFacture(Facture facture) throws SQLException {
        // Update the invoice
        String query = "UPDATE factures SET date = ?, montantTotal = ?, idClient = ? WHERE idFacture = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, facture.getDate());
            statement.setDouble(2, facture.getMontantTotal());
            statement.setInt(3, facture.getIdClient());
            statement.setInt(4, facture.getIdFacture());
            statement.executeUpdate();
        }

        // Delete existing invoice lines
        String deleteQuery = "DELETE FROM lignes_facture WHERE idFacture = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setInt(1, facture.getIdFacture());
            statement.executeUpdate();
        }

        // Add updated invoice lines
        for (LigneFacture ligne : facture.getLignesFacture()) {
            addLigneFacture(facture.getIdFacture(), ligne);
        }
    }

    public void deleteFacture(int idFacture) throws SQLException {
        // Delete invoice lines first
        String deleteLignesQuery = "DELETE FROM lignes_facture WHERE idFacture = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteLignesQuery)) {
            statement.setInt(1, idFacture);
            statement.executeUpdate();
        }

        // Delete the invoice
        String deleteFactureQuery = "DELETE FROM factures WHERE idFacture = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteFactureQuery)) {
            statement.setInt(1, idFacture);
            statement.executeUpdate();
        }
    }
}