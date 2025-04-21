public class Transaction {
   private int transNumber, transId;
   private double transAmt;

   public Transaction(int number, int id, double amount) {
      transNumber = number;
      transId = id;
      transAmt = amount;
   }

   public int getTransNumber() {
      return transNumber;
   }

   public int getTransId() { return transId; }

   public double getTransAmount() { return transAmt; }
}