/*  ThriftWise - The Wallet Application
 * 	This Application is meant manage all deposit and withdrawal transactions for easily tracking your expenses.
 * 
 */


// Main class and method which invokes the the application UI class

package walletapp;

import walletapp.ui.WalletAppUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WalletAppUI());
    }
}
