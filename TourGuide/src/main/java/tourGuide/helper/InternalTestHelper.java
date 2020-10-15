package tourGuide.helper;

/**
 * Class in charge of managing the number of users for initialization and testing purposes.
 */
public class InternalTestHelper {

	// Set this default up to 100,000 for testing
	private static int internalUserNumber = 10;

	/**
	 * Set the internal user number.
	 *
	 * @param internalUserNumber The internal user number
	 */
	public static void setInternalUserNumber(int internalUserNumber) {
		InternalTestHelper.internalUserNumber = internalUserNumber;
	}

	/**
	 * Get the internal user number.
	 *
	 * @return The internal user number
	 */
	public static int getInternalUserNumber() {
		return internalUserNumber;
	}
}
