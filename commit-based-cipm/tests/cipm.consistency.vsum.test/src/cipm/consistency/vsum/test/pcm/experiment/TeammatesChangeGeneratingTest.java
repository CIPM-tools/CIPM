package cipm.consistency.vsum.test.pcm.experiment;

import java.nio.file.Path;

import org.junit.jupiter.api.Disabled;

import cipm.consistency.vsum.Propagation;
import cipm.consistency.vsum.test.java.TEAMMATESCITestController;
import cipm.consistency.vsum.test.pcm.ChangeSaver;

/**
 * Runs all TEAMMATES test cases and saves the propagated changes while doing
 * so.
 * <p>
 * It is recommended to keep it disabled, in order to avoid unnecessarily
 * removing previously saved changes and test resources from TEAMMATES tests.
 */
@Disabled("Enable to generate propagated changes during vsum tests")
public class TeammatesChangeGeneratingTest extends TEAMMATESCITestController {
	@Override
	protected void saveChanges(Propagation prop, Path rootDirPath) {
		var layout = new JavaToPcmPropagationDirLayout(rootDirPath);
		ChangeSaver.saveUnresolvedChanges(prop, layout);
		System.out.println("Changes are saved for: " + prop.getCommitId());
	}
}
