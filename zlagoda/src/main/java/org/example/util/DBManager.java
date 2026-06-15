package org.example.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBManager {

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/ais_shop?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8&useUnicode=true";
        String user = "root";
        String password = "password";

        return DriverManager.getConnection(url, user, password);
    }
}