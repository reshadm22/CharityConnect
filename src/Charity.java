public class Charity {

    // All instance variables for a charity object
    private String name;
    private String category;
    private String city;
    private String description;

    // Constructor --> This method is called when a new Charity object is created
    public Charity(String name, String category, String city, String description) {
        this.name = name;
        this.category = category;
        this.city = city;
        this.description = description;
    }
    
    // Getters methods (encapsulation)
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

    // To string method used when writing to file
    public String toFileString() {
        return name + " | " + category + " | " + city + " | " + description;
    }
}
