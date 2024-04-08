import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date; // Import Date class
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException; // Import ParseException class
import java.text.SimpleDateFormat; // Import SimpleDateFormat class


class BankAccountGUI extends JFrame implements ActionListener {
    private ArrayList<BankAccount> accounts = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTextField accountNumberField, amountField;
    private int width = 120, height = 30;

    public BankAccountGUI() {
        setTitle("Bank Account Management System");
        setSize(440, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        ImageIcon icon = new ImageIcon("logo.png");
        setIconImage(icon.getImage());


        // Using RGB values to create a custom color
        Color customColor = Color.decode("#0d6efd");  // Creates a custom color with red component 255, green component 0,
                                                  // and blue component 0

        JPanel panel = new JPanel();
        getContentPane().add(panel);
        panel.setBackground(customColor);
        panel.setLayout(null);

        JLabel headerLabel = new JLabel("Bank Account Management System");
        headerLabel.setBounds(10, 10, 300, 20);
        headerLabel.setForeground(Color.WHITE);
        panel.add(headerLabel);

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable cell editing
            }
        };
        JTable table = new JTable(tableModel);
        tableModel.addColumn("Account Number");
        tableModel.addColumn("Account Holder Name");
        tableModel.addColumn("Account Type");
        tableModel.addColumn("Balance");
        table.getTableHeader().setReorderingAllowed(true); // Allow column reordering
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 40, 400, 200);
        panel.add(scrollPane);

        JLabel accountNumberLabel = new JLabel("Account Number:");
        accountNumberLabel.setForeground(Color.WHITE);
        accountNumberLabel.setBounds(10, 250, 120, 20);
        panel.add(accountNumberLabel);

        accountNumberField = new JTextField(10);
        accountNumberField.setBounds(130, 250, 120, 20);
        panel.add(accountNumberField);

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setForeground(Color.WHITE);
        amountLabel.setBounds(10, 280, 120, 20);
        panel.add(amountLabel);

        amountField = new JTextField(10);
        amountField.setBounds(130, 280, 120, 20);
        panel.add(amountField);

        JButton viewAllButton = new JButton("View All Accounts");
        viewAllButton.setBounds(10, 310, 170, 30);
        viewAllButton.addActionListener(this);
        panel.add(viewAllButton);

        JButton depositButton = new JButton("Deposit");
        depositButton.setBounds(190, 310, 170, 30);
        depositButton.addActionListener(this);
        panel.add(depositButton);

        JButton btnTrabsact = new JButton("View Transactions");
        btnTrabsact.setBounds(10, 410, 170, 30);
        btnTrabsact.addActionListener(this);
        panel.add(btnTrabsact);

        JButton searchButton = new JButton("Search Account");
        searchButton.setBounds(10, 360, 170, 30);
        searchButton.addActionListener(this);
        panel.add(searchButton);

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.setBounds(190, 360, 170, 30);
        withdrawButton.addActionListener(this);
        panel.add(withdrawButton);

        loadAccountsFromFile("bank_accounts.csv");
        displayAllAccounts();

        setVisible(true);
    }

    private void loadAccountsFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(",");

                try {
                    double balance = Double.parseDouble(data[3]);
                    BankAccount account = new BankAccount(data[0], data[1], data[2], balance);
                    // account.loadTransactionHistory("transactions.csv"); // Load transaction history for each account
                    accounts.add(account);

                    Object[] rowData = { account.getAccountNumber(), account.getAccountHolderName(),
                            account.getAccountType(), account.getBalance() };
                    tableModel.addRow(rowData);

                } catch (NumberFormatException e) {
                    System.err.println("Error parsing balance value: " + data[3]);
                }
            }
        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(this, "Error loading accounts from file: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayAllAccounts() {
        tableModel.setRowCount(0);
        for (BankAccount account : accounts) {
            Object[] rowData = { account.getAccountNumber(), account.getAccountHolderName(), account.getAccountType(),
                    account.getBalance() };
            tableModel.addRow(rowData);
        }
    }

    private void depositMoney(String accountNumber, double amount) {
        for (BankAccount account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                account.setBalance(account.getBalance() + amount);
                account.recordTransaction("Deposit", amount); // Record the deposit transaction
                updateBalanceInTable(accountNumber, account.getBalance());
                JOptionPane.showMessageDialog(this, "Deposit successful. New balance: " + account.getBalance(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Account not found", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void withdrawMoney(String accountNumber, double amount) {

        for (BankAccount account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                if (account.getBalance() >= amount && amount > 0) {
                    account.setBalance(account.getBalance() - amount);
                    account.recordTransaction("Withdrawal", amount); // Record the withdrawal transaction
                    updateBalanceInTable(accountNumber, account.getBalance());
                    JOptionPane.showMessageDialog(this, "Withdrawal successful. New balance: " + account.getBalance(),
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Please enter a positive amount", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient funds", "Error", JOptionPane.ERROR_MESSAGE);
                }
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Account not found", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void updateBalanceInTable(String accountNumber, double newBalance) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).equals(accountNumber)) {
                tableModel.setValueAt(newBalance, i, 3);
                return;
            }
        }
    }

    private int findRowIndex(String accountNumber) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).equals(accountNumber)) {
                return i;
            }
        }
        return -1;
    }

    private void searchAccount(String accountNumber) {
        int rowIndex = findRowIndex(accountNumber);
        if (rowIndex != -1) {
            tableModel.setRowCount(0);
            BankAccount account = accounts.get(rowIndex);
            Object[] rowData = { account.getAccountNumber(), account.getAccountHolderName(), account.getAccountType(),
                    account.getBalance() };
            tableModel.addRow(rowData);
        } else {
            JOptionPane.showMessageDialog(this, "Account not found", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private BankAccount findAccount(String accountNumber) {
        for (BankAccount account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Deposit")) {
            System.out.println("WIHTDRAW IS TRIGGERED");
            String accountNumber = accountNumberField.getText();
            String amountText = amountField.getText();
            if (!amountText.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountText);
                    if (amount > 0) {
                        depositMoney(accountNumber, amount);
                        reset();
                    } else {
                        JOptionPane.showMessageDialog(this, "Please enter a positive amount", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid amount input", "Error", JOptionPane.ERROR_MESSAGE);
                    reset();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter an amount", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getActionCommand().equals("Withdraw")) {
            String accountNumber = accountNumberField.getText();
            String amountText = amountField.getText();
            if (!amountText.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountText);
                    if (amount > 0) {
                        withdrawMoney(accountNumber, amount);
                        reset();
                    } else {
                        JOptionPane.showMessageDialog(this, "Please enter a positive amount", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid amount input", "Error", JOptionPane.ERROR_MESSAGE);
                    reset();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter an amount", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getActionCommand().equals("View All Accounts")) {
            displayAllAccounts();
        } else if (e.getActionCommand().equals("Search Account")) {
            searchAccount(accountNumberField.getText());
            reset();
        } else if (e.getActionCommand().equals("View Transactions")) {
            String accountNumber = accountNumberField.getText();
            BankAccount account = findAccount(accountNumber);
            if (account != null && !account.getTransactions().isEmpty()) {
                TransactionHistoryGUI transactionHistoryGUI = new TransactionHistoryGUI(accountNumber, accounts);
                transactionHistoryGUI.setVisible(true);
                reset();
            } else if (account != null && account.getTransactions().isEmpty()) {
                JOptionPane.showMessageDialog(this, "No transactions found for account " + accountNumber, "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Account not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void reset() {
        amountField.setText("");
        accountNumberField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BankAccountGUI();
        });
    }
}
