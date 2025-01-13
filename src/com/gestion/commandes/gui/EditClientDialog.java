package com.gestion.commandes.gui;

import com.gestion.commandes.models.Client;
import com.gestion.commandes.database.ClientDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class EditClientDialog extends JDialog {
    private JTextField nomField;
    private JTextField emailField;
    private JTextField telephoneField;
    private JButton saveButton;
    private ClientPanel clientPanel;
    private Client client;

    public EditClientDialog(JFrame parent, ClientPanel clientPanel, Client client) {
        super(parent, "Modifier un Client", true);
        this.clientPanel = clientPanel;
        this.client = client;

        setSize(300, 200);
        setLocationRelativeTo(parent);

        // Create form fields and pre-fill them with the client's details
        nomField = new JTextField(client.getNom(), 20);
        emailField = new JTextField(client.getEmail(), 20);
        telephoneField = new JTextField(client.getTelephone(), 20);

        // Create save button
        saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> saveClient());

        // Add components to the dialog
        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Nom:"));
        panel.add(nomField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Téléphone:"));
        panel.add(telephoneField);
        panel.add(new JLabel()); // Empty label for spacing
        panel.add(saveButton);

        add(panel);
    }

    private void saveClient() {
        String nom = nomField.getText();
        String email = emailField.getText();
        String telephone = telephoneField.getText();

        if (nom.isEmpty() || email.isEmpty() || telephone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update the client object
        client.setNom(nom);
        client.setEmail(email);
        client.setTelephone(telephone);

        ClientDAO clientDAO = new ClientDAO();

        try {
            clientDAO.updateClient(client);
            JOptionPane.showMessageDialog(this, "Client modifié avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
            clientPanel.loadClients(); // Refresh the client list
            dispose(); // Close the dialog
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification du client.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}