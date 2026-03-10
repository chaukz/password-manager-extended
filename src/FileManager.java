import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {

    public static void savePasswords(ArrayList<PasswordEntry> entries) {
        String filePath = "data/passwords.dat";
        try (FileWriter writer = new FileWriter(filePath)) {
            for (PasswordEntry entry : entries) {
                String encryptedPassword = EncryptionUtil.encrypt(entry.getPassword());
                writer.write(entry.getWebsite() + "," +
                        entry.getUsername() + "," +
                        encryptedPassword + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<PasswordEntry> loadPasswords() {
        ArrayList<PasswordEntry> entries = new ArrayList<>();
        String filePath = "data/passwords.dat";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String decryptedPassword = EncryptionUtil.decrypt(parts[2]);
                    entries.add(new PasswordEntry(parts[0], parts[1], decryptedPassword));
                }
            }
        } catch (IOException e) {
            System.out.println("No saved passwords found. Starting with an empty list.");
        }
        return entries;
    }
}