package tourGuide;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.BeforeClass;
import org.junit.Test;

import org.slf4j.Logger;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import tourGuide.domain.location.Attraction;
import tourGuide.domain.location.Location;
import tourGuide.domain.location.VisitedLocation;
import tourGuide.domain.user.UserReward;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.domain.user.User;

import utils.TourGuideTestUtil;

/**
 * Class including tests for the rewards service of the TourGuide application.
 */
public class TestRewardsService {

	private static String gpsServiceName;
	private static String gpsServicePort;
	private static String rewardsServiceName;
	private static String rewardsServicePort;
	private static String preferencesServiceName;
	private static String preferencesServicePort;

    @BeforeClass
    public static void beforeTest() {
        gpsServiceName = "localhost";
        gpsServicePort = "8081";
        rewardsServiceName = "localhost";
        rewardsServicePort = "8082";
        preferencesServiceName = "localhost";
        preferencesServicePort = "8083";

        LoggingSystem.get(ClassLoader.getSystemClassLoader()).setLogLevel(Logger.ROOT_LOGGER_NAME, LogLevel.INFO);
    }

	@Test
	public void calculateRewards() {
		//Added to fix NumberFormatException due to decimal number separator
		Locale.setDefault(new Locale("en", "US"));

		// ARRANGE
		InternalTestHelper.setInternalUserNumber(0);
		RewardsService rewardsService = new RewardsService(gpsServiceName, gpsServicePort, rewardsServiceName, rewardsServicePort);
		List<Attraction> allAttractions = rewardsService.getAllAttractions();
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), allAttractions.get(0), new Date()));

		// ACT
		rewardsService.calculateRewards(user, allAttractions);

		// ASSERT
		List<UserReward> userRewards = user.getUserRewards();
		assertEquals(1, userRewards.size());
	}

	@Test
	public void nearAttraction() {
		// ARRANGE
		RewardsService rewardsService = new RewardsService(gpsServiceName, gpsServicePort, rewardsServiceName, rewardsServicePort);
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);
		List<Attraction> allAttractions = rewardsService.getAllAttractions();
		VisitedLocation visitedLocationRandom = new VisitedLocation(UUID.randomUUID(), new Location(TourGuideTestUtil.generateRandomLatitude(), TourGuideTestUtil.generateRandomLongitude()), TourGuideTestUtil.getRandomTime());

		// ACT & ASSERT
		assertTrue(rewardsService.nearAttraction(visitedLocationRandom, allAttractions.get(0)));
	}

	@Test
	public void nearAllAttractions() {
		// ARRANGE
		InternalTestHelper.setInternalUserNumber(1);
		RewardsService rewardsService = new RewardsService(gpsServiceName, gpsServicePort, rewardsServiceName, rewardsServicePort);
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);
		List<Attraction> allAttractions = rewardsService.getAllAttractions();
		TourGuideService tourGuideService = new TourGuideService(rewardsService, gpsServiceName, gpsServicePort, preferencesServiceName, preferencesServicePort);
		tourGuideService.tracker.stopTracking();

		// ACT
		rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0), allAttractions);
		List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));

		// ASSERT
		// Total number of attractions = 26
		assertEquals(26, userRewards.size());
	}

}
