package businessbuzz.ledgersystem;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.time.LocalDate;
import java.io.FileWriter;

class Main{
    static final double TRANSACTION_LIMIT = 10000.0;
    static final int DESCRIPTION_LIMIT = 50;

    public static void main(String[] args) {
        LedgerCentral.initializeSystem();
        Scanner input = new Scanner(System.in);
//      troubleshoot(input);

        System.out.println("\n== BuzzFinance Ledger System ==");
        System.out.println("Business Buzz © 2025\n");
        
        while (true) {
            System.out.println("Login or Register: ");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.print("> ");

            try {
                int number = input.nextInt();
                input.nextLine();

                switch (number) {
                    case 1:
                        login(input);
                        break;
                    case 2:
                        register(input);
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                input.nextLine();
            }
        }
    }

    public static void troubleshoot(Scanner input) {
        System.out.println("\n===Troubleshooting Mode===");
        LedgerCentral.userCredentialsNew.put("v@g.c", "11111!");
        LedgerCentral.usernameStorage.put("v@g.c", "Vosshindran Thiyagaraja");
        LedgerCentral.accountBalanceMap.put("v@g.c", 1500.0); //for debit trial shortcut
        LedgerCentral.savingsPercentageMap.put("v@g.c", 0.0);
        LedgerCentral.savingsMap.put("v@g.c", 50.0);
        LedgerCentral.transactionsMap.put("v@g.c",new ArrayList<>());
        LoanData sampleLoan = new LoanData(5000,2.5,12,5000,"active",LocalDate.now());
        LedgerCentral.loansMap.put("v@g.c", sampleLoan);
//        LedgerCentral.savingsMap.put("v@g.c", 0.0);
        try {
            applyLoan(input, "v@g.c");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void login(Scanner input) {

        System.out.println("\n== Please enter your email and password ==");
        System.out.print("Email: ");
        String email = input.nextLine().toLowerCase(); // Convert email to lowercase
        System.out.print("Password: ");
        String password = input.nextLine();


        if (LedgerCentral.userCredentialsNew.containsKey(email) && BCrypt.checkpw(password, LedgerCentral.userCredentialsNew.get(email))) {
            System.out.println("\nLogin Successful!!!");
            checkLoanReminder(email); //display loan reminder
            dashboard (email, input);
        } else {
            System.out.println("Invalid email or password. If you don't have an account, you should register first.");
            System.out.print("Would you like to register? (Y/N) ");
            String choice = input.nextLine().toLowerCase();

            if (choice.equals("y")) {
                register(input); // If user wants to register, prompt for registration
            } else {
                System.out.println("Exiting the program.");
                System.exit(0); // Exit the program if user doesn't want to register
            }
        }
    }

    public static void register(Scanner input) {
        System.out.println("\n== Please fill in your details ==");

        System.out.print("Name: ");
        String name = input.nextLine();
        if (!Pattern.matches("^[a-zA-Z0-9 ]+$", name)) {
            System.out.println("Name must be alphanumeric and cannot contain special characters.");
            return;
        }


        System.out.print("Email: ");
        String email = input.nextLine().toLowerCase();
        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email)) {
            System.out.println("Invalid email format.");
            return;
        }

        if (LedgerCentral.accountBalanceMap.containsKey(email)) {
            System.out.println("Email already registered.");
            return;
        }


        System.out.print("Password: ");
        String password = input.nextLine();
        if (password.length() < 6 || !password.matches(".*[!@#$%^&*()].*")) {
            System.out.println("Password must be at least 6 characters long and contain at least one special character.");
            return;
        }


        System.out.print("Confirm Password: ");
        String confirmPassword = input.nextLine();
        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }
        
        password=BCrypt.hashpw(password, BCrypt.gensalt());
        
        LedgerCentral.userCredentialsNew.put(email, password);
        LedgerCentral.usernameStorage.put(email, name);
        LedgerCentral.accountBalanceMap.put(email, 0.0);
        LedgerCentral.savingsPercentageMap.put(email, 0.0);
        LedgerCentral.savingsMap.put(email, 0.0);
        LedgerCentral.transactionsMap.put(email, new ArrayList<>(Arrays.asList(new Transaction())));
        LedgerCentral.lastTransferDateMap.put(email, LocalDate.now());
        LedgerCentral.loansMap.put(email, new LoanData());

        LedgerCentral.clearEveryCSV();
        LedgerCentral.writeUserData();
        LedgerCentral.writeAccountBalances();
        LedgerCentral.writeSavingsPercentages();
        LedgerCentral.writeSavings();
        LedgerCentral.writeTransactions();
        LedgerCentral.writeLoans();
        LedgerCentral.writeLastTransferDates();



        System.out.println("\nRegistration Successful!!!");
        login(input); //ask email, pw and check if they're inside hashmap
    }

    public static void dashboard(String email, Scanner input) {
        while (true) {
            System.out.printf("\n== Welcome, %s ==\n", LedgerCentral.usernameStorage.get(email)); // Print the welcome message with the user's name by formatting the string with their registered name from the HashMap
            System.out.printf("Balance: $%.2f\n", LedgerCentral.accountBalanceMap.get(email));
            System.out.printf("Savings: $%.2f\n", LedgerCentral.savingsMap.get(email));

            if (LedgerCentral.loansMap.containsKey(email)) {
                System.out.printf("Loan: $%.2f (Outstanding: $%.2f)\n", LedgerCentral.loansMap.get(email).getPrincipalAmount(), LedgerCentral.loansMap.get(email).getOutstandingBalance());
            }

            checkLoanReminder(email);
            System.out.println("\n== Transaction ==");
            System.out.println("1. Debit");
            System.out.println("2. Credit");
            System.out.println("3. History");
            System.out.println("4. Savings");
            System.out.println("5. Credit Loan");
            System.out.println("6. Deposit Interest Predictor");
            System.out.println("7. Transaction Analysis");
            System.out.println("8. Logout");

            System.out.print("> ");

            int option = input.nextInt();
            input.nextLine(); // Clears the newline left in the buffer by nextInt() to prevent skipping the next user input

            if (option < 1 || option > 8) {
                System.out.println("Please select a valid option (1-8)");
                continue;
            }

            switch (option) { //pass down a Scanner object and the email to every case. EMAIL IS THE SOLE IDENTIFIER
                case 1:
                    debit(input,email);
                    break;
                case 2:
                    credit(input, email);
                    break;
                case 3:
                    displayHistory(email);
                    break;
                case 4:
                    activateSavings(input, email);
                    break;
                case 5:
                    creditLoan(input, email);
                    break;
                case 6:
                    depositInterestPredictor(input, email);
                    break;
                case 7:
                    showTransactionAnalysis(input, email);
                    break;
                case 8:
                    System.out.println("Thank you for using Ledger System.");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please, try again.");
            }
        }
    }
    public static void showTransactionAnalysis(Scanner input, String email) {
        while (true) {
            System.out.println("\n=== Transaction Analysis ===");
            System.out.println("1. View transactions by type (debit/credit)");
            System.out.println("2. View transactions by date range");
            System.out.println("3. View transactions sorted by amount (highest first)");
            System.out.println("4. View transactions sorted by date (newest first)");
            System.out.println("5. Return to main menu");

            System.out.print("Choose an option: ");
            int choice = input.nextInt();
            input.nextLine();

            FilterAndSortTransactions ledger = new FilterAndSortTransactions();
            for (Transaction t : LedgerCentral.transactionsMap.get(email)) {
                ledger.addTransaction(email, t);
            }

            List<Transaction> results;

            switch (choice) {
                case 1:
                    System.out.print("Enter type (debit/credit): ");
                    String type = input.nextLine().toLowerCase();
                    results = ledger.filterByType(email, type);
                    displayTransactions(results, "Transactions of type: " + type);
                    break;

                case 2:
                    try {
                        System.out.print("Enter start date (YYYY-MM-DD): ");
                        LocalDate startDate = LocalDate.parse(input.nextLine());
                        System.out.print("Enter end date (YYYY-MM-DD): ");
                        LocalDate endDate = LocalDate.parse(input.nextLine());
                        results = ledger.filterByDateRange(email, startDate, endDate);
                        displayTransactions(results, "Transactions between " + startDate + " and " + endDate);
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
                    }
                    break;

                case 3:
                    results = ledger.sortByAmountDescending(email);
                    displayTransactions(results, "Transactions sorted by amount (highest first)");
                    break;

                case 4:
                    results = ledger.sortByDateDescending(email);
                    displayTransactions(results, "Transactions sorted by date (newest first)");
                    break;

                case 5:
                    return;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void displayTransactions(List<Transaction> transactions, String header) {
        System.out.println("\n=== " + header + " ===");
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.printf("%-12s %-12s %-30s %-10s%n", "Date", "Type", "Description", "Amount");
        System.out.println("-".repeat(65));

        for (Transaction t : transactions) {
            System.out.printf("%-12s %-12s %-30s $%-10.2f%n",
                    t.getDate(),
                    t.getType(),
                    t.getDescription(),
                    t.getAmount());
        }
    }

    public static void debit(Scanner input, String email) { //Need to change ALOT to crspnd to LedgerCentral
        if (loanOverdueCheck(email)){
            return;
        }

        if (LedgerCentral.savingsPercentageMap.get(email) != 0.0){
            boolean savingsActivated = true;
        }

        System.out.println("== Debit ==");

        // Check if this is user's first time using savings
        if (LedgerCentral.savingsPercentageMap.get(email) == 0.0) {
            System.out.print("Would you like to activate automatic savings? (10% of deposits) [Y/N]: ");
            String response = input.nextLine().trim().toUpperCase();

            if (response.equals("Y")) {
                System.out.println("Savings will be returned to balance automatically after the first debit after the current month ends.");
                LedgerCentral.savingsPercentageMap.put(email, 0.10); // 10% savings rate
                boolean savingsActivated = true;
            } else {
                LedgerCentral.savingsPercentageMap.put(email, 0.0);  // No savings
                boolean savingsActivated = false;
            }
            LedgerCentral.clearSavingsPercentages();
            LedgerCentral.writeSavingsPercentages();
        }

        System.out.print("Enter amount: ");
        double amount = input.nextDouble();
        input.nextLine();
        System.out.print("Enter description: ");
        String description = input.nextLine();

        if (amount <= 0) {
            System.out.println("Invalid amount. Amount must be positive.");
            return;
        }
        if (amount > TRANSACTION_LIMIT) {
            System.out.println("Invalid amount. Amount must not exceed $" + TRANSACTION_LIMIT);
            return;
        }

        if (description.length() > DESCRIPTION_LIMIT) {
            System.out.println("Description too long. Maximum " + DESCRIPTION_LIMIT + " characters allowed.");
            return;
        }

        double savedAmount = (amount * LedgerCentral.savingsPercentageMap.get(email)); //Continuation from activateSavings() - negligible if activateSavings, (option 4), was not chosen prior to coming here
        //Akin to balance = (amount - savedAmount) + balance;
        double currentBalance = LedgerCentral.accountBalanceMap.get(email);
        currentBalance = currentBalance + (amount - savedAmount);
        LedgerCentral.accountBalanceMap.put(email, currentBalance);
        LedgerCentral.clearAccountBalances();
        LedgerCentral.writeAccountBalances();

        //
        //Akin to savings += savedAmount;
        double currentSavings = LedgerCentral.savingsMap.getOrDefault(email, 0.0);
        currentSavings = currentSavings + savedAmount;
        LedgerCentral.savingsMap.put(email, currentSavings);
        LedgerCentral.clearSavings();
        LedgerCentral.writeSavings();

        //
        Transaction txn = new Transaction("debit", amount, description, LocalDate.now());
//        transactions.add(txn); //Tanvir's obsolete hashmap for debit
        LedgerCentral.transactionsMap.get(email).add(txn);
        LedgerCentral.clearTransactions();
        LedgerCentral.writeTransactions();

        transferSavings(email);
        System.out.println("Debit Successfully Recorded.\n");

        if (savedAmount > 0) {
            System.out.printf("Amount saved: $%.2f\n", savedAmount);
        }
    }

    public static void credit(Scanner input, String email) { //need to change alot to crspnd to LedgerCentral
        if (loanOverdueCheck(email)){
            return;
        }
        System.out.println("== Credit ==");
        System.out.print("Enter amount: ");
        double amount = input.nextDouble();
        input.nextLine();
        System.out.print("Enter description: ");
        String description = input.nextLine();

        if (amount <= 0 || amount > TRANSACTION_LIMIT) {
            System.out.println("Invalid amount. Must be positive and not exceed $" + TRANSACTION_LIMIT);
            return;
        }
        if (amount > LedgerCentral.accountBalanceMap.get(email)) {
            System.out.println("Insufficient balance! You cannot credit more than the available balance.");
            return;
        }
        if (description.length() > DESCRIPTION_LIMIT) {
            System.out.println("Description too long. Maximum " + DESCRIPTION_LIMIT + " characters allowed.");
            return;
        }

        //balance = balance - amount
        double currentBalance = LedgerCentral.accountBalanceMap.get(email);
        currentBalance = currentBalance - amount;
        LedgerCentral.accountBalanceMap.put(email, currentBalance);
        LedgerCentral.clearAccountBalances();
        LedgerCentral.writeAccountBalances();

        Transaction txn = new Transaction("credit", amount, description, LocalDate.now());
        LedgerCentral.transactionsMap.get(email).add(txn);
        LedgerCentral.clearTransactions();
        LedgerCentral.writeTransactions();
        System.out.println("Credit Successfully Recorded.\n");

    }

    public static boolean loanOverdueCheck (String email){
        if (LedgerCentral.loansMap.containsKey(email)) {
            LoanData loan = LedgerCentral.loansMap.get(email);
            if (loan.getOutstandingBalance() > 0
                    && LocalDate.now().isAfter(loan.getCreationDate().plusMonths(loan.getRepaymentPeriod()))) {
                System.out.println("Loan overdue! Please visit the nearest branch to resolve this issue.");
                System.out.println("Transactions are blocked until the issue is resolved.");
                return true;
            }
        }
        return false;
    }

    public static void transferSavings(String email) { //This method transfers savings FROM savings back TO balance
        if (LocalDate.now().getMonth() != LedgerCentral.lastTransferDateMap.get(email).getMonth()) {//cannot transfer savings TO acc.balance in the same month -- to promote financial discipline

            double currentBalance = LedgerCentral.accountBalanceMap.get(email);
            double currentSavings = LedgerCentral.savingsMap.get(email);
            currentBalance = currentBalance + currentSavings;
            LedgerCentral.accountBalanceMap.put(email, currentBalance);
            LedgerCentral.clearAccountBalances();
            LedgerCentral.writeAccountBalances();

            LedgerCentral.savingsMap.put(email,0.0);
            LedgerCentral.clearSavings();
            LedgerCentral.writeSavings();

            LedgerCentral.lastTransferDateMap.put(email,LocalDate.now());
            LedgerCentral.clearLastTransferDates();
            LedgerCentral.writeLastTransferDates();

            System.out.println("Monthly Savings Transferred to Balance.");
        }
    }

    public static void activateSavings(Scanner input, String email) {
        System.out.print("Are you sure you want to manage your savings? (Y/N): ");
        String response = input.nextLine().toUpperCase();

        if (response.equals("Y")) {
            System.out.println("Savings will be returned to balance automatically after the first debit after the current month ends.\n");
            System.out.print("Enter the percentage you wish to debut from the next debit (%) \nor enter 0 if you wish to deactivate savings: ");
            //Akin to savingsPercentage = input.nextDouble();
            double currentSavingsPercentage = input.nextDouble(); //Please rmb, that savingsPercentages are read in percentage, but later stored in hashmap as decimal point form (like 0.95)

            //Just validate percentage
            if (currentSavingsPercentage > 100 || currentSavingsPercentage < 0) {
                System.out.println("Invalid percentage. Please enter a value between 0 and 100.");
                return; //E
            }
            currentSavingsPercentage /= 100;
            LedgerCentral.savingsPercentageMap.put(email,currentSavingsPercentage);
            LedgerCentral.clearSavingsPercentages();
            LedgerCentral.writeSavingsPercentages();

            input.nextLine(); //Eat up next line
            if (currentSavingsPercentage == 0.0) {
                System.out.println("\nSavings Deactivated!\n");
            } else if (currentSavingsPercentage > 0.0) {
                System.out.println("\nSavings Activated!\n");
            }

        }
    }
    public static void displayHistory(String email) {
        if (LedgerCentral.transactionsMap.get(email) == null || LedgerCentral.transactionsMap.get(email).isEmpty()) {
            System.out.println("No transactions recorded yet for this account.");
            return;
        }

        // Display console output
        System.out.println("== Transaction History ==");
        System.out.printf("%-15s %-25s %-15s %-15s %-15s\n", "Date", "Description", "Debit", "Credit", "Balance");
        System.out.println("-----------------------------------------------------------------------------------------");

        ArrayList<Transaction> transactions = LedgerCentral.transactionsMap.get(email);
        double balanceProgression = 0.0;

        // Handle both console display and CSV export
        try {
            String fileName = "transaction_history_" + LedgerCentral.usernameStorage.get(email) + ".csv";
            FileWriter writer = new FileWriter(fileName);
            writer.write("Date,Description,Debit,Credit,Balance\n");

            for (Transaction t : transactions) {
                double amount = t.getAmount();
                String debit = t.getType().equals("debit") ? String.format("%.2f", amount) : "";
                String credit = t.getType().equals("credit") ? String.format("%.2f", amount) : "";
                balanceProgression = t.getType().equals("credit") ?
                        balanceProgression - amount :
                        balanceProgression + amount;

                System.out.printf("%-15s %-25s %-15s %-15s $ %.2f\n",
                        t.getDate(), t.getDescription(),
                        debit.isEmpty() ? "" : "$ " + debit,
                        credit.isEmpty() ? "" : "$ " + credit,
                        balanceProgression);

                writer.write(String.format("%s,%s,%s,%s,%.2f\n",
                        t.getDate(),
                        t.getDescription(),
                        debit,
                        credit,
                        balanceProgression));
            }

            writer.close();
            System.out.printf("\nAmount Saved: $ %.2f\nNew balance after deducting savings: $ %.2f\n",
                    LedgerCentral.savingsMap.get(email),
                    LedgerCentral.accountBalanceMap.get(email));
            System.out.println("File Exported: " + fileName);

        } catch (IOException e) {
            System.out.println("Error exporting transactions: " + e.getMessage());
        }
    }

//    public static void displayHistory(String email) {
//        if (LedgerCentral.transactionsMap.get(email) == null || LedgerCentral.transactionsMap.get(email).isEmpty()) {
//            System.out.println("No transactions recorded yet for this account.");
//            return;
//        }
//
//        System.out.println("== Transaction History ==");
//        System.out.printf("%-15s %-25s %-15s %-15s %-15s\n", "Date", "Description", "Debit", "Credit", "Balance");
//        System.out.println("-----------------------------------------------------------------------------------------");
//
//        ArrayList<Transaction> transactions = LedgerCentral.transactionsMap.get(email);
//        double balanceProgression = 0.0;
//
//        for (Transaction t : transactions) {
//            double amount = t.getAmount();
//            String debit = t.getType().equals("debit") ? String.format("$ %.2f", amount) : "";
//            String credit = t.getType().equals("credit") ? String.format("$ %.2f", amount) : "";
//            balanceProgression = t.getType().equals("credit") ?
//                    balanceProgression - amount :
//                    balanceProgression  + amount;
//
//            System.out.printf("%-15s %-25s %-15s %-15s $ %.2f\n",
//                    t.getDate(), t.getDescription(), debit, credit, balanceProgression);
//        }
//        System.out.printf("\nAmount Saved: $ %.2f\nNew balance after deducting savings: $ %.2f\n", LedgerCentral.savingsMap.get(email), LedgerCentral.accountBalanceMap.get(email));
//
//    }

    public static void creditLoan(Scanner input, String email) {
//        loanOverdueCheck(email);
        System.out.println("\n== Credit Loan ==");
        System.out.println("1. Apply for Loan");
        System.out.println("2. Repay Loan");
        System.out.print("> ");
        int choice = input.nextInt();
        input.nextLine();

        switch (choice) {
            case 1 -> applyLoan(input, email);
            case 2 -> repayLoan(input, email);
            default -> System.out.println("Invalid option. Please try again.");
        }
    }
    //checks loan
    public static void checkLoanReminder(String email) {
        if (LedgerCentral.loansMap.containsKey(email)) {
            LoanData loan = LedgerCentral.loansMap.get(email);
            if (loan.getOutstandingBalance() > 0) { // Only display reminder if there's an outstanding loan
                System.out.println("\n** Reminder: You have an outstanding loan! **");
                System.out.printf("Loan Amount: $%.2f\n", loan.getOutstandingBalance());
                System.out.printf("Repayment Due Date: %s\n", loan.getCreationDate().plusMonths(loan.getRepaymentPeriod()));
            }
        }
    }

    //Note: Creation dates are already in the LoanData object, where the LoanData object is stored in the CSVs
    public static void applyLoan(Scanner input, String email) {

        if(LedgerCentral.loansMap.get(email).getOutstandingBalance() > 0) {
            System.out.println("You have an ongoing loan. \nPlease repay it before applying for a new loan.");
            return;
        }

        System.out.print("Enter loan amount: ");
        double principal = input.nextDouble();
        input.nextLine();

        System.out.print("Enter interest rate (as %): ");
        double interestRate = input.nextDouble() / 100;
        input.nextLine();

        System.out.print("Enter repayment period (in months): ");
        int repaymentPeriod = input.nextInt();
        input.nextLine();

        double interest = principal * interestRate * repaymentPeriod;
        double totalRepayment = principal + interest;
        double monthlyInstallment = totalRepayment / repaymentPeriod;

        LoanData loan = new LoanData (principal, interestRate, repaymentPeriod, totalRepayment, "active", LocalDate.now());
        LedgerCentral.loansMap.put(email,loan);
        LedgerCentral.clearLoans();
        LedgerCentral.writeLoans();

        System.out.printf("Loan of $%.2f approved.\nTotal repayment: $%.2f over %d months.\nMonthly installment: $%.2f\n\n",
                principal, totalRepayment, repaymentPeriod, monthlyInstallment);
    }

    public static void repayLoan(Scanner input, String email) {
        LoanData currentLoan = LedgerCentral.loansMap.get(email);
        if (currentLoan == null || currentLoan.getOutstandingBalance() <= 0) {
            System.out.println("No active loan to repay.");
            return;
        }

        double interest = LedgerCentral.loansMap.get(email).getPrincipalAmount()
                * LedgerCentral.loansMap.get(email).getInterestRate()
                * LedgerCentral.loansMap.get(email).getRepaymentPeriod();
        double totalRepayment = LedgerCentral.loansMap.get(email).getPrincipalAmount()
                + interest;
        double monthlyInstallment = totalRepayment / LedgerCentral.loansMap.get(email).getRepaymentPeriod();

        System.out.printf("%nOutstanding balance: $%.2f%n", LedgerCentral.loansMap.get(email).getOutstandingBalance());
        System.out.printf("Monthly Instalment: $%.2f%n" , monthlyInstallment);
        System.out.print("Enter repayment amount: ");
        double repayment = input.nextDouble();
        input.nextLine();

        if (repayment > currentLoan.getOutstandingBalance()) {
            System.out.printf("Repayment amount has exceeded. \nFull repayment is only $%.2f. \nAdjusting repayment amount...\n", LedgerCentral.loansMap.get(email).getOutstandingBalance());
            repayment = currentLoan.getOutstandingBalance();
        }

        double newBalance = currentLoan.getOutstandingBalance() - repayment;
        if (newBalance <= 0) {
            System.out.println("Loan fully repaid.");

            LedgerCentral.loansMap.put(email, new LoanData());
            LedgerCentral.clearLoans();
            LedgerCentral.writeLoans();
            return;
        }
        LoanData tempUpdatedLoan = new LoanData (currentLoan.getPrincipalAmount(), currentLoan.getInterestRate(), currentLoan.getRepaymentPeriod(), newBalance, newBalance <= 0? "completed" : "active", currentLoan.getCreationDate());
        LedgerCentral.loansMap.put(email,tempUpdatedLoan);
        LedgerCentral.clearLoans();
        LedgerCentral.writeLoans();

        System.out.printf("Repayment of $%.2f successful.\nRemaining loan: $%.2f\n", repayment, newBalance);
    }


    public static void depositInterestPredictor(Scanner input, String email) {
        System.out.println("\n== Interest Calculator ==");

        // Prompt for bank and interest rate
        System.out.print("Select bank (RHB/MayBank/HongLeong/Alliance/AmBank/StandardChartered): ");
        String bank = input.nextLine().toLowerCase().trim();

        double interestRate;
        switch (bank) {
            case "rhb":
                interestRate = 2.6;
                break;
            case "maybank":
                interestRate = 2.5;
                break;
            case "hongleong":
                interestRate = 2.3;
                break;
            case "alliance":
                interestRate = 2.85;
                break;
            case "ambank":
                interestRate = 2.55;
                break;
            case "standardchartered":
                interestRate = 2.65;
                break;
            default:
                System.out.println("Unknown/Invalid bank selection. Using default rate of 2.5%.");
                interestRate = 2.5;
        }

        System.out.print("Enter Interest Calculation Period (day/month/annual): ");
        String period = input.nextLine().toLowerCase().trim();

        String grammarFixPeriod = period;
        switch (grammarFixPeriod) {
            case "day" -> grammarFixPeriod = "day(s)";
            case "month" -> grammarFixPeriod = "month(s)";
            case "annual" -> grammarFixPeriod = "year(s)";
            default -> grammarFixPeriod = "month(s)";
        }

        System.out.printf("Enter the number of %s to calculate interest for: ", grammarFixPeriod);
        int duration = input.nextInt();
        input.nextLine();

        double rate = interestRate / 100;
        double totalInterest;

        switch (period) {
            case "month" -> totalInterest = (LedgerCentral.accountBalanceMap.get(email) * rate * duration) / 12;
            case "annual" -> totalInterest = LedgerCentral.accountBalanceMap.get(email) * rate * duration;
            case "day" -> totalInterest = (LedgerCentral.accountBalanceMap.get(email) * rate * duration) / 365;
            default -> totalInterest = (LedgerCentral.accountBalanceMap.get(email) * rate * duration) / 12;
        }


        System.out.printf("Estimated Interest over %d %s: $%.2f\n", duration, grammarFixPeriod, totalInterest);
    }
}





