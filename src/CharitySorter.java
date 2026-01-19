/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author resha
 */
import java.util.ArrayList;

public class CharitySorter {

    // Bubble sort by charity name (A–Z).  Bubble Sort works by repeatedly comparing adjacent elements
    public static void sortByName(ArrayList<Charity> charities) {
        // Outer loop controls the number of passes
        for (int i = 0; i < charities.size() - 1; i++) {
            // Inner loop compares adjacent charities
            for (int j = 0; j < charities.size() - 1 - i; j++) {

                // Gets the current charity and the next charity
                Charity current = charities.get(j);
                Charity next = charities.get(j + 1);

                // Compares the charity names alphabetically
                if (current.getName().compareToIgnoreCase(next.getName()) > 0) {

                    // Swaps the two charities if they are out of order
                    charities.set(j, next);
                    charities.set(j + 1, current);
                }
            }
        }
    }
}

