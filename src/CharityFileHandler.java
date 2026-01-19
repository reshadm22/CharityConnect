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

// This class reads all charities from a text file and stores them as objects inside an ArrayList
public class CharityFileHandler {
    
    public static ArrayList<Charity> loadCharities(String filePath) {
        // ArrayList to store all charity objects
        ArrayList<Charity> charities = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            
            // Reads the file line by line until the end of the file is reached
            while ((line = br.readLine()) != null) {
                // Splits each line using " | " as the delimiter and stores it in the parts array
                String[] parts = line.split("\\s*\\|\\s*"); 
                
                if (parts.length == 4) {  // Ensures the line has all required fields
                    charities.add(new Charity( // Adds the charity object to the ArrayList
                            parts[0],
                            parts[1],
                            parts[2],
                            parts[3]
                    ));
                }
            }

        } catch (IOException e) { // Displays an error if the file cannot be read
            System.out.println("Error loading charity file.");
        }

        return charities; // Returns the completed list of charities
    }
    // Writes all charity objects back to the file
    public static void saveCharities(String filePath, ArrayList<Charity> charities) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            // Loops through each charity in the list
            for (Charity c : charities) {
                bw.write(c.toFileString()); // Converts the charity object into a file string with pipes in between
                bw.newLine(); // Moves to the next line in the file
            }

        } catch (IOException e) { // Displays an error if writing fails
            System.out.println("Error saving charity file.");
        }
    }
}

