package businessbuzz.ledgersystem;

//File IO
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

//Date
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

//Data handling
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class LedgerCentral {
    static HashMap<String, String> userCredentialsNew = new HashMap<>();
    static HashMap<String, String> usernameStorage = new HashMap<>();
    static HashMap<String, Double> accountBalanceMap = new HashMap<>();
    static HashMap<String, Double> savingsMap = new HashMap<>();
    static HashMap<String, businessbuzz.ledgersystem.LoanData> loansMap = new HashMap<>();
    static HashMap<String, ArrayList<Transaction>> transactionsMap = new HashMap<>();
    static HashMap<String, LocalDate> lastTransferDateMap = new HashMap<>();
    static HashMap<String, Double> savingsPercentageMap = new HashMap<>();

    //No constructor, LedgerCentral is not instantiated, but its static methods are called via LedgerCentral

    //IMPORTANT 2: Load every csv respectively based on below methods
    public static void initializeSystem() { //
        initialiseCSVs(); //Check if csvs exists and create them if not
        //Load data into hashmaps
        loadUserData();
        loadAccountBalances();
        loadLoans();
        loadSavings();
        loadTransactions();
        loadLastTransferDates();
        loadSavingsPercentages();
    }

    //Hereafter are loading (read csvs, store csvs' data in HashMaps) methods
    public static void initialiseCSVs(){
        File accountBalanceFile = new File("account_balance.csv");
        if (!accountBalanceFile.exists()) {
            try {
                accountBalanceFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Error creating account_balance.csv file.");
            }
        }

//        File bankFile = new File("bank.csv");
//        if (!bankFile.exists()) {
//            try {
//                bankFile.createNewFile();
//            } catch (IOException e) {
//                System.out.println("Error creating bank.csv file.");
//            }
//        }

        File loansFile = new File ("loans.csv");
        if (!loansFile.exists()) {
            try {
                loansFile.createNewFile();
            } catch (Exception e) {
                System.out.println("Error creating loans.csv file.");
            }
        }

        File savingsFile = new File("savings.csv");
        if (!savingsFile.exists()) {
            try {
                savingsFile.createNewFile();
            } catch (Exception e) {
                System.out.println("Error creating savings.csv file.");
            }
        }

        File transactionsFile = new File("transactions.csv");
        if (!transactionsFile.exists()) {
            try {
                transactionsFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Error creating transactions.csv file.");
            }
        }

        File usersFile = new File("users.csv");
        if (!usersFile.exists()){
            try {
                usersFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Error creating users.csv file.");
            }
        }

        File lastTransferDateFile = new File("last_transfer_date.csv");
        if (!lastTransferDateFile.exists()) {
            try {
                lastTransferDateFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Error creating last_transfer_date.csv file.");
            }
        }

        File savingsPercentagesFile = new File("savings_percentages.csv");
        if (!savingsPercentagesFile.exists()) {
            try {
                savingsPercentagesFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Error creating savings_percentages.csv file.");
            }
        }

    }

    public static void loadUserData(){
        try {
            Scanner scUsers = new Scanner(new FileInputStream("users.csv"));
            while (scUsers.hasNextLine()) {
                String line = scUsers.nextLine();
                String[] parts = line.split(",");
                String name = parts[0];
                String email = parts[1];
                String password = parts[2];
                if (parts.length == 3) {
                    LedgerCentral.userCredentialsNew.put(email, password);
                    LedgerCentral.usernameStorage.put(email, name);
                }
            } //When exiting, user credentials new will store all the emails (key) and passwords(value)
            scUsers.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found. Please restart the program.");
        }
    }

    public static void loadAccountBalances(){
        try{
            Scanner scAccBal = new Scanner (new FileInputStream("account_balance.csv"));
            while(scAccBal.hasNextLine()){
                String[] parts = scAccBal.nextLine().split(",");
                String email = parts[0].trim();
                double balance = Double.parseDouble(parts[1]);
                if (parts.length == 2) {
                    accountBalanceMap.put(email, balance);
                }
            }
            scAccBal.close();

        } catch (FileNotFoundException e){
            System.out.println("File not found. Please restart the program.");
        }
    }

//    public static void loadBankRates(){
//        try{
//            Scanner scBkRate = new Scanner (new FileInputStream("bank.csv"));
//            while(scBkRate.hasNextLine()){
//                String[] parts = scBkRate.nextLine().split(",");
//                String bankName = parts[0].trim();
//                double interestRate = Double.parseDouble(parts[1]);
//                bankRatesMap.put(bankName, new BankRate(bankName, interestRate)); //DOUBT
//            }
//            scBkRate.close();
//        } catch (FileNotFoundException e){
//            System.out.println("File not found. Please restart the program.");
//        }
//    }

    public static void loadLoans(){
        try {
            Scanner scLn = new Scanner (new FileInputStream("loans.csv"));
            while(scLn.hasNextLine()){
                String[] parts = scLn.nextLine().split(",");
                try {
                    String email = parts[0].trim();
                    businessbuzz.ledgersystem.LoanData loan = new businessbuzz.ledgersystem.LoanData(
                            Double.parseDouble(parts[1].trim()),
                            Double.parseDouble(parts[2].trim()),
                            Integer.parseInt(parts[3].trim()),
                            Double.parseDouble(parts[4].trim()),
                            parts[5].trim(),
                            LocalDate.parse(parts[6].trim())
                    );
                    loansMap.put(email, loan);
                } catch (NumberFormatException | DateTimeParseException e) {
                    System.out.println("Number Format Error or Date Format Error has occurred. Please check the CSV file and try again.");
                }
            }
            scLn.close();

        } catch (FileNotFoundException e){
            System.out.println("File not found. Please restart the program.");
        }

    }

    public static void loadSavings(){
        try{
            Scanner scSav = new Scanner (new FileInputStream("savings.csv"));
            while(scSav.hasNextLine()){
                String[] parts = scSav.nextLine().split(",");
                String email = parts[0].trim();
                double savingsAmount = Double.parseDouble(parts[1].trim());
                savingsMap.put(email, savingsAmount);
            }
            scSav.close();
        } catch (FileNotFoundException e){
            System.out.println("File not found. Please restart the program.");
        }
    }

    public static void loadTransactions(){
        try {
            Scanner scTran = new Scanner(new FileInputStream("transactions.csv"));
            while (scTran.hasNextLine()){
                String[] parts = scTran.nextLine().split(",");
                String email = parts[0].trim();
                Transaction transaction = new Transaction(
                        parts[1].trim(),
                        Double.parseDouble(parts[2].trim()),
                        parts[3].trim(),
                        LocalDate.parse(parts[4].trim())
                );
                transactionsMap.putIfAbsent(email, new ArrayList<>()); //If it's a new user, their email will be added first and a space to store Transaction data (ArrayList)
                transactionsMap.get(email).add(transaction); //If true or false, based on email, transaction data is added
            }
            scTran.close();
        } catch (FileNotFoundException e){
            System.out.println("File not found. Please restart the program.");
        }
    }

    public static void loadLastTransferDates(){
        try{
            Scanner scLTD = new Scanner (new FileInputStream("last_transfer_date.csv"));
            while(scLTD.hasNextLine()){
                String[] parts = scLTD.nextLine().trim().split(",");
                String email = parts[0].trim();
                LocalDate date = LocalDate.parse(parts[1].trim());
                lastTransferDateMap.put(email, date);
            }
        } catch (FileNotFoundException e){
            System.out.println("File not found. Please restart the program.");
        }
    }

    public static void loadSavingsPercentages(){
        try{
            Scanner scPerc = new Scanner (new FileInputStream("savings_percentages.csv"));
            while(scPerc.hasNextLine()){
                String[] parts = scPerc.nextLine().trim().split(",");
                String email = parts[0].trim();
                Double percentage = Double.parseDouble(parts[1].trim());
                savingsPercentageMap.put(email, percentage);
            }
        } catch (FileNotFoundException e){
            System.out.println("File not found. Please restart the program.");
        }
    }

    public static void clearEveryCSV(){
        clearUserData();
        clearAccountBalances();
        clearLoans();
        clearSavings();
        clearTransactions();
        clearLastTransferDates();
        clearSavingsPercentages();
    }

    public static void writeEveryHashmapOnEveryCSV(){ //Unused. Will write everytime an operation is done
        writeUserData();
        writeAccountBalances();
        writeLoans();
        writeSavings();
        writeTransactions();
        writeLastTransferDates();
        writeSavingsPercentages();
    }

    public static void clearUserData(){
        try{
            FileWriter fw = new FileWriter("users.csv");
            fw.write("");
            fw.close();
        } catch (IOException e){
            System.out.println("Error erasing users.csv file.");
        }
    }

    public static void clearAccountBalances(){
        try{
            FileWriter fw = new FileWriter("account_balance.csv");
            fw.write("");
            fw.close();
        } catch (IOException e){
            System.out.println("Error erasing account_balance.csv file.");
        }
    }

    public static void clearLoans(){
        try{
            FileWriter fw = new FileWriter("loans.csv");
            fw.write("");
            fw.close();
        } catch (IOException e){
            System.out.println("Error erasing loans.csv file.");
        }
    }

    public static void clearSavings(){
        try{
            FileWriter fw = new FileWriter("savings.csv");
            fw.write("");
            fw.close();
        } catch (IOException e){
            System.out.println("Error erasing savings.csv file.");
        }
    }

    public static void clearTransactions(){
        try{
            FileWriter fw = new FileWriter("transactions.csv");
            fw.write("");
            fw.close();
        } catch (IOException e){
            System.out.println("Error erasing transactions.csv file.");
        }
    }

    public static void clearLastTransferDates(){
        try{
            FileWriter fw = new FileWriter("last_transfer_date.csv");
            fw.write("");
            fw.close();
        } catch (IOException e){
            System.out.println("Error erasing last_transfer_date.csv file.");
        }
    }

    public static void clearSavingsPercentages(){
        try{
            FileWriter fw = new FileWriter("savings_percentages.csv");
            fw.write("");
            fw.close();
        } catch (IOException e){
            System.out.println("Error erasing savings_percentages.csv file.");
        }
    }

    public static void writeUserData(){
        try {
            PrintWriter pwUsers = new PrintWriter(new FileOutputStream("users.csv"));
            usernameStorage.forEach((email, name) ->
            {
                pwUsers.println(name + "," + email + "," + userCredentialsNew.get(email));
            } );
            pwUsers.close();
        } catch (IOException e){
            System.out.println("Error writing on users.csv file.");
        }
    }

    public static void writeAccountBalances(){
        try {
            PrintWriter pwAccBal = new PrintWriter(new FileOutputStream("account_balance.csv"));
            accountBalanceMap.forEach((email, balance) ->
            {
                pwAccBal.println(email + "," + balance);
            } );
            pwAccBal.close();
        } catch (IOException e){
            System.out.println("Error writing on account_balance.csv file.");
        }
    }

    public static void writeLoans(){
        try{
            PrintWriter pwLoans = new PrintWriter (new FileOutputStream("loans.csv"));
            loansMap.forEach((email, loan) ->
            {
                pwLoans.printf("%s,%.2f,%.2f,%d,%.2f,%s,%s%n",
                        email,loan.getPrincipalAmount(),loan.getInterestRate(),loan.getRepaymentPeriod(),
                        loan.getOutstandingBalance(),loan.getStatus(),loan.getCreationDate());
            });
            pwLoans.close();
        } catch (IOException e){
            System.out.println("Error writing on loans.csv file.");
        }
    }

    public static void writeSavings(){
        try{
            PrintWriter pwSavings = new PrintWriter (new FileOutputStream("savings.csv"));
            savingsMap.forEach((email, savings) ->
            {
                pwSavings.printf("%s,%.2f%n", email, savings);
            });
            pwSavings.close();
        } catch (IOException e){
            System.out.println("Error writing on savings.csv file.");
        }
    }

    public static void writeTransactions(){
        try{
            PrintWriter pwTran = new PrintWriter(new FileOutputStream("transactions.csv"));
            transactionsMap.forEach((email, transactionList) ->
            {
                transactionList.forEach(transaction ->
                {
                    pwTran.printf("%s,%s,%.2f,%s,%s%n", email, transaction.getType(), transaction.getAmount(), transaction.getDescription(), transaction.getDate());
                });
            });
            pwTran.close();

        } catch (IOException e){
            System.out.println("Error erasing transactions.csv file.");
        }
    }

    public static void writeLastTransferDates(){
        try{
            PrintWriter pwLTD = new PrintWriter(new FileOutputStream("last_transfer_date.csv"));
            lastTransferDateMap.forEach((email, date) ->
            {
                pwLTD.printf("%s,%s%n", email, date);
            });
            pwLTD.close();
        } catch (IOException e){
            System.out.println("Error erasing last_transfer_date.csv file.");
        }
    }

    public static void writeSavingsPercentages(){
        try{
            PrintWriter pwSavPer = new PrintWriter (new FileOutputStream("savings_percentages.csv"));
            savingsPercentageMap.forEach((email, percentage) ->
            {
                pwSavPer.printf("%s,%.2f%n", email, percentage);
            });
            pwSavPer.close();
        } catch (IOException e){
            System.out.println("Error writing on savings_percentages.csv file.");
        }
    }
}