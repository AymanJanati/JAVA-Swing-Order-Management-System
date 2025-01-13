package com.gestion.commandes.database;

import com.gestion.commandes.models.Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {
    // Method to add a client
    public void addClient(Client client) throws SQLException {
        String query = "INSERT INTO clients (nom, email, telephone) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, client.getNom());
            statement.setString(2, client.getEmail());
            statement.setString(3, client.getTelephone());
            statement.executeUpdate();
        }
    }

    // Method to get all clients
    public List<Client> getAllClients() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM clients";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int idClient = resultSet.getInt("idClient");
                String nom = resultSet.getString("nom");
                String email = resultSet.getString("email");
                String telephone = resultSet.getString("telephone");
                clients.add(new Client(idClient, nom, email, telephone));
            }
        }
        return clients;
    }

    // Method to update a client
    public void updateClient(Client client) throws SQLException {
        String query = "UPDATE clients SET nom = ?, email = ?, telephone = ? WHERE idClient = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, client.getNom());
            statement.setString(2, client.getEmail());
            statement.setString(3, client.getTelephone());
            statement.setInt(4, client.getIdClient());
            statement.executeUpdate();
        }
    }

    // Method to delete a client
    public void deleteClient(int idClient) throws SQLException {
        String query = "DELETE FROM clients WHERE idClient = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idClient);
            statement.executeUpdate();
        }
    }
}