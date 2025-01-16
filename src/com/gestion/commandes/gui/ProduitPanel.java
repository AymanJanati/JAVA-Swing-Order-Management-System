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
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UIManager.getColor("Panel.background")); // Theme-aware background

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

        // Style the table for the current theme
        produitTable.setBackground(UIManager.getColor("Table.background")); // Theme-aware background
        produitTable.setForeground(UIManager.getColor("Table.foreground")); // Theme-aware text color
        produitTable.getTableHeader().setBackground(UIManager.getColor("TableHeader.background")); // Theme-aware header background
        produitTable.getTableHeader().setForeground(UIManager.getColor("TableHeader.foreground")); // Theme-aware header text color
        produitTable.setGridColor(UIManager.getColor("Table.gridColor")); // Theme-aware grid color

        JScrollPane scrollPane = new JScrollPane(produitTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove border
        add(scrollPane, BorderLayout.CENTER);

        // Load products from the database
        loadProduits();

        // Create buttons for CRUD operations
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UIManager.getColor("Panel.background")); // Theme-aware background
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add padding

        JButton addButton = createButton("Ajouter", UIManager.getColor("Button.background")); // Theme-aware button color
        JButton editButton = createButton("Modifier", UIManager.getColor("Button.background")); // Theme-aware button color
        JButton deleteButton = createButton("Supprimer", UIManager.getColor("Button.background")); // Theme-aware button color

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

    public void refreshUI() {
        // Refresh the panel's appearance
        setBackground(UIManager.getColor("Panel.background")); // Theme-aware background

        // Refresh the table's appearance
        produitTable.setBackground(UIManager.getColor("Table.background")); // Theme-aware background
        produitTable.setForeground(UIManager.getColor("Table.foreground")); // Theme-aware text color
        produitTable.getTableHeader().setBackground(UIManager.getColor("TableHeader.background")); // Theme-aware header background
        produitTable.getTableHeader().setForeground(UIManager.getColor("TableHeader.foreground")); // Theme-aware header text color
        produitTable.setGridColor(UIManager.getColor("Table.gridColor")); // Theme-aware grid color

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