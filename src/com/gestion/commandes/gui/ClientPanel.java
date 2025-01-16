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
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UIManager.getColor("Panel.background")); // Theme-aware background

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

        // Style the table for the current theme
        clientTable.setBackground(UIManager.getColor("Table.background")); // Theme-aware background
        clientTable.setForeground(UIManager.getColor("Table.foreground")); // Theme-aware text color
        clientTable.getTableHeader().setBackground(UIManager.getColor("TableHeader.background")); // Theme-aware header background
        clientTable.getTableHeader().setForeground(UIManager.getColor("TableHeader.foreground")); // Theme-aware header text color
        clientTable.setGridColor(UIManager.getColor("Table.gridColor")); // Theme-aware grid color

        JScrollPane scrollPane = new JScrollPane(clientTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove border
        add(scrollPane, BorderLayout.CENTER);

        // Load clients from the database
        loadClients();

        // Create buttons for CRUD operations
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UIManager.getColor("Panel.background")); // Theme-aware background
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add padding

        JButton addButton = createButton("Ajouter", UIManager.getColor("Button.background")); // Theme-aware button color
        JButton editButton = createButton("Modifier", UIManager.getColor("Button.background")); // Theme-aware button color
        JButton deleteButton = createButton("Supprimer", UIManager.getColor("Button.background")); // Theme-aware button color

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
        button.setForeground(UIManager.getColor("Button.foreground")); // Theme-aware text color
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 40));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(UIManager.getColor("Button.hoverBackground")); // Theme-aware hover color
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

    public void refreshUI() {
        // Refresh the panel's appearance
        setBackground(UIManager.getColor("Panel.background")); // Theme-aware background

        // Refresh the table's appearance
        clientTable.setBackground(UIManager.getColor("Table.background")); // Theme-aware background
        clientTable.setForeground(UIManager.getColor("Table.foreground")); // Theme-aware text color
        clientTable.getTableHeader().setBackground(UIManager.getColor("TableHeader.background")); // Theme-aware header background
        clientTable.getTableHeader().setForeground(UIManager.getColor("TableHeader.foreground")); // Theme-aware header text color
        clientTable.setGridColor(UIManager.getColor("Table.gridColor")); // Theme-aware grid color

        // Refresh buttons
        for (Component component : getComponents()) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                panel.setBackground(UIManager.getColor("Panel.background")); // Theme-aware background
                for (Component button : panel.getComponents()) {
                    if (button instanceof JButton) {
                        JButton btn = (JButton) button;
                        btn.setBackground(UIManager.getColor("Button.background")); // Theme-aware button color
                        btn.setForeground(UIManager.getColor("Button.foreground")); // Theme-aware text color
                    }
                }
            }
        }

        revalidate();
        repaint();
    }
}