package tourGuide.domain.user;


public class UserPreferences {
	
	private int attractionProximity = Integer.MAX_VALUE;
	private String currency = "USD";
	private Double lowerPricePoint = 0D;
	private Double highPricePoint = 1000000D;
	private int tripDuration = 1;
	private int ticketQuantity = 1;
	private int numberOfAdults = 1;
	private int numberOfChildren = 0;
	
	public UserPreferences() {
	}
	
	public void setAttractionProximity(int attractionProximity) {
		this.attractionProximity = attractionProximity;
	}
	
	public int getAttractionProximity() {
		return attractionProximity;
	}

	// Added getter and setter for CurrencyUnit
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Double getLowerPricePoint() {
		return lowerPricePoint;
	}

	public void setLowerPricePoint(Double lowerPricePoint) {
		this.lowerPricePoint = lowerPricePoint;
	}

	public Double getHighPricePoint() {
		return highPricePoint;
	}

	public void setHighPricePoint(Double highPricePoint) {
		this.highPricePoint = highPricePoint;
	}

	public int getTripDuration() {
		return tripDuration;
	}

	public void setTripDuration(int tripDuration) {
		this.tripDuration = tripDuration;
	}

	public int getTicketQuantity() {
		return ticketQuantity;
	}

	public void setTicketQuantity(int ticketQuantity) {
		this.ticketQuantity = ticketQuantity;
	}
	
	public int getNumberOfAdults() {
		return numberOfAdults;
	}

	public void setNumberOfAdults(int numberOfAdults) {
		this.numberOfAdults = numberOfAdults;
	}

	public int getNumberOfChildren() {
		return numberOfChildren;
	}

	public void setNumberOfChildren(int numberOfChildren) {
		this.numberOfChildren = numberOfChildren;
	}

}
