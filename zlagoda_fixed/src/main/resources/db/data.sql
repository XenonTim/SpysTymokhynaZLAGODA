INSERT INTO Category (category_name,category_number) VALUES
('meat',1),
('dairy products',2),
('seafood',3),
('alcohol',4),
('soft drinks',5),
('fruits',6),
('vegetables',7),
('dry goods',8),
('sweets',9),
('baked goods',10);

INSERT INTO Customer_Card (cust_surname,cust_name,cust_patronymic,phone_number,city,street,zip_code,percent,card_number) VALUES
('Petrenko','Svitlana','Olehivna','+380671002030','Kyiv','Baumana','10068',30,1001),
('Sydorenko','Petro','Pavlovych','+380932003040','Lviv','Lisozakhysna','10937',15,1002),
('Vasylenko','Hanna','Antonivna','+380503004050','Odesa','Avtozavodskyi','10003',25,1003),
('Lysenko','Mykhailo','Fedorovych','+380984005060','Dnipro','Saliutna','11495',20,1004),
('Pavlenko','Yuliia','Viktorivna','+380676007080','Kyiv','Yalynkovyi','12067',15,1005),
('Romaniuk','Artem','Leonidovych','+380957008090','Vinnytsia','Hostomelska','10375',25,1006),
('Kozak','Olena','Stepanivna','+380688009000','Poltava','Sorochynska','11580',15,1007),
('Danyliuk','Ihor','Mykhailovych','+380939000111','Chernihiv','Cherkaska','11858',10,1008),
('Stepaniuk','Iryna','Borysivna','+380500112233','Cherkasy','Basein','10062',25,1009),
('Melnyk','Oleh','Serhiiovych','+380635006074','Lviv','Svaromska','12040',5,1010);

INSERT INTO Employee (empl_surname,empl_name,empl_patronymic,empl_role,salary,date_of_birth,date_of_start,phone_number,city,street,zip_code,id_employee) VALUES
('Kovalenko','Ivan','Mykolaiovych','Менеджер',25000,'1990-05-15 00:00:00.000000','2020-01-10 00:00:00.000000','+380971112233','Kyiv','Umanska','11735',101),
('Melnyk','Olha','Petrivna','Касир',22000,'1995-08-20 00:00:00.000000','2021-03-15 00:00:00.000000','+380932223344','Kyiv','Harazhna','10304',102),
('Tkachenko','Andrii','Ivanovych','Касир',19000,'1988-12-02 00:00:00.000000','2019-11-05 00:00:00.000000','+380503334455','Kyiv','Trukhanivska','11711',103),
('Shevchenko','Mariia','Vasylivna','Касир',21000,'1992-03-25 00:00:00.000000','2022-06-01 00:00:00.000000','+380674445566','Kyiv','Borova','10184',104),
('Bondarenko','Serhii','Oleksandrovych','Касир',19500,'1997-07-10 00:00:00.000000','2023-01-20 00:00:00.000000','+380985556677','Kyiv','Telihy Oleny','11663',105),
('Kravchenko','Olena','Dmytrivna','Менеджер',23000,'1991-11-30 00:00:00.000000','2020-09-12 00:00:00.000000','+380636667788','Kyiv','Zelena','12449',106),
('Oliinyk','Viktor','Serhiiovych','Касир',20000,'1985-04-14 00:00:00.000000','2018-05-25 00:00:00.000000','+380507778899','Kyiv','Shosta','12573',107),
('Polishchuk','Anna','Ihorivna','Касир',22500,'1994-09-05 00:00:00.000000','2021-10-10 00:00:00.000000','+380678889900','Kyiv','Honcharna','10353',108),
('Savchenko','Tetiana','Volodymyrivna','Касир',18500,'1999-01-18 00:00:00.000000','2024-02-01 00:00:00.000000','+380939990011','Kyiv','Dachna','12636',109),
('Moroz','Dmytro','Yuriiovych','Менеджер',24000,'1989-06-22 00:00:00.000000','2019-02-28 00:00:00.000000','+380980001122','Kyiv','Yasenevyi','11961',110);

INSERT INTO Product (category_number,id_product,product_name,characteristics) VALUES
(1,101,'Chicken drumsticks','1kg'),
(2,102,'Milk','900ml, 2.5%, ultra pasterised'),
(3,103,'Salmon fillet','200g'),
(4,104,'Red wine','1l, 11%'),
(5,105,'Sparkling water','250ml'),
(6,106,'Bananas','imported from Equador'),
(7,107,'Tomatoes','domestic'),
(8,108,'Spagheti','400g'),
(9,109,'Chocolate','90g, dark with nuts'),
(10,110,'Bread','350g, sliced');

INSERT INTO Store_Product (UPC_prom,UPC,id_product,selling_price,products_number,promotional_product) VALUES
(1000000001,1000000001,101,109,32,true),
(1000000002,1000000002,102,70,100,true),
(NULL,1000000003,103,167,14,false),
(1000000004,1000000004,104,95,54,true),
(NULL,1000000005,105,45,48,false),
(NULL,1000000006,106,74,23,false),
(NULL,1000000007,107,190,90,false),
(NULL,1000000008,108,55,42,false),
(NULL,1000000009,109,100,100,false),
(NULL,1000000010,110,36,72,false);

INSERT INTO `Check` (id_employee,check_number,card_number,print_date,sum_total,vat) VALUES
(101,10001,1005,'2025-12-11 00:00:00.000000',298,60),
(102,10002,NULL,'2025-07-22 00:00:00.000000',710,142),
(103,10003,1006,'2025-11-04 00:00:00.000000',410,82),
(104,10004,1006,'2026-02-10 00:00:00.000000',374,75),
(105,10005,1007,'2025-10-13 00:00:00.000000',680,136),
(106,10006,NULL,'2025-07-22 00:00:00.000000',679,136),
(107,10007,NULL,'2025-01-16 00:00:00.000000',469,94),
(108,10008,1010,'2025-06-08 00:00:00.000000',573,115),
(109,10009,NULL,'2025-03-24 00:00:00.000000',399,80),
(110,10010,NULL,'2025-01-26 00:00:00.000000',636,127);

INSERT INTO Sale (UPC,check_number,product_number,selling_price) VALUES
(1000000001,10006,1,109),
(1000000002,10002,2,70),
(1000000003,10007,2,167),
(1000000003,10008,3,167),
(1000000003,10009,1,167),
(1000000003,10010,3,167),
(1000000004,10001,2,95),
(1000000005,10007,3,45),
(1000000005,10010,3,45),
(1000000006,10004,1,74),
(1000000006,10009,3,74),
(1000000007,10002,3,190),
(1000000007,10005,2,190),
(1000000007,10006,3,190),
(1000000008,10003,2,55),
(1000000009,10003,3,100),
(1000000009,10004,3,100),
(1000000009,10005,3,100),
(1000000010,10001,3,36),
(1000000010,10008,2,36);


-- ВИПРАВЛЕНО: попередні значення password_hash були "випадковими" рядками
-- (не відповідали жодному паролю через SHA-256+Base64, тому вхід у систему
-- був неможливим для жодного користувача).
-- Для всіх тестових облікових записів встановлено єдиний пароль "password123"
-- (PasswordUtil.hashPassword("password123") = Base64(SHA-256("password123")))
-- — відповідно до README.md.
INSERT INTO User_Auth (id_employee, password_hash) VALUES
(101, '75K3eLr+dx6JJFuJ7LwIpEpOFmwGZZkRiB84PURz6U8='), -- пароль: password123
(102, '75K3eLr+dx6JJFuJ7LwIpEpOFmwGZZkRiB84PURz6U8='), -- пароль: password123
(103, '75K3eLr+dx6JJFuJ7LwIpEpOFmwGZZkRiB84PURz6U8='), -- пароль: password123
(104, '75K3eLr+dx6JJFuJ7LwIpEpOFmwGZZkRiB84PURz6U8='), -- пароль: password123
(105, '75K3eLr+dx6JJFuJ7LwIpEpOFmwGZZkRiB84PURz6U8='), -- пароль: password123
(106, '75K3eLr+dx6JJFuJ7LwIpEpOFmwGZZkRiB84PURz6U8='), -- пароль: password123
(107, '75K3eLr+dx6JJFuJ7LwIpEpOFmwGZZkRiB84PURz6U8='), -- пароль: password123
(108, '75K3eLr+dx6JJFuJ7LwIpEpOFmwGZZkRiB84PURz6U8='), -- пароль: password123
(109, '75K3eLr+dx6JJFuJ7LwIpEpOFmwGZZkRiB84PURz6U8='), -- пароль: password123
(110, '75K3eLr+dx6JJFuJ7LwIpEpOFmwGZZkRiB84PURz6U8='); -- пароль: password123