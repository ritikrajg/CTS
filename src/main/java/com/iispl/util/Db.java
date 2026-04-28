package com.iispl.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {

    // 🔹 Change these as per your setup
    private static final String URL = "jdbc:postgresql://localhost:5432/cts";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    // 🔹 Static block (loads driver once)
    static {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("✅ PostgreSQL Driver Loaded");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver not found!");
            e.printStackTrace();
        }
    }

    // 🔹 Get DB Connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 🔹 Close Connection safely
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("🔒 Connection closed");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}