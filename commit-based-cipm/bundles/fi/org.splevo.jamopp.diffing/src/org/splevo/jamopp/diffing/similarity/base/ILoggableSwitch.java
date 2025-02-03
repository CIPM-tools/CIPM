package org.splevo.jamopp.diffing.similarity.base;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * An interface to help log switch classes without having to duplicate similar
 * log message methods. <br>
 * <br>
 * The underlying logging mechanism is assumed to have a name
 * {@link #getLoggerName()}, whose prefix is {@link #getLoggerPrefix()}. The
 * purpose of the prefix is to allow grouping of logging mechanisms across
 * different switches. This is supposed to help structure logging messages
 * better.
 * 
 * @author Alp Torac Genc
 */
public interface ILoggableSwitch {
	/**
	 * Can be used to get access to the name of the associated logging mechanism.
	 * <br>
	 * <br>
	 * Can be overridden in implementors to group log messages across different
	 * switches better.
	 * 
	 * @return The logger's name, which is responsible for logging the happenings in
	 *         this implementation. If not overridden, it returns:
	 *         {@code getLoggerPrefix() + this.getClass().getSimpleName()}.
	 * 
	 * @see {@link #getLoggerPrefix()}
	 */
	public default String getLoggerName() {
		return this.getLoggerPrefix() + this.getClass().getSimpleName();
	}

	/**
	 * Can be used to get access to the prefix of the name of the associated logging
	 * mechanism. <br>
	 * <br>
	 * Can be overridden in implementors to group log messages across different
	 * switches better.
	 * 
	 * @return The prefix of the logger's name, which is responsible for logging the
	 *         happenings in this implementation. Return the empty string ("") as
	 *         default value.
	 */
	public default String getLoggerPrefix() {
		return "";
	}

	/**
	 * Logs the given message at information level.
	 * 
	 * @see {@link Level#INFO}
	 */
	public default void logInfoMessage(String msg) {
		Logger.getLogger(this.getLoggerName()).log(Level.INFO, msg);
	}

	/**
	 * Logs the given message at warning level.
	 * 
	 * @see {@link Level#WARN}
	 */
	public default void logWarnMessage(String msg) {
		Logger.getLogger(this.getLoggerName()).log(Level.WARN, msg);
	}

	/**
	 * Logs the given message at debug level.
	 * 
	 * @see {@link Level#DEBUG}
	 */
	public default void logDebugMessage(String msg) {
		Logger.getLogger(this.getLoggerName()).log(Level.DEBUG, msg);
	}

	/**
	 * Logs the given message at error level.
	 * 
	 * @see {@link Level#ERROR}
	 */
	public default void logErrorMessage(String msg) {
		Logger.getLogger(this.getLoggerName()).log(Level.ERROR, msg);
	}
}
