package org.ThriftWise;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import org.jfree.data.general.DefaultPieDataset;

public class MyActionListener extends Component implements ActionListener {

    @Override
public void actionPerformed(ActionEvent e) {
    if (e.getSource() instanceof JButton) {
        if (e.getActionCommand().equals("+NEW")) {
            new NewExpense();
        }

        if (e.getActionCommand().equals("Summary")) {
            String[] categories = ExpenseDB.getCategories();
            StringBuilder summary = new StringBuilder();
            summary.append("Summary:\n\n");

            DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
            final double[] totalExpenses = {0.0};

            for (String category : categories) {
                if (category.equals("All")) continue;

                String categoryTotal = ExpenseDB.totalExpenses(category);

                if (categoryTotal == null || categoryTotal.isEmpty()) {
                    categoryTotal = "0";
                }

                categoryTotal = categoryTotal.replaceAll("[^0-9.]", "");

                summary.append(category).append(" : ").append(categoryTotal).append("\n");

                try {
                    double categoryValue = Double.parseDouble(categoryTotal);
                    dataset.setValue(category, categoryValue);
                    totalExpenses[0] += categoryValue;
                } catch (NumberFormatException ex) {
                    System.err.println("Invalid number format for category: " + category + " Total: " + categoryTotal);
                }
            }

            JOptionPane.showMessageDialog(this, summary.toString(), "Expense Summary", JOptionPane.INFORMATION_MESSAGE);
        }

        // Show Graph button action
        // if (e.getActionCommand().equals("Show Graph")) {
        //     // Call the ChartManager to display the Pie Chart
        //     ChartManager.showExpensePieChart();
        // }

        if (e.getActionCommand().equals("Delete")) { 
            String idToDelete = JOptionPane.showInputDialog(null, "Enter the ID of the expense to delete:");
            if (idToDelete != null && !idToDelete.trim().isEmpty()) {
                try {
                    int id = Integer.parseInt(idToDelete.trim());
                    boolean success = ExpenseDB.deleteExpenseById(id);
                    if (success) {
                        JOptionPane.showMessageDialog(null, "Expense deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        HomeWindow.updateTable();
                        HomeWindow.updateTotal();
                    } else {
                        JOptionPane.showMessageDialog(null, "Expense ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid ID. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }}
    }

    if (e.getSource() instanceof JComboBox) {
        HomeWindow.updateTable();
        HomeWindow.updateTotal();
    }
}

}
