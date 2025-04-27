public class Account {
    protected String name; // Account holder's name
    protected double balance; // Don't define this in the CheckingAccount class

    public Account(String acctName, double initBalance) {
        balance = initBalance;
        name = acctName;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}