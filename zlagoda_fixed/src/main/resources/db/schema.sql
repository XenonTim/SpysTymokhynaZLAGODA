SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS Sale;
DROP TABLE IF EXISTS Store_Product;
DROP TABLE IF EXISTS Product;
DROP TABLE IF EXISTS `Check`;
DROP TABLE IF EXISTS User_Auth;
DROP TABLE IF EXISTS Employee;
DROP TABLE IF EXISTS Customer_Card;
DROP TABLE IF EXISTS Category;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE Category (
category_number INT(11) NOT NULL AUTO_INCREMENT,
category_name VARCHAR(50) NOT NULL,
PRIMARY KEY (category_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Customer_Card (
card_number INT(11) NOT NULL AUTO_INCREMENT,
cust_surname VARCHAR(50) NOT NULL,
cust_name VARCHAR(50) NOT NULL,
cust_patronymic VARCHAR(50),
phone_number VARCHAR(13) NOT NULL,
city VARCHAR(50),
street VARCHAR(50),
zip_code VARCHAR(9),
percent INT(11) DEFAULT 0 NOT NULL,
PRIMARY KEY (card_number),
CONSTRAINT chk_card_percent CHECK (percent BETWEEN 0 AND 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Employee (
id_employee INT(11) NOT NULL AUTO_INCREMENT,
empl_surname VARCHAR(50) NOT NULL,
empl_name VARCHAR(50) NOT NULL,
empl_patronymic VARCHAR(50),
empl_role VARCHAR(10) NOT NULL,
salary DECIMAL(13,2) DEFAULT 0.00 NOT NULL,
date_of_birth DATETIME NOT NULL,
date_of_start DATETIME NOT NULL,
phone_number VARCHAR(13),
city VARCHAR(50) NOT NULL,
street VARCHAR(50) NOT NULL,
zip_code VARCHAR(9) NOT NULL,
PRIMARY KEY (id_employee),
CONSTRAINT chk_employee_salary CHECK (salary >= 0),
CONSTRAINT chk_employee_birth_year CHECK (YEAR(date_of_birth) <= 2008)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE User_Auth (
id_employee INT(11) NOT NULL,
password_hash VARCHAR(255) NOT NULL,
PRIMARY KEY (id_employee),
CONSTRAINT fk_auth_employee FOREIGN KEY (id_employee) REFERENCES Employee (id_employee) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Check` (
check_number INT(11) NOT NULL AUTO_INCREMENT,
id_employee INT(11) NOT NULL,
card_number INT(11) DEFAULT NULL,
print_date DATETIME NOT NULL,
sum_total DECIMAL(13,2) DEFAULT 0.00 NOT NULL,
vat DECIMAL(13,2) DEFAULT 0.00 NOT NULL,
PRIMARY KEY (check_number),
CONSTRAINT fk_check_employee FOREIGN KEY (id_employee) REFERENCES Employee (id_employee) ON DELETE RESTRICT ON UPDATE CASCADE,
CONSTRAINT fk_check_customer_card FOREIGN KEY (card_number) REFERENCES Customer_Card (card_number) ON DELETE SET NULL ON UPDATE CASCADE,
CONSTRAINT chk_check_sum_total CHECK (sum_total >= 0),
CONSTRAINT chk_check_vat CHECK (vat >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Product (
id_product INT(11) NOT NULL AUTO_INCREMENT,
category_number INT(11) NOT NULL,
product_name VARCHAR(50) NOT NULL,
characteristics VARCHAR(100) NOT NULL,
PRIMARY KEY (id_product),
CONSTRAINT fk_product_category FOREIGN KEY (category_number) REFERENCES Category (category_number) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Store_Product (
UPC INT(11) NOT NULL AUTO_INCREMENT,
UPC_prom INT(11) DEFAULT NULL,
id_product INT(11) NOT NULL,
selling_price DECIMAL(13,2) DEFAULT 0.00 NOT NULL,
products_number INT(11) DEFAULT 0 NOT NULL,
promotional_product BOOLEAN DEFAULT false,
PRIMARY KEY (UPC),
CONSTRAINT fk_store_product_product FOREIGN KEY (id_product) REFERENCES Product (id_product) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT fk_store_product_prom FOREIGN KEY (UPC_prom) REFERENCES Store_Product (UPC) ON DELETE SET NULL ON UPDATE CASCADE,
CONSTRAINT chk_store_product_price CHECK (selling_price >= 0),
CONSTRAINT chk_store_product_qty CHECK (products_number >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Sale (
UPC INT(11) NOT NULL,
check_number INT(11) NOT NULL,
product_number INT(11) DEFAULT 0 NOT NULL,
selling_price DECIMAL(13,2) DEFAULT 0.00 NOT NULL,
PRIMARY KEY (UPC, check_number),
CONSTRAINT fk_sale_store_product FOREIGN KEY (UPC) REFERENCES Store_Product (UPC) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT fk_sale_check FOREIGN KEY (check_number) REFERENCES `Check` (check_number) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT chk_sale_qty CHECK (product_number > 0),
CONSTRAINT chk_sale_price CHECK (selling_price >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;