public class User {

    private String fullName;
    private String email;
    private String password;
    private boolean isAdmin;

    public User(String fullName, String email, String password) {
        this(fullName, email, password, false);
    }

    public User(String fullName, String email, String password, boolean isAdmin) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public String getFullName() {
        return fullName;
    }

    public String getFirstName() {
        String[] parts = fullName.trim().split("\\s+");
        return parts.length >= 1 ? parts[0] : "";
    }

    public String getLastName() {
        String[] parts = fullName.trim().split("\\s+");
        return parts.length >= 2 ? parts[parts.length - 1] : "";
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean checkPassword(String input) {
        return password.equals(input);
    }

    public String toFileString() {
        return fullName + " | " + email + " | " + password + " | " + (isAdmin ? "ADMIN" : "USER");
    }
}
