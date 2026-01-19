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

    private ArrayList<User> users; // Stores all registered users loaded from the file
    private User currentUser; // If no one is logged in, this value is null

    // Constructor receives the list of users from the file handler
    public UserManager(ArrayList<User> users) {
        this.users = users;
        this.currentUser = null; // no user logged in at start
    }
    
    public boolean login(String email, String password) {
        for (User user : users) { // Loops through each registered user
            if (user.getEmail().equalsIgnoreCase(email) // Checks if both email and password match
                    && user.getPassword().equals(password)) {
                currentUser = user; // Stores the logged-in user
                return true;
            }
        }
        return false; // Login failed if no match was found, returns false
    }
    // Method for checking for duplicate email addresses
    public boolean emailExists(String email) {
        for (User user : users) { // Loops through all registered users
            if (user.getEmail().equalsIgnoreCase(email)) { // If emails match, returns true
                return true;
            }
        }
        return false; // Returns false if the email is not a duplicate
    }

    public void register(User user) { //  Registers a new user and adds them to the system.
        users.add(user);
        currentUser = user;
    }

    public void logout() { // Logs the current user out
        currentUser = null;
    }

    public boolean isLoggedIn() { // Checks whether a user is currently logged in.
        return currentUser != null;
    }

    public User getCurrentUser() { // Returns the currently logged-in user.
        return currentUser;
    }
}

