package tourGuide.domain.tripdeal;

import java.util.UUID;

public class Provider {
    public String name;
    public double price;
    public UUID tripId;

    public Provider(UUID tripId, String name, double price) {
        this.name = name;
        this.tripId = tripId;
        this.price = price;
    }

    public Provider() {
    }
}
