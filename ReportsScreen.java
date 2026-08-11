// ReportsScreen.java
// This screen lets HR Admin generate 3 reports:
// 1. Total pay for the month by job title
// 2. Total pay for the month by division
// 3. New employee hires within a date range

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ReportsScreen extends JFrame {

    private JTextArea resultArea;
    private JLabel messageLabel;
    private JTextField startDateField;
    private JTextField endDateField;

    public ReportsScreen() {
        setTitle("HR Admin Reports");
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Title
        JLabel title = new JLabel("HR Admin Reports", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(title, BorderLayout.NORTH);

        // Left panel with buttons - fixed layout
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(200, 400));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        // Pay by Job Title button
        JButton payByTitleBtn = new JButton("Pay by Job Title");
        payByTitleBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        payByTitleBtn.setMaximumSize(new Dimension(190, 45));
        payByTitleBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Pay by Division button
        JButton payByDivBtn = new JButton("Pay by Division");
        payByDivBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        payByDivBtn.setMaximumSize(new Dimension(190, 45));
        payByDivBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // New Hires section
        JPanel hiresPanel = new JPanel(new GridLayout(5, 1, 3, 3));
        hiresPanel.setBorder(BorderFactory.createTitledBorder("New Hires Report"));
        hiresPanel.setMaximumSize(new Dimension(190, 150));

        startDateField = new JTextField("2022-01-01");
        endDateField   = new JTextField("2026-12-31");

        hiresPanel.add(new JLabel("Start (YYYY-MM-DD):"));
        hiresPanel.add(startDateField);
        hiresPanel.add(new JLabel("End (YYYY-MM-DD):"));
        hiresPanel.add(endDateField);

        JButton newHiresBtn = new JButton("Get New Hires");
        newHiresBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        newHiresBtn.setMaximumSize(new Dimension(190, 40));
        newHiresBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(payByTitleBtn);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(payByDivBtn);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(hiresPanel);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(newHiresBtn);

        // Result area on the right
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scroll = new JScrollPane(resultArea);

        // Message label at bottom
        messageLabel = new JLabel("Select a report from the left.", SwingConstants.CENTER);
        messageLabel.setForeground(Color.GRAY);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(scroll, BorderLayout.CENTER);
        centerPanel.add(messageLabel, BorderLayout.SOUTH);

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Close button
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { dispose(); }
        });
        mainPanel.add(closeBtn, BorderLayout.SOUTH);

        add(mainPanel);

        // Button actions
        payByTitleBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { reportPayByJobTitle(); }
        });

        payByDivBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { reportPayByDivision(); }
        });

        newHiresBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { reportNewHires(); }
        });

        setVisible(true);
    }

    private void reportPayByJobTitle() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query =
                "SELECT jt.job_title, COUNT(DISTINCT e.empid) AS num_employees, SUM(p.earnings) AS total_pay " +
                "FROM employees e " +
                "JOIN employee_job_titles ejt ON e.empid = ejt.empid " +
                "JOIN job_titles jt ON ejt.job_titleID = jt.job_titleID " +
                "JOIN payroll p ON e.empid = p.empid " +
                "GROUP BY jt.job_title ORDER BY total_pay DESC";

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            StringBuilder sb = new StringBuilder();
            sb.append("TOTAL PAY BY JOB TITLE\n");
            sb.append("=".repeat(55)).append("\n");
            sb.append(String.format("%-30s %-10s %-12s%n", "Job Title", "Employees", "Total Pay"));
            sb.append("-".repeat(55)).append("\n");

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                sb.append(String.format("%-30s %-10d $%-11.2f%n",
                    rs.getString("job_title"),
                    rs.getInt("num_employees"),
                    rs.getDouble("total_pay")));
            }
            if (!hasData) sb.append("No payroll data found.\n");
            conn.close();
            resultArea.setText(sb.toString());
            messageLabel.setForeground(new Color(0, 150, 0));
            messageLabel.setText("Report loaded successfully.");
        } catch (SQLException e) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    private void reportPayByDivision() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query =
                "SELECT d.divName AS division_name, COUNT(DISTINCT e.empid) AS num_employees, SUM(p.earnings) AS total_pay " +
                "FROM employees e " +
                "JOIN employee_division ed ON e.empid = ed.empid " +
                "JOIN division d ON ed.divID = d.divID " +
                "JOIN payroll p ON e.empid = p.empid " +
                "GROUP BY d.divName ORDER BY total_pay DESC";

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            StringBuilder sb = new StringBuilder();
            sb.append("TOTAL PAY BY DIVISION\n");
            sb.append("=".repeat(55)).append("\n");
            sb.append(String.format("%-25s %-10s %-12s%n", "Division", "Employees", "Total Pay"));
            sb.append("-".repeat(55)).append("\n");

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                sb.append(String.format("%-25s %-10d $%-11.2f%n",
                    rs.getString("division_name"),
                    rs.getInt("num_employees"),
                    rs.getDouble("total_pay")));
            }
            if (!hasData) sb.append("No payroll data found.\n");
            conn.close();
            resultArea.setText(sb.toString());
            messageLabel.setForeground(new Color(0, 150, 0));
            messageLabel.setText("Report loaded successfully.");
        } catch (SQLException e) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    private void reportNewHires() {
        String startDate = startDateField.getText().trim();
        String endDate   = endDateField.getText().trim();

        if (startDate.isEmpty() || endDate.isEmpty()) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Please enter start and end dates.");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            String query =
                "SELECT empid, Fname, Lname, email, HireDate, Salary " +
                "FROM employees WHERE HireDate BETWEEN ? AND ? ORDER BY HireDate ASC";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            ResultSet rs = stmt.executeQuery();

            StringBuilder sb = new StringBuilder();
            sb.append("NEW HIRES: " + startDate + " to " + endDate + "\n");
            sb.append("=".repeat(65)).append("\n");
            sb.append(String.format("%-5s %-20s %-25s %-12s%n", "ID", "Name", "Email", "Hire Date"));
            sb.append("-".repeat(65)).append("\n");

            int count = 0;
            while (rs.next()) {
                count++;
                sb.append(String.format("%-5d %-20s %-25s %-12s%n",
                    rs.getInt("empid"),
                    rs.getString("Fname") + " " + rs.getString("Lname"),
                    rs.getString("email"),
                    rs.getString("HireDate")));
            }
            if (count == 0) sb.append("No new hires found in this date range.\n");
            else sb.append("\nTotal new hires: " + count);

            conn.close();
            resultArea.setText(sb.toString());
            messageLabel.setForeground(new Color(0, 150, 0));
            messageLabel.setText("Report loaded successfully.");
        } catch (SQLException e) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Error: " + e.getMessage());
        }
    }
}
