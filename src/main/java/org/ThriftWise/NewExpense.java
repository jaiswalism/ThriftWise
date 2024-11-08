package org.ThriftWise;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewExpense {
    @SuppressWarnings("unused")
    public NewExpense() {

        JFrame frame = new JFrame("Input Details");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(0, 2));
        frame.setLocationRelativeTo(null);

        frame.add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        frame.add(nameField);

        frame.add(new JLabel("Description:"));
        JTextField descriptionField = new JTextField();
        frame.add(descriptionField);

        frame.add(new JLabel("Amount:"));
        JTextField amountField = new JTextField();
        frame.add(amountField);

        frame.add(new JLabel("Date:"));
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd-MM-yyyy");
        frame.add(dateChooser);

        frame.add(new JLabel("Category:"));
        String[] categoryList = ExpenseDB.getCategories();
        JComboBox<String> categoryField = new JComboBox<>(categoryList);
        frame.add(categoryField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e1 -> {
            String name = nameField.getText();
            String description = descriptionField.getText();
            int amount = Integer.parseInt(amountField.getText());
            Date selectedDate = dateChooser.getDate();  // Get the selected date

            // Check if date is selected
            if (selectedDate != null) {
                // Format the Date object into a String (dd-MM-yyyy)
                String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(selectedDate);

                String category = (String) categoryField.getSelectedItem();
                frame.dispose();
                ExpenseDB.insertExpenses(name, description, amount, category, formattedDate);  // Pass formatted date as String

                try {
                    HomeWindow.updateTable();
                    HomeWindow.updateTotal();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a valid date.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        frame.add(submitButton);

        frame.pack();
        frame.setVisible(true);
    }
}
