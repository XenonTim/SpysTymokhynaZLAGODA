ZLAGODA — AIS for Mini-Supermarket
Web application for managing sales processes in the ZLAGODA mini-supermarket.
Built with Java Servlets + JSP + MySQL, runs on Tomcat 7 via Maven.
Requirements
Java 17+
Maven 3.6+
MySQL 8.0+
Database Setup
Open MySQL and create the database:
CREATE DATABASE ais_shop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Run the schema file to create all tables:
src/main/resources/db/schema.sql
Run the data file to populate with sample data:
src/main/resources/db/data.sql
If needed, update the DB credentials in:
src/main/java/org/example/util/DBManager.java
Default values: user = "root", password = "password", database = ais_shop, port 3306.
Running the Application
From the project root directory, run:

mvn clean tomcat7:run
Then open in your browser:

http://localhost:8080/zlagoda
Login Credentials
All accounts use the same password: password123
RoleEmployee IDNameManager101Kovalenko IvanManager106Kravchenko OlenaManager110Moroz DmytroCashier102Melnyk OlhaCashier103Tkachenko AndriiUse the Employee ID as the login and password123 as the password.
User Roles
Manager — full access:

Add, edit, delete employees, products, categories, store products, customer cards, checks
View analytics and reports filtered by cashier and date range
Print reports for any section
Cashier — limited access:

Create and view their own checks
Search products and customer cards
Add and edit customer cards
Print individual check receipts
Key Features
Role-based access control (Manager / Cashier)
Product management with promotional pricing (20% discount)
Check creation with optional customer card discount
VAT calculation (20%) included in all prices
Filtering and sorting across all sections
Print preview for reports (opens in a separate window, no browser UI elements)
Individual check receipt printing in A5 format

