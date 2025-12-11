package cipm.consistency.cpr.pcmjava.internal;

import org.apache.log4j.Logger;

/**
 * A minimal utility class to use {@link org.apache.log4j.Logger} in CPRs.
 * 
 * @author Alp Torac Genc
 */
public class CprLogUtil {
	private static final String loggerName = "cipm.consistency.cpr.pcmjava";

	public static void logCpr(Object cprObj) {
		Logger.getLogger(loggerName).info(cprObj.getClass().getSimpleName());
	}
}
