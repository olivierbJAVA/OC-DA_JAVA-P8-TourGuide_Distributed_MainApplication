package tourGuide.controller;

import java.util.HashMap;
import java.util.List;

import tourGuide.domain.location.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsoniter.output.JsonStream;

import tourGuide.domain.location.VisitedLocation;
import tourGuide.domain.user.User;
import tourGuide.domain.user.UserPreferences;
import tourGuide.service.TourGuideService;
import tourGuide.domain.tripdeal.Provider;

@RestController
public class TourGuideController {

	@Autowired
    TourGuideService tourGuideService;
	
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public String getLocation(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
		return JsonStream.serialize(visitedLocation.location);
    }
    
    @RequestMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
    	return JsonStream.serialize(tourGuideService.getNearByAttractions(visitedLocation, getUser(userName)));
    }
    
    @RequestMapping("/getRewards") 
    public String getRewards(@RequestParam String userName) {
    	return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }

    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
        HashMap<String, Location> allCurrentLocations = tourGuideService.getAllCurrentLocations();
        return JsonStream.serialize(allCurrentLocations);
    }
    
    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
    	List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
    	return JsonStream.serialize(providers);
    }

    @RequestMapping("/getPreferences")
    public String getPreferences(@RequestParam String userName) {
        UserPreferences userPreferences = tourGuideService.getUserPreferences(getUser(userName));
        return JsonStream.serialize(userPreferences);
    }

    @RequestMapping("/postPreferences")
    public String postPreferences(@RequestParam String userName, @RequestBody UserPreferences userPreferences) {
        return JsonStream.serialize(tourGuideService.postUserPreferences(getUser(userName), userPreferences));
    }

    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }

}