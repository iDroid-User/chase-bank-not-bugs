import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EOptionsPanel extends JPanel {
   private JLabel prompt;
   private JRadioButton one, two, three, four;

   // Creates a panel with a label and a group of radio buttons that present options to the user
   public EOptionsPanel() {
      prompt = new JLabel("Choose action:"); // Panel title
      prompt.setFont(new Font("Helvetica", Font.BOLD, 24));

      one = new JRadioButton("Enter Transaction");
      one.setBackground(Color.green);
      two = new JRadioButton("List All Transactions");
      two.setBackground(Color.green);
      three = new JRadioButton("List All Checks");
      three.setBackground(Color.green);
      four = new JRadioButton("List All Deposits");
      four.setBackground(Color.green);

      ButtonGroup group = new ButtonGroup();
      group.add(one);
      group.add(two);
      group.add(three);
      group.add(four);

      EOptionListener listener = new EOptionListener();
      one.addActionListener(listener);
      two.addActionListener(listener);
      three.addActionListener(listener);
      four.addActionListener(listener);

      // Adds the components to the JPanel
      add(prompt);
      add(one);
      add(two);
      add(three);
      add(four);
      setBackground(Color.green);
      setPreferredSize(new Dimension(400, 100));
   }

   // The listener class for the radio buttons
   private class EOptionListener implements ActionListener {
      // Calls the method to process the radio button that was pressed
      public void actionPerformed(ActionEvent event) {
         Object source = event.getSource();

         if (source == one) {
            Main.setTransactionInfo();
         } else if (source == two) {
            Main.getTransactionInfo();
         } else if (source == three) {
            Main.getChecks();
         } else if (source == four) {
            Main.getDeposits();
         }
      }
   }
}