package com.gestion.commandes.gui;

import java.io.FileNotFoundException;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatLaf;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.swing.FontIcon;
import java.io.FileInputStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MainGUI extends JFrame {
    private JPanel contentPanel; // Make contentPanel a class variable
    private JPanel sidebar; // Make sidebar a class variable
    private boolean isSidebarVisible = true; // Track sidebar visibility
    private boolean isDarkMode = true; // Track dark mode state
    private JButton toggleThemeButton; // Declare toggleThemeButton as a class variable

    // Panels
    private ClientPanel clientPanel;
    private ProduitPanel produitPanel;
    private FacturePanel facturePanel;
    private CommandePanel commandePanel;

    // Private constructor to enforce login before accessing MainGUI
    MainGUI() {
        initializeUI();
    }

    // Static method to create an instance of MainGUI after successful login
    public static void showMainGUI() {
        SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
    }

    // Load custom theme properties
    public static void loadCustomTheme(String absolutePath) {
        System.out.println("Attempting to load theme file from: " + absolutePath);

        try (InputStream input = new FileInputStream(absolutePath)) {
            Properties properties = new Properties();
            properties.load(input);

            // Convert Properties to Map<String, String>
            Map<String, String> themeMap = new HashMap<>();
            for (String key : properties.stringPropertyNames()) {
                themeMap.put(key, properties.getProperty(key));
            }

            // Set the custom theme properties
            FlatLaf.setGlobalExtraDefaults(themeMap);
            System.out.println("Successfully loaded theme from: " + absolutePath);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load theme from: " + absolutePath, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeUI() {
        // Load the custom dark theme by default
        loadCustomTheme("C:/GI2/s3/GL&POO JAVA/GestionCommandesFactures/src/main/resources/custom_dark.properties");
        FlatDarkLaf.setup();

        setTitle("AstraOrders - Gestion des Commandes et des Factures"); // Updated title
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
        mainPanel.setBackground(UIManager.getColor("Panel.background")); // Theme-aware color

        // Create the sidebar with a distinct color
        sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        // Create the content panel (will display Clients, Produits, Factures, Commandes)
        contentPanel = new JPanel(new CardLayout()); // Initialize contentPanel
        contentPanel.setBackground(UIManager.getColor("Panel.background")); // Theme-aware color

        // Initialize panels
        clientPanel = new ClientPanel();
        produitPanel = new ProduitPanel();
        facturePanel = new FacturePanel();
        commandePanel = new CommandePanel();

        // Add panels to the content panel
        contentPanel.add(clientPanel, "Clients");
        contentPanel.add(produitPanel, "Produits");
        contentPanel.add(facturePanel, "Factures");
        contentPanel.add(commandePanel, "Commandes");

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add a toggle button to hide/show the sidebar (placed in the north region)
        JButton toggleSidebarButton = new JButton("☰");
        toggleSidebarButton.setBackground(UIManager.getColor("Button.background")); // Theme-aware color
        toggleSidebarButton.setForeground(UIManager.getColor("Button.foreground")); // Theme-aware color
        toggleSidebarButton.setFocusPainted(false);
        toggleSidebarButton.setBorderPainted(false);
        toggleSidebarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleSidebar();
            }
        });

        // Initialize the toggle button for light/dark mode
        toggleThemeButton = new JButton("🌙"); // Moon icon for dark mode
        toggleThemeButton.setBackground(UIManager.getColor("Button.background")); // Theme-aware color
        toggleThemeButton.setForeground(UIManager.getColor("Button.foreground")); // Theme-aware color
        toggleThemeButton.setFocusPainted(false);
        toggleThemeButton.setBorderPainted(false);
        toggleThemeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleTheme();
            }
        });

        // Create a panel for the toggle buttons (to center them in the north region)
        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        togglePanel.setBackground(UIManager.getColor("Panel.background")); // Theme-aware color
        togglePanel.add(toggleSidebarButton);
        togglePanel.add(toggleThemeButton);

        // Add the toggle button panel to the north region of the main panel
        mainPanel.add(togglePanel, BorderLayout.NORTH);

        // Add a logout button to the sidebar
        JButton logoutButton = createSidebarButton("Logout", FontAwesome.SIGN_OUT);
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    MainGUI.this,
                    "Are you sure you want to logout?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                dispose(); // Close the MainGUI window
                new LoginFrame().setVisible(true); // Open the login window
            }
        });
        sidebar.add(Box.createVerticalStrut(20)); // Add spacing
        sidebar.add(logoutButton);

        // Add the main panel to the frame
        add(mainPanel);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UIManager.getColor("Sidebar.background")); // Theme-aware color
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
        button.setBackground(UIManager.getColor("SidebarButton.background")); // Theme-aware color
        button.setForeground(UIManager.getColor("SidebarButton.foreground")); // Theme-aware color
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(180, 40));
        button.setMaximumSize(new Dimension(180, 40));

        // Add icon to the button
        FontIcon fontIcon = FontIcon.of(icon, 18, UIManager.getColor("SidebarButton.foreground")); // Theme-aware color
        button.setIcon(fontIcon);
        button.setHorizontalAlignment(SwingConstants.LEFT); // Align text and icon to the left
        button.setIconTextGap(10); // Add spacing between icon and text

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(UIManager.getColor("SidebarButton.hoverBackground")); // Theme-aware hover color
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(UIManager.getColor("SidebarButton.background")); // Restore original color
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

    private void toggleTheme() {
        try {
            if (isDarkMode) {
                // Switch to light mode
                loadCustomTheme("C:/GI2/s3/GL&POO JAVA/GestionCommandesFactures/src/main/resources/custom_light.properties");
                FlatLightLaf.setup();
                isDarkMode = false;
                toggleThemeButton.setText("🌙"); // Moon icon for dark mode
            } else {
                // Switch to dark mode
                loadCustomTheme("C:/GI2/s3/GL&POO JAVA/GestionCommandesFactures/src/main/resources/custom_dark.properties");
                FlatDarkLaf.setup();
                isDarkMode = true;
                toggleThemeButton.setText("☀️"); // Sun icon for light mode
            }
            // Update the UI for all panels
            refreshAllPanels();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to toggle theme.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAllPanels() {
        // Refresh the main frame
        SwingUtilities.updateComponentTreeUI(this);

        // Refresh the sidebar
        sidebar.setBackground(UIManager.getColor("Sidebar.background"));
        for (Component component : sidebar.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                button.setBackground(UIManager.getColor("SidebarButton.background"));
                button.setForeground(UIManager.getColor("SidebarButton.foreground"));
            }
        }

        // Refresh all panels
        clientPanel.refreshUI(); // Refresh ClientPanel
        produitPanel.refreshUI(); // Refresh ProduitPanel
        facturePanel.refreshUI(); // Refresh FacturePanel
        commandePanel.refreshUI(); // Refresh CommandePanel
    }
}