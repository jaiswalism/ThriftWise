package walletapp.models;

import java.util.Date;

public class Transaction {
    private int id; // ID for the transaction in the database
    private double amount;
    private Date date;

    public Transaction(int id, double amount) {
        this.id = id;
        this.amount = amount;
        this.date = new Date();
    }

    // Getters and Setters
    public int getId() { return id; }
    public double getAmount() { return amount; }
    public Date getDate() { return date; }
}
