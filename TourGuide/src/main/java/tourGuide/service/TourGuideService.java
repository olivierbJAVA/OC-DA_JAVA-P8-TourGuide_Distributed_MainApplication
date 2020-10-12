package tourGuide.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import tourGuide.configuration.TourGuideInitialization;
import tourGuide.domain.location.NearbyAttraction;
import tourGuide.domain.location.Attraction;
import tourGuide.domain.location.Location;
import tourGuide.domain.location.VisitedLocation;
import tourGuide.tracker.Tracker;
import tourGuide.domain.user.User;
import tourGuide.domain.user.UserPreferences;
import tourGuide.domain.user.UserReward;
import tourGuide.domain.tripdeal.Provider;

@Service
@PropertySource("classpath:application.properties")
public class TourGuideService {
	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);

	TourGuideInitialization init = new TourGuideInitialization();

	private final RewardsService rewardsService;
	public final Tracker tracker;
	private boolean testMode = true;

	private final String gpsServiceName;
	private final String gpsServicePort;
	private final String preferencesServiceName;
	private final String preferencesServicePort;

	public TourGuideService(RewardsService rewardsService, @Value("${service.gps.name}") String gpsServiceName, @Value("${service.gps.port}") String gpsServicePort, @Value("${service.preferences.name}") String preferencesServiceName, @Value("${service.preferences.port}") String preferencesServicePort) {
		this.rewardsService = rewardsService;
		this.gpsServiceName = gpsServiceName;
		this.gpsServicePort = gpsServicePort;
		this.preferencesServiceName = preferencesServiceName;
		this.preferencesServicePort = preferencesServicePort;

		if(testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			init.initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this, rewardsService);
		addShutDownHook();
	}

	public User getUser(String userName) {
		return init.getInternalUserMap().get(userName);
	}

	public List<User> getAllUsers() {
		return  init.getInternalUserMap().values().stream().collect(Collectors.toList());
	}

	public void addUser(User user) {
		if(!init.getInternalUserMap().containsKey(user.getUserName())) {
			init.getInternalUserMap().put(user.getUserName(), user);
		}
	}

	public VisitedLocation trackUserLocation(User user) {
		logger.debug("Track Location - Thread : " + Thread.currentThread().getName() + " - User : " + user.getUserName());

		VisitedLocation visitedLocation = new VisitedLocation();

		logger.debug("Request getUserLocation build");
		HttpClient client = HttpClient.newHttpClient();
		String requestURI = "http://"+gpsServiceName+":"+gpsServicePort+"/getUserLocation?userId=" + user.getUserId();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(requestURI))
				//.header("userId", user.getUserId().toString())
				.GET()
				.build();

		// Essai sendAsync
		/*
		try {
			CompletableFuture<HttpResponse <String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
			ObjectMapper mapper = new ObjectMapper();
			visitedLocation = mapper.readValue(response.get().body(), VisitedLocation.class);
		} catch (IOException | InterruptedException | ExecutionException e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		*/

		try {
			HttpResponse <String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			logger.debug("Status code = " + response.statusCode());
			logger.debug("Response Body = " + response.body());
			ObjectMapper mapper = new ObjectMapper();
			visitedLocation = mapper.readValue(response.body(), VisitedLocation.class);
		} catch (IOException | InterruptedException e) {
			logger.error(e.toString());
			e.printStackTrace();
		}

		user.addToVisitedLocations(visitedLocation);

		return visitedLocation;
	}

	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	public VisitedLocation getUserLocation(User user) {
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
			user.getLastVisitedLocation() :
			trackUserLocation(user);
		return visitedLocation;
	}

	public HashMap<String, Location> getAllCurrentLocations() {
		HashMap<String, Location> allCurrentLocations = new HashMap<>();
		List<User> allUsers = getAllUsers();
		allUsers.forEach(user -> allCurrentLocations.put(user.getUserId().toString(), user.getLastVisitedLocation().getLocation()));
		return allCurrentLocations;
	}

	public List<Provider> getTripDeals(User user) {
		int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();

		List<Provider> providers = new ArrayList<>();

		logger.debug("Request getTripDeals build");
		HttpClient client = HttpClient.newHttpClient();
		String requestURI = "http://"+preferencesServiceName+":"+preferencesServicePort+"/getPrice?apiKey=" + TourGuideInitialization.getTripPricerApiKey() + "&attractionId=" + user.getUserId() + "&adults=" + user.getUserPreferences().getNumberOfAdults() + "&children=" + user.getUserPreferences().getNumberOfChildren() + "&nightsStay=" + user.getUserPreferences().getTripDuration() + "&rewardsPoints=" + cumulativeRewardPoints;

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(requestURI))
				.GET()
				.build();
		try {
			HttpResponse <String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			logger.debug("Status code = " + response.statusCode());
			logger.debug("Response Body = " + response.body());
			ObjectMapper mapper = new ObjectMapper();
			providers = mapper.readValue(response.body(), new TypeReference<List<Provider>>(){ });
		} catch (IOException | InterruptedException e) {
			logger.error(e.toString());
			e.printStackTrace();
		}

		user.setTripDeals(providers);
		return providers;
	}

	public List<NearbyAttraction> getNearByAttractions(VisitedLocation visitedLocation, User user) {
		List<NearbyAttraction> nearbyAttractions = new ArrayList<>();
		List<Attraction> allAttractions = rewardsService.getAllAttractions();

		TreeMap<Double, NearbyAttraction> treeAttractionDistance = new TreeMap<>();
		allAttractions.forEach(attraction -> treeAttractionDistance.put(rewardsService.getDistance(attraction, visitedLocation.getLocation()), new NearbyAttraction(attraction.getAttractionName(), new Location(attraction.getLatitude(), attraction.getLongitude()), visitedLocation.getLocation(), rewardsService.getDistance(attraction, visitedLocation.getLocation()), rewardsService.getRewardPoints(attraction, user))));
		nearbyAttractions = treeAttractionDistance.values().stream()
															.limit(5)
															.collect(Collectors.toList());

		return nearbyAttractions;
	}

	public UserPreferences getUserPreferences(User user) {
		UserPreferences userPreferences = user.getUserPreferences();
		return userPreferences;
	}

	public UserPreferences postUserPreferences(User user, UserPreferences userPreferences) {
		user.setUserPreferences(userPreferences);
		return userPreferences;
	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(tracker::stopTracking));
	}
}
