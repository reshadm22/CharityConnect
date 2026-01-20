import java.util.ArrayList;

public class UserManager {

    private ArrayList<User> users;
    private User currentUser;

    public UserManager(ArrayList<User> users) {
        this.users = users;
    }

    public boolean login(String email, String password) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password)) {
                currentUser = u;
                return true;
            }
        }
        return false;
    }

    public boolean emailExists(String email) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    public void register(User user) {
        users.add(user);
        currentUser = user;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAdminLoggedIn() {
        return isLoggedIn() && currentUser != null && currentUser.isAdmin();
    }
}
