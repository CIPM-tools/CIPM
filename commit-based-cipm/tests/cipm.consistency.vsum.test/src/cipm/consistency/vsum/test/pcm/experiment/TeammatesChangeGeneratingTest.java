package cipm.consistency.vsum.test.pcm.experiment;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import cipm.consistency.vsum.Propagation;
import cipm.consistency.vsum.test.java.TEAMMATESCITestController;
import cipm.consistency.vsum.test.pcm.ChangeSaver;

/**
 * Evaluates the Java -> PCM change propagation, since it extends
 * TEAMMATESCITestController
 */
public class TeammatesChangeGeneratingTest extends TEAMMATESCITestController {
	private static final String experimentRootDirNamePrefix = "Teammates-Experiment-";

	private static final List<JavaToPcmPropagationDirLayout> dirLayouts = new ArrayList<>();

	@Override
	protected void saveChanges(Propagation prop, Path rootDirPath) {
		var layout = new JavaToPcmPropagationDirLayout(rootDirPath);
		dirLayouts.add(layout);
		new ChangeSaver(layout).saveUnresolvedChanges(prop);
		System.out.println("Changes are saved for: " + prop.getCommitId());
	}

	@Override
	protected void setup(boolean overwrite) {
	}

	@Override
	public void cleanupAfterTest() {
	}

	@Test
	@Override
	public void testTeammates() {
//		super.testTeammates();

		dirLayouts.add(new JavaToPcmPropagationDirLayout(Paths.get("target", "TEAMMATESCITest-1-6484257")));
		dirLayouts.add(new JavaToPcmPropagationDirLayout(Paths.get("target", "TEAMMATESCITest-2-48b67ba")));
		dirLayouts.add(new JavaToPcmPropagationDirLayout(Paths.get("target", "TEAMMATESCITest-3-83f518e")));
		dirLayouts.add(new JavaToPcmPropagationDirLayout(Paths.get("target", "TEAMMATESCITest-4-f33d0bc")));
		dirLayouts.add(new JavaToPcmPropagationDirLayout(Paths.get("target", "TEAMMATESCITest-5-ce4463a")));

		var pcmToJavaPropTest = new PcmToJavaChangePropagationTest();

//		for (int i = 1; i < dirLayouts.size() - 1; i++) {
//			pcmToJavaPropTest.pcmToJavaChangePropagationTestTemplate(
//					new PcmToJavaChangePropagationDirLayout(dirLayouts.get(i - 1), dirLayouts.get(i),
//							Path.of("target", experimentRootDirNamePrefix + (i - 1) + "-to-" + i).toAbsolutePath()));
//		}
		pcmToJavaPropTest.pcmToJavaChangePropagationTestTemplate(
		new PcmToJavaChangePropagationDirLayout(dirLayouts.get(0), dirLayouts.get(1),
				Path.of("target", experimentRootDirNamePrefix + (1) + "-to-" + 2).toAbsolutePath()));
	}
}
