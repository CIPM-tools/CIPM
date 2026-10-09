package org.splevo.jamopp.diffing.similarity;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import com.google.common.base.Strings;

/**
 * An interface to help log the switch classes without having to duplicate
 * similar log message structures.
 * <br><br>
 * Log messages' level is set to {@code Priority.INFO} to avoid flooding the console
 * and causing memory issues.
 * 
 * @author atora
 */
public interface ILoggableSwitch {
	public default Logger getLogger() {
		return Logger.getLogger(this.getClass().getSimpleName());
	}

	public default void logComparison(String subject1, String subject2, String subjectDesc) {
		this.logMessage("Comparing " + subjectDesc + "s (1 vs 2): " +
    			Strings.nullToEmpty(subject1) + " vs " + Strings.nullToEmpty(subject2));
    }
    
	public default void logResult(boolean result, String subjectDesc) {
		this.logMessage("Result of comparing " + subjectDesc + "s: " + result);
    }

	public default void logMessage(String msg) {
		this.logMessage(msg, Level.INFO);
	}
	
	public default void logMessage(String msg, Level p) {
		this.getLogger().log(p, msg);
	}
	
	public default void logMessage(String msg, String subjectDesc) {
		this.logMessage(msg + " (while comparing" + subjectDesc + ")");
	}
}
