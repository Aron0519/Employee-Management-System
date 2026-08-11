// DeleteEmployeeScreen.java
// This screen lets HR Admin search for an employee by empID
// and delete them from the database after confirmation.

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DeleteEmployeeScreen extends JFrame {

    private JTextField searchField;
    private JButton searchBtn;
    private JButton deleteBtn;
    private JLabel messageLabel;
    private JPanel infoPanel;
    private JLabel empInfoLabel;
    private int currentEmpID = -1;

    public DeleteEmployeeScreen() {
        setTitle("Delete Employee");
        setSize(450, 320);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("Delete Employee", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        mainPanel.add(title, gbc);

        // Search row
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Employee ID:"), gbc);

        searchField = new JTextField(10);
        gbc.gridx = 1; gbc.gridy = 1;
        mainPanel.add(searchField, gbc);

        searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 2; gbc.gridy = 1;
        mainPanel.add(searchBtn, gbc);

        // Employee info box (hidden until found)
        infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Employee Found"));
        empInfoLabel = new JLabel("", SwingConstants.CENTER);
        empInfoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        infoPanel.add(empInfoLabel, BorderLayout.CENTER);
        infoPanel.setVisible(false);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        gbc.ipady = 30;
        mainPanel.add(infoPanel, gbc);
        gbc.ipady = 0;

        // Delete button (hidden until found)
        deleteBtn = new JButton("Delete This Employee");
        deleteBtn.setFont(new Font("Arial", Font.BOLD, 13));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setBackground(Color.RED);
        deleteBtn.setOpaque(true);
        deleteBtn.setVisible(false);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        mainPanel.add(deleteBtn, gbc);

        // Message label
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        mainPanel.add(messageLabel, gbc);

        add(mainPanel);

        // Search button action
        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchEmployee();
            }
        });

        // Allow Enter key in search field
        searchField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchEmployee();
            }
        });

        // Delete button action
        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                confirmAndDelete();
            }
        });

        setVisible(true);
    }

    private void searchEmployee() {
        String idText = searchField.getText().trim();

        if (idText.isEmpty()) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Please enter an Employee ID.");
            return;
        }

        int empID;
        try {
            empID = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Employee ID must be a number.");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "SELECT empid, Fname, Lname, email, Salary FROM employees WHERE empid = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, empID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Employee found — show their info
                currentEmpID = empID;
                String info = "<html><center>" +
                    "ID: " + rs.getInt("empid") + "<br>" +
                    "Name: " + rs.getString("Fname") + " " + rs.getString("Lname") + "<br>" +
                    "Email: " + rs.getString("email") + "<br>" +
                    "Salary: $" + rs.getDouble("Salary") +
                    "</center></html>";
                empInfoLabel.setText(info);
                infoPanel.setVisible(true);
                deleteBtn.setVisible(true);
                messageLabel.setText("");
            } else {
                // Not found
                currentEmpID = -1;
                infoPanel.setVisible(false);
                deleteBtn.setVisible(false);
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("No employee found with ID: " + empID);
            }

            conn.close();
            revalidate();
            repaint();

        } catch (SQLException e) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Database error: " + e.getMessage());
        }
    }

    private void confirmAndDelete() {
        if (currentEmpID == -1) return;

        // Ask HR Admin to confirm deletion
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete employee ID " + currentEmpID + "?\nThis cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DatabaseConnection.getConnection();

                // Delete from addresses first (foreign key)
                PreparedStatement addrStmt = conn.prepareStatement("DELETE FROM addresses WHERE empid = ?");
                addrStmt.setInt(1, currentEmpID);
                addrStmt.executeUpdate();

                // Delete from employee_division
                PreparedStatement divStmt = conn.prepareStatement("DELETE FROM employee_division WHERE empid = ?");
                divStmt.setInt(1, currentEmpID);
                divStmt.executeUpdate();

                // Delete from employee_job_titles
                PreparedStatement jtStmt = conn.prepareStatement("DELETE FROM employee_job_titles WHERE empid = ?");
                jtStmt.setInt(1, currentEmpID);
                jtStmt.executeUpdate();

                // Delete from payroll
                PreparedStatement payStmt = conn.prepareStatement("DELETE FROM payroll WHERE empid = ?");
                payStmt.setInt(1, currentEmpID);
                payStmt.executeUpdate();

                // Finally delete from employees
                PreparedStatement empStmt = conn.prepareStatement("DELETE FROM employees WHERE empid = ?");
                empStmt.setInt(1, currentEmpID);
                empStmt.executeUpdate();

                conn.close();

                // Hide the info and delete button
                infoPanel.setVisible(false);
                deleteBtn.setVisible(false);
                searchField.setText("");
                messageLabel.setForeground(new Color(0, 150, 0));
                messageLabel.setText("Employee " + currentEmpID + " deleted successfully.");
                currentEmpID = -1;

            } catch (SQLException e) {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Error: " + e.getMessage());
            }
        } else {
            // User clicked No
            messageLabel.setForeground(Color.BLUE);
            messageLabel.setText("Deletion cancelled.");
        }
    }
}
