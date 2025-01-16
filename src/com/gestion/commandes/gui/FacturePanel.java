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
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UIManager.getColor("Panel.background")); // Theme-aware background

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

        // Style the table for the current theme
        factureTable.setBackground(UIManager.getColor("Table.background")); // Theme-aware background
        factureTable.setForeground(UIManager.getColor("Table.foreground")); // Theme-aware text color
        factureTable.getTableHeader().setBackground(UIManager.getColor("TableHeader.background")); // Theme-aware header background
        factureTable.getTableHeader().setForeground(UIManager.getColor("TableHeader.foreground")); // Theme-aware header text color
        factureTable.setGridColor(UIManager.getColor("Table.gridColor")); // Theme-aware grid color

        JScrollPane scrollPane = new JScrollPane(factureTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove border
        add(scrollPane, BorderLayout.CENTER);

        // Load invoices from the database
        loadFactures();

        // Create buttons for CRUD operations
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UIManager.getColor("Panel.background")); // Theme-aware background
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add padding

        JButton addButton = createButton("Ajouter", UIManager.getColor("Button.background")); // Theme-aware button color
        JButton editButton = createButton("Modifier", UIManager.getColor("Button.background")); // Theme-aware button color
        JButton deleteButton = createButton("Supprimer", UIManager.getColor("Button.background")); // Theme-aware button color
        JButton exportButton = createButton("Exporter en PDF", UIManager.getColor("Button.background")); // Theme-aware button color

        // Add discount input field
        discountField = new JTextField(10); // For entering discount percentage
        discountField.setBackground(UIManager.getColor("TextField.background")); // Theme-aware background
        discountField.setForeground(UIManager.getColor("TextField.foreground")); // Theme-aware text color
        discountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        discountField.setBorder(BorderFactory.createLineBorder(UIManager.getColor("TextField.borderColor"))); // Theme-aware border color

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
        button.setForeground(UIManager.getColor("Button.foreground")); // Theme-aware text color
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 40));

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

    public void refreshUI() {
        // Refresh the panel's appearance
        setBackground(UIManager.getColor("Panel.background")); // Theme-aware background

        // Refresh the table's appearance
        factureTable.setBackground(UIManager.getColor("Table.background")); // Theme-aware background
        factureTable.setForeground(UIManager.getColor("Table.foreground")); // Theme-aware text color
        factureTable.getTableHeader().setBackground(UIManager.getColor("TableHeader.background")); // Theme-aware header background
        factureTable.getTableHeader().setForeground(UIManager.getColor("TableHeader.foreground")); // Theme-aware header text color
        factureTable.setGridColor(UIManager.getColor("Table.gridColor")); // Theme-aware grid color

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

        // Refresh the discount field
        discountField.setBackground(UIManager.getColor("TextField.background")); // Theme-aware background
        discountField.setForeground(UIManager.getColor("TextField.foreground")); // Theme-aware text color
        discountField.setBorder(BorderFactory.createLineBorder(UIManager.getColor("TextField.borderColor"))); // Theme-aware border color

        revalidate();
        repaint();
    }
}