// SetupDatabase.java
// Run this file ONE TIME to set up the entire database.
// It creates all tables and inserts all sample data automatically.
// After running this you never need to touch dBeaver again.

import java.sql.*;

public class SetupDatabase {

    public static void main(String[] args) {
        System.out.println("Setting up database...");

        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();

            // turn off foreign key checks
            stmt.execute("SET FOREIGN_KEY_CHECKS=0");

            // drop all old tables so we start fresh
            System.out.println("Dropping old tables...");
            stmt.execute("DROP TABLE IF EXISTS addresses");
            stmt.execute("DROP TABLE IF EXISTS employee_division");
            stmt.execute("DROP TABLE IF EXISTS employee_job_titles");
            stmt.execute("DROP TABLE IF EXISTS payroll");
            stmt.execute("DROP TABLE IF EXISTS employees");
            stmt.execute("DROP TABLE IF EXISTS cities");
            stmt.execute("DROP TABLE IF EXISTS states");
            stmt.execute("DROP TABLE IF EXISTS division");
            stmt.execute("DROP TABLE IF EXISTS job_titles");

            // create all tables
            System.out.println("Creating tables...");

            stmt.execute("CREATE TABLE employees (" +
                "empid INT NOT NULL AUTO_INCREMENT," +
                "Fname VARCHAR(65) NOT NULL," +
                "Lname VARCHAR(65) NOT NULL," +
                "email VARCHAR(65) NOT NULL," +
                "HireDate DATE," +
                "Salary DECIMAL(10,2) NOT NULL," +
                "SSN VARCHAR(12)," +
                "PRIMARY KEY (empid))");

            stmt.execute("CREATE TABLE job_titles (" +
                "job_titleID INT NOT NULL AUTO_INCREMENT," +
                "job_title VARCHAR(125) NOT NULL," +
                "PRIMARY KEY (job_titleID))");

            stmt.execute("CREATE TABLE division (" +
                "divID INT NOT NULL AUTO_INCREMENT," +
                "divName VARCHAR(100) DEFAULT NULL," +
                "city VARCHAR(50) NOT NULL," +
                "addressLine1 VARCHAR(50) NOT NULL," +
                "addressLine2 VARCHAR(50) DEFAULT NULL," +
                "state VARCHAR(50) DEFAULT NULL," +
                "country VARCHAR(50) NOT NULL," +
                "postalCode VARCHAR(15) NOT NULL," +
                "PRIMARY KEY (divID))");

            stmt.execute("CREATE TABLE cities (" +
                "cityID INT NOT NULL AUTO_INCREMENT," +
                "cityName VARCHAR(100) NOT NULL," +
                "PRIMARY KEY (cityID))");

            stmt.execute("CREATE TABLE states (" +
                "stateID INT NOT NULL AUTO_INCREMENT," +
                "stateAbbr CHAR(2) NOT NULL," +
                "PRIMARY KEY (stateID))");

            stmt.execute("CREATE TABLE payroll (" +
                "payID INT NOT NULL AUTO_INCREMENT," +
                "pay_date DATE," +
                "earnings DECIMAL(8,2)," +
                "fed_tax DECIMAL(7,2)," +
                "fed_med DECIMAL(7,2)," +
                "fed_SS DECIMAL(7,2)," +
                "state_tax DECIMAL(7,2)," +
                "retire_401k DECIMAL(7,2)," +
                "health_care DECIMAL(7,2)," +
                "empid INT NOT NULL," +
                "PRIMARY KEY (payID)," +
                "FOREIGN KEY (empid) REFERENCES employees(empid))");

            stmt.execute("CREATE TABLE employee_job_titles (" +
                "empid INT NOT NULL," +
                "job_titleID INT NOT NULL," +
                "PRIMARY KEY (empid, job_titleID)," +
                "FOREIGN KEY (empid) REFERENCES employees(empid)," +
                "FOREIGN KEY (job_titleID) REFERENCES job_titles(job_titleID))");

            stmt.execute("CREATE TABLE employee_division (" +
                "empid INT NOT NULL," +
                "divID INT NOT NULL," +
                "PRIMARY KEY (empid)," +
                "FOREIGN KEY (empid) REFERENCES employees(empid)," +
                "FOREIGN KEY (divID) REFERENCES division(divID))");

            stmt.execute("CREATE TABLE addresses (" +
                "empid INT NOT NULL," +
                "street VARCHAR(100) NOT NULL," +
                "cityID INT NOT NULL," +
                "stateID INT NOT NULL," +
                "zip VARCHAR(10) NOT NULL," +
                "DOB DATE," +
                "phone VARCHAR(20)," +
                "emergency_contact VARCHAR(100)," +
                "emergency_phone VARCHAR(20)," +
                "PRIMARY KEY (empid)," +
                "FOREIGN KEY (empid) REFERENCES employees(empid)," +
                "FOREIGN KEY (cityID) REFERENCES cities(cityID)," +
                "FOREIGN KEY (stateID) REFERENCES states(stateID))");

            System.out.println("Tables created successfully!");

            // insert all 50 states
            System.out.println("Inserting states...");
            String[] states = {"AL","AK","AZ","AR","CA","CO","CT","DE","FL","GA",
                               "HI","ID","IL","IN","IA","KS","KY","LA","ME","MD",
                               "MA","MI","MN","MS","MO","MT","NE","NV","NH","NJ",
                               "NM","NY","NC","ND","OH","OK","OR","PA","RI","SC",
                               "SD","TN","TX","UT","VT","VA","WA","WV","WI","WY"};
            for (String s : states) {
                stmt.execute("INSERT INTO states (stateAbbr) VALUES ('" + s + "')");
            }

            // insert starter cities
            stmt.execute("INSERT INTO cities (cityName) VALUES ('Atlanta')");
            stmt.execute("INSERT INTO cities (cityName) VALUES ('New York')");
            stmt.execute("INSERT INTO cities (cityName) VALUES ('Los Angeles')");
            stmt.execute("INSERT INTO cities (cityName) VALUES ('Chicago')");
            stmt.execute("INSERT INTO cities (cityName) VALUES ('Houston')");

            System.out.println("Inserting sample employees...");

            // insert 15 realistic employees with gmail addresses and recent hire dates
            stmt.execute("INSERT INTO employees (empid,Fname,Lname,email,HireDate,Salary,SSN) VALUES (1,'James','Wilson','james.wilson@gmail.com','2020-03-15',95000.00,'111-11-1111')");
            stmt.execute("INSERT INTO employees (empid,Fname,Lname,email,HireDate,Salary,SSN) VALUES (2,'Sarah','Johnson','sarah.johnson@gmail.com','2019-06-01',88000.00,'111-22-1111')");
            stmt.execute("INSERT INTO employees (empid,Fname,Lname,email,HireDate,Salary,SSN) VALUES (3,'Michael','Davis','michael.davis@gmail.com','2021-01-10',75000.00,'111-33-1111')");
            stmt.execute("INSERT INTO employees (empid,Fname,Lname,email,HireDate,Salary,SSN) VALUES (4,'Emily','Brown','emily.brown@gmail.com','2022-04-20',72000.00,'111-44-1111')");
            stmt.execute("INSERT INTO employees (empid,Fname,Lname,email,HireDate,Salary,SSN) VALUES (5,'Daniel','Martinez','daniel.martinez@gmail.com','2020-09-05',68000.00,'111-55-1111')");
            stmt.execute("INSERT INTO employees (empid,Fname,Lname,email,HireDate,Salary,SSN) VALUES (6,'Ashley','Taylor','ashley.taylor@gmail.com','2023-02-14',55000.00,'111-66-1111')");
            stmt.execute("INSERT INTO employees (empid,Fname,Lname,email,HireDate,Salary,SSN) VALUES (7,'Christopher','Anderson','chris.anderson@gmail.com','2021-07-19',82000.00,'111-77-1111')");
            stmt.execute("INSERT INTO employees (empid,Fname,Lname,email,HireDate,Salary,SSN) VALUES (8,'Jessica','Thomas','jessica.thomas@gmail.com','2022-11-03',63000.00,'111-88-1111')");
            stmt.execute("INSERT INTO employees (empid,Fname,Lname,email,HireDate,Salary,SSN) VALUES (9,'Matthew','Jackson','matt.jackson@gmail.com','2019-08-22',91000.00,'111-99-1111')");
            stmt.execute("INSERT INTO employees (empid,Fname,Lname,email,HireDate,Salary,SSN) VALUES (10,'Amanda','White','amanda.white@gmail.com','2023-05-30',58000.00,'111-00-1111')");
            stmt.execute("INSERT INTO employees (empid,Fname,Lname,email,HireDate,Salary,SSN) VALUES (11,'Joshua','Harris','joshua.harris@gmail.com','2020-12-07',47000.00,'222-11-1111')");
            stmt.execute("INSERT INTO employees (empid,Fname,Lname,email,HireDate,Salary,SSN) VALUES (12,'Stephanie','Clark','stephanie.clark@gmail.com','2021-03-25',44000.00,'333-11-1111')");
            stmt.execute("INSERT INTO employees (empid,Fname,Lname,email,HireDate,Salary,SSN) VALUES (13,'Ryan','Lewis','ryan.lewis@gmail.com','2022-08-16',42000.00,'444-11-1111')");
            stmt.execute("INSERT INTO employees (empid,Fname,Lname,email,HireDate,Salary,SSN) VALUES (14,'Nicole','Robinson','nicole.robinson@gmail.com','2023-01-09',40000.00,'555-11-1111')");
            stmt.execute("INSERT INTO employees (empid,Fname,Lname,email,HireDate,Salary,SSN) VALUES (15,'Kevin','Walker','kevin.walker@gmail.com','2024-06-17',38000.00,'777-11-1111')");

            // insert job titles
            System.out.println("Inserting job titles...");
            stmt.execute("INSERT INTO job_titles (job_titleID,job_title) VALUES (100,'software manager')");
            stmt.execute("INSERT INTO job_titles (job_titleID,job_title) VALUES (101,'software architect')");
            stmt.execute("INSERT INTO job_titles (job_titleID,job_title) VALUES (102,'software engineer')");
            stmt.execute("INSERT INTO job_titles (job_titleID,job_title) VALUES (103,'software developer')");
            stmt.execute("INSERT INTO job_titles (job_titleID,job_title) VALUES (200,'marketing manager')");
            stmt.execute("INSERT INTO job_titles (job_titleID,job_title) VALUES (201,'marketing associate')");
            stmt.execute("INSERT INTO job_titles (job_titleID,job_title) VALUES (202,'marketing assistant')");
            stmt.execute("INSERT INTO job_titles (job_titleID,job_title) VALUES (900,'Chief Exec. Officer')");
            stmt.execute("INSERT INTO job_titles (job_titleID,job_title) VALUES (901,'Chief Finn. Officer')");
            stmt.execute("INSERT INTO job_titles (job_titleID,job_title) VALUES (902,'Chief Info. Officer')");

            // insert divisions
            System.out.println("Inserting divisions...");
            stmt.execute("INSERT INTO division (divID,divName,city,addressLine1,addressLine2,state,country,postalCode) VALUES (1,'Technology Engineering','Atlanta','200 17th Street NW','','GA','USA','30363')");
            stmt.execute("INSERT INTO division (divID,divName,city,addressLine1,addressLine2,state,country,postalCode) VALUES (2,'Marketing','Atlanta','200 17th Street NW','','GA','USA','30363')");
            stmt.execute("INSERT INTO division (divID,divName,city,addressLine1,addressLine2,state,country,postalCode) VALUES (3,'Human Resources','New York','45 West 57th Street','','NY','USA','00034')");
            stmt.execute("INSERT INTO division (divID,divName,city,addressLine1,addressLine2,state,country,postalCode) VALUES (999,'HQ','New York','45 West 57th Street','','NY','USA','00034')");

            // connect employees to job titles
            System.out.println("Inserting employee job titles...");
            int[][] ejt = {{1,900},{2,901},{3,902},{4,101},{5,102},{6,102},{7,100},{8,103},{9,101},{10,102},{11,200},{12,201},{13,202},{14,201},{15,103}};
            for (int[] pair : ejt) {
                stmt.execute("INSERT INTO employee_job_titles (empid,job_titleID) VALUES (" + pair[0] + "," + pair[1] + ")");
            }

            // connect employees to divisions
            stmt.execute("INSERT INTO employee_division (empid,divID) VALUES (1,999)");
            stmt.execute("INSERT INTO employee_division (empid,divID) VALUES (2,999)");
            stmt.execute("INSERT INTO employee_division (empid,divID) VALUES (3,999)");
            stmt.execute("INSERT INTO employee_division (empid,divID) VALUES (4,1)");
            stmt.execute("INSERT INTO employee_division (empid,divID) VALUES (5,1)");
            stmt.execute("INSERT INTO employee_division (empid,divID) VALUES (6,1)");
            stmt.execute("INSERT INTO employee_division (empid,divID) VALUES (7,1)");
            stmt.execute("INSERT INTO employee_division (empid,divID) VALUES (8,1)");
            stmt.execute("INSERT INTO employee_division (empid,divID) VALUES (9,2)");
            stmt.execute("INSERT INTO employee_division (empid,divID) VALUES (10,2)");
            stmt.execute("INSERT INTO employee_division (empid,divID) VALUES (11,2)");
            stmt.execute("INSERT INTO employee_division (empid,divID) VALUES (12,3)");
            stmt.execute("INSERT INTO employee_division (empid,divID) VALUES (13,3)");
            stmt.execute("INSERT INTO employee_division (empid,divID) VALUES (14,3)");
            stmt.execute("INSERT INTO employee_division (empid,divID) VALUES (15,3)");

            // insert addresses
            // cityID: 1=Atlanta 2=New York 3=Los Angeles 4=Chicago 5=Houston
            // stateID: 10=GA 32=NY 5=CA 14=IL 43=TX
            System.out.println("Inserting addresses...");
            stmt.execute("INSERT INTO addresses VALUES (1,'123 Peachtree St',1,10,'30301','1985-06-12','404-111-0001','Mary Wilson','404-111-9001')");
            stmt.execute("INSERT INTO addresses VALUES (2,'456 Park Ave',2,32,'10001','1980-09-23','212-111-0002','Tom Johnson','212-111-9002')");
            stmt.execute("INSERT INTO addresses VALUES (3,'789 Sunset Blvd',3,5,'90001','1990-03-08','310-111-0003','Lisa Davis','310-111-9003')");
            stmt.execute("INSERT INTO addresses VALUES (4,'321 Maple St',1,10,'30302','1995-11-17','404-111-0004','John Brown','404-111-9004')");
            stmt.execute("INSERT INTO addresses VALUES (5,'654 Oak Ave',4,14,'60601','1988-07-04','312-111-0005','Ana Martinez','312-111-9005')");
            stmt.execute("INSERT INTO addresses VALUES (6,'987 Pine Rd',1,10,'30303','1998-02-28','404-111-0006','Mark Taylor','404-111-9006')");
            stmt.execute("INSERT INTO addresses VALUES (7,'111 Elm St',1,10,'30304','1987-05-15','404-111-0007','Sue Anderson','404-111-9007')");
            stmt.execute("INSERT INTO addresses VALUES (8,'222 Cedar Ln',2,32,'10002','1993-10-31','212-111-0008','Bob Thomas','212-111-9008')");
            stmt.execute("INSERT INTO addresses VALUES (9,'333 Birch Way',1,10,'30305','1982-12-25','404-111-0009','Kim Jackson','404-111-9009')");
            stmt.execute("INSERT INTO addresses VALUES (10,'444 Walnut Dr',5,43,'77001','1996-04-19','713-111-0010','Dan White','713-111-9010')");
            stmt.execute("INSERT INTO addresses VALUES (11,'555 Spruce Ct',1,10,'30306','1991-08-07','404-111-0011','Amy Harris','404-111-9011')");
            stmt.execute("INSERT INTO addresses VALUES (12,'666 Hickory Pl',1,10,'30307','1994-01-14','404-111-0012','Joe Clark','404-111-9012')");
            stmt.execute("INSERT INTO addresses VALUES (13,'777 Magnolia Rd',1,10,'30308','1997-06-30','404-111-0013','Eve Lewis','404-111-9013')");
            stmt.execute("INSERT INTO addresses VALUES (14,'888 Willow Ave',2,32,'10003','1999-03-22','212-111-0014','Sam Robinson','212-111-9014')");
            stmt.execute("INSERT INTO addresses VALUES (15,'999 Poplar St',1,10,'30309','2000-09-05','404-111-0015','Jen Walker','404-111-9015')");

            // insert payroll records for all employees
            System.out.println("Inserting payroll data...");
            stmt.execute("INSERT INTO payroll (pay_date,empid,earnings,fed_tax,fed_med,fed_SS,state_tax,retire_401k,health_care) SELECT '2026-01-31',empid,Salary/52.0,(Salary/52.0)*0.32,(Salary/52.0)*0.0145,(Salary/52.0)*0.062,(Salary/52.0)*0.12,(Salary/52.0)*0.004,(Salary/52.0)*0.031 FROM employees");
            stmt.execute("INSERT INTO payroll (pay_date,empid,earnings,fed_tax,fed_med,fed_SS,state_tax,retire_401k,health_care) SELECT '2025-12-31',empid,Salary/52.0,(Salary/52.0)*0.32,(Salary/52.0)*0.0145,(Salary/52.0)*0.062,(Salary/52.0)*0.12,(Salary/52.0)*0.004,(Salary/52.0)*0.031 FROM employees");
            stmt.execute("INSERT INTO payroll (pay_date,empid,earnings,fed_tax,fed_med,fed_SS,state_tax,retire_401k,health_care) SELECT '2025-11-30',empid,Salary/52.0,(Salary/52.0)*0.32,(Salary/52.0)*0.0145,(Salary/52.0)*0.062,(Salary/52.0)*0.12,(Salary/52.0)*0.004,(Salary/52.0)*0.031 FROM employees");

            // turn foreign key checks back on
            stmt.execute("SET FOREIGN_KEY_CHECKS=1");

            conn.close();
            System.out.println("=================================");
            System.out.println("DATABASE SETUP COMPLETE!");
            System.out.println("=================================");
            System.out.println("HR Admin login: admin / admin123");
            System.out.println("Employee login: empID / SSN");
            System.out.println("Example: 7 / 111-77-1111");

        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}
