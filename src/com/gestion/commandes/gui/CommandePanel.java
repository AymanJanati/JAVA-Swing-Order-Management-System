package com.gestion.commandes.gui;

import com.gestion.commandes.database.CommandeDAO;
import com.gestion.commandes.models.Commande;
import com.gestion.commandes.models.LigneCommande;
import com.gestion.commandes.utils.PDFExporter;
import com.gestion.commandes.utils.CSVExporter;

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
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UIManager.getColor("Panel.background")); // Theme-aware background

        // Initialize the DAO
        commandeDAO = new CommandeDAO();

        // Create a table model with columns: ID, Date, ID Client
        tableModel = new DefaultTableModel(new String[]{"ID", "Date", "ID Client"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make the table non-editable
            }
        };
        commandeTable = new JTable(tableModel);
        commandeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only single row selection
        commandeTable.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Set font
        commandeTable.setRowHeight(30); // Increase row height for better readability
        commandeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14)); // Set header font

        // Style the table for the current theme
        commandeTable.setBackground(UIManager.getColor("Table.background")); // Theme-aware background
        commandeTable.setForeground(UIManager.getColor("Table.foreground")); // Theme-aware text color
        commandeTable.getTableHeader().setBackground(UIManager.getColor("TableHeader.background")); // Theme-aware header background
        commandeTable.getTableHeader().setForeground(UIManager.getColor("TableHeader.foreground")); // Theme-aware header text color
        commandeTable.setGridColor(UIManager.getColor("Table.gridColor")); // Theme-aware grid color

        JScrollPane scrollPane = new JScrollPane(commandeTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove border
        add(scrollPane, BorderLayout.CENTER);

        // Load orders from the database
        loadCommandes();

        // Create buttons for CRUD operations
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UIManager.getColor("Panel.background")); // Theme-aware background
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add padding

        JButton addButton = createButton("Ajouter", UIManager.getColor("Button.background")); // Theme-aware button color
        JButton editButton = createButton("Modifier", UIManager.getColor("Button.background")); // Theme-aware button color
        JButton deleteButton = createButton("Supprimer", UIManager.getColor("Button.background")); // Theme-aware button color
        JButton exportPDFButton = createButton("Exporter en PDF", UIManager.getColor("Button.background")); // Theme-aware button color
        JButton exportCSVButton = createButton("Exporter en CSV", UIManager.getColor("Button.background")); // Theme-aware button color

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

    public void refreshUI() {
        // Refresh the panel's appearance
        setBackground(UIManager.getColor("Panel.background")); // Theme-aware background

        // Refresh the table's appearance
        commandeTable.setBackground(UIManager.getColor("Table.background")); // Theme-aware background
        commandeTable.setForeground(UIManager.getColor("Table.foreground")); // Theme-aware text color
        commandeTable.getTableHeader().setBackground(UIManager.getColor("TableHeader.background")); // Theme-aware header background
        commandeTable.getTableHeader().setForeground(UIManager.getColor("TableHeader.foreground")); // Theme-aware header text color
        commandeTable.setGridColor(UIManager.getColor("Table.gridColor")); // Theme-aware grid color

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