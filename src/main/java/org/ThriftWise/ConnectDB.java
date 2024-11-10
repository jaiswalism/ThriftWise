package org.ThriftWise;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    private static Connection conn = null;

    // Modified to use synchronized to ensure thread safety.
    private static synchronized void startConnection() {
        if (conn == null) {
            try {
                String url = "jdbc:sqlite:./expense.db";
                conn = DriverManager.getConnection(url);
                System.out.println("Connected!");
            } catch (SQLException e) {
                System.out.println("Connection error: " + e.getMessage());
            }
        }
    }

    public static Connection connect() {
        if (conn == null) {
            startConnection();
        }
        return conn;
    }

    // Ensure the connection is closed properly when the app shuts down
    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Connection closed!");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}
