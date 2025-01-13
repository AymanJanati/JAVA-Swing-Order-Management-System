package com.gestion.commandes.gui;
import javax.swing.table.DefaultTableModel;

import com.gestion.commandes.database.CommandeDAO;
import com.gestion.commandes.database.ProduitDAO;
import com.gestion.commandes.models.Commande;
import com.gestion.commandes.models.LigneCommande;
import com.gestion.commandes.models.Produit;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddCommandeDialog extends JDialog {
    private JTextField dateField;
    private JTextField idClientField;
    private JButton saveButton;
    private CommandePanel commandePanel;
    private JTable ligneTable;
    private DefaultTableModel ligneTableModel;

    public AddCommandeDialog(JFrame parent, CommandePanel commandePanel) {
        super(parent, "Ajouter une Commande", true);
        this.commandePanel = commandePanel;

        setSize(500, 400);
        setLocationRelativeTo(parent);

        // Create form fields
        dateField = new JTextField(20);
        idClientField = new JTextField(20);

        // Create a table for order lines
        ligneTableModel = new DefaultTableModel(new String[]{"ID Produit", "Quantité", "Sous-Total"}, 0);
        ligneTable = new JTable(ligneTableModel);
        JScrollPane ligneScrollPane = new JScrollPane(ligneTable);

        // Create buttons for adding lines
        JButton addLigneButton = new JButton("Ajouter Ligne");
        addLigneButton.addActionListener(e -> addLigne());

        // Create save button
        saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> saveCommande());

        // Add components to the dialog
        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(2, 2));
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        formPanel.add(dateField);
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
        // Open a dialog to add a new order line
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

    private void saveCommande() {
        String date = dateField.getText();
        int idClient;

        // Validate date format (YYYY-MM-DD)
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer une date valide au format YYYY-MM-DD.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            idClient = Integer.parseInt(idClientField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un ID client valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create a new Commande object
        Commande commande = new Commande(0, date, idClient, new ArrayList<>());

        // Add order lines from the table
        for (int i = 0; i < ligneTableModel.getRowCount(); i++) {
            int idProduit = (int) ligneTableModel.getValueAt(i, 0);
            int quantite = (int) ligneTableModel.getValueAt(i, 1);
            double sousTotal = (double) ligneTableModel.getValueAt(i, 2);
            commande.getLignesCommande().add(new LigneCommande(0, 0, idProduit, quantite, sousTotal));
        }

        CommandeDAO commandeDAO = new CommandeDAO();

        try {
            commandeDAO.addCommande(commande);
            JOptionPane.showMessageDialog(this, "Commande ajoutée avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
            commandePanel.loadCommandes(); // Refresh the order list
            dispose(); // Close the dialog
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de la commande.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}