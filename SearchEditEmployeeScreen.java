// SearchEditEmployeeScreen.java
// This screen lets HR Admin search for an employee by empID
// and then edit their information.

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SearchEditEmployeeScreen extends JFrame {

    // Search section
    private JTextField searchField;
    private JButton searchBtn;

    // Edit fields (shown after search)
    private JTextField fnameField, lnameField, emailField;
    private JTextField hireDateField, salaryField, ssnField;
    private JTextField streetField, cityField, stateField, zipField;
    private JTextField dobField, phoneField, emergNameField, emergPhoneField;
    private JLabel messageLabel;
    private JButton saveBtn;
    private JPanel editPanel;

    // Store current empID being edited
    private int currentEmpID = -1;

    public SearchEditEmployeeScreen() {
        setTitle("Search / Edit Employee");
        setSize(520, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        // ── TOP: Search bar ──────────────────────────────────────
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Enter Employee ID:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        searchField = new JTextField(10);
        searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Arial", Font.BOLD, 12));
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // ── MIDDLE: Edit fields (hidden until search finds employee) ──
        editPanel = new JPanel(new GridBagLayout());
        editPanel.setVisible(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel editTitle = new JLabel("Edit Employee Info", SwingConstants.CENTER);
        editTitle.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        editPanel.add(editTitle, gbc);
        gbc.gridwidth = 1;

        int row = 1;
        gbc.gridx = 0; gbc.gridy = row; editPanel.add(new JLabel("First Name:"), gbc);
        fnameField = new JTextField(18); gbc.gridx = 1; editPanel.add(fnameField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; editPanel.add(new JLabel("Last Name:"), gbc);
        lnameField = new JTextField(18); gbc.gridx = 1; editPanel.add(lnameField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; editPanel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(18); gbc.gridx = 1; editPanel.add(emailField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; editPanel.add(new JLabel("Hire Date (YYYY-MM-DD):"), gbc);
        hireDateField = new JTextField(18); gbc.gridx = 1; editPanel.add(hireDateField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; editPanel.add(new JLabel("Salary:"), gbc);
        salaryField = new JTextField(18); gbc.gridx = 1; editPanel.add(salaryField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; editPanel.add(new JLabel("SSN:"), gbc);
        ssnField = new JTextField(18); gbc.gridx = 1; editPanel.add(ssnField, gbc); row++;

        JLabel addrTitle = new JLabel("-- Address & Contact --", SwingConstants.CENTER);
        addrTitle.setFont(new Font("Arial", Font.BOLD, 11));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; editPanel.add(addrTitle, gbc);
        gbc.gridwidth = 1; row++;

        gbc.gridx = 0; gbc.gridy = row; editPanel.add(new JLabel("Street:"), gbc);
        streetField = new JTextField(18); gbc.gridx = 1; editPanel.add(streetField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; editPanel.add(new JLabel("City:"), gbc);
        cityField = new JTextField(18); gbc.gridx = 1; editPanel.add(cityField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; editPanel.add(new JLabel("State (e.g. GA):"), gbc);
        stateField = new JTextField(18); gbc.gridx = 1; editPanel.add(stateField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; editPanel.add(new JLabel("Zip:"), gbc);
        zipField = new JTextField(18); gbc.gridx = 1; editPanel.add(zipField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; editPanel.add(new JLabel("Date of Birth (YYYY-MM-DD):"), gbc);
        dobField = new JTextField(18); gbc.gridx = 1; editPanel.add(dobField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; editPanel.add(new JLabel("Phone:"), gbc);
        phoneField = new JTextField(18); gbc.gridx = 1; editPanel.add(phoneField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; editPanel.add(new JLabel("Emergency Contact Name:"), gbc);
        emergNameField = new JTextField(18); gbc.gridx = 1; editPanel.add(emergNameField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; editPanel.add(new JLabel("Emergency Contact Phone:"), gbc);
        emergPhoneField = new JTextField(18); gbc.gridx = 1; editPanel.add(emergPhoneField, gbc); row++;

        // Save button
        saveBtn = new JButton("Save Changes");
        saveBtn.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        editPanel.add(saveBtn, gbc); row++;

        // Message label
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        editPanel.add(messageLabel, gbc);

        JScrollPane scrollPane = new JScrollPane(editPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);

        // ── Actions ──────────────────────────────────────────────

        // Search button
        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchEmployee();
            }
        });

        // Allow pressing Enter in search field
        searchField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchEmployee();
            }
        });

        // Save button
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveChanges();
            }
        });

        setVisible(true);
    }

    // Search for employee by empID and fill in the edit fields
    private void searchEmployee() {
        String idText = searchField.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Employee ID.");
            return;
        }

        int empID;
        try {
            empID = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Employee ID must be a number.");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();

            // Get employee info
            String query = "SELECT e.empid, e.Fname, e.Lname, e.email, e.HireDate, e.Salary, e.SSN, " +
                           "a.street, c.cityName, s.stateAbbr, a.zip, a.DOB, a.phone, " +
                           "a.emergency_contact, a.emergency_phone " +
                           "FROM employees e " +
                           "LEFT JOIN addresses a ON e.empid = a.empid " +
                           "LEFT JOIN cities c ON a.cityID = c.cityID " +
                           "LEFT JOIN states s ON a.stateID = s.stateID " +
                           "WHERE e.empid = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, empID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Fill in all the fields with the employee data
                currentEmpID = empID;
                fnameField.setText(rs.getString("Fname"));
                lnameField.setText(rs.getString("Lname"));
                emailField.setText(rs.getString("email"));
                hireDateField.setText(rs.getString("HireDate") != null ? rs.getString("HireDate") : "");
                salaryField.setText(String.valueOf(rs.getDouble("Salary")));
                ssnField.setText(rs.getString("SSN") != null ? rs.getString("SSN") : "");
                streetField.setText(rs.getString("street") != null ? rs.getString("street") : "");
                cityField.setText(rs.getString("cityName") != null ? rs.getString("cityName") : "");
                stateField.setText(rs.getString("stateAbbr") != null ? rs.getString("stateAbbr") : "");
                zipField.setText(rs.getString("zip") != null ? rs.getString("zip") : "");
                dobField.setText(rs.getString("DOB") != null ? rs.getString("DOB") : "");
                phoneField.setText(rs.getString("phone") != null ? rs.getString("phone") : "");
                emergNameField.setText(rs.getString("emergency_contact") != null ? rs.getString("emergency_contact") : "");
                emergPhoneField.setText(rs.getString("emergency_phone") != null ? rs.getString("emergency_phone") : "");

                // Show the edit panel
                editPanel.setVisible(true);
                messageLabel.setText("");
                revalidate();
                repaint();

            } else {
                // Employee not found
                editPanel.setVisible(false);
                JOptionPane.showMessageDialog(this,
                    "No employee found with ID: " + empID,
                    "Not Found",
                    JOptionPane.WARNING_MESSAGE);
            }

            conn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    // Save the edited employee data back to the database
    private void saveChanges() {
        if (currentEmpID == -1) return;

        String fname      = fnameField.getText().trim();
        String lname      = lnameField.getText().trim();
        String email      = emailField.getText().trim();
        String hireDate   = hireDateField.getText().trim();
        String salaryText = salaryField.getText().trim();
        String ssn        = ssnField.getText().trim();
        String street     = streetField.getText().trim();
        String city       = cityField.getText().trim();
        String state      = stateField.getText().trim();
        String zip        = zipField.getText().trim();
        String dob        = dobField.getText().trim();
        String phone      = phoneField.getText().trim();
        String emergName  = emergNameField.getText().trim();
        String emergPhone = emergPhoneField.getText().trim();

        if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || salaryText.isEmpty()) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Name, email and salary are required.");
            return;
        }

        double salary;
        try {
            salary = Double.parseDouble(salaryText);
        } catch (NumberFormatException e) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Salary must be a number.");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();

            // Update employees table
            String empUpdate = "UPDATE employees SET Fname=?, Lname=?, email=?, HireDate=?, Salary=?, SSN=? WHERE empid=?";
            PreparedStatement empStmt = conn.prepareStatement(empUpdate);
            empStmt.setString(1, fname);
            empStmt.setString(2, lname);
            empStmt.setString(3, email);
            empStmt.setString(4, hireDate);
            empStmt.setDouble(5, salary);
            empStmt.setString(6, ssn);
            empStmt.setInt(7, currentEmpID);
            empStmt.executeUpdate();

            // Get cityID and stateID
            int cityID  = getOrCreateCity(conn, city);
            int stateID = getStateID(conn, state.toUpperCase());

            if (stateID == -1) {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("State not found. Use 2 letter code like GA.");
                conn.close();
                return;
            }

            // Update addresses table
            String addrUpdate = "UPDATE addresses SET street=?, cityID=?, stateID=?, zip=?, DOB=?, " +
                                "phone=?, emergency_contact=?, emergency_phone=? WHERE empid=?";
            PreparedStatement addrStmt = conn.prepareStatement(addrUpdate);
            addrStmt.setString(1, street);
            addrStmt.setInt(2, cityID);
            addrStmt.setInt(3, stateID);
            addrStmt.setString(4, zip);
            addrStmt.setString(5, dob.isEmpty() ? null : dob);
            addrStmt.setString(6, phone);
            addrStmt.setString(7, emergName);
            addrStmt.setString(8, emergPhone);
            addrStmt.setInt(9, currentEmpID);
            addrStmt.executeUpdate();

            conn.close();

            messageLabel.setForeground(new Color(0, 150, 0));
            messageLabel.setText("Employee updated successfully!");

        } catch (SQLException e) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    private int getOrCreateCity(Connection conn, String cityName) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT cityID FROM cities WHERE cityName = ?");
        stmt.setString(1, cityName);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) return rs.getInt("cityID");
        PreparedStatement ins = conn.prepareStatement("INSERT INTO cities (cityName) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
        ins.setString(1, cityName);
        ins.executeUpdate();
        ResultSet k = ins.getGeneratedKeys();
        k.next();
        return k.getInt(1);
    }

    private int getStateID(Connection conn, String stateAbbr) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT stateID FROM states WHERE stateAbbr = ?");
        stmt.setString(1, stateAbbr);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) return rs.getInt("stateID");
        return -1;
    }
}
