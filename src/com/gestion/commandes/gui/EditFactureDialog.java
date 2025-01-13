package com.gestion.commandes.gui;

import javax.swing.table.DefaultTableModel;
import com.gestion.commandes.database.FactureDAO;
import com.gestion.commandes.database.ProduitDAO;
import com.gestion.commandes.models.Facture;
import com.gestion.commandes.models.Produit;
import com.gestion.commandes.models.LigneFacture;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditFactureDialog extends JDialog {
    private JTextField dateField;
    private JTextField montantTotalField;
    private JTextField idClientField;
    private JButton saveButton;
    private FacturePanel facturePanel;
    private Facture facture;
    private JTable ligneTable;
    private DefaultTableModel ligneTableModel;

    public EditFactureDialog(JFrame parent, FacturePanel facturePanel, Facture facture) {
        super(parent, "Modifier une Facture", true);
        this.facturePanel = facturePanel;
        this.facture = facture;

        setSize(500, 400);
        setLocationRelativeTo(parent);

        // Create form fields and pre-fill them with the invoice's details
        dateField = new JTextField(facture.getDate(), 20);
        montantTotalField = new JTextField(String.valueOf(facture.getMontantTotal()), 20);
        idClientField = new JTextField(String.valueOf(facture.getIdClient()), 20);

        // Create a table for invoice lines
        ligneTableModel = new DefaultTableModel(new String[]{"ID Produit", "Quantité", "Sous-Total"}, 0);
        ligneTable = new JTable(ligneTableModel);
        JScrollPane ligneScrollPane = new JScrollPane(ligneTable);

        // Pre-fill the table with existing invoice lines
        for (LigneFacture ligne : facture.getLignesFacture()) {
            ligneTableModel.addRow(new Object[]{ligne.getIdProduit(), ligne.getQuantite(), ligne.getSousTotal()});
        }

        // Create buttons for adding lines
        JButton addLigneButton = new JButton("Ajouter Ligne");
        addLigneButton.addActionListener(e -> addLigne());

        // Create save button
        saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> saveFacture());

        // Add components to the dialog
        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Montant Total:"));
        formPanel.add(montantTotalField);
        formPanel.add(new JLabel("ID Client:"));
        formPanel.add(idClientField);

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

        // Validate date format (YYYY-MM-DD)
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer une date valide au format YYYY-MM-DD.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            montantTotal = Double.parseDouble(montantTotalField.getText());
            idClient = Integer.parseInt(idClientField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un montant total et un ID client valides.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update the invoice object
        facture.setDate(date);
        facture.setMontantTotal(montantTotal);
        facture.setIdClient(idClient);

        // Update invoice lines
        List<LigneFacture> lignes = new ArrayList<>();
        for (int i = 0; i < ligneTableModel.getRowCount(); i++) {
            int idProduit = (int) ligneTableModel.getValueAt(i, 0);
            int quantite = (int) ligneTableModel.getValueAt(i, 1);
            double sousTotal = (double) ligneTableModel.getValueAt(i, 2);
            lignes.add(new LigneFacture(0, facture.getIdFacture(), idProduit, quantite, sousTotal));
        }
        facture.setLignesFacture(lignes);

        FactureDAO factureDAO = new FactureDAO();

        try {
            factureDAO.updateFacture(facture);
            JOptionPane.showMessageDialog(this, "Facture modifiée avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
            facturePanel.loadFactures(); // Refresh the invoice list
            dispose(); // Close the dialog
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification de la facture.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}