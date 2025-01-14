package com.gestion.commandes.gui;

import com.gestion.commandes.database.FactureDAO;
import com.gestion.commandes.models.Facture;
import com.gestion.commandes.models.LigneFacture;
import com.gestion.commandes.utils.PDFExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class FacturePanel extends JPanel {
    private JTable factureTable;
    private DefaultTableModel tableModel;
    private FactureDAO factureDAO;
    private JTextField discountField; // New field for discount input

    public FacturePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 18)); // Updated to dark theme background: #121212

        // Initialize the DAO
        factureDAO = new FactureDAO();

        // Create a table model with columns: ID, Date, Montant Total, ID Client, Discount
        tableModel = new DefaultTableModel(new String[]{"ID", "Date", "Montant Total", "ID Client", "Remise (%)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make the table non-editable
            }
        };
        factureTable = new JTable(tableModel);
        factureTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only single row selection
        factureTable.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Set font
        factureTable.setRowHeight(30); // Increase row height for better readability
        factureTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14)); // Set header font

        // Style the table for dark theme
        factureTable.setBackground(new Color(37, 37, 38)); // Table background: #252526
        factureTable.setForeground(Color.WHITE); // Text color: White
        factureTable.getTableHeader().setBackground(new Color(52, 21, 57)); // Header background: #341539
        factureTable.getTableHeader().setForeground(Color.WHITE); // Header text color: White
        factureTable.setGridColor(new Color(58, 58, 58)); // Grid color: #3A3A3A

        JScrollPane scrollPane = new JScrollPane(factureTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove border
        add(scrollPane, BorderLayout.CENTER);

        // Load invoices from the database
        loadFactures();

        // Create buttons for CRUD operations
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(18, 18, 18)); // Updated to dark theme background: #121212
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add padding

        JButton addButton = createButton("Ajouter", new Color(52, 21, 57)); // Accent color: #341539
        JButton editButton = createButton("Modifier", new Color(44, 62, 80)); // Secondary color: #2C3E50
        JButton deleteButton = createButton("Supprimer", new Color(231, 76, 60)); // Highlight color: #E74C3C
        JButton exportButton = createButton("Exporter en PDF", new Color(52, 152, 219)); // Export button color: #3498DB

        // Add discount input field
        discountField = new JTextField(10); // For entering discount percentage
        discountField.setBackground(new Color(37, 37, 38)); // Input field background: #252526
        discountField.setForeground(Color.WHITE); // Input field text color: White
        discountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        discountField.setBorder(BorderFactory.createLineBorder(new Color(58, 58, 58))); // Border color: #3A3A3A

        buttonPanel.add(new JLabel("Remise (%):"));
        buttonPanel.add(discountField);

        // Add action listeners
        addButton.addActionListener(e -> {
            AddFactureDialog dialog = new AddFactureDialog((JFrame) SwingUtilities.getWindowAncestor(this), this);
            dialog.setVisible(true);
        });

        editButton.addActionListener(e -> {
            int selectedRow = factureTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une facture à modifier.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the selected invoice's ID
            int idFacture = (int) factureTable.getValueAt(selectedRow, 0);
            String date = (String) factureTable.getValueAt(selectedRow, 1);
            double montantTotal = (double) factureTable.getValueAt(selectedRow, 2);
            int idClient = (int) factureTable.getValueAt(selectedRow, 3);
            double discount = (double) factureTable.getValueAt(selectedRow, 4); // Get discount from the table

            // Fetch the invoice lines from the database
            try {
                List<LigneFacture> lignes = factureDAO.getLignesFacture(idFacture);
                Facture facture = new Facture(idFacture, date, montantTotal, idClient, lignes, discount); // Include discount

                // Open the EditFactureDialog
                EditFactureDialog dialog = new EditFactureDialog((JFrame) SwingUtilities.getWindowAncestor(this), this, facture);
                dialog.setVisible(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des lignes de facture.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = factureTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une facture à supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the selected invoice's ID
            int idFacture = (int) factureTable.getValueAt(selectedRow, 0);

            // Confirm deletion
            int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer cette facture?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    factureDAO.deleteFacture(idFacture);
                    JOptionPane.showMessageDialog(this, "Facture supprimée avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    loadFactures(); // Refresh the invoice list
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de la facture.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add PDF export functionality
        exportButton.addActionListener(e -> exportToPDF());

        // Add buttons to the panel
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE); // Text color: White
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 40));

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

    public void loadFactures() {
        try {
            List<Facture> factures = factureDAO.getAllFactures();
            tableModel.setRowCount(0); // Clear the table
            for (Facture facture : factures) {
                tableModel.addRow(new Object[]{
                        facture.getIdFacture(),
                        facture.getDate(),
                        facture.getMontantTotal(),
                        facture.getIdClient(),
                        facture.getDiscount() // Add discount to the table
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des factures.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportToPDF() {
        StringBuilder content = new StringBuilder("Liste des Factures:\n\n");
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            content.append("ID: ").append(tableModel.getValueAt(i, 0)).append("\n");
            content.append("Date: ").append(tableModel.getValueAt(i, 1)).append("\n");
            content.append("Montant Total: ").append(tableModel.getValueAt(i, 2)).append("\n");
            content.append("ID Client: ").append(tableModel.getValueAt(i, 3)).append("\n");
            content.append("Remise (%): ").append(tableModel.getValueAt(i, 4)).append("\n\n"); // Add discount to the PDF
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer le fichier PDF");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.endsWith(".pdf")) {
                filePath += ".pdf";
            }
            PDFExporter.exportToPDF(filePath, content.toString());
            JOptionPane.showMessageDialog(this, "Exportation réussie!", "Succès", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}