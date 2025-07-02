package cipm.consistency.vsum.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import cipm.consistency.commitintegration.JavaParserAndPropagatorUtils;
import cipm.consistency.commitintegration.detection.BuildFileBasedComponentDetectionStrategy;
import cipm.consistency.cpr.javapcm.CommitIntegrationJavaPCMChangePropagationSpecification;
import tools.vitruv.framework.propagation.ChangePropagationSpecification;

public class CoronaWarnAppCITest extends AbstractCITest {
	private static final String[] COMMITS = {
			"7e1b610aa3334afb770eebef79ba60120e2169bc", // Version 2.25.0
			"6e97024a789fa4b68dd3a779ae81dadb3a67ab57", // Version 2.26.0
			"c22f9321075eb1a6754afe1917e149380243c835", // Version 2.27.0
			"33d1c90d58ddc25d9b596547acdf8246b51c4287", // Version 2.27.1
			"9323b87169bd54e5fb0015dcc7791d2fb70aa786", // Version 2.28.0
			"206e8c3b5aa25a99694bd157e0856b7d218ac65d", // Version 3.0.0
			"3977e6b06f72aa9585dee36025df9387eb5e9a7e", // Version 3.1.0
			"94bca6a6cf595ba0c9116d7fe1318fdc495a719f" // Version 3.2.0
		};
	
	@BeforeAll
	public static void setUpForAll() {
		JavaParserAndPropagatorUtils.setConfiguration(new JavaParserAndPropagatorUtils.Configuration(false,
				new BuildFileBasedComponentDetectionStrategy()));
	}

	@Override
	protected String getTestPath() {
		return "target" + File.separator + "cwa";
	}

	@Override
	protected String getRepositoryPath() {
		return "https://github.com/corona-warn-app/cwa-server";
	}

	@Override
	protected String getSettingsPath() {
		return "cwa-exec-files" + File.separator + "settings.properties";
	}

	@Override
	protected ChangePropagationSpecification getJavaPCMSpecification() {
		return new CommitIntegrationJavaPCMChangePropagationSpecification();
	}

	@Override
	protected String getReferenceRepositoryModelDirectoryName() {
		return null;
	}
	
	@Test
	@Order(1)
	public void testCwaCommits() throws Exception {
		super.tearDown();
		propagateMultipleCommits(COMMITS);
	}
	
	private void propagateMultipleCommits(String[] commits) throws Exception {
		List<String> successfulCommits = new ArrayList<>();
		String oldCommit = null;
		for (int idx = 0; idx < commits.length; idx++) {
			var newCommit = commits[idx];
			super.setUp();
			boolean result = executePropagationAndEvaluation(oldCommit, newCommit, idx);
			super.tearDown();
			if (result) {
				super.setUp();
				performIndependentEvaluation();
				super.tearDown();
				oldCommit = newCommit;
				successfulCommits.add(oldCommit);
				System.out.println("Successful propagated: " + oldCommit);
			}
		}
	}
}
