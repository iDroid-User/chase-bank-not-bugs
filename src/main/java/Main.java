import javax.swing.JOptionPane;
import javax.swing.JFrame;
import java.text.DecimalFormat;

public class Main {
   // global variables
   private static boolean isBelow500 = false; // Tracks whether the balance has dropped below $500.00
    // define a CheckingAccount object to keep track of the account information
   private static CheckingAccount account;
   public static JFrame frame;

   public static void main(String[] args) {
      // define local variables
      String message;
      double initialBalance;
      // get initial balance from the user
      initialBalance = Double.parseDouble(JOptionPane.showInputDialog("Enter your initial balance:"));
      // open an account with the initial balance
      account = new CheckingAccount(initialBalance);

      frame = new JFrame("Choose action:");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ends the program rather than a transaction code of 0
      EOptionsPanel panel = new EOptionsPanel();
      frame.getContentPane().add(panel);
      frame.pack();
      frame.setVisible(true);
   }

   // Get the transaction code from the user and process it with the appropriate helper method
   public static void setTransactionInfo() {
       frame.setVisible(false); // Hides the JFrame

      int transactionCode = Integer.parseInt(JOptionPane.showInputDialog("Enter your transaction code:"));
      double transactionAmount = Double.parseDouble(JOptionPane.showInputDialog("Enter your transaction amount:"));
      Transaction transaction = new Transaction(account.getTransCount(), transactionCode, transactionAmount);
      account.setBalance(transactionAmount, transactionCode);
      account.addTrans(transaction);

      // process the transaction
       String message;
       if (1 == transactionCode) {
           message = processCheck(transactionAmount);
       } else if (2 == transactionCode) {
           message = processDeposit(transactionAmount);
       } else if (0 == transactionCode) {
          message = "Transaction: End\nCurrent Balance: " + formatAmount(account.getBalance())
                  + "\nTotal Service Charge: "
                  + formatAmount(account.getServiceCharge()) + "\nFinal Balance: "
                  + formatAmount(account.getBalance() - account.getServiceCharge());
          JOptionPane.showMessageDialog(null, message);
       }
      else {
           message = "Invalid transaction code";
       }
      JOptionPane.showMessageDialog(null, message);
      frame.setVisible(true);
   }

   public static void getTransactionInfo() {
      frame.setVisible(false);

      String message, transType = "";
      message = "List All Transactions" + "\n\nID\tType\t\t\t\t\tAmount";
      for (int index = 0; index < account.getTransCount(); index++) {
         if (account.getTrans(index).getTransId() == 1) {
            transType = "Check";
         } else if (account.getTrans(index).getTransId() == 2) {
            transType = "Deposit";
         } else if (account.getTrans(index).getTransId() == 3) {
            transType = "Svc. Chg.";
         }
         message += "\n" + account.getTrans(index).getTransNumber() + "\t" + transType + "\t\t" +
                 formatAmount(account.getTrans(index).getTransAmount());
      }
      JOptionPane.showMessageDialog(null, message);

      frame.setVisible(true);
   }

   public static String processCheck(double transAmt) {
      String msg = "Transaction: Check in Amount of " + formatAmount(transAmt) + "\nCurrent Balance: "
            + formatAmount(account.getBalance())
            + "\nService Charge: Check --- charge $0.15";
      Transaction serviceCharge = new Transaction(account.getTransCount(), 3, 0.15);
      account.addTrans(serviceCharge);

      // Charges $5.00 the first time the balance drops below $500.00
      if (account.getBalance() < 500 && !isBelow500) {
         isBelow500 = true;
         msg += "\nServiceCharge: Below $500 --- charge $5.00";
         account.setServiceCharge(5.00);
         serviceCharge = new Transaction(account.getTransCount(), 3, 5.00);
         account.addTrans(serviceCharge);
      }
      // Issues a warning when the balance drops below $50.00
      if (account.getBalance() < 50) {
         msg += "\nWarning: Balance below $50";
      }
      // Charges an overdraft fee of $10.00
      if (account.getBalance() < 0) {
         msg += "\nServiceCharge: Below $0 --- charge $10.00";
         account.setServiceCharge(10.00);
         serviceCharge = new Transaction(account.getTransCount(), 3, 10.00);
         account.addTrans(serviceCharge);
      }
      msg += "\nTotal Service Charge: " + formatAmount(account.getServiceCharge());
      return msg;
   }

   public static String processDeposit(double transAmt) {
      String msg = "Transaction: Deposit in Amount of " + formatAmount(transAmt) + "\nCurrent Balance: "
            + formatAmount(account.getBalance())
            + "\nService Charge: Deposit --- charge $0.10\nTotal Service Charge: "
            + formatAmount(account.getServiceCharge());
      Transaction serviceCharge = new Transaction(account.getTransCount(), 3, 0.10);
      account.addTrans(serviceCharge);
      return msg;
   }

   public static String formatAmount(double amount) {
      DecimalFormat fmtNegative = new DecimalFormat("($0.00)"); // For a negative balance
      DecimalFormat fmtPositive = new DecimalFormat("$0.00"); // For a positive balance

      if (amount < 0) {
         return fmtNegative.format(Math.abs(amount));
      } else {
         return fmtPositive.format(amount);
      }
   }
}