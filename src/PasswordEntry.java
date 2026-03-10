public class PasswordEntry {
    private String website;
    private String username;
    private String password;

    public PasswordEntry(String w, String u, String p) {
        this.website = w;
        this.username = u;
        this.password = p;
    }

    public String getWebsite() {
        return website;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toString() {
        return "Website: " + website + ", Username: " + username + ", Password: " + password;
    }
}