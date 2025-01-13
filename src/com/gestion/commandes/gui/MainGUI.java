package com.gestion.commandes.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame {
    private JPanel contentPanel; // Make contentPanel a class variable

    public MainGUI() {
        // Set the FlatLaf dark theme
        FlatDarkLaf.setup();

        setTitle("Gestion des Commandes et des Factures");
        setSize(1200, 800); // Increased size for better layout
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the main panel with a BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(21, 34, 56)); // Main color: #152238

        // Create the sidebar
        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        // Create the content panel (will display Clients, Produits, Factures, Commandes)
        contentPanel = new JPanel(new CardLayout()); // Initialize contentPanel
        contentPanel.setBackground(new Color(245, 246, 250)); // Background color: #F5F6FA

        // Add panels to the content panel
        contentPanel.add(new ClientPanel(), "Clients");
        contentPanel.add(new ProduitPanel(), "Produits");
        contentPanel.add(new FacturePanel(), "Factures");
        contentPanel.add(new CommandePanel(), "Commandes");

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add the main panel to the frame
        add(mainPanel);

        // Display the window
        setVisible(true);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(52, 21, 57)); // Accent color: #341539
        sidebar.setPreferredSize(new Dimension(200, getHeight())); // Sidebar width: 200px
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Add buttons for navigation
        JButton clientsButton = createSidebarButton("Clients", FontAwesome.USERS);
        JButton produitsButton = createSidebarButton("Produits", FontAwesome.CUBES);
        JButton facturesButton = createSidebarButton("Factures", FontAwesome.FILE_TEXT);
        JButton commandesButton = createSidebarButton("Commandes", FontAwesome.SHOPPING_CART);

        // Add action listeners to switch panels
        clientsButton.addActionListener(e -> showPanel("Clients"));
        produitsButton.addActionListener(e -> showPanel("Produits"));
        facturesButton.addActionListener(e -> showPanel("Factures"));
        commandesButton.addActionListener(e -> showPanel("Commandes"));

        // Add buttons to the sidebar
        sidebar.add(Box.createVerticalStrut(20)); // Add some spacing
        sidebar.add(clientsButton);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(produitsButton);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(facturesButton);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(commandesButton);

        return sidebar;
    }

    private JButton createSidebarButton(String text, FontAwesome icon) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(new Color(44, 62, 80)); // Secondary color: #2C3E50
        button.setForeground(Color.WHITE); // Text color: White
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(180, 40));
        button.setMaximumSize(new Dimension(180, 40));

        // Add icon to the button
        FontIcon fontIcon = FontIcon.of(icon, 18, Color.WHITE);
        button.setIcon(fontIcon);
        button.setHorizontalAlignment(SwingConstants.LEFT); // Align text and icon to the left
        button.setIconTextGap(10); // Add spacing between icon and text

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 21, 57)); // Accent color: #341539
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(44, 62, 80)); // Secondary color: #2C3E50
            }
        });

        return button;
    }
    private void showPanel(String panelName) {
        // Get the CardLayout from the contentPanel
        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        // Show the panel with the specified name
        cardLayout.show(contentPanel, panelName);
    }

    public static void main(String[] args) {
        // Run the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new MainGUI());
    }
}