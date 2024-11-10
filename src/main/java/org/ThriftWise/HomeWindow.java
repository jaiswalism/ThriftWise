package org.ThriftWise;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.toedter.calendar.JDateChooser;  // Importing the JDateChooser
import java.text.SimpleDateFormat;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomeWindow extends JFrame {
    private static JTable expenseTable;
    private static JComboBox<String> categories;
    private static final JLabel label = new JLabel();
    private static JDateChooser startDateChooser;  // Start date chooser
    private static JDateChooser endDateChooser;    // End date chooser

    public HomeWindow() throws SQLException {
        setTitle("ThriftWise");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        createView();
        updateTotal();
    }

    private void createView() throws SQLException {
        // Initialize date choosers
        startDateChooser = new JDateChooser();
        endDateChooser = new JDateChooser();
        
        // Configure date choosers (set preferred size, etc.)
        startDateChooser.setPreferredSize(new Dimension(100, 20));
        endDateChooser.setPreferredSize(new Dimension(100, 20));
    
        // Call the rest of the createView method to add components to the UI
        JPanel selectionPanel = createSelectionPanel();
        JScrollPane scrollPane = createExpensePanel();
    
        JPanel panel = new JPanel();
        new BoxLayout(panel, BoxLayout.Y_AXIS);
        Box[] boxes = new Box[2];
    
        boxes[0] = Box.createHorizontalBox();
        boxes[1] = Box.createHorizontalBox();
        panel.add(boxes[0]);
        panel.add(Box.createRigidArea(new Dimension(1000, 10)));
        panel.add(boxes[1]);
    
        boxes[0].add(selectionPanel);
        boxes[1].add(scrollPane);
        getContentPane().add(panel);
    }

    public static void updateTotal() {
        try {
            java.util.Date startDate = startDateChooser.getDate();
            java.util.Date endDate = endDateChooser.getDate();
    
            // Get the selected category from the combo box
            String category = getSort();
    
            // If no date range is selected, get total of all data
            if (startDate == null || endDate == null) {
                label.setText(ExpenseDB.totalExpenses(category));  // Pass category here
            } else {
                // If date range is selected, get total of filtered data
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String startDateStr = sdf.format(startDate);
                String endDateStr = sdf.format(endDate);
    
                label.setText(ExpenseDB.totalExpensesInRange(startDateStr, endDateStr, category));  // Pass category here
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    

    private JPanel createSelectionPanel() {
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.X_AXIS));

        JButton newButton = new JButton("+NEW");
        newButton.addActionListener(new MyActionListener());

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new MyActionListener());

        JComboBox<String> categoriesBox;
        categoriesBox = createSortPanel();

        JButton calculateButton = new JButton("Summary");
        calculateButton.addActionListener(new MyActionListener());

        // New Show Graph button
        JButton showGraphButton = new JButton("Show Graph");
        showGraphButton.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the selected start and end dates from the date choosers
                java.util.Date startDate = startDateChooser.getDate();
                java.util.Date endDate = endDateChooser.getDate();
    
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String startDateStr = (startDate != null) ? sdf.format(startDate) : null;
                String endDateStr = (endDate != null) ? sdf.format(endDate) : null;
    
                // Call the method to show the pie chart with the selected date range
                showGraphSelectionDialog(startDateStr, endDateStr);
            }
        });

        JPanel totalPanel = new JPanel();
        totalPanel.add(label);

        // Button to open date range selector
        JButton dateRangeButton = new JButton("Select Date Range");
        dateRangeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDateRangePopup();
            }
        });

        // Add the components with spacing between them
        selectionPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        selectionPanel.add(newButton);
        selectionPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        selectionPanel.add(deleteButton);
        selectionPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        selectionPanel.add(new JLabel("Sort:"));
        selectionPanel.add(Box.createRigidArea(new Dimension(3, 0)));
        selectionPanel.add(categoriesBox);
        selectionPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        selectionPanel.add(totalPanel);
        selectionPanel.add(Box.createRigidArea(new Dimension(150, 0)));

         // Add the Select Date Range button
         selectionPanel.add(dateRangeButton);
         selectionPanel.add(Box.createRigidArea(new Dimension(20, 0)));

        //  Summary Button
        selectionPanel.add(calculateButton);
        selectionPanel.add(Box.createRigidArea(new Dimension(20, 0)));

        // Add the Show Graph button to the panel
        selectionPanel.add(showGraphButton);
        selectionPanel.add(Box.createRigidArea(new Dimension(20, 0)));

        selectionPanel.setPreferredSize(new Dimension(1000, 30));
        return selectionPanel;
    }

    private void showGraphSelectionDialog(String startDateStr, String endDateStr) {
        // Create a panel for the dialog
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel label = new JLabel("Select the type of graph to display:");
        label.setAlignmentX(Component.LEFT_ALIGNMENT); // Align the label to the left
        
        // Create a sub-panel for buttons with horizontal alignment
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // This aligns buttons horizontally in the center
        
        JButton pieChartButton = new JButton("Pie Chart");
        JButton barChartButton = new JButton("Bar Chart");
        
        // Action listener for Pie Chart button
        pieChartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call method to show pie chart
                ChartManager.showExpensePieChart(startDateStr, endDateStr);
                // Close the dialog after selection
                ((JDialog) SwingUtilities.getWindowAncestor(pieChartButton)).dispose();
            }
        });
        
        // Action listener for Bar Chart button
        barChartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call method to show bar chart
                ChartManager.showExpenseBarChart(startDateStr, endDateStr);
                // Close the dialog after selection
                ((JDialog) SwingUtilities.getWindowAncestor(barChartButton)).dispose();
            }
        });
        
        // Add the buttons to the button panel
        buttonPanel.add(pieChartButton);
        buttonPanel.add(barChartButton);
        
        // Add the label and button panel to the main panel
        panel.add(label);
        panel.add(buttonPanel);
        
        // Create a dialog with the panel
        JDialog dialog = new JDialog((Frame) null, "Select Graph Type", true); // Modal dialog
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(null); // Center the dialog on the screen
        dialog.setVisible(true); // Show the dialog
    }         

    private JComboBox<String> createSortPanel() {
        String[] categoryList = ExpenseDB.getCategories();

        categories = new JComboBox<>(categoryList);
        categories.addActionListener(new MyActionListener());

        categories.setPreferredSize(new Dimension(70, 20));
        return categories;
    }

    private JScrollPane createExpensePanel() throws SQLException {
        JPanel expensePanel = new JPanel();

        DefaultTableModel model = createTableModel();

        expenseTable = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(expenseTable);
        expenseTable.setPreferredScrollableViewportSize(new Dimension(900, 450));
        expenseTable.setFillsViewportHeight(true);
        expenseTable.setPreferredScrollableViewportSize(new Dimension(900, 450));

        expenseTable.setFillsViewportHeight(true);

        expensePanel.setBackground(Color.BLACK);
        expensePanel.setPreferredSize(new Dimension(1000, 600));
        return scrollPane;
    }

    private DefaultTableModel createTableModel() throws SQLException {
        ResultSet rs = ExpenseDB.getALL();
        String[] columns = ExpenseDB.getColumns();
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        while (rs.next()) {
            Object[] row = new Object[columns.length];
            for (int i = 0; i < columns.length; i++) {
                row[i] = rs.getString(i + 1);
            }
            model.addRow(row);
        }

        return model;
    }

    // Method to show the date range popup dialog
    private void showDateRangePopup() {
        JPanel panel = new JPanel();
        JLabel startLabel = new JLabel("Start Date:");
        startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("yyyy-MM-dd");

        JLabel endLabel = new JLabel("End Date:");
        endDateChooser = new JDateChooser();
        endDateChooser.setDateFormatString("yyyy-MM-dd");

        panel.add(startLabel);
        panel.add(startDateChooser);
        panel.add(Box.createRigidArea(new Dimension(20, 0)));  // Spacing
        panel.add(endLabel);
        panel.add(endDateChooser);

        int option = JOptionPane.showConfirmDialog(this, panel, "Select Date Range", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            updateTable(); // Update table data within the date range
            updateTotal(); // Update total expenses within the date range
        }
    }


    public static void updateTable() {
    try {
        DefaultTableModel model = (DefaultTableModel) expenseTable.getModel();
        model.setRowCount(0); // Clear the existing table

        // Get the selected start and end dates from the date choosers
        java.util.Date startDate = startDateChooser.getDate();
        java.util.Date endDate = endDateChooser.getDate();
        String category = getSort();  // Get the selected category

        ResultSet rs;

        // If both start and end dates are null, get all data
        if (startDate == null && endDate == null) {
            // Fetch data without filtering by date, but still use category filter
            rs = ExpenseDB.getFilteredData("2000-01-01", "2100-01-01", category);  // Use a very wide date range
        } else if (startDate != null && endDate != null) {
            // If both dates are selected, fetch filtered data
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startDateStr = sdf.format(startDate);
            String endDateStr = sdf.format(endDate);

            // Call with start date, end date, and category
            rs = ExpenseDB.getFilteredData(startDateStr, endDateStr, category);  // Pass all 3 parameters
        } else {
            // If only one date is selected, you could add handling here if desired
            JOptionPane.showMessageDialog(null, "Please select both a start and end date.", "Date Range Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] columns = ExpenseDB.getColumns();
        while (rs.next()) {
            Object[] row = new Object[columns.length];
            for (int i = 0; i < columns.length; i++) {
                row[i] = rs.getString(i + 1);
            }
            model.addRow(row);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error while updating table: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    public static String getSort() {
        return (String) categories.getSelectedItem();
    }
}
