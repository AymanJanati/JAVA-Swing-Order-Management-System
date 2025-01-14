package com.gestion.commandes.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainGUI extends JFrame {
    private JPanel contentPanel; // Make contentPanel a class variable
    private JPanel sidebar; // Make sidebar a class variable
    private boolean isSidebarVisible = true; // Track sidebar visibility

    public MainGUI() {
        // Set the FlatLaf dark theme
        FlatDarkLaf.setup();

        setTitle("Gestion des Commandes et des Factures");
        setSize(1200, 800); // Increased size for better layout
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set custom application icon using an absolute file path
        String iconPath = "C:\\GI2\\s3\\GL&POO JAVA\\GestionCommandesFactures\\icons\\app_icon.jpg";
        ImageIcon icon = new ImageIcon(iconPath);
        if (icon.getImageLoadStatus() == MediaTracker.ERRORED) {
            System.err.println("Icon file not found at: " + iconPath);
        } else {
            setIconImage(icon.getImage());
        }

        // Create the main panel with a BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(18, 18, 18)); // Main background: #121212

        // Create the sidebar with a slightly lighter color (#1E1E1E)
        sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        // Create the content panel (will display Clients, Produits, Factures, Commandes)
        contentPanel = new JPanel(new CardLayout()); // Initialize contentPanel
        contentPanel.setBackground(new Color(18, 18, 18)); // Background color: #121212

        // Add panels to the content panel
        contentPanel.add(new ClientPanel(), "Clients");
        contentPanel.add(new ProduitPanel(), "Produits");
        contentPanel.add(new FacturePanel(), "Factures");
        contentPanel.add(new CommandePanel(), "Commandes");

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add a toggle button to hide/show the sidebar (placed in the north region)
        JButton toggleSidebarButton = new JButton("☰");
        toggleSidebarButton.setBackground(new Color(37, 37, 38)); // Button background: #252526
        toggleSidebarButton.setForeground(Color.WHITE); // Text color: White
        toggleSidebarButton.setFocusPainted(false);
        toggleSidebarButton.setBorderPainted(false);
        toggleSidebarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleSidebar();
            }
        });

        // Create a panel for the toggle button (to center it in the north region)
        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        togglePanel.setBackground(new Color(18, 18, 18)); // Background: #121212
        togglePanel.add(toggleSidebarButton);

        // Add the toggle button panel to the north region of the main panel
        mainPanel.add(togglePanel, BorderLayout.NORTH);

        // Add the main panel to the frame
        add(mainPanel);

        // Display the window
        setVisible(true);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(30, 30, 30)); // Sidebar background: #1E1E1E (slightly lighter than #121212)
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

        // Add buttons to the sidebar (at the top)
        sidebar.add(clientsButton); // Button at the top
        sidebar.add(Box.createVerticalStrut(10)); // Add spacing
        sidebar.add(produitsButton);
        sidebar.add(Box.createVerticalStrut(10)); // Add spacing
        sidebar.add(facturesButton);
        sidebar.add(Box.createVerticalStrut(10)); // Add spacing
        sidebar.add(commandesButton);

        return sidebar;
    }

    private JButton createSidebarButton(String text, FontAwesome icon) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(new Color(37, 37, 38)); // Button background: #252526
        button.setForeground(Color.WHITE); // Text color: White
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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
                button.setBackground(new Color(58, 58, 58)); // Hover color: #3A3A3A
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(37, 37, 38)); // Default color: #252526
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

    private void toggleSidebar() {
        isSidebarVisible = !isSidebarVisible;
        sidebar.setVisible(isSidebarVisible);
        revalidate(); // Refresh the layout
        repaint(); // Ensure the UI updates
    }

    public static void main(String[] args) {
        // Run the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new MainGUI());
    }
}