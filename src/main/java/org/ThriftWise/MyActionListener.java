package org.ThriftWise;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.labels.PieToolTipGenerator;

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
        if (e.getActionCommand().equals("Show Graph")) {
            String[] categories = ExpenseDB.getCategories();
            DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
            final double[] totalExpenses = {0.0};

            for (String category : categories) {
                if (category.equals("All")) continue;

                String categoryTotal = ExpenseDB.totalExpenses(category);

                if (categoryTotal == null || categoryTotal.isEmpty()) {
                    categoryTotal = "0";
                }

                categoryTotal = categoryTotal.replaceAll("[^0-9.]", "");

                try {
                    double categoryValue = Double.parseDouble(categoryTotal);
                    dataset.setValue(category, categoryValue);
                    totalExpenses[0] += categoryValue;
                } catch (NumberFormatException ex) {
                    System.err.println("Invalid number format for category: " + category + " Total: " + categoryTotal);
                }
            }

            JFreeChart chart = ChartFactory.createPieChart(
                    "Expense Summary",    // Title of the chart
                    dataset,              // Data
                    true,                 // Include legend/labels
                    true,                 // Tooltips
                    false                 // URLs (disabled)
            );

            PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));

            // Beautify the PieChart for better aesthetics
            plot.setSectionPaint("Food", new Color(255, 230, 102));
            plot.setSectionPaint("Transportation", new Color(119, 255, 51));
            plot.setSectionPaint("Housing", new Color(0, 102, 51));
            plot.setSectionPaint("Entertainment", new Color(177, 27, 27));
            plot.setSectionPaint("Utilities", new Color(102, 166, 255));
            plot.setSectionPaint("Health Care", new Color(0, 34, 204));
            plot.setSectionPaint("Shopping", new Color(180, 31, 255));
            plot.setSectionPaint("Insurance", new Color(255, 199, 250));
            plot.setSectionPaint("Taxes", new Color(255, 51, 102));
            plot.setSectionPaint("Miscellaneous", new Color(255, 158, 31));

            // Custom PieToolTipGenerator to format the tooltip text
            PieToolTipGenerator toolTipGenerator = (dataset1, key) -> {
                if (key != null) {
                    double value = dataset1.getValue(key).doubleValue();
                    double percentage = (totalExpenses[0] > 0) ? (value / totalExpenses[0]) * 100 : 0.0;
                    return key + ": " + value + " (" + String.format("%.2f", percentage) + "%)";
                }
                return null;
            };
            plot.setToolTipGenerator(toolTipGenerator);

            // Set the font for the labels to match FlatLaf IntelliJ theme
            plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 14));
            plot.setLabelPaint(Color.BLACK);

            // Customize the border and background
            plot.setBackgroundPaint(new Color(211,211,211));
            plot.setOutlinePaint(Color.WHITE);

            // Create and configure chart panel
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(600, 400));
            chartPanel.setMouseWheelEnabled(true);

            // Create a new JFrame for the chart
            JFrame chartFrame = new JFrame("Category Expenses Pie Chart");
            chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            chartFrame.setLayout(new BorderLayout());
            chartFrame.add(chartPanel, BorderLayout.CENTER);
            chartFrame.pack();
            chartFrame.setVisible(true);
        }

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
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }}
    }

    if (e.getSource() instanceof JComboBox) {
        try {
            HomeWindow.updateTable();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
        HomeWindow.updateTotal();
    }
}

}
