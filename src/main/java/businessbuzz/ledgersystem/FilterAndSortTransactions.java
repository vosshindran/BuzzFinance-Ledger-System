package businessbuzz.ledgersystem;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class FilterAndSortTransactions{
    private final Map<String, List<Transaction>> transactionsByEmail;

    public FilterAndSortTransactions() {
        this.transactionsByEmail = new HashMap<>();
    }

    public void addTransaction(String email, Transaction transaction) {
        transactionsByEmail.computeIfAbsent(email, k -> new ArrayList<>()).add(transaction);
    }

    public List<Transaction> filterByType(String email, String type) {
        return transactionsByEmail.getOrDefault(email, new ArrayList<>()).stream()
                .filter(transaction -> transaction.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    public List<Transaction> filterByDateRange(String email, LocalDate startDate, LocalDate endDate) {
        return transactionsByEmail.getOrDefault(email, new ArrayList<>()).stream()
                .filter(transaction -> (transaction.getDate().isEqual(startDate) ||
                        transaction.getDate().isAfter(startDate)) &&
                        (transaction.getDate().isEqual(endDate) ||
                                transaction.getDate().isBefore(endDate)))
                .collect(Collectors.toList());
    }

    public List<Transaction> sortByAmountAscending(String email) {
        return transactionsByEmail.getOrDefault(email, new ArrayList<>()).stream()
                .sorted(Comparator.comparingDouble(Transaction::getAmount))
                .collect(Collectors.toList());
    }

    public List<Transaction> sortByAmountDescending(String email) {
        return transactionsByEmail.getOrDefault(email, new ArrayList<>()).stream()
                .sorted(Comparator.comparingDouble(Transaction::getAmount).reversed())
                .collect(Collectors.toList());
    }

    public List<Transaction> sortByDateAscending(String email) {
        return transactionsByEmail.getOrDefault(email, new ArrayList<>()).stream()
                .sorted(Comparator.comparing(Transaction::getDate))
                .collect(Collectors.toList());
    }

    public List<Transaction> sortByDateDescending(String email) {
        return transactionsByEmail.getOrDefault(email, new ArrayList<>()).stream()
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactions(String email) {
        return new ArrayList<>(transactionsByEmail.getOrDefault(email, new ArrayList<>()));
    }
}