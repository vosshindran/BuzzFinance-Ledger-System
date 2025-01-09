package businessbuzz.ledgersystem;

import java.time.LocalDate;


class LoanData {
    private final double principalAmount;
    private final double interestRate;
    private final int repaymentPeriod;
    private final double outstandingBalance;
    private final String status;
    private final LocalDate creationDate;

    public LoanData(){
        this.principalAmount = 0.0;
        this.interestRate = 0.0;
        this.repaymentPeriod = 0;
        this.outstandingBalance = 0.0;
        this.status = " ";
        this.creationDate = LocalDate.now();
    }

    public LoanData(double principalAmount, double interestRate, int repaymentPeriod,
                    double outstandingBalance, String status, LocalDate creationDate) { //const.
        this.principalAmount = principalAmount;
        this.interestRate = interestRate;
        this.repaymentPeriod = repaymentPeriod;
        this.outstandingBalance = outstandingBalance;
        this.status = status;
        this.creationDate = creationDate;
    }

    public double getPrincipalAmount() {return principalAmount;}
    public double getInterestRate() {return interestRate;}
    public int getRepaymentPeriod() {return repaymentPeriod;}
    public double getOutstandingBalance() {return outstandingBalance;}
    public String getStatus() {return status;}
    public LocalDate getCreationDate() {return creationDate;}

}
