// SalaryUpdateScreen.java
// This screen lets HR Admin update salaries by a percentage
// for all employees whose salary is within a specified range.

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SalaryUpdateScreen extends JFrame {

    private JTextField minSalaryField;
    private JTextField maxSalaryField;
    private JTextField percentageField;
    private JButton updateBtn;
    private JLabel messageLabel;
    private JTextArea resultArea;

    public SalaryUpdateScreen() {
        setTitle("Update Salary by %");
        setSize(520, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("Update Salary by Percentage", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 15));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(title, gbc);

        // Instructions
        JLabel instructions = new JLabel(
            "<html><center>Enter a salary range and a percentage.<br>" +
            "All employees with salary in that range will get the increase.</center></html>",
            SwingConstants.CENTER);
        instructions.setFont(new Font("Arial", Font.PLAIN, 11));
        instructions.setForeground(Color.GRAY);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        mainPanel.add(instructions, gbc);
        gbc.gridwidth = 1;

        // Min salary
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Minimum Salary ($):"), gbc);
        minSalaryField = new JTextField(15);
        gbc.gridx = 1; mainPanel.add(minSalaryField, gbc);

        // Max salary
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Maximum Salary ($):"), gbc);
        maxSalaryField = new JTextField(15);
        gbc.gridx = 1; mainPanel.add(maxSalaryField, gbc);

        // Percentage
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Increase Percentage (%):"), gbc);
        percentageField = new JTextField(15);
        gbc.gridx = 1; mainPanel.add(percentageField, gbc);

        // Update button
        updateBtn = new JButton("Apply Salary Increase");
        updateBtn.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        mainPanel.add(updateBtn, gbc);

        // Message label - row 6
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        mainPanel.add(messageLabel, gbc);

        // Updated Employees label - row 7
        JLabel resultLabel = new JLabel("Updated Employees:");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        mainPanel.add(resultLabel, gbc);

        // Result text area - row 8
        resultArea = new JTextArea(6, 30);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scroll = new JScrollPane(resultArea);
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        mainPanel.add(scroll, gbc);

        // Close button - row 9
        JButton cancelBtn = new JButton("Close");
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2;
        mainPanel.add(cancelBtn, gbc);

        add(mainPanel);

        updateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { applySalaryIncrease(); }
        });

        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { dispose(); }
        });

        setVisible(true);
    }

    private void applySalaryIncrease() {
        String minText = minSalaryField.getText().trim();
        String maxText = maxSalaryField.getText().trim();
        String pctText = percentageField.getText().trim();

        if (minText.isEmpty() || maxText.isEmpty() || pctText.isEmpty()) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Please fill in all fields.");
            return;
        }

        double minSalary, maxSalary, percentage;
        try {
            minSalary  = Double.parseDouble(minText);
            maxSalary  = Double.parseDouble(maxText);
            percentage = Double.parseDouble(pctText);
        } catch (NumberFormatException e) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("All values must be numbers.");
            return;
        }

        if (minSalary >= maxSalary) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Minimum salary must be less than maximum salary.");
            return;
        }

        if (percentage <= 0) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Percentage must be greater than 0.");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();

            String selectQuery = "SELECT empid, Fname, Lname, Salary FROM employees WHERE Salary >= ? AND Salary <= ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
            selectStmt.setDouble(1, minSalary);
            selectStmt.setDouble(2, maxSalary);
            ResultSet rs = selectStmt.executeQuery();

            StringBuilder result = new StringBuilder();
            result.append(String.format("%-5s %-20s %-12s %-12s%n", "ID", "Name", "Old Salary", "New Salary"));
            result.append("-".repeat(52)).append("\n");

            int count = 0;
            while (rs.next()) {
                int empID     = rs.getInt("empid");
                String name   = rs.getString("Fname") + " " + rs.getString("Lname");
                double oldSal = rs.getDouble("Salary");
                double newSal = oldSal * (1 + percentage / 100.0);
                result.append(String.format("%-5d %-20s $%-11.2f $%-11.2f%n", empID, name, oldSal, newSal));
                count++;
            }

            if (count == 0) {
                messageLabel.setForeground(Color.BLUE);
                messageLabel.setText("No employees found in that salary range.");
                resultArea.setText("No employees found in the range $" + minSalary + " - $" + maxSalary);
                conn.close();
                return;
            }

            String updateQuery = "UPDATE employees SET Salary = Salary * (1 + ? / 100) WHERE Salary >= ? AND Salary <= ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setDouble(1, percentage);
            updateStmt.setDouble(2, minSalary);
            updateStmt.setDouble(3, maxSalary);
            updateStmt.executeUpdate();

            conn.close();

            resultArea.setText(result.toString());
            messageLabel.setForeground(new Color(0, 150, 0));
            messageLabel.setText(count + " employee(s) updated with +" + percentage + "% salary increase.");

        } catch (SQLException e) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Database error: " + e.getMessage());
        }
    }
}
