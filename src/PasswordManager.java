import java.util.ArrayList;

public class PasswordManager {

    // Cache entries in memory — DB is the source of truth on load,
    // but in-memory list is what the UI reads to avoid stale re-queries.
    private ArrayList<PasswordEntry> cache;

    public PasswordManager() {
        cache = DatabaseManager.getAllEntries();
    }

    public boolean entryExists(String website) {
        return searchEntry(website) != null;
    }

    public boolean addEntry(PasswordEntry entry) {
        String[] encrypted = EncryptionUtil.encrypt(entry.getPassword());
        boolean inserted = DatabaseManager.insertEntry(
                entry.getWebsite(), entry.getUsername(),
                encrypted[0], encrypted[1]);
        if (inserted)
            cache.add(entry);
        return inserted;
    }

    public void deleteEntry(String website) {
        DatabaseManager.deleteEntry(website);
        cache.removeIf(e -> e.getWebsite().equalsIgnoreCase(website));
    }

    public PasswordEntry searchEntry(String website) {
        for (PasswordEntry e : cache)
            if (e.getWebsite().equalsIgnoreCase(website))
                return e;
        return null;
    }

    public boolean updateEntry(String website, String newUsername, String newPassword) {
        String[] encrypted = EncryptionUtil.encrypt(newPassword);
        boolean updated = DatabaseManager.updateEntry(website, newUsername, encrypted[0], encrypted[1]);
        if (updated) {
            PasswordEntry e = searchEntry(website);
            if (e != null) {
                e.setUsername(newUsername);
                e.setPassword(newPassword);
            }
        }
        return updated;
    }

    public ArrayList<PasswordEntry> listEntries() {
        return cache;
    }
}