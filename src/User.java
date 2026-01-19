/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author resha
 */
public class User {

    // All instance variables for a User object
    private String fullName;
    private String email;
    private String password;

    // Constructor method that initializes all user information when a user is created
    public User(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    // All getter methods (Encapsulation)
    public String getFirstName() {
        return fullName.split(" ")[0]; // Splits the full name by spaces and returns the first word
    }
    public String getLastName() {
        return fullName.split(" ")[1]; // Splits the full name by spaces and returns the second word
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }

    // This method is used by BufferedWriter when saving users to a file
    public String toFileString() {
        return fullName + " | " + email + " | " + password;
    }
}