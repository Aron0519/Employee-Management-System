// HRAdminMenu.java
// This is the main menu window for HR Admin.
// It shows after HR Admin logs in successfully.
// It has buttons for all HR Admin features.

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HRAdminMenu extends JFrame {

    public HRAdminMenu() {
        setTitle("Employee Management System - HR Admin Menu");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Title at the top
        JLabel titleLabel = new JLabel("HR Admin Menu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Buttons panel in the middle
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(7, 1, 10, 10));

        // Create all the buttons
        JButton addEmployeeBtn    = new JButton("Add New Employee");
        JButton searchEditBtn     = new JButton("Search / Edit Employee");
        JButton deleteEmployeeBtn = new JButton("Delete Employee");
        JButton salaryUpdateBtn   = new JButton("Update Salary by %");
        JButton reportsBtn        = new JButton("View Reports");
        JButton viewAllBtn        = new JButton("View All Employees");
        JButton logoutBtn         = new JButton("Logout");

        // Set font for all buttons
        Font btnFont = new Font("Arial", Font.PLAIN, 14);
        addEmployeeBtn.setFont(btnFont);
        searchEditBtn.setFont(btnFont);
        deleteEmployeeBtn.setFont(btnFont);
        salaryUpdateBtn.setFont(btnFont);
        reportsBtn.setFont(btnFont);
        viewAllBtn.setFont(btnFont);
        logoutBtn.setFont(btnFont);

        // Make logout button red
        logoutBtn.setForeground(Color.RED);

        // Add buttons to panel
        buttonPanel.add(addEmployeeBtn);
        buttonPanel.add(searchEditBtn);
        buttonPanel.add(deleteEmployeeBtn);
        buttonPanel.add(salaryUpdateBtn);
        buttonPanel.add(reportsBtn);
        buttonPanel.add(viewAllBtn);
        buttonPanel.add(logoutBtn);

        panel.add(buttonPanel, BorderLayout.CENTER);
        add(panel);

        // ── Button Actions ──────────────────────────────────────

        // Add New Employee button
        addEmployeeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AddEmployeeScreen();
            }
        });

        // Search / Edit Employee button
        searchEditBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new SearchEditEmployeeScreen();
            }
        });

        // Delete Employee button
        deleteEmployeeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new DeleteEmployeeScreen();
            }
        });

        // Update Salary button
        salaryUpdateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new SalaryUpdateScreen();
            }
        });

        // Reports button
        reportsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ReportsScreen();
            }
        });

        // View All Employees button
        viewAllBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ViewAllEmployeesScreen();
            }
        });

        // Logout button - goes back to login screen
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
}
