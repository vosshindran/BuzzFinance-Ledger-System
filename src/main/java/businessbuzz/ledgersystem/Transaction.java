package businessbuzz.ledgersystem;

import java.time.LocalDate;

class Transaction {
    private final String type;
    private final double amount;
    private final String description;
    private final LocalDate date;

    public Transaction(){
        this.type = " ";
        this.amount = 0.0;
        this.description = "Account Created.";
        this.date = LocalDate.now();
    }

    public Transaction(String type, double amount, String description, LocalDate date) { //const.
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }
    public String getType() {return type;}
    public double getAmount() {return amount;}
    public String getDescription() {return description;}
    public LocalDate getDate() {return date;}
}
