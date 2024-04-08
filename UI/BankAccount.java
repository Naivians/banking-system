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


class BankAccount {
    private String accountNumber;
    private String accountHolderName;
    private String accountType;
    private double balance;
    private ArrayList<Transaction> transactions;

    public BankAccount(String accountNumber, String accountHolderName, String accountType, double balance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.accountType = accountType;
        this.balance = balance;
        this.transactions = new ArrayList<>();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public String getAccountType() {
        return accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void loadTransactionHistory(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isFirstLine = true; // Flag to skip the header line
            boolean accountFound = false; // Flag to check if transactions for the account are found
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the header line
                }

                String[] data = line.split(",");
                if (data.length == 4 && data[0].equals(accountNumber)) {
                    accountFound = true; // Set flag to true if transactions for the account are found
                    Transaction transaction = new Transaction(data[1], Double.parseDouble(data[2]),
                            new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(data[3])); // Parse date string
                    transactions.add(transaction);
                }
            }

            if (!accountFound) {
                System.out.println("No transactions found for account " + accountNumber);
            }

        } catch (IOException | NumberFormatException | ParseException e) {
            System.err.println("Error loading transaction history for account " + accountNumber + ": " + e.getMessage());
        }
    }

    public void recordTransaction(String action, double amount) {
        boolean accountExists = checkAccountExists(accountNumber); // Check if account exists
        if (!accountExists) {
            System.out.println("Error: Account " + accountNumber + " does not exist.");
            return; // Exit method if account does not exist
        }
    
        transactions.add(new Transaction(action, amount, new Date()));
        saveTransactionsToFile("transactions.csv");
    }

    private boolean checkAccountExists(String accountNumber) {
        try (BufferedReader br = new BufferedReader(new FileReader("bank_accounts.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length > 0 && data[0].equals(accountNumber)) {
                    return true; // Account found
                }
            }
        } catch (IOException e) {
            System.err.println("Error checking account existence: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }
        return false; // Account not found
    }

    private void saveTransactionsToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)))) {
            for (Transaction transaction : transactions) {
                writer.println(accountNumber + "," + transaction.getAction() + "," + transaction.getAmount() + ","
                        + transaction.getDate());
            }
            System.out.println("Transactions saved to file: " + filename); // Debug statement
        } catch (IOException e) {
            System.err.println("Error saving transactions to file: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }
    }

  
    private void saveAccountToFile(String filename) {
        ArrayList<String> lines = new ArrayList<>();
        boolean accountFound = false;

        // Read existing data and update balance if account found
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4 && data[0].equals(accountNumber)) {
                    // Update balance for the account
                    lines.add(accountNumber + "," + accountHolderName + "," + accountType + "," + balance);
                    accountFound = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading account details from file: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }

        // If account not found, add it as a new entry
        if (!accountFound) {
            lines.add(accountNumber + "," + accountHolderName + "," + accountType + "," + balance);
        }

        // Write updated data back to the file
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filename)))) {
            for (String line : lines) {
                writer.println(line);
            }
            System.out.println("Account details updated in file: " + filename); // Debug statement
        } catch (IOException e) {
            System.err.println("Error saving account details to file: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }
    }

    // toString method
    @Override
    public String toString() {
        return "Account Number: " + accountNumber +
                ", Account Holder Name: " + accountHolderName +
                ", Account Type: " + accountType +
                ", Balance: " + balance;
    }

}