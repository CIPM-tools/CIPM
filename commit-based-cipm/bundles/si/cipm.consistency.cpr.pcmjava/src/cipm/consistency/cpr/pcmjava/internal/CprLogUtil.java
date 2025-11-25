package cipm.consistency.cpr.pcmjava.internal;

import org.apache.log4j.Logger;

public class CprLogUtil {
	public static void logCpr(Object cprObj) {
		Logger.getLogger("cipm.consistency.cpr.pcmjava").info(cprObj.getClass().getSimpleName());
	}
}
