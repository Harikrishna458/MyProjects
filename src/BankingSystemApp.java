import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Scanner;

public class BankingSystemApp {
    private static final String URL = "jdbc:postgresql://localhost:5432/bank";
    private static final String USER = "postgres";  // Replace with your PostgreSQL username
    private static final String PASSWORD = "pass";  // Replace with your PostgreSQL password

    // Method to establish database connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Method to create a new account
    public void createAccount(String customerName, long accountNo, String currentLocation, double initialBalance) {
        String sql = "INSERT INTO customer_accounts (customer_name, account_no, current_location, balance) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerName);
            pstmt.setLong(2, accountNo);
            pstmt.setString(3, currentLocation);
            pstmt.setDouble(4, initialBalance);
            pstmt.executeUpdate();
            System.out.println("Account created successfully for " + customerName);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update the balance (deposit or withdraw)
    public void updateBalance(int accountId, double amount, boolean isDeposit) {
        String sql = "UPDATE customer_accounts SET balance = balance + ?, last_withdraw = ? WHERE account_id = ?";

        if (!isDeposit) {
            sql = "UPDATE customer_accounts SET balance = balance - ?, last_withdraw = ? WHERE account_id = ?";
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, amount);
            pstmt.setTimestamp(2, new Timestamp(new Date().getTime()));
            pstmt.setInt(3, accountId);
            pstmt.executeUpdate();
            System.out.println("Balance updated successfully for account ID " + accountId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to generate the account statement
    public void generateAccountStatement(int accountId) {
        String sql = "SELECT account_id, customer_name, account_no, current_location, last_withdraw, balance FROM customer_accounts WHERE account_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Account ID: " + rs.getInt("account_id"));
                System.out.println("Customer Name: " + rs.getString("customer_name"));
                System.out.println("Account No: " + rs.getLong("account_no"));
                System.out.println("Current Location: " + rs.getString("current_location"));
                System.out.println("Last Withdraw: " + rs.getTimestamp("last_withdraw"));
                System.out.println("Balance: " + rs.getDouble("balance"));
            } else {
                System.out.println("Account not found for ID " + accountId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        BankingSystemApp bankingSystem = new BankingSystemApp();
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\nBanking System Menu:");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Generate Account Statement");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    // Create Account
                    System.out.print("Enter customer name: ");
                    scanner.nextLine();  // Consume newline
                    String customerName = scanner.nextLine();
                    System.out.print("Enter account number: ");
                    long accountNo = scanner.nextLong();
                    System.out.print("Enter current location: ");
                    scanner.nextLine();  // Consume newline
                    String currentLocation = scanner.nextLine();
                    System.out.print("Enter initial balance: ");
                    double initialBalance = scanner.nextDouble();
                    bankingSystem.createAccount(customerName, accountNo, currentLocation, initialBalance);
                    break;

                case 2:
                    // Deposit
                    System.out.print("Enter account ID: ");
                    int depositAccountId = scanner.nextInt();
                    System.out.print("Enter deposit amount: ");
                    double depositAmount = scanner.nextDouble();
                    bankingSystem.updateBalance(depositAccountId, depositAmount, true);
                    break;

                case 3:
                    // Withdraw
                    System.out.print("Enter account ID: ");
                    int withdrawAccountId = scanner.nextInt();
                    System.out.print("Enter withdrawal amount: ");
                    double withdrawAmount = scanner.nextDouble();
                    bankingSystem.updateBalance(withdrawAccountId, withdrawAmount, false);
                    break;

                case 4:
                    // Generate Account Statement
                    System.out.print("Enter account ID: ");
                    int statementAccountId = scanner.nextInt();
                    bankingSystem.generateAccountStatement(statementAccountId);
                    break;

                case 5:
                    // Exit
                    System.out.println("Exiting the Banking System. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (choice != 5);

        scanner.close();
    }
}
