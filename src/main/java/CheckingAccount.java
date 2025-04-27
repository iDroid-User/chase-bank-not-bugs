import java.util.ArrayList;

public class CheckingAccount extends Account {
   private double balance;
   private double totalServiceCharge;
   // New data and methods for the CheckingAccount class
   private ArrayList<Transaction> transList; // Keeps a list of Transactions for the account
   private int transCount; // The Transaction count

   public CheckingAccount(String name, double initialBalance) {
      super(name, initialBalance);
      balance = initialBalance;
      totalServiceCharge = 0.0;
      transList = new ArrayList<>();
      transCount = 0;
   }

   public double getBalance() { return balance; }

   public void setBalance(double transAmt, int tCode) {
      if (1 == tCode) {
         balance -= transAmt;
         totalServiceCharge += 0.15; // A withdrawal fee of $0.15 per transaction
      } else if (2 == tCode) {
         balance += transAmt;
         totalServiceCharge += 0.10; // A deposit fee of $0.10 per transaction
      } else {
         balance += 0.0;
         totalServiceCharge += 0.0;
      }
   }

   public double getServiceCharge() {
      return totalServiceCharge;
   }

   // Charges fees depending on balance amount
   public void setServiceCharge(double currentServiceCharge) { totalServiceCharge += currentServiceCharge; }

   // Adds a Transaction to the transList
   public void addTrans(Transaction newTrans) {
      transList.add(newTrans);
      transCount++;
   }

   // Returns the current value of transCount
   public int getTransCount() {
      return transCount;
   }

   // Returns the i-th Transaction in the transList
   public Transaction getTrans(int i) {
      return transList.get(i);
   }
}