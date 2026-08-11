// LoginScreen.java
// This is the first window that appears when the app starts.
// HR Admin logs in with username: admin and password: admin123
// General employees log in with their empID and SSN as password

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginScreen extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    public LoginScreen() {
        setTitle("Employee Management System - Login");
        setSize(480, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Employee Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Subtitle
        JLabel subLabel = new JLabel("Please log in to continue", SwingConstants.CENTER);
        subLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        subLabel.setForeground(Color.GRAY);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(subLabel, gbc);

        // Username field
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Username / Employee ID:"), gbc);
        usernameField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(passwordField, gbc);

        // Login button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        // Message label
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(messageLabel, gbc);

        add(panel);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { checkLogin(); }
        });

        passwordField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { checkLogin(); }
        });

        setVisible(true);
    }

    private void checkLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password.");
            return;
        }

        // HR Admin check
        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            messageLabel.setForeground(new Color(0, 150, 0));
            messageLabel.setText("Welcome, HR Admin!");
            Timer timer = new Timer(800, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new HRAdminMenu();
                    dispose();
                }
            });
            timer.setRepeats(false);
            timer.start();
            return;
        }

        // General employee check
        int empID;
        try {
            empID = Integer.parseInt(username);
        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid Employee ID or Password.");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "SELECT empid, Fname, Lname FROM employees WHERE empid = ? AND SSN = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, empID);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String fname = rs.getString("Fname");
                String lname = rs.getString("Lname");
                int foundEmpID = rs.getInt("empid");
                conn.close();
                messageLabel.setForeground(new Color(0, 150, 0));
                messageLabel.setText("Welcome, " + fname + " " + lname + "!");
                Timer timer = new Timer(800, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        new EmployeeMenu(foundEmpID, fname + " " + lname);
                        dispose();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                conn.close();
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Invalid Employee ID or Password.");
            }

        } catch (SQLException e) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Database error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() { new LoginScreen(); }
        });
    }
}
