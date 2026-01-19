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

public class CharityFileHandler {

    public static ArrayList<Charity> loadCharities(String filePath) {
        ArrayList<Charity> charities = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s*\\|\\s*");

                if (parts.length == 4) {
                    charities.add(new Charity(
                            parts[0],
                            parts[1],
                            parts[2],
                            parts[3]
                    ));
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading charity file.");
        }

        return charities;
    }

    public static void saveCharities(String filePath, ArrayList<Charity> charities) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {

            for (Charity c : charities) {
                bw.write(c.toFileString());
                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error saving charity file.");
        }
    }
}

