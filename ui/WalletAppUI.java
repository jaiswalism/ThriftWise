// Class to handle UI part of the application

package walletapp.ui;

import walletapp.models.Transaction;
import walletapp.services.WalletService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class WalletAppUI {
    private WalletService walletService;
    private JFrame frame;
    private JTextField amountField;
    private JTable transactionTable;
    private DefaultTableModel tableModel;

    public WalletAppUI() {
        walletService = new WalletService();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("ThriftWise");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JLabel amountLabel = new JLabel("Amount:");
        amountField = new JTextField(10);
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton refreshButton = new JButton("Refresh");

        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(depositButton);
        panel.add(withdrawButton);
        panel.add(refreshButton);

        tableModel = new DefaultTableModel(new Object[]{"Transaction ID", "Amount", "Date"}, 0);
        transactionTable = new JTable(tableModel);

        depositButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                double amount = Double.parseDouble(amountField.getText());
                walletService.deposit(amount);
                updateTransactionTable();
            }
        });

        withdrawButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                double amount = Double.parseDouble(amountField.getText());
                walletService.withdraw(amount);
                updateTransactionTable();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTransactionTable();
            }
        });

        frame.add(panel, BorderLayout.NORTH);
        frame.add(new JScrollPane(transactionTable), BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void updateTransactionTable() {
        tableModel.setRowCount(0); // Clear existing data
        List<Transaction> transactions = walletService.getTransactionHistory();
        for (Transaction t : transactions) {
            tableModel.addRow(new Object[]{t.getId(), t.getAmount(), t.getDate()});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WalletAppUI());
    }
}
