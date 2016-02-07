package clock;

import application.Log;

public class ClockServiceFactory {
	private static ClockService currentClockService;
	private static String currentClockType = null;

	public static ClockService setClockService(String clockType, TimeStamp ts) {
		if (clockType == null) {
			return null;
		}
		if (currentClockType != null && !clockType.equalsIgnoreCase(currentClockType))
			Log.error("ClockServiceFactory", "Clock type changed");
		currentClockType = clockType;
		if (clockType.equalsIgnoreCase("Logical")) {
			if (currentClockService == null)
				currentClockService = new LogicalClockService(ts);
			return currentClockService;
		} else if (clockType.equalsIgnoreCase("Logical")) {
			if (currentClockService == null)
				currentClockService = new VectorClockService(ts);
			return currentClockService;
		}
		return null;
	}

	public static ClockService setClockService(String clockType, int size) {
		if (clockType == null) {
			return null;
		}
		if (currentClockType != null && !clockType.equalsIgnoreCase(currentClockType))
			Log.error("ClockServiceFactory", "Clock type changed");
		currentClockType = clockType;
		if (clockType.equalsIgnoreCase("Logical")) {
			if (currentClockService == null)
				currentClockService = new LogicalClockService();
			return currentClockService;
		} else if (clockType.equalsIgnoreCase("Logical")) {
			if (currentClockService == null)
				currentClockService = new VectorClockService(size);
			return currentClockService;
		}

		return null;
	}
	
	public static ClockService getClockService() {
		if (currentClockType == null || currentClockService == null)
			Log.error("ClockServiceFactory", "Clock service not initialized");		
		return currentClockService;
	}
}
