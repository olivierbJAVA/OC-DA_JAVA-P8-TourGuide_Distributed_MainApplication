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

@RestController
public class TourGuideController {

	@Autowired
    private TourGuideService tourGuideService;
	
    @GetMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @GetMapping("/getLocation")
    public Location getLocation(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
		return visitedLocation.getLocation();
    }
    
    @GetMapping("/getNearbyAttractions")
    public List<NearbyAttraction> getNearbyAttractions(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
    	return tourGuideService.getNearByAttractions(visitedLocation, getUser(userName));
    }
    
    @GetMapping("/getRewards")
    public List<UserReward> getRewards(@RequestParam String userName) {
    	return tourGuideService.getUserRewards(getUser(userName));
    }

    @GetMapping("/getAllCurrentLocations")
    public HashMap<String, Location> getAllCurrentLocations() {
        HashMap<String, Location> allCurrentLocations = tourGuideService.getAllCurrentLocations();
        return allCurrentLocations;
    }
    
    @GetMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
    	List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
    	return providers;
    }

    @GetMapping("/getPreferences")
    public UserPreferences getPreferences(@RequestParam String userName) {
        UserPreferences userPreferences = tourGuideService.getUserPreferences(getUser(userName));
        return userPreferences;
    }

    @PostMapping("/postPreferences")
    public UserPreferences postPreferences(@RequestParam String userName, @RequestBody UserPreferences userPreferences) {
        return tourGuideService.postUserPreferences(getUser(userName), userPreferences);
    }

    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }

}