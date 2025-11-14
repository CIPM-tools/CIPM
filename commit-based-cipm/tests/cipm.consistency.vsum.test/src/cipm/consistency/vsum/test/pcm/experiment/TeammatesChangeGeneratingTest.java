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

		var integrationPath = Paths.get("target", "TEAMMATESCITest-1-6484257");
		var firstPropPath = Paths.get("target", "TEAMMATESCITest-2-48b67ba");
		var secondPropPath = Paths.get("target", "TEAMMATESCITest-3-83f518e");
		var thirdPropPath = Paths.get("target", "TEAMMATESCITest-4-f33d0bc");
		var fourthPropPath = Paths.get("target", "TEAMMATESCITest-5-ce4463a");

		dirLayouts.add(new JavaToPcmPropagationDirLayout(integrationPath));
		dirLayouts.add(new JavaToPcmPropagationDirLayout(firstPropPath));
		dirLayouts.add(new JavaToPcmPropagationDirLayout(secondPropPath));
		dirLayouts.add(new JavaToPcmPropagationDirLayout(thirdPropPath));
		dirLayouts.add(new JavaToPcmPropagationDirLayout(fourthPropPath));

//		dirLayouts.add(new JavaToPcmPropagationDirLayout(integrationPath, null, null, null));
//
//		dirLayouts.add(new JavaToPcmPropagationDirLayout(firstPropPath,
//				firstPropPath.resolve("code").resolve("parsed-1-648425746bb9434051647c8266dfab50a8f2d6a3.code.javaxmi"),
//				firstPropPath.resolve("pcm").resolve("Repository-1-648425746bb9434051647c8266dfab50a8f2d6a3.repository"),
//				integrationPath.resolve("vsum").resolve("vsum").resolve("correspondences.correspondence")));
//
//		dirLayouts.add(new JavaToPcmPropagationDirLayout(secondPropPath,
//				secondPropPath.resolve("code").resolve("parsed-2-48b67bae03babf5a5e578aefce47f0285e8de8b4.code.javaxmi"),
//				secondPropPath.resolve("pcm").resolve("Repository-2-48b67bae03babf5a5e578aefce47f0285e8de8b4.repository"),
//				firstPropPath.resolve("vsum").resolve("vsum").resolve("correspondences.correspondence")));
//		
//		
//		dirLayouts.add(new JavaToPcmPropagationDirLayout(thirdPropPath,
//				thirdPropPath.resolve("code").resolve("parsed-3-83f518e279807dc7eb7023d008a4d1ab290fefee.code.javaxmi"),
//				thirdPropPath.resolve("pcm").resolve("Repository-3-83f518e279807dc7eb7023d008a4d1ab290fefee.repository"),
//				secondPropPath.resolve("vsum").resolve("vsum").resolve("correspondences.correspondence")));
//		
//		dirLayouts.add(new JavaToPcmPropagationDirLayout(fourthPropPath,
//				fourthPropPath.resolve("code").resolve("parsed-4-f33d0bcd5843678b832efd8ee2963e72a95ecfc9.code.javaxmi"),
//				fourthPropPath.resolve("pcm").resolve("Repository-4-f33d0bcd5843678b832efd8ee2963e72a95ecfc9.repository"),
//				thirdPropPath.resolve("vsum").resolve("vsum").resolve("correspondences.correspondence")));

		var pcmToJavaPropTest = new PcmToJavaChangePropagationTest();

//		for (int i = 1; i < dirLayouts.size() - 1; i++) {
//			pcmToJavaPropTest.pcmToJavaChangePropagationTestTemplate(
//					new PcmToJavaChangePropagationDirLayout(dirLayouts.get(i - 1), dirLayouts.get(i),
//							Path.of("target", experimentRootDirNamePrefix + (i - 1) + "-to-" + i).toAbsolutePath()));
//		}
		pcmToJavaPropTest.pcmToJavaChangePropagationTestTemplate(new ExperimentDirLayout(
				Path.of("target", experimentRootDirNamePrefix + 1).toAbsolutePath(), dirLayouts.get(1),
				integrationPath.resolve("code").resolve("Java.javaxmi"),
				integrationPath.resolve("pcm").resolve("Repository.repository"),
				integrationPath.resolve("im").resolve("imm.imm"),
				integrationPath.resolve("vsum").resolve("vsum").resolve("correspondences.correspondence")));
	}
}
