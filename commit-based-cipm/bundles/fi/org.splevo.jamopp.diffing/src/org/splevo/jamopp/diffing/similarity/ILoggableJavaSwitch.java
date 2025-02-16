package org.splevo.jamopp.diffing.similarity;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import com.google.common.base.Strings;

/**
 * An interface to help log the switch classes without having to duplicate
 * similar log message structures. <br>
 * <br>
 * Log messages' level is set to {@code Priority.INFO} to avoid flooding the
 * console and causing memory issues.
 * 
 * @author atora
 */
public interface ILoggableJavaSwitch {
	/**
	 * @return The logger associated with the concrete implementor.
	 */
	public default Logger getLogger() {
		return Logger.getLogger(this.getLoggerName());
	}

	/**
	 * Can be overridden in implementors to group log messages better.
	 * 
	 * @return The logger's name, which can be accessed by {@link #getLogger()}.
	 */
	public default String getLoggerName() {
		return this.getLoggerPrefix() + this.getClass().getSimpleName();
	}

	/**
	 * Can be overridden in implementors to group log messages better.
	 * 
	 * @return The prefix of the logger's name, which can be accessed by
	 *         {@link #getLogger()}.
	 */
	public default String getLoggerPrefix() {
		return "javaswitch.";
	}

	/**
	 * The version of {@link #logComparison(String, String, String)} for Object.
	 */
	public default void logComparison(Object subject1, Object subject2, String subjectDesc) {
		String s1 = subject1 != null ? subject1.toString() : null;
		String s2 = subject2 != null ? subject2.toString() : null;

		this.logComparison(s1, s2, subjectDesc);
	}

	/**
	 * Logs the comparison of the both subjects.
	 */
	public default void logComparison(String subject1, String subject2, String subjectDesc) {
		this.logMessage("Comparing " + subjectDesc + "s (1 vs 2): " + Strings.nullToEmpty(subject1) + " vs "
				+ Strings.nullToEmpty(subject2));
	}

	/**
	 * Logs a boolean result with the given subject description.
	 */
	public default void logResult(boolean result, String subjectDesc) {
		this.logMessage("Result of comparing " + subjectDesc + "s: " + result);
	}

	/**
	 * Logs the given message.
	 */
	public default void logMessage(String msg) {
		this.logMessage(msg, Level.INFO);
	}

	/**
	 * Logs the given message at the given level.
	 */
	public default void logMessage(String msg, Level level) {
		this.getLogger().log(level, msg);
	}
}
