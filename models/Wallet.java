package walletapp.models;

import java.util.ArrayList;
import java.util.List;

public class Wallet {
    private double balance;
    private List<Transaction> transactions;

    public Wallet() {
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
    }

    public double getBalance() { return balance; }
    public List<Transaction> getTransactions() { return transactions; }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            transactions.add(new Transaction(0, amount)); // ID will be generated by the DB
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            transactions.add(new Transaction(0, -amount)); // ID will be generated by the DB
        }
    }
}
