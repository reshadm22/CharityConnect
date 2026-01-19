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

    private ArrayList<Charity> charities;

    public CharityManager(ArrayList<Charity> charities) {
        this.charities = charities;
    }

    // LINEAR SEARCH
    public ArrayList<Charity> search(String keyword, String searchBy, String category) {
        ArrayList<Charity> results = new ArrayList<>();

        for (Charity c : charities) {

            boolean matchesSearch = false;

            if (searchBy.equals("City")) {
                matchesSearch = c.getCity().equalsIgnoreCase(keyword);
            }
            else if (searchBy.equals("Charity Name")) {
                matchesSearch = c.getName().toLowerCase().contains(keyword.toLowerCase());
            }

            boolean matchesCategory =
                    category.equals("All") ||
                    c.getCategory().equalsIgnoreCase(category);

            if (matchesSearch && matchesCategory) {
                results.add(c);
            }
        }
        return results;
    }

    // RECURSIVE DISPLAY METHOD
    public void displayResults(ArrayList<Charity> list, int index, StringBuilder sb) {
        if (index == list.size()) {
            return;
        }

        Charity c = list.get(index);

        sb.append(c.getName()).append("\n")
          .append("Category: ").append(c.getCategory()).append("\n")
          .append("City: ").append(c.getCity()).append("\n")
          .append(c.getDescription()).append("\n")
          .append("---------------------------------\n");

        displayResults(list, index + 1, sb);
    }
}

