/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author resha
 */
public class User {

    private String fullName;
    private String email;
    private String password;

    public User(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    public String getFirstName() {
        return fullName.split(" ")[0];
    }
    
    public String getLastName() {
        return fullName.split(" ")[1];
    }

    public String getEmail() {
        return email;
    }
    
    public String getPassword() {
        return password;
    }

    public boolean checkPassword(String input) {
        return password.equals(input);
    }
    
    public String toFileString() {
        return fullName + " | " + email + " | " + password;
    }
}