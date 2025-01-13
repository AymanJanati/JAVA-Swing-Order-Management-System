package com.gestion.commandes.gui;

import com.gestion.commandes.models.Produit;
import com.gestion.commandes.database.ProduitDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class EditProduitDialog extends JDialog {
    private JTextField nomField;
    private JTextField prixField;
    private JTextField quantiteField;
    private JButton saveButton;
    private ProduitPanel produitPanel;
    private Produit produit;

    public EditProduitDialog(JFrame parent, ProduitPanel produitPanel, Produit produit) {
        super(parent, "Modifier un Produit", true);
        this.produitPanel = produitPanel;
        this.produit = produit;

        setSize(300, 200);
        setLocationRelativeTo(parent);

        // Create form fields and pre-fill them with the product's details
        nomField = new JTextField(produit.getNom(), 20);
        prixField = new JTextField(String.valueOf(produit.getPrix()), 20);
        quantiteField = new JTextField(String.valueOf(produit.getQuantiteEnStock()), 20);

        // Create save button
        saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> saveProduit());

        // Add components to the dialog
        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Nom:"));
        panel.add(nomField);
        panel.add(new JLabel("Prix:"));
        panel.add(prixField);
        panel.add(new JLabel("Quantité en Stock:"));
        panel.add(quantiteField);
        panel.add(new JLabel()); // Empty label for spacing
        panel.add(saveButton);

        add(panel);
    }

    private void saveProduit() {
        String nom = nomField.getText();
        double prix;
        int quantite;

        try {
            prix = Double.parseDouble(prixField.getText());
            quantite = Integer.parseInt(quantiteField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un prix et une quantité valides.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (nom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update the product object
        produit.setNom(nom);
        produit.setPrix(prix);
        produit.setQuantiteEnStock(quantite);

        ProduitDAO produitDAO = new ProduitDAO();

        try {
            produitDAO.updateProduit(produit);
            JOptionPane.showMessageDialog(this, "Produit modifié avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
            produitPanel.loadProduits(); // Refresh the product list
            dispose(); // Close the dialog
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification du produit.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}