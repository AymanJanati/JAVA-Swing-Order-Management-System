package com.gestion.commandes.gui;

import com.gestion.commandes.database.ProduitDAO;
import com.gestion.commandes.models.Produit;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List; // Correct import

public class ProduitPanel extends JPanel {
    private JTable produitTable;
    private DefaultTableModel tableModel;
    private ProduitDAO produitDAO;

    public ProduitPanel() {
        setLayout(new BorderLayout());

        // Initialize the DAO
        produitDAO = new ProduitDAO();

        // Initialize the table model
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Prix", "Quantité en Stock"}, 0);
        produitTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(produitTable);
        add(scrollPane, BorderLayout.CENTER);

        // Load products from the database
        loadProduits();

        // Create buttons for CRUD operations
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Ajouter");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");

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

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadProduits() {
        try {
            List<Produit> produits = produitDAO.getAllProduits(); // Use java.util.List
            tableModel.setRowCount(0); // Clear the table
            for (Produit produit : produits) { // Use foreach loop
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