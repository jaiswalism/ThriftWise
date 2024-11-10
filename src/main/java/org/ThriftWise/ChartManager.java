package org.ThriftWise;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ChartManager {

    public static void showExpensePieChart(String startDateStr, String endDateStr) {
        // Retrieve the categories from the database
        String[] categories = ExpenseDB.getCategories();
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        final double[] totalExpenses = {0.0};

        // Loop through each category and gather the data
        for (String category : categories) {
            if (category.equals("All")) continue;

            String categoryTotal = null;
            try {
                // Retrieve the total expenses for the category in the specified range
                categoryTotal = (startDateStr == null || endDateStr == null) 
                        ? ExpenseDB.totalExpenses(category) 
                        : ExpenseDB.totalExpensesInRange(startDateStr, endDateStr, category);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (categoryTotal == null || categoryTotal.isEmpty()) {
                categoryTotal = "0";
            }

            // Remove non-numeric characters (if any) and convert to a number
            categoryTotal = categoryTotal.replaceAll("[^0-9.]", "");

            try {
                // Parse the category total into a double and add to the dataset
                double categoryValue = Double.parseDouble(categoryTotal);
                dataset.setValue(category, categoryValue);
                totalExpenses[0] += categoryValue;
            } catch (NumberFormatException ex) {
                System.err.println("Invalid number format for category: " + category + " Total: " + categoryTotal);
            }
        }

        // Create a pie chart with the dataset
        JFreeChart chart = ChartFactory.createPieChart(
                "Expense Summary: " + startDateStr + " - " + endDateStr, // Title of the chart
                dataset,  // Data
                true,     // Include legend/labels
                true,     // Tooltips
                false     // URLs (disabled)
        );

        // Customize the PiePlot
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
        plot.setBackgroundPaint(new Color(211, 211, 211));
        plot.setOutlinePaint(Color.WHITE);

        // Create and configure the chart panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        chartPanel.setMouseWheelEnabled(true);

        // Create a new JFrame to display the chart
        JFrame chartFrame = new JFrame("Category Expenses Pie Chart");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.setLayout(new BorderLayout());
        chartFrame.add(chartPanel, BorderLayout.CENTER);
        chartFrame.pack();
        chartFrame.setVisible(true);
    }

    public static void showExpenseBarChart(String startDateStr, String endDateStr) {
        // Retrieve the categories from the database
        String[] categories = ExpenseDB.getCategories();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Loop through each category and gather the data
        for (String category : categories) {
            if (category.equals("All")) continue;

            String categoryTotal = null;
            try {
                // Retrieve the total expenses for the category in the specified range
                categoryTotal = (startDateStr == null || endDateStr == null) 
                        ? ExpenseDB.totalExpenses(category) 
                        : ExpenseDB.totalExpensesInRange(startDateStr, endDateStr, category);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (categoryTotal == null || categoryTotal.isEmpty()) {
                categoryTotal = "0";
            }

            // Remove non-numeric characters (if any) and convert to a number
            categoryTotal = categoryTotal.replaceAll("[^0-9.]", "");

            try {
                // Parse the category total into a double and add to the dataset
                double categoryValue = Double.parseDouble(categoryTotal);
                dataset.addValue(categoryValue, "Expenses", category);
            } catch (NumberFormatException ex) {
                System.err.println("Invalid number format for category: " + category + " Total: " + categoryTotal);
            }
        }

        // Create a bar chart with the dataset
        JFreeChart barChart = ChartFactory.createBarChart(
                "Expense Summary: " + startDateStr + " - " + endDateStr,  // Title of the chart
                "Total Expenses",                                         // X-axis Label
                "Category",                                               // Y-axis Label
                dataset,                                                   // Data
                PlotOrientation.HORIZONTAL,                               // Plot Orientation (Horizontal)
                true,                                                      // Include legend
                true,                                                      // Tooltips
                false                                                     // URLs (disabled)
        );

        // Customize the bar chart appearance
        barChart.setBackgroundPaint(new Color(211, 211, 211));  // Set background color

        // Create and configure the chart panel
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        chartPanel.setMouseWheelEnabled(true);

        // Create a new JFrame to display the chart
        JFrame chartFrame = new JFrame("Category Expenses Bar Chart");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.setLayout(new BorderLayout());
        chartFrame.add(chartPanel, BorderLayout.CENTER);
        chartFrame.pack();
        chartFrame.setVisible(true);
    }
}
