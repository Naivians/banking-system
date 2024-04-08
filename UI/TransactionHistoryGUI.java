import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

class TransactionHistoryGUI extends JFrame {
    public TransactionHistoryGUI(String accountNumber, ArrayList<BankAccount> accounts) {
        setTitle("Transaction History");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window when closed
        setLocationRelativeTo(null);

        ImageIcon icon = new ImageIcon("logo.png");
        setIconImage(icon.getImage());

        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBackground(Color.WHITE); // Setting background color of the scroll pane
        textArea.setBackground(Color.BLUE); // Setting background color of the text area
        textArea.setForeground(Color.white);
        add(scrollPane);

        // Load transaction history for the specified account number and display in the
        // text area
        loadTransactionHistory(accountNumber, accounts, textArea);
    }

    private void loadTransactionHistory(String accountNumber, ArrayList<BankAccount> accounts, JTextArea textArea) {
        // Find the account by account number
        boolean transactionsFound = false;
        for (BankAccount account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                // Append transaction details to the text area
                textArea.setText("Transaction History for Account: " + accountNumber + "\n\n");
                textArea.append("Date\t\tType\t\tAmount\n");
                for (Transaction transaction : account.getTransactions()) {
                    textArea.append(transaction.getDate() + "\t" + transaction.getAction() + "\t\t$"
                            + transaction.getAmount() + "\n"); // Corrected formatting
                }
                transactionsFound = true;
                break;
            }
        }

        // If no transactions found, print a message
        if (!transactionsFound) {
            textArea.setText("No transactions found for account " + accountNumber);
        }
    }
}
