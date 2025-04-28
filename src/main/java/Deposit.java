public class Deposit extends Transaction{
    private final double cashAmt, checkAmt;

    public Deposit(int tId, double tAmt, int tCount, double cashAmt, double checkAmt) {
        super(tCount, tId, tAmt);
        this.cashAmt = cashAmt;
        this.checkAmt = checkAmt;
    }

    public double getCashAmt() { return cashAmt; }
    public double getCheckAmt() { return checkAmt; }
}
