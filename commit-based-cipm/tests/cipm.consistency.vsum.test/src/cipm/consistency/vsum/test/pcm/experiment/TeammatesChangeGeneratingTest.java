package cipm.consistency.vsum.test.pcm.experiment;

import cipm.consistency.vsum.Propagation;
import cipm.consistency.vsum.test.java.TEAMMATESCITestController;
import cipm.consistency.vsum.test.pcm.ChangeSaver;

/**
 * Evaluates the Java -> PCM change propagation, since it extends
 * TEAMMATESCITestController
 */
public class TeammatesChangeGeneratingTest extends TEAMMATESCITestController {
	private ChangeGeneratingTestDirLayout changesDirLayout;

	@Override
	protected void saveChanges(Propagation prop) {
		// TODO Run all Teammates tests and save the Java and PCM changes
//		new ChangeSaver(this.getRootPath()).saveChanges(prop, true);
		new ChangeSaver(changesDirLayout.getChangesRootPath()).saveChanges(prop, true);
		System.out.println("Changes are saved for: " + prop.getCommitId());
	}
}
