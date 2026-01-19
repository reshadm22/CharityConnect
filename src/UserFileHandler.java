/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author resha
 */
import java.io.*;
import java.util.ArrayList;

public class UserFileHandler {
    // Reads users from a file and stores them as User objects
    public static ArrayList<User> loadUsers(String filePath) {
        ArrayList<User> users = new ArrayList<>(); // ArrayList to store all user objects

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Reads each line of the file until the end
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s*\\|\\s*"); // Splits the line using " | " as the separator

                if (parts.length == 3) { // Ensures the line has all required data (Fullname, Email, Password)
                    // Creates a User object from the file data (all 3 parts) and adds it to the ArrayList
                    users.add(new User(parts[0], parts[1], parts[2]));   
                }
            }
        } catch (IOException e) { // Displays an error if the file cannot be read
            System.out.println("Error loading users file.");
        }
        // Returns the completed list of users
        return users;
    }
    // This method rewrites the file with updated user data.
    public static void saveUser(String filePath, User user) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(user.toFileString()); // Converts the User object into a file-friendly string with pipes
            bw.newLine(); // Moves to the next line in the file
        } catch (IOException e) { // Displays an error if writing fails
            System.out.println("Error saving user.");
        }
    }
}

