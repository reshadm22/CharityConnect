public class Charity {

    private String name;
    private String category;
    private String city;
    private String description;

    public Charity(String name, String category, String city, String description) {
        this.name = name;
        this.category = category;
        this.city = city;
        this.description = description;
    }

    // Getters only (encapsulation)
    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getCity() {
        return city;
    }

    public String getDescription() {
        return description;
    }

    // Used when writing to file
    public String toFileString() {
        return name + " | " + category + " | " + city + " | " + description;
    }
}
