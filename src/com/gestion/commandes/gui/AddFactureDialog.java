package com.gestion.commandes.gui;

import com.gestion.commandes.database.FactureDAO;
import com.gestion.commandes.database.ProduitDAO;
import com.gestion.commandes.models.Facture;
import com.gestion.commandes.models.LigneFacture;
import com.gestion.commandes.models.Produit;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class AddFactureDialog extends JDialog {
    private JTextField dateField;
    private JTextField montantTotalField;
    private JTextField idClientField;
    private JTextField discountField; // New field for discount input
    private JButton saveButton;
    private FacturePanel facturePanel;
    private JTable ligneTable;
    private DefaultTableModel ligneTableModel;

    public AddFactureDialog(JFrame parent, FacturePanel facturePanel) {
        super(parent, "Ajouter une Facture", true);
        this.facturePanel = facturePanel;

        setSize(500, 400);
        setLocationRelativeTo(parent);

        // Create form fields
        dateField = new JTextField(20);
        montantTotalField = new JTextField(20);
        idClientField = new JTextField(20);
        discountField = new JTextField(20); // New field for discount

        // Create a table for invoice lines
        ligneTableModel = new DefaultTableModel(new String[]{"ID Produit", "Quantité", "Sous-Total"}, 0);
        ligneTable = new JTable(ligneTableModel);
        JScrollPane ligneScrollPane = new JScrollPane(ligneTable);

        // Create buttons for adding lines
        JButton addLigneButton = new JButton("Ajouter Ligne");
        addLigneButton.addActionListener(e -> addLigne());

        // Create save button
        saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> saveFacture());

        // Add components to the dialog
        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2)); // Updated to 4 rows for the discount field
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Montant Total:"));
        formPanel.add(montantTotalField);
        formPanel.add(new JLabel("ID Client:"));
        formPanel.add(idClientField);
        formPanel.add(new JLabel("Remise (%):")); // New label for discount
        formPanel.add(discountField); // New field for discount

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(ligneScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addLigneButton);
        buttonPanel.add(saveButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private void addLigne() {
        // Open a dialog to add a new invoice line
        String idProduitStr = JOptionPane.showInputDialog(this, "Entrez l'ID du produit:");
        String quantiteStr = JOptionPane.showInputDialog(this, "Entrez la quantité:");

        try {
            int idProduit = Integer.parseInt(idProduitStr);
            int quantite = Integer.parseInt(quantiteStr);

            // Fetch the product price from the database
            ProduitDAO produitDAO = new ProduitDAO();
            Produit produit = produitDAO.getProduitById(idProduit);

            if (produit == null) {
                JOptionPane.showMessageDialog(this, "Produit non trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double sousTotal = produit.getPrix() * quantite;

            // Add the line to the table
            ligneTableModel.addRow(new Object[]{idProduit, quantite, sousTotal});
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs valides.", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération du produit.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveFacture() {
        String date = dateField.getText();
        double montantTotal;
        int idClient;
        double discount;

        // Validate date format (YYYY-MM-DD)
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer une date valide au format YYYY-MM-DD.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            montantTotal = Double.parseDouble(montantTotalField.getText());
            idClient = Integer.parseInt(idClientField.getText());
            discount = Double.parseDouble(discountField.getText()) / 100.0; // Convert percentage to decimal
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un montant total, un ID client et une remise valides.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create a new Facture object with the discount
        Facture facture = new Facture(0, date, montantTotal, idClient, new ArrayList<>(), discount);

        // Add invoice lines from the table
        for (int i = 0; i < ligneTableModel.getRowCount(); i++) {
            int idProduit = (int) ligneTableModel.getValueAt(i, 0);
            int quantite = (int) ligneTableModel.getValueAt(i, 1);
            double sousTotal = (double) ligneTableModel.getValueAt(i, 2);
            facture.getLignesFacture().add(new LigneFacture(0, 0, idProduit, quantite, sousTotal));
        }

        FactureDAO factureDAO = new FactureDAO();

        try {
            factureDAO.addFacture(facture);
            JOptionPane.showMessageDialog(this, "Facture ajoutée avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
            facturePanel.loadFactures(); // Refresh the invoice list
            dispose(); // Close the dialog
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de la facture.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}