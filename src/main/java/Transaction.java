public class Transaction {
   protected int transNumber, transId;
   protected double transAmt;

   public Transaction(int number, int id, double amount) {
      transNumber = number; // Transaction number
      transId = id; // Transaction code
      transAmt = amount;
   }

   public int getTransNumber() {
      return transNumber;
   }

   public int getTransId() { return transId; }

   public double getTransAmount() { return transAmt; }
}