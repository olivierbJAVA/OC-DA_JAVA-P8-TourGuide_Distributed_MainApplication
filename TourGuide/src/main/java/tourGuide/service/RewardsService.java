package tourGuide.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import tourGuide.domain.location.Attraction;
import tourGuide.domain.location.Location;
import tourGuide.domain.location.VisitedLocation;
import org.springframework.stereotype.Service;
import tourGuide.domain.user.User;
import tourGuide.domain.user.UserReward;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@PropertySource("classpath:application.properties")
public class RewardsService {
	private Logger logger = LoggerFactory.getLogger(RewardsService.class);

	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
    private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;

	private final String gpsServiceName;
	private final String gpsServicePort;
	private final String rewardsServiceName;
	private final String rewardsServicePort;

	public RewardsService(@Value("${service.gps.name}") String gpsServiceName, @Value("${service.gps.port}") String gpsServicePort, @Value("${service.rewards.name}") String rewardsServiceName, @Value("${service.rewards.port}") String rewardsServicePort) {
		this.gpsServiceName=gpsServiceName;
		this.gpsServicePort=gpsServicePort;
		this.rewardsServiceName=rewardsServiceName;
		this.rewardsServicePort=rewardsServicePort;
	}

	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}

	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	public void calculateRewards(User user, List<Attraction> allAttractions) {
		logger.debug("Calculate Rewards - Thread : " + Thread.currentThread().getName() + " - User : " + user.getUserName());

		List<VisitedLocation> userLocations = user.getVisitedLocations();

		for(VisitedLocation visitedLocation : userLocations) {
			for(Attraction attraction : allAttractions) {
				if(user.getUserRewards().stream().filter(r -> r.getAttraction().getAttractionName().equals(attraction.getAttractionName())).count() == 0) {
					if(nearAttraction(visitedLocation, attraction)) {
						user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
					}
				}
			}
		}
	}

	public List<Attraction> getAllAttractions(){
		List<Attraction> attractions = new ArrayList<>();

		logger.debug("Request getAttractions build");
		HttpClient client = HttpClient.newHttpClient();

		String requestURI = "http://"+gpsServiceName+":"+gpsServicePort+"/getAttractions";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(requestURI))
				.GET()
				.build();
		try {
			HttpResponse <String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			logger.debug("Status code = " + response.statusCode());
			logger.debug("Response Body = " + response.body());
			ObjectMapper mapper = new ObjectMapper();
			attractions = mapper.readValue(response.body(), new TypeReference<List<Attraction>>(){ });
		} catch (IOException | InterruptedException e) {
			logger.error(e.toString());
			e.printStackTrace();
		}

		return attractions;
	}

	public int getRewardPoints(Attraction attraction, User user) {
		int rewardsPoint=0;

		logger.debug("Request getRewardPoints build");
		HttpClient client = HttpClient.newHttpClient();
		String requestURI = "http://"+rewardsServiceName+":"+rewardsServicePort+"/getRewardPoints?attractionId=" + attraction.getAttractionId() + "&userId=" + user.getUserId();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(requestURI))
				.GET()
				.build();
		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			logger.debug("Status code = " + response.statusCode());
			logger.debug("Response Body = " + response.body());
			rewardsPoint=Integer.parseInt(response.body());
		} catch (IOException | NumberFormatException | InterruptedException e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		logger.debug("Response RewardsPoint = " + rewardsPoint);
		return rewardsPoint;
	}

	public boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.getLocation()) > proximityBuffer ? false : true;
	}

	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.getLatitude());
        double lon1 = Math.toRadians(loc1.getLongitude());
        double lat2 = Math.toRadians(loc2.getLatitude());
        double lon2 = Math.toRadians(loc2.getLongitude());

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}
}
