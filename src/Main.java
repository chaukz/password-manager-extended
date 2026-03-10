import java.util.Scanner;
import java.util.ArrayList;

public class Main {

    // SHA-256 hash of "BoniSecretKey123" — never store plain text passwords!
    private static final String MASTER_PASSWORD_HASH = "X4YX8onsIjZhWR5+ExL/YXjMnn1XYF6mIMf8ZSE+S/o=";
    private static final int MAX_ATTEMPTS = 3;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        if (!authenticate(scanner)) {
            System.out.println("Too many failed attempts. Exiting.");
            scanner.close();
            return;
        }

        PasswordManager manager = new PasswordManager();
        boolean running = true;

        while (running) {
            System.out.println("\n=== Password Manager ===");
            System.out.println("1. Add Entry");
            System.out.println("2. Delete Entry");
            System.out.println("3. Search Entry");
            System.out.println("4. List Entries");
            System.out.println("5. Update Entry");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = -1;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number between 1 and 6.");
                continue;
            }

            switch (choice) {
                case 1:
                    String website = promptNonEmpty(scanner, "Enter website: ");
                    String username = promptNonEmpty(scanner, "Enter username: ");
                    String password = promptNonEmpty(scanner, "Enter password: ");
                    if (manager.addEntry(new PasswordEntry(website, username, password))) {
                        System.out.println("Entry added!");
                    } else {
                        System.out.println("Entry for '" + website + "' already exists. Use Update instead.");
                    }
                    break;
                case 2:
                    String delWebsite = promptNonEmpty(scanner, "Enter website to delete: ");
                    manager.deleteEntry(delWebsite);
                    System.out.println("Entry deleted (if it existed).");
                    break;
                case 3:
                    String searchWebsite = promptNonEmpty(scanner, "Enter website to search: ");
                    PasswordEntry entry = manager.searchEntry(searchWebsite);
                    if (entry != null) {
                        System.out.println("Found: " + entry);
                    } else {
                        System.out.println("Entry not found.");
                    }
                    break;
                case 4:
                    ArrayList<PasswordEntry> all = manager.listEntries();
                    if (all.isEmpty()) {
                        System.out.println("No entries saved yet.");
                    } else {
                        System.out.println("\n--- Saved Entries ---");
                        for (PasswordEntry e : all) {
                            System.out.println(e);
                        }
                    }
                    break;
                case 5:
                    String updateWebsite = promptNonEmpty(scanner, "Enter website to update: ");
                    if (manager.searchEntry(updateWebsite) == null) {
                        System.out.println("No entry found for '" + updateWebsite + "'.");
                        break;
                    }
                    String newUsername = promptNonEmpty(scanner, "Enter new username: ");
                    String newPassword = promptNonEmpty(scanner, "Enter new password: ");
                    if (manager.updateEntry(updateWebsite, newUsername, newPassword)) {
                        System.out.println("Entry updated!");
                    }
                    break;
                case 6:
                    running = false;
                    scanner.close();
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Enter a number from 1 to 6.");
            }
        }
    }

    private static boolean authenticate(Scanner scanner) {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            System.out.print("Enter master password: ");
            String input = scanner.nextLine();
            // Hash what user typed and compare — never compare plain text
            if (EncryptionUtil.hashPassword(input).equals(MASTER_PASSWORD_HASH)) {
                System.out.println("Access granted!\n");
                return true;
            }
            int remaining = MAX_ATTEMPTS - attempt;
            if (remaining > 0) {
                System.out.println("Incorrect password. " + remaining + " attempt(s) remaining.");
            }
        }
        return false;
    }

    private static String promptNonEmpty(Scanner scanner, String prompt) {
        String input = "";
        while (input.isEmpty()) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("This field cannot be empty.");
            }
        }
        return input;
    }
}