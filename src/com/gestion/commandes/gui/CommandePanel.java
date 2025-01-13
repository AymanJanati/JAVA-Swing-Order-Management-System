package com.gestion.commandes.gui;

import com.gestion.commandes.database.CommandeDAO;
import com.gestion.commandes.models.Commande;
import com.gestion.commandes.models.LigneCommande;
import com.gestion.commandes.utils.PDFExporter;
import com.gestion.commandes.utils.CSVExporter; // Import the CSVExporter

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CommandePanel extends JPanel {
    private JTable commandeTable;
    private DefaultTableModel tableModel;
    private CommandeDAO commandeDAO;

    public CommandePanel() {
        setLayout(new BorderLayout());

        // Initialize the DAO
        commandeDAO = new CommandeDAO();

        // Create a table model with columns: ID, Date, ID Client
        tableModel = new DefaultTableModel(new String[]{"ID", "Date", "ID Client"}, 0);
        commandeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(commandeTable);
        add(scrollPane, BorderLayout.CENTER);

        // Load orders from the database
        loadCommandes();

        // Create buttons for CRUD operations
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Ajouter");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");
        JButton exportPDFButton = new JButton("Exporter en PDF");
        JButton exportCSVButton = new JButton("Exporter en CSV"); // New CSV export button

        // Add action listeners
        addButton.addActionListener(e -> {
            AddCommandeDialog dialog = new AddCommandeDialog((JFrame) SwingUtilities.getWindowAncestor(this), this);
            dialog.setVisible(true);
        });

        editButton.addActionListener(e -> {
            int selectedRow = commandeTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une commande à modifier.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the selected order's ID
            int idCommande = (int) commandeTable.getValueAt(selectedRow, 0);
            String date = (String) commandeTable.getValueAt(selectedRow, 1);
            int idClient = (int) commandeTable.getValueAt(selectedRow, 2);

            // Fetch the order lines from the database
            try {
                List<LigneCommande> lignes = commandeDAO.getLignesCommande(idCommande);
                Commande commande = new Commande(idCommande, date, idClient, lignes);

                // Open the EditCommandeDialog
                EditCommandeDialog dialog = new EditCommandeDialog((JFrame) SwingUtilities.getWindowAncestor(this), this, commande);
                dialog.setVisible(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des lignes de commande.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = commandeTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une commande à supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the selected order's ID
            int idCommande = (int) commandeTable.getValueAt(selectedRow, 0);

            // Confirm deletion
            int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer cette commande?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    commandeDAO.deleteCommande(idCommande);
                    JOptionPane.showMessageDialog(this, "Commande supprimée avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    loadCommandes(); // Refresh the order list
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de la commande.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add PDF export functionality
        exportPDFButton.addActionListener(e -> exportToPDF());

        // Add CSV export functionality
        exportCSVButton.addActionListener(e -> exportToCSV());

        // Add buttons to the panel
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportPDFButton);
        buttonPanel.add(exportCSVButton); // Add the CSV export button

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadCommandes() {
        try {
            List<Commande> commandes = commandeDAO.getAllCommandes();
            tableModel.setRowCount(0); // Clear the table
            for (Commande commande : commandes) {
                tableModel.addRow(new Object[]{
                        commande.getIdCommande(),
                        commande.getDate(),
                        commande.getIdClient()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des commandes.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportToPDF() {
        StringBuilder content = new StringBuilder("Liste des Commandes:\n\n");
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            content.append("ID: ").append(tableModel.getValueAt(i, 0)).append("\n");
            content.append("Date: ").append(tableModel.getValueAt(i, 1)).append("\n");
            content.append("ID Client: ").append(tableModel.getValueAt(i, 2)).append("\n\n");
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

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer le fichier CSV");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.endsWith(".csv")) {
                filePath += ".csv";
            }
            CSVExporter.exportToCSV(commandeTable, filePath);
            JOptionPane.showMessageDialog(this, "Exportation CSV réussie!", "Succès", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}