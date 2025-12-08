package cipm.consistency.cpr.pcmjava.internal;

import org.apache.log4j.Logger;

public class CprLogUtil {
	private static final String loggerName = "cipm.consistency.cpr.pcmjava";

	public static void logCpr(Object cprObj) {
		Logger.getLogger(loggerName).info(cprObj.getClass().getSimpleName());
	}
}
