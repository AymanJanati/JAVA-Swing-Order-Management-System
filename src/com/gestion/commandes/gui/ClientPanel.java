package com.gestion.commandes.gui;

import com.gestion.commandes.database.ClientDAO;
import com.gestion.commandes.models.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import com.gestion.commandes.models.Client;


public class ClientPanel extends JPanel {
    private JTable clientTable;
    private DefaultTableModel tableModel;
    private ClientDAO clientDAO;

    public ClientPanel() {
        setLayout(new BorderLayout());

        // Initialize the DAO
        clientDAO = new ClientDAO();

        // Create a table model with columns: ID, Nom, Email, Téléphone
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Email", "Téléphone"}, 0);
        clientTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(clientTable);
        add(scrollPane, BorderLayout.CENTER);

        // Load clients from the database
        loadClients();

        // Create buttons for CRUD operations
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Ajouter");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");

        // Add action listeners
        addButton.addActionListener(e -> {
            AddClientDialog dialog = new AddClientDialog((JFrame) SwingUtilities.getWindowAncestor(this), this);
            dialog.setVisible(true);
        });

        // Update the ActionListener for the "Modifier" button
        editButton.addActionListener(e -> {
            int selectedRow = clientTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client à modifier.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the selected client's ID
            int idClient = (int) clientTable.getValueAt(selectedRow, 0);
            String nom = (String) clientTable.getValueAt(selectedRow, 1);
            String email = (String) clientTable.getValueAt(selectedRow, 2);
            String telephone = (String) clientTable.getValueAt(selectedRow, 3);

            // Create a Client object with the selected data
            Client client = new Client(idClient, nom, email, telephone);

            // Open the EditClientDialog
            EditClientDialog dialog = new EditClientDialog((JFrame) SwingUtilities.getWindowAncestor(this), this, client);
            dialog.setVisible(true);
        });

        // Update the ActionListener for the "Supprimer" button
        deleteButton.addActionListener(e -> {
            int selectedRow = clientTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client à supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the selected client's ID
            int idClient = (int) clientTable.getValueAt(selectedRow, 0);

            // Confirm deletion
            int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer ce client?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    ClientDAO clientDAO = new ClientDAO();
                    clientDAO.deleteClient(idClient);
                    JOptionPane.showMessageDialog(this, "Client supprimé avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    loadClients(); // Refresh the client list
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du client.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadClients() {
        try {
            List<Client> clients = clientDAO.getAllClients();
            tableModel.setRowCount(0); // Clear the table
            for (Client client : clients) {
                tableModel.addRow(new Object[]{
                        client.getIdClient(),
                        client.getNom(),
                        client.getEmail(),
                        client.getTelephone()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des clients.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}