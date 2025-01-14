package com.gestion.commandes.gui;

import com.gestion.commandes.database.ClientDAO;
import com.gestion.commandes.models.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ClientPanel extends JPanel {
    private JTable clientTable;
    private DefaultTableModel tableModel;
    private ClientDAO clientDAO;

    public ClientPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 18)); // Updated to dark theme background: #121212

        // Initialize the DAO
        clientDAO = new ClientDAO();

        // Create a table model with columns: ID, Nom, Email, Téléphone
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Email", "Téléphone"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make the table non-editable
            }
        };
        clientTable = new JTable(tableModel);
        clientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only single row selection
        clientTable.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Set font
        clientTable.setRowHeight(30); // Increase row height for better readability
        clientTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14)); // Set header font

        // Style the table for dark theme
        clientTable.setBackground(new Color(37, 37, 38)); // Table background: #252526
        clientTable.setForeground(Color.WHITE); // Text color: White
        clientTable.getTableHeader().setBackground(new Color(52, 21, 57)); // Header background: #341539
        clientTable.getTableHeader().setForeground(Color.WHITE); // Header text color: White
        clientTable.setGridColor(new Color(58, 58, 58)); // Grid color: #3A3A3A

        JScrollPane scrollPane = new JScrollPane(clientTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove border
        add(scrollPane, BorderLayout.CENTER);

        // Load clients from the database
        loadClients();

        // Create buttons for CRUD operations
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(18, 18, 18)); // Updated to dark theme background: #121212
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add padding

        JButton addButton = createButton("Ajouter", new Color(52, 21, 57)); // Accent color: #341539
        JButton editButton = createButton("Modifier", new Color(44, 62, 80)); // Secondary color: #2C3E50
        JButton deleteButton = createButton("Supprimer", new Color(231, 76, 60)); // Highlight color: #E74C3C

        // Add action listeners
        addButton.addActionListener(e -> {
            AddClientDialog dialog = new AddClientDialog((JFrame) SwingUtilities.getWindowAncestor(this), this);
            dialog.setVisible(true);
        });

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
                    clientDAO.deleteClient(idClient);
                    JOptionPane.showMessageDialog(this, "Client supprimé avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    loadClients(); // Refresh the client list
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du client.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add buttons to the panel
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE); // Text color: White
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 40));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter()); // Slightly lighter on hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color); // Restore original color
            }
        });

        return button;
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