package framework.base;

/**
 * The PerformanceUtils class that contains all methods to measure execution times.
 *
 * @author carlos.cadena
 */
public class PerformanceUtils {

	/** The time start. */
	private static long timeStart;

	/** The time stop. */
	private static long timeStop;

	/** The time average. */
	private static float timeAverage;

	/**
	 * Start timer.
	 * 
	 * @author carlos.cadena
	 * 
	 */
	public static void startTimer() {
		timeStart = System.currentTimeMillis();
	}

	/**
	 * Stop timer.
	 * 
	 * @author carlos.cadena
	 *
	 * @return after the timer is stopped the timeframe between start and end 
	 */
	public static long stopTimer() {
		if (timeStart == 0.0) {
			try {
				throw new NoSuchFieldException("Debes iniciar el timer para poder obtener el tiempo");
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		timeStop = System.currentTimeMillis();
		return timeStop - timeStart;
	}

	/**
	 * Adds the time to average.
	 * 
	 * @author carlos.cadena
	 * @param time the time
	 */
	public static void addTimeToAverage(long time) {
		if (timeAverage == 0.0)
			timeAverage = timeAverage + time;
		else
			timeAverage = (timeAverage + time) / 2;
	}

	/**
	 * Gets the time average in seconds.
	 * 
	 * @author carlos.cadena
	 * @return the average time in seconds
	 */
	public static float getTimeAverageInSeconds() {
		return timeAverage / 1000;
	}
}
