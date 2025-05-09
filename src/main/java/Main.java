import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class Main {
   // global variables
   private static boolean isBelow500 = false; // Tracks whether the balance has dropped below $500.00
   public static JFrame frame;

   public static void main(String[] args) {
      // define local variables
      String accountHolder;
      double initialBalance;

      // get the name of the person who owns the account
      accountHolder = (JOptionPane.showInputDialog("Enter the account name:"));
      // get initial balance from the user
      initialBalance = Double.parseDouble(JOptionPane.showInputDialog("Enter your initial balance:"));
      // open an account with the initial balance
      CheckOptionsPanel.account = new CheckingAccount(accountHolder, initialBalance);

      frame = new JFrame("Choose action:");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ends the program rather than a transaction code of 0
      CheckOptionsPanel panel = new CheckOptionsPanel();
      frame.getContentPane().add(panel);
      frame.pack();
      frame.setVisible(true);
   }

   // Get the transaction code from the user and process it with the appropriate helper method
   public static void setTransactionInfo() {
       frame.setVisible(false); // Hides the JFrame

      String message = "Transaction cancelled"; // Default message
      double transactionAmount;
      int transactionCode = Integer.parseInt(JOptionPane.showInputDialog("Enter your transaction code:"));
      if (0 == transactionCode) {
         message = "Transaction: End\nCurrent Balance: " + formatAmount(CheckOptionsPanel.account.getBalance())
                 + "\nTotal Service Charge: "
                 + formatAmount(CheckOptionsPanel.account.getServiceCharge()) + "\nFinal Balance: "
                 + formatAmount(CheckOptionsPanel.account.getBalance() - CheckOptionsPanel.account.getServiceCharge());
      } else if (1 == transactionCode) {
         int checkNumber = Integer.parseInt(JOptionPane.showInputDialog("Enter your check number:"));
         transactionAmount = Double.parseDouble(JOptionPane.showInputDialog("Enter your check amount:"));
         Check check = new Check(transactionCode, transactionAmount, CheckOptionsPanel.account.getTransCount(), checkNumber);
         CheckOptionsPanel.account.setBalance(transactionAmount, transactionCode);
         CheckOptionsPanel.account.addTrans(check);
         message = processCheck(transactionAmount, checkNumber);
      } else if (2 == transactionCode) {
         // The depositPanel has two input fields in the same dialog box
         JPanel depositPanel = new JPanel(new GridLayout(2, 2));
         JTextField cashField = new JTextField(10);
         JTextField checkField = new JTextField(10);
         depositPanel.add(new JLabel("Cash"));
         depositPanel.add(cashField);
         depositPanel.add(new JLabel("Checks"));
         depositPanel.add(checkField);

         int result = JOptionPane.showConfirmDialog(null, depositPanel, "Deposit Window",
                 JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
         if (result == JOptionPane.OK_OPTION) {
             try {
                 String cashText = cashField.getText().trim();
                 String checkText = checkField.getText().trim();
                 
                 // Convert empty fields to 0.0
                 double cashAmount = cashText.isEmpty() ? 0.0 : Double.parseDouble(cashText);
                 double checkAmount = checkText.isEmpty() ? 0.0 : Double.parseDouble(checkText);
                 
                 transactionAmount = cashAmount + checkAmount;
                 Deposit deposit = new Deposit(transactionCode, transactionAmount,
                         CheckOptionsPanel.account.getTransCount(), cashAmount, checkAmount);
                 CheckOptionsPanel.account.setBalance(transactionAmount, transactionCode);
                 CheckOptionsPanel.account.addTrans(deposit);
                 message = processDeposit(transactionAmount);
             } catch (Exception e) {
                 message = "Error: Please enter valid numerical values";
             }
         }
      }
      JOptionPane.showMessageDialog(null, message);
      frame.setVisible(true);
   }

   public static void getTransactionInfo() {
      frame.setVisible(false);

      String message, transType;
      message = "List All Transactions" + "\nName: " + CheckOptionsPanel.account.getName() + "\n\nID\t\tType\t\tAmount";
      for (int index = 0; index < CheckOptionsPanel.account.getTransCount(); index++) {
         if (CheckOptionsPanel.account.getTrans(index).getTransId() == 1) {
            transType = "Check\t\t\t";
         } else if (CheckOptionsPanel.account.getTrans(index).getTransId() == 2) {
            transType = "Deposit\t\t";
         } else if (CheckOptionsPanel.account.getTrans(index).getTransId() == 3) { // ID 3: service charges
            transType = "Svc. Chg.\t";
         } else {
            transType = "";
         }
         message += "\n" + CheckOptionsPanel.account.getTrans(index).getTransNumber() + "\t\t" + transType +
                 formatAmount(CheckOptionsPanel.account.getTrans(index).getTransAmount());
      }
      JOptionPane.showMessageDialog(null, message);

      frame.setVisible(true);
   }

   public static void getChecks() {
      frame.setVisible(false);

      String message;
      message = "List All Checks" + "\nName: " + CheckOptionsPanel.account.getName() + "\n\nID\tCheck\tAmount";
      for (int index = 0; index < CheckOptionsPanel.account.getTransCount(); index++) {
         if (CheckOptionsPanel.account.getTrans(index).getTransId() == 1) {
            message += "\n" + CheckOptionsPanel.account.getTrans(index).getTransNumber() + "\t" +
                    ((Check)CheckOptionsPanel.account.getTrans(index)).getCheckNumber() + "\t" +
                    formatAmount(CheckOptionsPanel.account.getTrans(index).getTransAmount());
         }
      }
      JOptionPane.showMessageDialog(null, message);

      frame.setVisible(true);
   }

   public static void getDeposits() {
      frame.setVisible(false);

      String message;
      message = "List All Deposits\nName: " + CheckOptionsPanel.account.getName() + "\n\nID\tCash\t\tCheck\t\tAmount";
      for (int index = 0; index < CheckOptionsPanel.account.getTransCount(); index++) {
         if (CheckOptionsPanel.account.getTrans(index).getTransId() == 2) {
            message += "\n" + CheckOptionsPanel.account.getTrans(index).getTransNumber() + "\t" +
                    formatAmount(((Deposit)CheckOptionsPanel.account.getTrans(index)).getCashAmt()) + "\t\t" +
                    formatAmount(((Deposit)CheckOptionsPanel.account.getTrans(index)).getCheckAmt()) + "\t\t" +
                    formatAmount(((Deposit)CheckOptionsPanel.account.getTrans(index)).getTransAmount());
         }
      }
      JOptionPane.showMessageDialog(null, message);

      frame.setVisible(true);
   }

   public static String processCheck(double transAmt, int checkNumber) {
      String msg = CheckOptionsPanel.account.getName() + "'s Account\nTransaction: Check #" + checkNumber + " in Amount of "
              + formatAmount(transAmt) + "\nCurrent Balance: " + formatAmount(CheckOptionsPanel.account.getBalance())
              + "\nService Charge: Check --- charge $0.15";
      Transaction serviceCharge = new Transaction(CheckOptionsPanel.account.getTransCount(), 3, 0.15);
      CheckOptionsPanel.account.addTrans(serviceCharge);

      // Charges $5.00 the first time the balance drops below $500.00
      if (CheckOptionsPanel.account.getBalance() < 500 && !isBelow500) {
         isBelow500 = true;
         msg += "\nServiceCharge: Below $500 --- charge $5.00";
         CheckOptionsPanel.account.setServiceCharge(5.00);
         serviceCharge = new Transaction(CheckOptionsPanel.account.getTransCount(), 3, 5.00);
         CheckOptionsPanel.account.addTrans(serviceCharge);
      }
      // Issues a warning when the balance drops below $50.00
      if (CheckOptionsPanel.account.getBalance() < 50) {
         msg += "\nWarning: Balance below $50";
      }
      // Charges an overdraft fee of $10.00
      if (CheckOptionsPanel.account.getBalance() < 0) {
         msg += "\nServiceCharge: Below $0 --- charge $10.00";
         CheckOptionsPanel.account.setServiceCharge(10.00);
         serviceCharge = new Transaction(CheckOptionsPanel.account.getTransCount(), 3, 10.00);
         CheckOptionsPanel.account.addTrans(serviceCharge);
      }
      msg += "\nTotal Service Charge: " + formatAmount(CheckOptionsPanel.account.getServiceCharge());
      return msg;
   }

   public static String processDeposit(double transAmt) {
      String msg = CheckOptionsPanel.account.getName() + "'s Account\nTransaction: Deposit in Amount of " + formatAmount(transAmt)
            + "\nCurrent Balance: " + formatAmount(CheckOptionsPanel.account.getBalance())
            + "\nService Charge: Deposit --- charge $0.10\nTotal Service Charge: "
            + formatAmount(CheckOptionsPanel.account.getServiceCharge());
      Transaction serviceCharge = new Transaction(CheckOptionsPanel.account.getTransCount(), 3, 0.10);
      CheckOptionsPanel.account.addTrans(serviceCharge);
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