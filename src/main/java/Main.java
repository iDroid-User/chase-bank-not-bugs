import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class Main {
   // global variables
   private static boolean isBelow500 = false; // Tracks whether the balance has dropped below $500.00
    // define a CheckingAccount object to keep track of the account information
   private static CheckingAccount account;
   public static JFrame frame;

   public static void main(String[] args) {
      // define local variables
      String message, accountHolder;
      double initialBalance;

      // get the name of the person who owns the account
      accountHolder = (JOptionPane.showInputDialog("Enter the account name:"));
      // get initial balance from the user
      initialBalance = Double.parseDouble(JOptionPane.showInputDialog("Enter your initial balance:"));
      // open an account with the initial balance
      account = new CheckingAccount(accountHolder, initialBalance);

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

      String message;
      double transactionAmount;
      int transactionCode = Integer.parseInt(JOptionPane.showInputDialog("Enter your transaction code:"));
      if (0 == transactionCode) {
         message = "Transaction: End\nCurrent Balance: " + formatAmount(account.getBalance())
                 + "\nTotal Service Charge: "
                 + formatAmount(account.getServiceCharge()) + "\nFinal Balance: "
                 + formatAmount(account.getBalance() - account.getServiceCharge());
      } else if (1 == transactionCode) {
         int checkNumber = Integer.parseInt(JOptionPane.showInputDialog("Enter your check number:"));
         transactionAmount = Double.parseDouble(JOptionPane.showInputDialog("Enter your check amount:"));
         Check check = new Check(transactionCode, transactionAmount, account.getTransCount(), checkNumber);
         account.setBalance(transactionAmount, transactionCode);
         account.addTrans(check);
         message = processCheck(transactionAmount, checkNumber);
      } else if (2 == transactionCode) {
         // Merge these dialog boxes somehow
         JPanel depositPanel = new JPanel(new GridLayout(2, 2));
         JTextField cashField = new JTextField(10);
         JTextField checkField = new JTextField(10);
         depositPanel.add(new JLabel("Cash"));
         depositPanel.add(cashField);
         depositPanel.add(new JLabel("Checks"));
         depositPanel.add(checkField);

         int result = JOptionPane.showConfirmDialog(null, depositPanel, "Deposit Window",
                 JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
         if (result == JOptionPane.OK_OPTION) {
            double cashAmount = Double.parseDouble(cashField.getText());
            double checkAmount = Double.parseDouble(checkField.getText());
            transactionAmount = cashAmount + checkAmount;
            Deposit deposit = new Deposit(transactionCode, transactionAmount, account.getTransCount(), cashAmount,
                    checkAmount);
            account.setBalance(transactionAmount, transactionCode);
            account.addTrans(deposit);
            message = processDeposit(transactionAmount);
         } else { message = "Transaction cancelled"; }
      } else { message = "Invalid transaction code"; }
      JOptionPane.showMessageDialog(null, message);
      frame.setVisible(true);
   }

   public static void getTransactionInfo() {
      frame.setVisible(false);

      String message, transType;
      message = "List All Transactions" + "\n\nID\t\tType\t\tAmount";
      for (int index = 0; index < account.getTransCount(); index++) {
         if (account.getTrans(index).getTransId() == 1) {
            transType = "Check\t\t\t";
         } else if (account.getTrans(index).getTransId() == 2) {
            transType = "Deposit\t\t";
         } else if (account.getTrans(index).getTransId() == 3) {
            transType = "Svc. Chg.\t";
         } else {
            transType = "";
         }
         message += "\n" + account.getTrans(index).getTransNumber() + "\t\t" + transType +
                 formatAmount(account.getTrans(index).getTransAmount());
      }
      JOptionPane.showMessageDialog(null, message);

      frame.setVisible(true);
   }

   public static void getChecks() {
      frame.setVisible(false);

      String message;
      message = "List All Checks" + "\n\nID\tAmount";
      for (int index = 0; index < account.getTransCount(); index++) {
         if (account.getTrans(index).getTransId() == 1) {
            message += "\n" + account.getTrans(index).getTransNumber() + "\t" +
                    formatAmount(account.getTrans(index).getTransAmount());
         }
      }
      JOptionPane.showMessageDialog(null, message);

      frame.setVisible(true);
   }

   public static void getDeposits() {
      frame.setVisible(false);

      String message;
      message = "List All Deposits" + "\n\nID\tAmount";
      for (int index = 0; index < account.getTransCount(); index++) {
         if (account.getTrans(index).getTransId() == 2) {
            message += "\n" + account.getTrans(index).getTransNumber() + "\t" +
                    formatAmount(account.getTrans(index).getTransAmount());
         }
      }
      JOptionPane.showMessageDialog(null, message);

      frame.setVisible(true);
   }

   public static String processCheck(double transAmt, int checkNumber) {
      String msg = account.getName() + "'s Account\nTransaction: Check #" + checkNumber + " in Amount of "
              + formatAmount(transAmt) + "\nCurrent Balance: " + formatAmount(account.getBalance())
              + "\nService Charge: Check --- charge $0.15";
      Transaction serviceCharge = new Transaction(account.getTransCount(), 3, 0.15); // ID 3: service charges
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
      String msg = account.getName() + "'s Account\nTransaction: Deposit in Amount of " + formatAmount(transAmt)
            + "\nCurrent Balance: " + formatAmount(account.getBalance())
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