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

    public static ArrayList<User> loadUsers(String filePath) {
        ArrayList<User> users = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s*\\|\\s*");

                if (parts.length == 3) {
                    users.add(new User(parts[0], parts[1], parts[2]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading users file.");
        }

        return users;
    }

    public static void saveUser(String filePath, User user) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(user.toFileString());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error saving user.");
        }
    }
}

