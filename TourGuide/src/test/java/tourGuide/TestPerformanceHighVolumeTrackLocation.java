package tourGuide;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import org.slf4j.Logger;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import tourGuide.domain.location.Attraction;
import tourGuide.helper.InternalTestHelper;

import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.domain.user.User;

/**
 * Class including performance tests for the tracking user location of TourGuide application.
 */
public class TestPerformanceHighVolumeTrackLocation {

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
	/*
	 * A note on performance improvements:
	 *     
	 *     The number of users generated for the high volume tests can be easily adjusted via this method:
	 *     
	 *     		InternalTestHelper.setInternalUserNumber(100000);
	 *     
	 *     
	 *     These tests can be modified to suit new solutions, just as long as the performance metrics
	 *     at the end of the tests remains consistent. 
	 * 
	 *     These are performance metrics that we are trying to hit:
	 *     
	 *     highVolumeTrackLocation: 100,000 users within 15 minutes:
	 *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     *     highVolumeGetRewards: 100,000 users within 20 minutes:
	 *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 */
	//@Ignore
	@Test
	public void highVolumeTrackLocation() {

		//Added to fix NumberFormatException due to decimal number separator
		Locale.setDefault(new Locale("en", "US"));

		// ARRANGE
		// Users should be incremented up to 100,000, and test finishes within 15 minutes
		InternalTestHelper.setInternalUserNumber(100);

		RewardsService mockRewardsService = Mockito.spy(new RewardsService(gpsServiceName, gpsServicePort, rewardsServiceName, rewardsServicePort));
		List<Attraction> allAttractions = mockRewardsService.getAllAttractions();
		doNothing().when(mockRewardsService).calculateRewards(any(User.class), ArgumentMatchers.anyList());
		TourGuideService tourGuideService = new TourGuideService(mockRewardsService, gpsServiceName, gpsServicePort, preferencesServiceName, preferencesServicePort);
		List<User> allUsers = tourGuideService.getAllUsers();
		tourGuideService.tracker.stopTracking();
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		// ACT
		ForkJoinPool forkJoinPool = new ForkJoinPool(100);
		allUsers.forEach((user)-> {
			CompletableFuture
					.runAsync(()->tourGuideService.trackUserLocation(user), forkJoinPool)
					.thenRun(()->mockRewardsService.calculateRewards(user, allAttractions));
		});
		boolean result = forkJoinPool.awaitQuiescence(15,TimeUnit.MINUTES);
		stopWatch.stop();
		forkJoinPool.shutdown();

		// ASSERT
		System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
		assertTrue(result);

	}
}
