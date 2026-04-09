package growmate.model;

public class User {
    private int id;
    private String username;
    private String passwordHash;

    public User() {}
    public User(int id, String username, String passwordHash) {
        this.id = id; this.username = username; this.passwordHash = passwordHash;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
