import java.io.File;

public class Main {
    public static void main(String[] args) {
        // ── FIX 6: DB is initialized BEFORE the GUI starts.
        // Old code launched the GUI first — race condition meant
        // tables might not exist when the login screen tried to
        // read the master password hash.

        // Ensure data/ directory exists
        new File("data").mkdirs();

        // Set up tables first
        DatabaseManager.createTables();

        // Then launch GUI
        GUI.launch();
    }
}