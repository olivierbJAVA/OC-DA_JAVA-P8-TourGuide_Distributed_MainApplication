package tourGuide.tracker;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tourGuide.domain.location.Attraction;
import tourGuide.domain.user.User;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;

public class Tracker extends Thread {
	private Logger logger = LoggerFactory.getLogger(Tracker.class);

	private static final long trackingPollingInterval = TimeUnit.MINUTES.toSeconds(1);//initial = 5
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	//private final ExecutorService executorService = Executors.newFixedThreadPool(10);
	private final TourGuideService tourGuideService;
	private final RewardsService rewardsService;
	private boolean stop = false;

	public Tracker(TourGuideService tourGuideService, RewardsService rewardsService) {
		this.tourGuideService = tourGuideService;
		this.rewardsService = rewardsService;

		executorService.submit(this);
	}
	
	/**
	 * Assures to shut down the Tracker thread
	 */
	public void stopTracking() {
		stop = true;
		executorService.shutdownNow();
	}
	
	@Override
	public void run() {
		StopWatch stopWatch = new StopWatch();
		while(true) {
			if(Thread.currentThread().isInterrupted() || stop) {
				logger.debug("Tracker stopping");
				break;
			}
			
			List<User> users = tourGuideService.getAllUsers();

			List<Attraction> allAttractions = rewardsService.getAllAttractions();

			logger.debug("Begin Tracker. Tracking " + users.size() + " users.");
			stopWatch.start();

			ForkJoinPool forkJoinPool = new ForkJoinPool(100);
			//final ForkJoinPool test = new ForkJoinPool(1,	ForkJoinPool.defaultForkJoinWorkerThreadFactory, null,true);

			/*
			logger.debug("Request getUserLocation build in Tracker");
			List<URI> targets = new ArrayList<>();
			HttpClient client = HttpClient.newBuilder().executor(forkJoinPool).build();
			*/
			/*
			// Essai 1
			users.forEach((user)-> {
						try {
							targets.add( new URI("http://localhost:8081/getUserLocation?userId=" + user.getUserId()));
						} catch (URISyntaxException e) {
							e.printStackTrace();
						}

					});

					List<CompletableFuture<String>> result = targets
					.stream()
					.map(target -> client.sendAsync( HttpRequest.newBuilder(target).GET().build(), HttpResponse.BodyHandlers.ofString() )
					.thenApply(response -> user.addToVisitedLocations(response.body()) )     )
					.collect(Collectors.toList());
			*/
			/*
			// Essai 2
			users.forEach((user)-> {
				CompletableFuture
						.runAsync(() -> {
							try {
								client
												.sendAsync( HttpRequest
														.newBuilder(
																new URI("http://localhost:8081/getUserLocation?userId=" + user.getUserId()))
														.GET()
														.build(), HttpResponse.BodyHandlers.ofString() );
							} catch (URISyntaxException e) {
								e.printStackTrace();
							}
						})
						.thenApply(response -> user.addToVisitedLocations(response.body()) )
						.thenAccept(unused -> rewardsService.calculateRewards(user, allAttractions));
			});
			*/

			users.forEach((user)-> {
				CompletableFuture
						.runAsync(()->tourGuideService.trackUserLocation(user), forkJoinPool)
						.thenAccept(unused->rewardsService.calculateRewards(user, allAttractions))
						.exceptionally(exception -> {
							logger.error("Error in Tracker : " + exception );
							return null;
						})
				;
			});

			//Optional : in case you want to wait for the completion of track users and calculate rewards before Tracker sleeping
			//Wait maximum between Timeout and forkJoinPool has finished tasks
			forkJoinPool.awaitQuiescence(10,TimeUnit.MINUTES);

			stopWatch.stop();
			logger.debug("Tracker Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."); 
			stopWatch.reset();
			try {
				logger.debug("Tracker sleeping");
				TimeUnit.SECONDS.sleep(trackingPollingInterval);
			} catch (InterruptedException e) {
				break;
			}

			/*
			//Optional : in case we want to be sure that the completion of the tasks have been done in the trackingPollingInterval
			boolean result = forkJoinPool.awaitQuiescence(1,TimeUnit.MINUTES);
			if(result) {
				logger.debug("Tracking done in trackingPollingInterval");
			} else {
				logger.debug("Warning : Tracking last more than trackingPollingInterval ");
			}
			*/
		}
	}
}
