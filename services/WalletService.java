package walletapp.services;

import walletapp.models.Transaction;
import walletapp.models.Wallet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WalletService {
    private Wallet wallet;
    private Connection connection;

    public WalletService() {
        this.wallet = new Wallet();
        connectDatabase();
    }

    private void connectDatabase() {
        try {
            // Adjust the URL according to your database setup
            connection = DriverManager.getConnection("jdbc:sqlite:wallet.db");
            Statement stmt = connection.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, amount REAL, date TIMESTAMP)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getBalance() {
        return wallet.getBalance();
    }

    public void deposit(double amount) {
        wallet.deposit(amount);
        saveTransaction(amount);
    }

    public void withdraw(double amount) {
        wallet.withdraw(amount);
        saveTransaction(-amount);
    }

    private void saveTransaction(double amount) {
        String sql = "INSERT INTO transactions (amount, date) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Transaction> getTransactionHistory() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(new Transaction(rs.getInt("id"), rs.getDouble("amount")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
}
