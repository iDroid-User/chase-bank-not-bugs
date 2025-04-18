import javax.swing.JOptionPane;
import javax.swing.JFrame;
import java.text.DecimalFormat;

public class Main {
   // global variables
   private static Boolean isBelow500 = false; // Tracks whether the balance has dropped below $500.00
   private static int transactionCode;
   private static double transactionAmount;
   // define a CheckingAccount object to keep track of the account information
   private static CheckingAccount account;
   public static JFrame frame;

   public static void main(String[] args) {
      // defines local variables
      String message;
      double initialBalance;
      // get initial balance from the user
      initialBalance = Double.parseDouble(JOptionPane.showInputDialog("Enter your initial balance: "));
      // open an account with the initial balance
      account = new CheckingAccount(initialBalance);

      frame = new JFrame("Choose action:");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ends the program rather than a transaction code of 0
      EOptionsPanel panel = new EOptionsPanel();
      frame.getContentPane().add(panel);
      frame.pack();

      // perform in a loop until the transaction code == 0
      do {
         frame.setVisible(true); // Displays the JFrame
         // get the transaction code from the user
         transactionCode = getTransCode();
         // break the loop if the transaction code is 0
         if (0 == transactionCode)
            break;
         // and process it with appropriate helper method
         transactionAmount = getTransAmt();
         account.setBalance(transactionAmount, transactionCode); // Make a transaction

         // process the transaction
         if (1 == transactionCode)
            message = processCheck(transactionAmount);
         else if (2 == transactionCode)
            message = processDeposit(transactionAmount);
         else
            message = "Invalid transaction code";
         JOptionPane.showMessageDialog(null, message);
      } while (transactionCode > 0);

      // When loop ends show final balance to user.
      message = "Transaction: End\nCurrent Balance: " + formatAmount(account.getBalance())
            + "\nTotal Service Charge: "
            + formatAmount(account.getServiceCharge()) + "\nFinal Balance: "
            + formatAmount(account.getBalance() - account.getServiceCharge());
      JOptionPane.showMessageDialog(null, message);
   }

   public static int getTransCode() {
      return Integer.parseInt(JOptionPane.showInputDialog("Enter your transaction code: "));
   }

   public static double getTransAmt() {
      return Double.parseDouble(JOptionPane.showInputDialog("Enter your transaction amount: "));
   }

   public static String processCheck(double transAmt) {
      String msg = "Transaction: Check in Amount of " + formatAmount(transAmt) + "\nCurrent Balance: "
            + formatAmount(account.getBalance())
            + "\nService Charge: Check --- charge $0.15";
      // Charges $5.00 the first time the balance drops below $500.00
      if (account.getBalance() < 500 && isBelow500 == false) {
         isBelow500 = true;
         msg += "\nServiceCharge: Below $500 --- charge $5.00";
         account.setServiceCharge(5.00);
      }
      // Issues a warning when the balance drops below $50.00
      if (account.getBalance() < 50) {
         msg += "\nWarning: Balance below $50";
      }
      // Charges an overdraft fee of $10.00
      if (account.getBalance() < 0) {
         msg += "\nServiceCharge: Below $0 --- charge $10.00";
         account.setServiceCharge(10.00);
      }
      msg += "\nTotal Service Charge: " + formatAmount(account.getServiceCharge());
      return msg;
   }

   public static String processDeposit(double transAmt) {
      String msg = "Transaction: Deposit in Amount of " + formatAmount(transAmt) + "\nCurrent Balance: "
            + formatAmount(account.getBalance())
            + "\nService Charge: Deposit --- charge $0.10\nTotal Service Charge: "
            + formatAmount(account.getServiceCharge());
      account.setServiceCharge(0.10);
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