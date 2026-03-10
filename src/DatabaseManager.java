import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:data/vault.db";

    // ── FIX 1: createTables() now also seeds master password if missing ──
    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println("DB connection error: " + e.getMessage());
        }
        return conn;
    }

    public static void createTables() {
        String masterTable = "CREATE TABLE IF NOT EXISTS master_password ("
                + "id INTEGER PRIMARY KEY,"
                + "password_hash TEXT NOT NULL,"
                + "salt TEXT,"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ");";

        // ── FIX 2: iv column is now actually used (AES/CBC via EncryptionUtil) ──
        String passwordTable = "CREATE TABLE IF NOT EXISTS password_entries ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "website TEXT UNIQUE NOT NULL,"
                + "username TEXT,"
                + "password_encrypted TEXT NOT NULL,"
                + "iv TEXT NOT NULL,"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ");";

        try (Connection conn = connect();
                Statement stmt = conn.createStatement()) {
            stmt.execute(masterTable);
            stmt.execute(passwordTable);
            System.out.println("Tables ready.");
        } catch (SQLException e) {
            System.out.println("createTables error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // MASTER PASSWORD
    // ══════════════════════════════════════════════════════════════════

    /** Returns true if a master password row exists. */
    public static boolean masterPasswordExists() {
        String sql = "SELECT COUNT(*) FROM master_password";
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    /** Saves (or replaces) the master password hash + salt. */
    public static void saveMasterPassword(String hash, String salt) {
        String sql = "INSERT OR REPLACE INTO master_password (id, password_hash, salt, created_at) "
                + "VALUES (1, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hash);
            ps.setString(2, salt);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("saveMasterPassword error: " + e.getMessage());
        }
    }

    /** Returns the stored master password hash, or null if not set. */
    public static String getMasterPasswordHash() {
        String sql = "SELECT password_hash FROM master_password WHERE id = 1";
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next())
                return rs.getString("password_hash");
        } catch (SQLException e) {
            System.out.println("getMasterPasswordHash error: " + e.getMessage());
        }
        return null;
    }

    // ══════════════════════════════════════════════════════════════════
    // PASSWORD ENTRIES (replaces FileManager as the single source of truth)
    // ══════════════════════════════════════════════════════════════════

    /** Insert a new entry. Returns false if website already exists. */
    public static boolean insertEntry(String website, String username,
            String encryptedPassword, String iv) {
        String sql = "INSERT INTO password_entries (website, username, password_encrypted, iv) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, website);
            ps.setString(2, username);
            ps.setString(3, encryptedPassword);
            ps.setString(4, iv);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            // UNIQUE constraint on website fires here
            return false;
        }
    }

    /**
     * Update username + password for an existing website. Returns false if not
     * found.
     */
    public static boolean updateEntry(String website, String username,
            String encryptedPassword, String iv) {
        String sql = "UPDATE password_entries "
                + "SET username = ?, password_encrypted = ?, iv = ?, updated_at = CURRENT_TIMESTAMP "
                + "WHERE website = ?";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, encryptedPassword);
            ps.setString(3, iv);
            ps.setString(4, website);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("updateEntry error: " + e.getMessage());
            return false;
        }
    }

    /** Delete by website name (case-insensitive). */
    public static void deleteEntry(String website) {
        String sql = "DELETE FROM password_entries WHERE LOWER(website) = LOWER(?)";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, website);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("deleteEntry error: " + e.getMessage());
        }
    }

    /** Load all entries, decrypting passwords before returning them. */
    public static ArrayList<PasswordEntry> getAllEntries() {
        ArrayList<PasswordEntry> list = new ArrayList<>();
        String sql = "SELECT website, username, password_encrypted, iv FROM password_entries ORDER BY website";
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String plain = EncryptionUtil.decrypt(rs.getString("password_encrypted"),
                        rs.getString("iv"));
                list.add(new PasswordEntry(
                        rs.getString("website"),
                        rs.getString("username"),
                        plain));
            }
        } catch (SQLException e) {
            System.out.println("getAllEntries error: " + e.getMessage());
        }
        return list;
    }

    /** Fetch a single entry by website (case-insensitive), or null if not found. */
    public static PasswordEntry getEntry(String website) {
        String sql = "SELECT website, username, password_encrypted, iv "
                + "FROM password_entries WHERE LOWER(website) = LOWER(?)";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, website);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String plain = EncryptionUtil.decrypt(rs.getString("password_encrypted"),
                        rs.getString("iv"));
                return new PasswordEntry(rs.getString("website"),
                        rs.getString("username"), plain);
            }
        } catch (SQLException e) {
            System.out.println("getEntry error: " + e.getMessage());
        }
        return null;
    }
}