package tourGuide.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.web.bind.annotation.*;
import tourGuide.domain.location.Location;
import org.springframework.beans.factory.annotation.Autowired;

import tourGuide.domain.location.NearbyAttraction;
import tourGuide.domain.location.VisitedLocation;
import tourGuide.domain.user.User;
import tourGuide.domain.user.UserPreferences;
import tourGuide.domain.user.UserReward;
import tourGuide.service.TourGuideService;
import tourGuide.domain.tripdeal.Provider;

/**
 * Controller in charge of managing the endpoints for the TourGuide application.
 */
@RestController
public class TourGuideController {

	@Autowired
    private TourGuideService tourGuideService;

    /**
     * Method managing the GET "/" endpoint HTTP request.
     *
     * @return A welcome message
     */
    @GetMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    /**
     * Method managing the GET "/getLocation" endpoint HTTP request to get the location of a user.
     *
     * @param userName The name of the user
     * @return The location of the user
     */
    @GetMapping("/getLocation")
    public Location getLocation(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
		return visitedLocation.getLocation();
    }

    /**
     * Method managing the GET "/getNearbyAttractions" endpoint HTTP request to get the 5 nearest attractions of a user.
     *
     * @param userName The name of the user
     * @return The 5 nearest attraction of the user
     */
    @GetMapping("/getNearbyAttractions")
    public List<NearbyAttraction> getNearbyAttractions(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
    	return tourGuideService.getNearByAttractions(visitedLocation, getUser(userName));
    }

    /**
     * Method managing the GET "/getRewards" endpoint HTTP request to get the list of rewards of a user.
     *
     * @param userName The name of the user
     * @return The list of rewards of the user
     */
    @GetMapping("/getRewards")
    public List<UserReward> getRewards(@RequestParam String userName) {
    	return tourGuideService.getUserRewards(getUser(userName));
    }

    /**
     * Method managing the GET "/getAllCurrentLocations" endpoint HTTP request to get the current location of all users.
     *
     * @return A HashMap containing for all users : the user id in a String format (Key) and its current location in a Location object (Value)
     */
    @GetMapping("/getAllCurrentLocations")
    public HashMap<String, Location> getAllCurrentLocations() {
        HashMap<String, Location> allCurrentLocations = tourGuideService.getAllCurrentLocations();
        return allCurrentLocations;
    }

    /**
     * Method managing the GET "/getTripDeals" endpoint HTTP request to get a list of travels proposed to the user depending on its preferences and rewards points.
     *
     * @param userName The name of the user
     * @return The list of proposed travels to the user
     */
    @GetMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
    	List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
    	return providers;
    }

    /**
     * Method managing the GET "/getPreferences" endpoint HTTP request to get the travel preferences of a user.
     *
     * @param userName The name of the user
     * @return The travel preferences of the user
     */
    @GetMapping("/getPreferences")
    public UserPreferences getPreferences(@RequestParam String userName) {
        UserPreferences userPreferences = tourGuideService.getUserPreferences(getUser(userName));
        return userPreferences;
    }

    /**
     * Method managing the POST "/postPreferences" endpoint HTTP request to post the travel preferences of a user.
     *
     * @param userName The name of the user
     * @return The posted travel preferences of the user
     */
    @PostMapping("/postPreferences")
    public UserPreferences postPreferences(@RequestParam String userName, @RequestBody UserPreferences userPreferences) {
        return tourGuideService.postUserPreferences(getUser(userName), userPreferences);
    }

    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }

}