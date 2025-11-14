package cipm.consistency.vsum.test.pcm.experiment;

import java.nio.file.Path;

import cipm.consistency.vsum.Propagation;
import cipm.consistency.vsum.test.java.TEAMMATESCITestController;
import cipm.consistency.vsum.test.pcm.ChangeSaver;

/**
 * Evaluates the Java -> PCM change propagation, since it extends
 * TEAMMATESCITestController
 */
public class TeammatesChangeGeneratingTest extends TEAMMATESCITestController {
	@Override
	protected void saveChanges(Propagation prop, Path rootDirPath) {
		var layout = new JavaToPcmPropagationDirLayout(rootDirPath);
		new ChangeSaver(layout).saveUnresolvedChanges(prop);
		System.out.println("Changes are saved for: " + prop.getCommitId());
	}
}
