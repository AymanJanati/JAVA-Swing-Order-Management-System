package com.gestion.commandes.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.gestion.commandes.database.DatabaseConnection;
import com.gestion.commandes.utils.PasswordUtil;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        // Set the FlatLaf dark theme
        FlatDarkLaf.setup();

        setTitle("AstraOrders - Login");
        setSize(400, 450); // Adjusted size for a modern look
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(18, 18, 18)); // Main background: #121212

        // Logo panel
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(new Color(18, 18, 18)); // Background: #121212
        String iconPath = "C:\\GI2\\s3\\GL&POO JAVA\\GestionCommandesFactures\\icons\\app_icon.jpg"; // Path to your logo
        ImageIcon logoIcon = new ImageIcon(iconPath);
        if (logoIcon.getImageLoadStatus() == MediaTracker.ERRORED) {
            System.err.println("Logo file not found at: " + iconPath);
        } else {
            // Resize the logo to fit the login window
            Image logoImage = logoIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(logoImage);
            JLabel logoLabel = new JLabel(logoIcon);
            logoPanel.add(logoLabel);
        }
        mainPanel.add(logoPanel, BorderLayout.NORTH);

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome to AstraOrders", SwingConstants.CENTER);
        welcomeLabel.setForeground(Color.WHITE); // Text color: White
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Modern font
        mainPanel.add(welcomeLabel, BorderLayout.CENTER);

        // Create a panel for the login form
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(new Color(18, 18, 18)); // Background: #121212
        loginPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for better control

        // GridBagConstraints for layout control
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username field
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        usernamePanel.setBackground(new Color(18, 18, 18)); // Background: #121212
        FontIcon userIcon = FontIcon.of(FontAwesome.USER, 20, Color.WHITE); // User icon
        JLabel usernameLabel = new JLabel(userIcon);
        usernameField = new JTextField(20);
        usernameField.setBackground(new Color(37, 37, 38)); // Field background: #252526
        usernameField.setForeground(Color.WHITE); // Text color: White
        usernameField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Padding
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);

        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(usernamePanel, gbc);

        // Password field
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passwordPanel.setBackground(new Color(18, 18, 18)); // Background: #121212
        FontIcon passwordIcon = FontIcon.of(FontAwesome.LOCK, 20, Color.WHITE); // Lock icon
        JLabel passwordLabel = new JLabel(passwordIcon);
        passwordField = new JPasswordField(20);
        passwordField.setBackground(new Color(37, 37, 38)); // Field background: #252526
        passwordField.setForeground(Color.WHITE); // Text color: White
        passwordField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Padding
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(passwordPanel, gbc);

        // Login button
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(37, 37, 38)); // Button background: #252526
        loginButton.setForeground(Color.WHITE); // Text color: White
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginButton.setPreferredSize(new Dimension(100, 40));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (authenticate(username, password)) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Login Successful!");
                    dispose(); // Close the login window
                    MainGUI.showMainGUI(); // Open the main application
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(loginButton, gbc);

        // Add the login panel to the main panel
        mainPanel.add(loginPanel, BorderLayout.SOUTH);

        // Add the main panel to the frame
        add(mainPanel);
    }

    private boolean authenticate(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password");
                return PasswordUtil.checkPassword(password, storedHash); // Verify the password
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}