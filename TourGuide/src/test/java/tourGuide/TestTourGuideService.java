package tourGuide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.*;

import org.junit.Test;

import tourGuide.domain.location.Attraction;
import tourGuide.domain.location.Location;
import tourGuide.domain.location.NearbyAttraction;
import tourGuide.domain.location.VisitedLocation;
import tourGuide.domain.tripdeal.Provider;
import tourGuide.domain.user.UserPreferences;
import tourGuide.helper.InternalTestHelper;

import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.domain.user.User;

public class TestTourGuideService {

	@Test
	public void getUserLocation() {
		//Added to fix NumberFormatException due to decimal number separator
		Locale.setDefault(new Locale("en", "US"));

		// ARRANGE
		InternalTestHelper.setInternalUserNumber(0);
		RewardsService rewardsService = new RewardsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService);
		tourGuideService.tracker.stopTracking();

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		// ACT
		VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);

		// ASSERT
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}

	@Test
	public void getAllCurrentLocations() {
		// ARRANGE
		InternalTestHelper.setInternalUserNumber(0);
		RewardsService rewardsService = new RewardsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService);
		tourGuideService.tracker.stopTracking();

        User user1 = new User(UUID.fromString("123e4567-e89b-42d3-a456-556642440001"), "jon1", "001", "jon1@tourGuide.com");
        User user2 = new User(UUID.fromString("123e4567-e89b-42d3-a456-556642440002"), "jon2", "002", "jon2@tourGuide.com");
        User user3 = new User(UUID.fromString("123e4567-e89b-42d3-a456-556642440003"), "jon3", "003", "jon3@tourGuide.com");
		tourGuideService.addUser(user1);
		tourGuideService.addUser(user2);
		tourGuideService.addUser(user3);
        Location location1 = new Location(61.218887D, -149.877502D);
        Location location2 = new Location(62.218887D, -148.877502D);
        Location location3 = new Location(63.218887D, -147.877502D);
		user1.addToVisitedLocations(new VisitedLocation(user1.getUserId(), location1, new Date()));
		user2.addToVisitedLocations(new VisitedLocation(user2.getUserId(), location2, new Date()));
		user3.addToVisitedLocations(new VisitedLocation(user3.getUserId(), location3, new Date()));

		HashMap<String, Location> allCurrentLocationsExpected = new HashMap<>();
		allCurrentLocationsExpected.put("123e4567-e89b-42d3-a456-556642440001", location1);
		allCurrentLocationsExpected.put("123e4567-e89b-42d3-a456-556642440002", location2);
		allCurrentLocationsExpected.put("123e4567-e89b-42d3-a456-556642440003", location3);

      	// ACT
		HashMap<String, Location> allCurrentLocationsActual = tourGuideService.getAllCurrentLocations();

		// ASSERT
		assertEquals(allCurrentLocationsExpected, allCurrentLocationsActual);
	}

	@Test
	public void getUser() {
		// ARRANGE
		InternalTestHelper.setInternalUserNumber(0);
		RewardsService rewardsService = new RewardsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService);
		tourGuideService.tracker.stopTracking();

		User user1 = new User(UUID.randomUUID(), "jon1", "000", "jon1@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user1);
		tourGuideService.addUser(user2);

		// ACT
		User retrievedUser1 = tourGuideService.getUser(user1.getUserName());
		User retrievedUser2 = tourGuideService.getUser(user2.getUserName());

		// ASSERT
		assertEquals(user1, retrievedUser1);
		assertEquals(user2, retrievedUser2);
	}

	@Test
	public void addUser() {
		// ARRANGE
		InternalTestHelper.setInternalUserNumber(0);
		RewardsService rewardsService = new RewardsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService);
		tourGuideService.tracker.stopTracking();

		User user1 = new User(UUID.randomUUID(), "jon1", "000", "jon1@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		// ACT
		tourGuideService.addUser(user1);
		tourGuideService.addUser(user2);

		// ASSERT
		User retrievedUser1 = tourGuideService.getUser(user1.getUserName());
		User retrievedUser2 = tourGuideService.getUser(user2.getUserName());

		assertEquals(user1, retrievedUser1);
		assertEquals(user2, retrievedUser2);
	}

	@Test
	public void getAllUsers() {
		// ARRANGE
		InternalTestHelper.setInternalUserNumber(0);
		RewardsService rewardsService = new RewardsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService);
		tourGuideService.tracker.stopTracking();

		User user1 = new User(UUID.randomUUID(), "jon1", "000", "jon1@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user1);
		tourGuideService.addUser(user2);

		// ACT
		List<User> allUsers = tourGuideService.getAllUsers();

		// ASSERT
		assertEquals(2, allUsers.size());
		assertTrue(allUsers.contains(user1));
		assertTrue(allUsers.contains(user2));
	}
	
	@Test
	public void trackUserLocation() {
		//Added to fix NumberFormatException due to decimal number separator
		Locale.setDefault(new Locale("en", "US"));

		// ARRANGE
		InternalTestHelper.setInternalUserNumber(0);
		RewardsService rewardsService = new RewardsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService);
		tourGuideService.tracker.stopTracking();

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		// ACT
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		
		// ASSERT
		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	@Test
	public void getNearbyAttractions() {
		//Added to fix NumberFormatException due to decimal number separator
		Locale.setDefault(new Locale("en", "US"));

		// ARRANGE
		InternalTestHelper.setInternalUserNumber(0);
		RewardsService rewardsService = new RewardsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService);
		tourGuideService.tracker.stopTracking();

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), new Location(47.305969D, 71.710449D), new Date());

		List<NearbyAttraction> nearbyAttractionsExpected = new ArrayList<>();

		NearbyAttraction nearbyAttraction1 = new NearbyAttraction("McKinley Tower", new Location(61.218887D, -149.877502D), new Location(47.305969D, 71.710449D), 4586.179236787266D, rewardsService.getRewardPoints(new Attraction("McKinley Tower", "Anchorage", "AK", 61.218887D, -149.877502D), user));
		NearbyAttraction nearbyAttraction2 = new NearbyAttraction("Franklin Park Zoo", new Location(42.302601D, -71.086731D), new Location(47.305969D, 71.710449D), 5836.916287407119D, rewardsService.getRewardPoints(new Attraction("Franklin Park Zoo", "Boston", "MA", 42.302601D, -71.086731D), user));
		NearbyAttraction nearbyAttraction3 = new NearbyAttraction("Bronx Zoo", new Location(40.852905D, -73.872971D), new Location(47.305969D, 71.710449D), 5985.996919050997D, rewardsService.getRewardPoints(new Attraction("Bronx Zoo", "Bronx", "NY", 40.852905D, -73.872971D), user));
		NearbyAttraction nearbyAttraction4 = new NearbyAttraction("Flatiron Building", new Location(40.741112D, -73.989723D), new Location(47.305969D, 71.710449D), 5995.465468529265, rewardsService.getRewardPoints(new Attraction("Flatiron Building", "New York City", "NY", 40.741112D, -73.989723D), user));
		NearbyAttraction nearbyAttraction5 = new NearbyAttraction("Jackson Hole", new Location(43.582767D, -110.821999D), new Location(47.305969D, 71.710449D), 6150.946648899067D, rewardsService.getRewardPoints(new Attraction("Jackson Hole", "Jackson Hole", "WY", 43.582767D, -110.821999D), user));
		nearbyAttractionsExpected.add(nearbyAttraction1);
		nearbyAttractionsExpected.add(nearbyAttraction2);
		nearbyAttractionsExpected.add(nearbyAttraction3);
		nearbyAttractionsExpected.add(nearbyAttraction4);
		nearbyAttractionsExpected.add(nearbyAttraction5);

		// ACT
		List<NearbyAttraction> nearbyAttractionsActual = tourGuideService.getNearByAttractions(visitedLocation, user);

		// ASSERT
		assertEquals(5, nearbyAttractionsActual.size());

		for (int j=0; j<5; j++) {
			assertEquals(nearbyAttractionsExpected.get(j).getAttractionName(), nearbyAttractionsActual.get(j).getAttractionName());
		}
	}

	@Test
	public void getTripDeals() {
		// ARRANGE
		InternalTestHelper.setInternalUserNumber(0);
		RewardsService rewardsService = new RewardsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService);
		tourGuideService.tracker.stopTracking();

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		// ACT
		List<Provider> providers = tourGuideService.getTripDeals(user);

		// ASSERT
		assertEquals(5, providers.size());// initial wrong = 10
	}

	@Test
	public void getUserPreferences() {
		// ARRANGE
		InternalTestHelper.setInternalUserNumber(0);
		RewardsService rewardsService = new RewardsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService);
		tourGuideService.tracker.stopTracking();

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		UserPreferences userPreferences = new UserPreferences();
		userPreferences.setAttractionProximity(1000);
		userPreferences.setCurrency("USD");
		userPreferences.setLowerPricePoint(0D);
		userPreferences.setHighPricePoint(1000000D);
		userPreferences.setTripDuration(5);
		userPreferences.setTicketQuantity(3);
		userPreferences.setNumberOfAdults(2);
		userPreferences.setNumberOfChildren(1);

		user.setUserPreferences(userPreferences);

		// ACT
		UserPreferences userPreferencesRetrieved = tourGuideService.getUserPreferences(user);

		// ASSERT
		assertEquals(userPreferences, userPreferencesRetrieved);
	}

	@Test
	public void postUserPreferences() {
		// ARRANGE
		InternalTestHelper.setInternalUserNumber(0);
		RewardsService rewardsService = new RewardsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService);
		tourGuideService.tracker.stopTracking();

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		UserPreferences userPreferences = new UserPreferences();
		userPreferences.setAttractionProximity(1000);
		userPreferences.setCurrency("USD");
		userPreferences.setLowerPricePoint(0D);
		userPreferences.setHighPricePoint(1000000D);
		userPreferences.setTripDuration(5);
		userPreferences.setTicketQuantity(3);
		userPreferences.setNumberOfAdults(2);
		userPreferences.setNumberOfChildren(1);

		// ACT
		tourGuideService.postUserPreferences(user, userPreferences);

		// ASSERT
		assertEquals(userPreferences, user.getUserPreferences());
	}
}
