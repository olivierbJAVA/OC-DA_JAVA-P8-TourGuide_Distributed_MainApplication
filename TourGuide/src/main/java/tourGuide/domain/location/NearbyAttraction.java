package tourGuide.domain.location;

public class NearbyAttraction {

    private String attractionName;
    private Location attractionLocation;
    private Location userLocation;
    private double distanceAttractionUserLocation;
    private int rewardsPointForAttraction;

    public NearbyAttraction(String attractionName, Location attractionLocation, Location userLocation, double distanceAttractionUserLocation, int rewardsPointForAttraction) {
        this.attractionName = attractionName;
        this.attractionLocation = attractionLocation;
        this.userLocation = userLocation;
        this.distanceAttractionUserLocation = distanceAttractionUserLocation;
        this.rewardsPointForAttraction = rewardsPointForAttraction;
    }

    public NearbyAttraction() {
    }

    public String getAttractionName() {
        return attractionName;
    }

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    public Location getAttractionLocation() {
        return attractionLocation;
    }

    public void setAttractionLocation(Location attractionLocation) {
        this.attractionLocation = attractionLocation;
    }

    public Location getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
    }

    public double getDistanceAttractionUserLocation() {
        return distanceAttractionUserLocation;
    }

    public void setDistanceAttractionUserLocation(double distanceAttractionUserLocation) {
        this.distanceAttractionUserLocation = distanceAttractionUserLocation;
    }

    public int getRewardsPointForAttraction() {
        return rewardsPointForAttraction;
    }

    public void setRewardsPointForAttraction(int rewardsPointForAttraction) {
        this.rewardsPointForAttraction = rewardsPointForAttraction;
    }
}
