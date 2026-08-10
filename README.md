# Employee-Management-System
A Java-based Employee Management System with MySQL database integration for managing employee records, payroll, job titles, divisions, and HR operations.

## Features

- Employee login using Employee ID and SSN
- HR administrator login
- Add new employees
- Delete employees
- Search and edit employee information
- View employee records
- Manage employee job titles and divisions
- Manage employee addresses
- Store and retrieve payroll information
- MySQL database integration using JDBC

## Technologies Used

- Java
- MySQL
- JDBC
- MySQL Connector/J
- Aiven MySQL Cloud Database

## Project Structure

The project includes several Java classes for different parts of the system, including:

- `DatabaseConnection.java` – Connects the application to the MySQL database
- `SetupDatabase.java` – Creates and initializes the database tables
- `EmployeeMenu.java` – Main employee menu
- `HRAdminMenu.java` – Main HR administrator menu
- `AddEmployeeScreen.java` – Adds new employee records
- `DeleteEmployeeScreen.java` – Deletes employee records
- Additional classes for searching, editing, and managing employee information

## Database Setup

Before running the application, configure your database connection in:

`DatabaseConnection.java`

Update the following values with your own database credentials:

```java
HOST
PORT
DATABASE
USERNAME
PASSWORD
```
## Running the Project

Make sure you have MySQL installed on your computer
Open DatabaseConnection.java and on line 14 change NewPassword123! to your own MySQL password
Open terminal and go to the EmployeeSystem folder
Run this to set up the database:
java -cp .:mysql-connector-j-9.6.0.jar SetupDatabase
Then run the app:
java -cp .:mysql-connector-j-9.6.0.jar LoginScreen

## Sample Login Information

After running the database setup:

### HR Administrator

- Username: `admin`
- Password: `admin123`

### Employee

Log in using an Employee ID and SSN.

Example:

- Employee ID: `7`
- SSN: `111-77-1111`

