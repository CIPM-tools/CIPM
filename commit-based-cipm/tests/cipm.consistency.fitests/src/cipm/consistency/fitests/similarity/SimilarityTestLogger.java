package cipm.consistency.fitests.similarity;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * A utility class that encapsulates logging. <br>
 * <br>
 * Make sure to call {@link SimilarityTestLogger#setUpLogger()} prior to other
 * methods.
 * 
 * @author Alp Torac Genc
 */
public class SimilarityTestLogger {
	/**
	 * The prefix of each CIPM-related logger without the separating dot (".")
	 */
	private final static String cipmRootLoggerName = "cipm";
	/**
	 * The prefix of each CIPM-related logger with the separating dot (".")
	 */
	private final static String cipmLoggerNamePrefix = cipmRootLoggerName + ".";

	/**
	 * Whether loggers have been set up
	 */
	private static boolean areLoggersSetUp = false;

	/**
	 * @return The Logger with the given name
	 */
	private static Logger getLoggerFor(String loggerName) {
		return Logger.getLogger(loggerName);
	}

	/**
	 * @return The Logger for the given class
	 */
	private static Logger getLoggerFor(Class<?> cls) {
		return getLoggerFor(cipmLoggerNamePrefix + cls.getSimpleName());
	}

	/**
	 * Sets up all loggers that have the {@value #cipmLoggerNamePrefix} prefix in
	 * their name. Calling this method multiple times will have no effect on
	 * loggers, unless {@code forceSetUp == true}.
	 * 
	 * @param forceSetUp Whether logger should be set anew after already being set
	 *                   previously
	 */
	public static void setUpLogger(boolean forceSetUp) {
		if (forceSetUp || !areLoggersSetUp) {
			/*
			 * Order of precedence in logging levels:
			 * 
			 * OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE > ALL
			 */

			Logger logger = getLoggerFor(cipmRootLoggerName);
			logger.setLevel(Level.DEBUG);

			// logger = Logger.getLogger("jamopp");
			// logger.setLevel(Level.ALL);

			// TODO Re-think how logging should work

			logger = Logger.getRootLogger();
			logger.setLevel(Level.OFF);
			logger.removeAllAppenders();
			ConsoleAppender ap = new ConsoleAppender(new PatternLayout("[%d{DATE}] %-5p: %c - %m%n"),
					ConsoleAppender.SYSTEM_OUT);
			logger.addAppender(ap);
			areLoggersSetUp = true;
		}
	}

	/**
	 * Sets up all loggers, if not already done.
	 * 
	 * @see {@link #setUpLogger(boolean)}
	 */
	public static void setUpLogger() {
		setUpLogger(false);
	}

	/**
	 * Logs the given message at {@link Level#DEBUG} level, using the logger for the
	 * given class.
	 */
	public static void logDebugMsg(String msg, Class<?> cls) {
		getLoggerFor(cls).debug(msg);
	}

	/**
	 * Logs the given message at {@link Level#INFO} level, using the logger for the
	 * given class.
	 */
	public static void logInfoMsg(String msg, Class<?> cls) {
		getLoggerFor(cls).info(msg);
	}

	/**
	 * Logs the given message at {@link Level#ERROR} level, using the logger for the
	 * given class.
	 */
	public static void logErrorMsg(String msg, Class<?> cls) {
		getLoggerFor(cls).error(msg);
	}

	/**
	 * Logs the given message at the {@link Level} that corresponds to the given
	 * priority, using the logger for the given class.
	 */
	public static void logMsg(String msg, int priority, Class<?> cls) {
		getLoggerFor(cls).log(Level.toLevel(priority), msg);
	}
}
