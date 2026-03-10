import java.util.ArrayList;

public class PasswordManager {
    private ArrayList<PasswordEntry> entries;

    public PasswordManager() {
        entries = FileManager.loadPasswords();
    }

    public boolean entryExists(String website) {
        return searchEntry(website) != null;
    }

    // Returns true if added, false if duplicate
    public boolean addEntry(PasswordEntry entry) {
        if (entryExists(entry.getWebsite())) {
            return false;
        }
        entries.add(entry);
        FileManager.savePasswords(entries);
        return true;
    }

    public void deleteEntry(String website) {
        entries.removeIf(entry -> entry.getWebsite().equalsIgnoreCase(website));
        FileManager.savePasswords(entries);
    }

    public PasswordEntry searchEntry(String website) {
        for (PasswordEntry entry : entries) {
            if (entry.getWebsite().equalsIgnoreCase(website)) {
                return entry;
            }
        }
        return null;
    }

    // Returns true if updated, false if not found
    public boolean updateEntry(String website, String newUsername, String newPassword) {
        PasswordEntry entry = searchEntry(website);
        if (entry == null) return false;
        entry.setUsername(newUsername);
        entry.setPassword(newPassword);
        FileManager.savePasswords(entries);
        return true;
    }

    public ArrayList<PasswordEntry> listEntries() {
        return entries;
    }
}