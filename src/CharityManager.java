/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author resha
 */
import java.util.ArrayList;

public class CharityManager {

    private ArrayList<Charity> charities; // Storing the list of all charities in an ArrayList

    public CharityManager(ArrayList<Charity> charities) {
        this.charities = charities; 
        // Receives the list of charities from the main program and stores it for searching
    }

    // This method searches for charities based on category, city, or name using a linear search algorithm
    public ArrayList<Charity> search(String keyword, String searchBy, String category) {
        // List to store charities that match the search criteria
        ArrayList<Charity> results = new ArrayList<>();

        for (Charity c : charities) { // Loops through each charity one at a time (linear search)

            boolean matchesSearch = false;

            if (searchBy.equals("City")) { // Checks if the search is by city
                matchesSearch = c.getCity().equalsIgnoreCase(keyword); // matchesSearch becomes true if match is found
            }
            else if (searchBy.equals("Charity Name")) { // Checks if the search is by charity name
                matchesSearch = c.getName().toLowerCase().contains(keyword.toLowerCase()); // matchesSearch becomes true if match is found
            }

            boolean matchesCategory = // Checks if the selected category matches
                    category.equals("All") ||
                    c.getCategory().equalsIgnoreCase(category);

            if (matchesSearch && matchesCategory) { // If both search and category are true, add to results
                results.add(c); 
            }
        }
        return results; // Returns the list of matching charities with the search
    }

    // Recusrive Display Method --> Converts a list of Charity objects into a formatted string for output
    public void displayResults(ArrayList<Charity> charity, int index, StringBuilder sb) {
        
        // Base case: If the index reaches the size of the list, all charities have been processed, so stop recursion
        if (index == charity.size()) {
            return;
        }

        Charity c = charity.get(index); // Gets the charity at the current index

        // Appends the charity's information to the StringBuilder. All info is put on a new line.
        sb.append(c.getName()).append("\n")
          .append("Category: ").append(c.getCategory()).append("\n")
          .append("City: ").append(c.getCity()).append("\n")
          .append(c.getDescription()).append("\n")
          .append("---------------------------------\n"); // Seperates different charity results with line

        // Recursive call --> calls the same method again with the next index
        displayResults(charity, index + 1, sb); 
    }
}

