package com.gestion.commandes.gui;
import com.gestion.commandes.gui.FacturePanel;
import com.gestion.commandes.gui.CommandePanel;


import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame {
    public MainGUI() {
        setTitle("Gestion des Commandes et des Factures");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Create a tabbed pane for different sections (Clients, Produits, Factures, Commandes)
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add tabs
        tabbedPane.addTab("Clients", new ClientPanel());
        tabbedPane.addTab("Produits", new ProduitPanel());
        tabbedPane.addTab("Factures", new FacturePanel());
        tabbedPane.addTab("Commandes", new CommandePanel());

        // Add the tabbed pane to the frame
        add(tabbedPane);

        // Display the window
        setVisible(true);
    }

    public static void main(String[] args) {
        // Run the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new MainGUI());
    }
}