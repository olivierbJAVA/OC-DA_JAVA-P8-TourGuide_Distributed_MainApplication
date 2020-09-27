package tourGuide.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TourGuideConfiguration {

	@Bean
	public TourGuideInitialization getTourGuideInitialization() {
		return new TourGuideInitialization();
	}
}
