// EmployeeMenu.java
// This is the main menu for general employees.
// They can only see their own personal data and pay history.

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class EmployeeMenu extends JFrame {

    private int empID;
    private String empName;

    public EmployeeMenu(int empID, String empName) {
        this.empID   = empID;
        this.empName = empName;

        setTitle("Employee Portal - " + empName);
        setSize(450, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Welcome message at top
        JLabel welcomeLabel = new JLabel("Welcome, " + empName, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JLabel subtitleLabel = new JLabel("Employee Portal", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(welcomeLabel);
        topPanel.add(subtitleLabel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Buttons in the middle
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        JButton viewDataBtn   = new JButton("View My Personal Data");
        JButton payHistoryBtn = new JButton("View My Pay History");
        JButton logoutBtn     = new JButton("Logout");

        Font btnFont = new Font("Arial", Font.PLAIN, 14);
        viewDataBtn.setFont(btnFont);
        payHistoryBtn.setFont(btnFont);
        logoutBtn.setFont(btnFont);
        logoutBtn.setForeground(Color.RED);

        buttonPanel.add(viewDataBtn);
        buttonPanel.add(payHistoryBtn);
        buttonPanel.add(logoutBtn);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);

        // View personal data button
        viewDataBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewPersonalData();
            }
        });

        // View pay history button
        payHistoryBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewPayHistory();
            }
        });

        // Logout button
        logoutBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to logout?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    new LoginScreen();
                    dispose();
                }
            }
        });

        setVisible(true);
    }

    // Show the employee their own personal and demographic data
    private void viewPersonalData() {
        try {
            Connection conn = DatabaseConnection.getConnection();

            String query =
                "SELECT e.empid, e.Fname, e.Lname, e.email, e.HireDate, e.Salary, " +
                "a.street, c.cityName, s.stateAbbr, a.zip, a.DOB, a.phone, " +
                "a.emergency_contact, a.emergency_phone, " +
                "COALESCE(jt.job_title, 'N/A') AS job_title " +
                "FROM employees e " +
                "LEFT JOIN addresses a ON e.empid = a.empid " +
                "LEFT JOIN cities c ON a.cityID = c.cityID " +
                "LEFT JOIN states s ON a.stateID = s.stateID " +
                "LEFT JOIN employee_job_titles ejt ON e.empid = ejt.empid " +
                "LEFT JOIN job_titles jt ON ejt.job_titleID = jt.job_titleID " +
                "WHERE e.empid = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, empID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Build a nice display of personal data
                String info =
                    "<html><body style='padding:10px; font-family:Arial;'>" +
                    "<h2 style='color:#2E75B6;'>My Personal Data</h2>" +
                    "<table>" +
                    "<tr><td><b>Employee ID:</b></td><td>" + rs.getInt("empid") + "</td></tr>" +
                    "<tr><td><b>Name:</b></td><td>" + rs.getString("Fname") + " " + rs.getString("Lname") + "</td></tr>" +
                    "<tr><td><b>Email:</b></td><td>" + rs.getString("email") + "</td></tr>" +
                    "<tr><td><b>Job Title:</b></td><td>" + rs.getString("job_title") + "</td></tr>" +
                    "<tr><td><b>Hire Date:</b></td><td>" + rs.getString("HireDate") + "</td></tr>" +
                    "<tr><td><b>Salary:</b></td><td>$" + String.format("%.2f", rs.getDouble("Salary")) + "</td></tr>" +
                    "<tr><td><b>Date of Birth:</b></td><td>" + (rs.getString("DOB") != null ? rs.getString("DOB") : "N/A") + "</td></tr>" +
                    "<tr><td><b>Phone:</b></td><td>" + (rs.getString("phone") != null ? rs.getString("phone") : "N/A") + "</td></tr>" +
                    "<tr><td><b>Address:</b></td><td>" +
                        (rs.getString("street") != null ? rs.getString("street") + ", " + rs.getString("cityName") + ", " + rs.getString("stateAbbr") + " " + rs.getString("zip") : "N/A") +
                    "</td></tr>" +
                    "<tr><td><b>Emergency Contact:</b></td><td>" + (rs.getString("emergency_contact") != null ? rs.getString("emergency_contact") : "N/A") + "</td></tr>" +
                    "<tr><td><b>Emergency Phone:</b></td><td>" + (rs.getString("emergency_phone") != null ? rs.getString("emergency_phone") : "N/A") + "</td></tr>" +
                    "</table></body></html>";

                JLabel infoLabel = new JLabel(info);
                JScrollPane scroll = new JScrollPane(infoLabel);
                scroll.setPreferredSize(new Dimension(420, 350));

                JOptionPane.showMessageDialog(this, scroll, "My Personal Data", JOptionPane.PLAIN_MESSAGE);
            }

            conn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // Show the employee their pay statement history sorted by most recent date
    private void viewPayHistory() {
        try {
            Connection conn = DatabaseConnection.getConnection();

            String query =
                "SELECT payID, pay_date, earnings, fed_tax, fed_med, fed_SS, " +
                "state_tax, retire_401k, health_care " +
                "FROM payroll " +
                "WHERE empid = ? " +
                "ORDER BY pay_date DESC";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, empID);
            ResultSet rs = stmt.executeQuery();

            // Table columns
            String[] columns = {"Pay ID", "Pay Date", "Earnings", "Fed Tax", "Medicare",
                                 "Social Sec", "State Tax", "401k", "Health Care"};

            // Count rows
            java.util.List<Object[]> rows = new java.util.ArrayList<>();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("payID"),
                    rs.getString("pay_date"),
                    "$" + String.format("%.2f", rs.getDouble("earnings")),
                    "$" + String.format("%.2f", rs.getDouble("fed_tax")),
                    "$" + String.format("%.2f", rs.getDouble("fed_med")),
                    "$" + String.format("%.2f", rs.getDouble("fed_SS")),
                    "$" + String.format("%.2f", rs.getDouble("state_tax")),
                    "$" + String.format("%.2f", rs.getDouble("retire_401k")),
                    "$" + String.format("%.2f", rs.getDouble("health_care"))
                };
                rows.add(row);
            }

            conn.close();

            if (rows.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No pay history found.");
                return;
            }

            Object[][] data = rows.toArray(new Object[0][]);
            JTable table = new JTable(data, columns) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            table.setFont(new Font("Arial", Font.PLAIN, 11));
            table.setRowHeight(20);
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

            JScrollPane scroll = new JScrollPane(table);
            scroll.setPreferredSize(new Dimension(700, 250));

            JOptionPane.showMessageDialog(this, scroll,
                "My Pay History - " + empName, JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
