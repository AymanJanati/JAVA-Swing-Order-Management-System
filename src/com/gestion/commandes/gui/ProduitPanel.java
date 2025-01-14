package com.gestion.commandes.gui;

import com.gestion.commandes.database.ProduitDAO;
import com.gestion.commandes.models.Produit;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ProduitPanel extends JPanel {
    private JTable produitTable;
    private DefaultTableModel tableModel;
    private ProduitDAO produitDAO;

    public ProduitPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 18)); // Updated to dark theme background: #121212

        // Initialize the DAO
        produitDAO = new ProduitDAO();

        // Create a table model with columns: ID, Nom, Prix, Quantité en Stock
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Prix", "Quantité en Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make the table non-editable
            }
        };
        produitTable = new JTable(tableModel);
        produitTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only single row selection
        produitTable.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Set font
        produitTable.setRowHeight(30); // Increase row height for better readability
        produitTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14)); // Set header font

        // Style the table for dark theme
        produitTable.setBackground(new Color(37, 37, 38)); // Table background: #252526
        produitTable.setForeground(Color.WHITE); // Text color: White
        produitTable.getTableHeader().setBackground(new Color(52, 21, 57)); // Header background: #341539
        produitTable.getTableHeader().setForeground(Color.WHITE); // Header text color: White
        produitTable.setGridColor(new Color(58, 58, 58)); // Grid color: #3A3A3A

        JScrollPane scrollPane = new JScrollPane(produitTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove border
        add(scrollPane, BorderLayout.CENTER);

        // Load products from the database
        loadProduits();

        // Create buttons for CRUD operations
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(18, 18, 18)); // Updated to dark theme background: #121212
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add padding

        JButton addButton = createButton("Ajouter", new Color(52, 21, 57)); // Accent color: #341539
        JButton editButton = createButton("Modifier", new Color(44, 62, 80)); // Secondary color: #2C3E50
        JButton deleteButton = createButton("Supprimer", new Color(231, 76, 60)); // Highlight color: #E74C3C

        // Add action listeners
        addButton.addActionListener(e -> {
            AddProduitDialog dialog = new AddProduitDialog((JFrame) SwingUtilities.getWindowAncestor(this), this);
            dialog.setVisible(true);
        });

        editButton.addActionListener(e -> {
            int selectedRow = produitTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit à modifier.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the selected product's ID
            int idProduit = (int) produitTable.getValueAt(selectedRow, 0);
            String nom = (String) produitTable.getValueAt(selectedRow, 1);
            double prix = (double) produitTable.getValueAt(selectedRow, 2);
            int quantiteEnStock = (int) produitTable.getValueAt(selectedRow, 3);

            // Create a Produit object with the selected data
            Produit produit = new Produit(idProduit, nom, prix, quantiteEnStock);

            // Open the EditProduitDialog
            EditProduitDialog dialog = new EditProduitDialog((JFrame) SwingUtilities.getWindowAncestor(this), this, produit);
            dialog.setVisible(true);
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = produitTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit à supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the selected product's ID
            int idProduit = (int) produitTable.getValueAt(selectedRow, 0);

            // Confirm deletion
            int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer ce produit?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    produitDAO.deleteProduit(idProduit);
                    JOptionPane.showMessageDialog(this, "Produit supprimé avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    loadProduits(); // Refresh the product list
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du produit.", "Erreur", JOptionPane.ERROR_MESSAGE);
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

    public void loadProduits() {
        try {
            List<Produit> produits = produitDAO.getAllProduits();
            tableModel.setRowCount(0); // Clear the table
            for (Produit produit : produits) {
                tableModel.addRow(new Object[]{
                        produit.getIdProduit(),
                        produit.getNom(),
                        produit.getPrix(),
                        produit.getQuantiteEnStock()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des produits.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}