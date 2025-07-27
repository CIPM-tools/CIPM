package cipm.consistency.fitests.similarity;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * An interface that encapsulates logging. <br>
 * <br>
 * Make sure to call {@link ILoggable#setUpLogger()} prior to other methods.
 * 
 * @author Alp Torac Genc
 */
public interface ILoggable {
	/**
	 * @return The Logger with the given name
	 */
	private static Logger getLoggerFor(String loggerName) {
		return Logger.getLogger(loggerName);
	}

	/**
	 * Sets up all loggers that have the {@code "cipm"} prefix in their name.
	 */
	public static void setUpLogger() {
		/*
		 * Order of precedence in logging levels:
		 * 
		 * OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE > ALL
		 */

		Logger logger = getLoggerFor("cipm");
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
	}

	/**
	 * Logs the given message at {@link Level#DEBUG} level.
	 */
	public default void logDebugMsg(String msg) {
		var logger = getLoggerFor("cipm." + this.getClass().getSimpleName());
		logger.debug(msg);
	}

	/**
	 * Logs the given message at {@link Level#INFO} level.
	 */
	public default void logInfoMsg(String msg) {
		var logger = getLoggerFor("cipm." + this.getClass().getSimpleName());
		logger.info(msg);
	}

	/**
	 * Logs the given message at {@link Level#ERROR} level.
	 */
	public default void logErrorMsg(String msg) {
		var logger = getLoggerFor("cipm." + this.getClass().getSimpleName());
		logger.error(msg);
	}

	/**
	 * Logs the given message at the {@link Level} that corresponds to the given
	 * priority.
	 */
	public default void logMsg(String msg, int priority) {
		var logger = getLoggerFor("cipm." + this.getClass().getSimpleName());
		logger.log(Level.toLevel(priority), msg);
	}
}
