// DatabaseConnection.java
// This file handles the connection to the MySQL database.
// Every other file in the project will use this to connect.

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Database connection details
    private static final String HOST     = "localhost"; //change this to your MySQL HOST
    private static final String PORT     = "3306"; //change this to your Port
    private static final String DATABASE = "employeeData"; //change this to your MySQL DATABASE
    private static final String USERNAME = "your username"; //change this to you MySQL USERNAME
    private static final String PASSWORD = "your password"; //change this to you MySQL PASSWORD

    // This builds the full connection URL MySQL needs
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?sslMode=REQUIRED";

    // This method returns a connection to the database
    // Call this from any other file when you need to talk to the database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    // Quick test to make sure the connection works
    // You can run this file by itself to test before running the full app
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        try {
            Connection conn = getConnection();
            if (conn != null) {
                System.out.println("SUCCESS - Connected to database: " + DATABASE);
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("FAILED - Could not connect to database.");
            System.out.println("Error: " + e.getMessage());
        }
    }
}