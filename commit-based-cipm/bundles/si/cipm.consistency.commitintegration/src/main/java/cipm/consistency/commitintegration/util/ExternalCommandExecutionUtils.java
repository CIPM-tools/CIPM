package cipm.consistency.commitintegration.util;

import java.io.File;
import java.io.IOException;
// TODO
//import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

/**
 * A utility class for executing external commands.
 * 
 * @author Martin Armbruster
 */
public final class ExternalCommandExecutionUtils {
    private static final Logger LOGGER = Logger
        .getLogger("cipm." + ExternalCommandExecutionUtils.class.getSimpleName());

    private ExternalCommandExecutionUtils() {
    }

    /**
     * Runs an external script.
     * 
     * @param directory
     *            directory in which the script shall run.
     * @param command
     *            the script to run.
     * @return true if the script was successfully executed. false otherwise.
     */
    public static boolean runScript(File directory, String command) {
        int result = -1;
        LOGGER.debug("Executing " + command);
//		if (SystemUtils.IS_OS_WINDOWS) {
//			result = internalRunScript(directory, "cmd.exe", "/c", "\"" + command + "\"");
//		} else {
        result = internalRunScript(directory, command);
//		}
        return result == 0;
    }

    private static int internalRunScript(File directory, String... command) {
        try {
            Process process = new ProcessBuilder().directory(directory)
                .inheritIO()
                .command(command)
                .start();
            return process.waitFor();
        } catch (IOException | InterruptedException e) {
            return -1;
        }
    }
}
