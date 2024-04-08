import java.util.Date;
class Transaction {
    private String action;
    private double amount;
    private Date date; // Add Date attribute

    public Transaction(String action, double amount, Date date) {
        this.action = action;
        this.amount = amount;
        this.date = date;
    }

    public String getAction() {
        return action;
    }

    public double getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }
}