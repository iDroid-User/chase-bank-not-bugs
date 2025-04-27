public class Deposit extends Transaction{
    int depositNumber;
    // private double cashAmt, checkAmt; // May not be necessary, just arrays to track them

    public Deposit(int tId, double tAmt, int tCount, int depositNumber) {
        super(tCount, tId, tAmt);
        this.depositNumber = depositNumber;
    }

    public int getDepositNumber() { return depositNumber; }
    public void setDepositNumber(int depositNumber) { this.depositNumber = depositNumber; }
}
