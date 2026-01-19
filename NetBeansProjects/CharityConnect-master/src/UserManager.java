/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author resha
 */
import java.util.ArrayList;

public class UserManager {

    private ArrayList<User> users;
    private User currentUser;

    public UserManager(ArrayList<User> users) {
        this.users = users;
    }

    public boolean login(String email, String password) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)
                    && u.getPassword().equals(password)) {
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
}

