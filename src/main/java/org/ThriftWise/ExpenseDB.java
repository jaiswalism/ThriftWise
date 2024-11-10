package org.ThriftWise;

import java.sql.*;
import java.util.ArrayList;

public class ExpenseDB {
    // Singleton pattern for database connection
    private static final Connection conn = ConnectDB.connect();
    private static final Statement stat;

    // Static block to initialize the statement and create necessary tables if not already existing
    static {
        try {
            stat = conn.createStatement();
            stat.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS Expenses (
                       ID INTEGER PRIMARY KEY AUTOINCREMENT,
                       Name VARCHAR(100),
                       Description VARCHAR(255),
                       Amount DECIMAL(10, 2),
                       Category VARCHAR(50),
                       Date DATE
                    );
                    CREATE TABLE IF NOT EXISTS categories (
                       category_name VARCHAR(255) NOT NULL UNIQUE
                    );
                    INSERT OR IGNORE INTO categories (category_name) VALUES
                        ('All'),
                        ('Housing'),
                        ('Transportation'),
                        ('Utilities'),
                        ('Food'),
                        ('Shopping'),
                        ('Health Care'),
                        ('Insurance'),
                        ('Taxes'),
                        ('Entertainment'),
                        ('Miscellaneous');
                    """
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Constructor is empty as we are using static methods
    public ExpenseDB() {}

    // Method to fetch all expenses or filter by category
    public static ResultSet getALL() throws SQLException {
        String selectSQL;
        if ("All".equals(HomeWindow.getSort())) {
            selectSQL = "SELECT ID, Name, Description, Amount, Category, Date FROM expenses";
        } else {
            selectSQL = "SELECT ID, Name, Description, Amount, Category, Date FROM expenses WHERE Category = ?";
        }

        PreparedStatement pstmt = conn.prepareStatement(selectSQL);
        if (!"All".equals(HomeWindow.getSort())) {
            pstmt.setString(1, HomeWindow.getSort());
        }
        return pstmt.executeQuery();
    }

    // Method to get column names from the Expenses table
    public static String[] getColumns() throws SQLException {
        ResultSet rs = getALL();
        int count = rs.getMetaData().getColumnCount();
        String[] columns = new String[count];
        for (int i = 0; i < count; i++) {
            columns[i] = rs.getMetaData().getColumnName(i + 1);
        }
        return columns;
    }

    // Method to get categories from the categories table
    public static String[] getCategories() {
        String selectSQL = "SELECT category_name FROM categories";
        ArrayList<String> categoryList = new ArrayList<>();
        try {
            ResultSet rs = stat.executeQuery(selectSQL);
            while (rs.next()) {
                categoryList.add(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categoryList.toArray(new String[0]);
    }

    // Method to insert a new expense into the database
    public static void insertExpenses(String name, String description, int amount, String category, String date) {
        String selectSQL = "INSERT INTO expenses (Name, Description, Amount, Category, Date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setInt(3, amount);
            pstmt.setString(4, category);
            pstmt.setString(5, date);
            pstmt.executeUpdate();
            System.out.println("Inserted!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to delete an expense by its ID
    public static boolean deleteExpenseById(int id) throws SQLException {
        String deleteSQL = "DELETE FROM Expenses WHERE ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Deleted!");
            return rowsAffected > 0;
        }
    }

    // Method to get the total expenses (all or filtered by category)
    public static String totalExpenses() {
        String selectSQL;
        try {
            if ("All".equals(HomeWindow.getSort())) {
                selectSQL = "SELECT SUM(Amount) FROM expenses";
            } else {
                selectSQL = "SELECT SUM(Amount) FROM expenses WHERE Category = ?";
            }
            PreparedStatement pstmt = conn.prepareStatement(selectSQL);
            if (!"All".equals(HomeWindow.getSort())) {
                pstmt.setString(1, HomeWindow.getSort());
            }
            ResultSet rs = pstmt.executeQuery();
            return "Total Expenses : " + rs.getString(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to get total monthly expenses (grouped by year and month)
    public static String totalMonthlyExpenses() {
        String selectSQL;
        ResultSet rs;
        try {
            if ("All".equals(HomeWindow.getSort())) {
                selectSQL = "SELECT SUM(Amount) FROM expenses GROUP BY strftime('%Y', date), strftime('%m', date)";
            } else {
                selectSQL = "SELECT SUM(Amount) FROM expenses WHERE Category = ? GROUP BY strftime('%Y', date), strftime('%m', date)";
            }

            PreparedStatement pstmt = conn.prepareStatement(selectSQL);
            if (!"All".equals(HomeWindow.getSort())) {
                pstmt.setString(1, HomeWindow.getSort());
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "test";
    }

    // Method to get total expenses for a specific date range
    public static String totalExpenses(String from, String to) {
        String selectSQL;
        if ("All".equals(HomeWindow.getSort())) {
            selectSQL = "SELECT SUM(Amount) FROM expenses WHERE Date >= ? AND Date <= ?";
        } else {
            selectSQL = "SELECT SUM(Amount) FROM expenses WHERE Category = ? AND Date >= ? AND Date <= ?";
        }
        try (PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setString(1, from);
            pstmt.setString(2, to);
            if (!"All".equals(HomeWindow.getSort())) {
                pstmt.setString(3, HomeWindow.getSort());
            }
            ResultSet rs = pstmt.executeQuery();
            return "Total Expenses : " + rs.getString(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to get total expenses for a specific category (optional)
    public static String totalExpenses(String sort) {
        String selectSQL;
        try {
            if (sort.equals("All")) {
                selectSQL = "SELECT SUM(Amount) FROM expenses";
                return "Total Expenses : " + stat.executeQuery(selectSQL).getString(1);
            } else {
                selectSQL = "SELECT SUM(Amount) FROM expenses WHERE Category = ?";
                PreparedStatement pstmt = conn.prepareStatement(selectSQL);
                pstmt.setString(1, sort);
                return pstmt.executeQuery().getString(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to get filtered data based on date range and category
    public static ResultSet getFilteredData(String startDate, String endDate, String category) throws SQLException {
        String query = "SELECT * FROM expenses WHERE date BETWEEN ? AND ?";
        if (!"All".equals(category)) {
            query += " AND category = ?"; // Add category filter
        }

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, startDate);
        stmt.setString(2, endDate);
        if (!"All".equals(category)) {
            stmt.setString(3, category); // Set the category if not "All"
        }
        return stmt.executeQuery();
    }

    // Method to get total expenses for a specific date range and category
    public static String totalExpensesInRange(String startDate, String endDate, String category) throws SQLException {
        String query = "SELECT SUM(amount) FROM expenses WHERE date BETWEEN ? AND ?";
        if (!"All".equals(category)) {
            query += " AND category = ?"; // Add category filter
        }
    
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            if (!"All".equals(category)) {
                stmt.setString(3, category); // Set the category if not "All"
            }
    
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return String.format("Total Expenses: %.2f", rs.getDouble(1));
            }
        }
        return "Total Expenses: 0.00";
    }    

    // Method to close the database connection when the application ends
    public static void closeConnection() {
        ConnectDB.closeConnection();
    }
}
