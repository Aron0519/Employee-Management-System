// ViewAllEmployeesScreen.java
// This screen shows a table of all employees in the database.
// HR Admin can use this to quickly see everyone at once.

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ViewAllEmployeesScreen extends JFrame {

    public ViewAllEmployeesScreen() {
        setTitle("All Employees");
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title
        JLabel title = new JLabel("All Employees", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(title, BorderLayout.NORTH);

        // Table columns
        String[] columns = {"ID", "First Name", "Last Name", "Email", "Hire Date", "Salary", "Job Title"};

        // Load data from database
        Object[][] data = loadEmployees();

        // Create table
        JTable table = new JTable(data, columns) {
            // Make table cells not editable
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(22);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(160);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with count and close button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel countLabel = new JLabel("Total employees: " + data.length);
        countLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        countLabel.setForeground(Color.GRAY);
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { dispose(); }
        });
        bottomPanel.add(countLabel);
        bottomPanel.add(closeBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    // Load all employees from the database and return as 2D array for the table
    private Object[][] loadEmployees() {
        try {
            Connection conn = DatabaseConnection.getConnection();

            String query =
                "SELECT e.empid, e.Fname, e.Lname, e.email, e.HireDate, e.Salary, " +
                "COALESCE(jt.job_title, 'N/A') AS job_title " +
                "FROM employees e " +
                "LEFT JOIN employee_job_titles ejt ON e.empid = ejt.empid " +
                "LEFT JOIN job_titles jt ON ejt.job_titleID = jt.job_titleID " +
                "ORDER BY e.empid ASC";

            PreparedStatement stmt = conn.prepareStatement(query,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery();

            // Count rows first
            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst();

            Object[][] data = new Object[rowCount][7];
            int i = 0;
            while (rs.next()) {
                data[i][0] = rs.getInt("empid");
                data[i][1] = rs.getString("Fname");
                data[i][2] = rs.getString("Lname");
                data[i][3] = rs.getString("email");
                data[i][4] = rs.getString("HireDate");
                data[i][5] = "$" + String.format("%.2f", rs.getDouble("Salary"));
                data[i][6] = rs.getString("job_title");
                i++;
            }

            conn.close();
            return data;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading employees: " + e.getMessage());
            return new Object[0][7];
        }
    }
}
