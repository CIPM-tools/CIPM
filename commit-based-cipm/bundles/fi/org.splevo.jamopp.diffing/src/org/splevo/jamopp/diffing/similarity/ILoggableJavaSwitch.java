package org.splevo.jamopp.diffing.similarity;

import org.splevo.jamopp.diffing.similarity.base.ILoggableSwitch;

/**
 * An extension of {@link ILoggableSwitch} for Java-related similarity switches.
 * 
 * @author Alp Torac Genc
 */
public interface ILoggableJavaSwitch extends ILoggableSwitch {
	/**
	 * Can be overridden in implementors to group log messages across different
	 * switches better.
	 * 
	 * @return The prefix of the logger's name, which is responsible for logging the
	 *         happenings in this implementation. Return the empty string ("") as
	 *         default value.
	 */
	public default String getLoggerPrefix() {
		return "javaswitch.";
	}
}
