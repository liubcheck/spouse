package liubomyr.stepanenko.spouse.model;

public class Location {
    private final String name;
    private final String city;
    private final String address;
    private final String description;
    private final String imagePath;

    public Location(String name, String city,
                    String address, String description, String imagePath) {
        this.name = name;
        this.city = city;
        this.address = address;
        this.description = description;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public String toString() {
        return "Location{" + "name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
