package cipm.consistency.vsum.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import cipm.consistency.cpr.javapcm.CommitIntegrationJavaPCMChangePropagationSpecification;
import tools.vitruv.framework.propagation.ChangePropagationSpecification;

/**
 * A test class for the TeaStore.
 * 
 * @author Martin Armbruster
 */
@Disabled("Needs to follow manual control.")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TeaStoreCITest extends AbstractCITest {
	private static final Logger LOGGER = Logger.getLogger("cipm." + TeaStoreCITest.class.getSimpleName());
	private static final String COMMIT_TAG_1_0 = "b0d046e178dbaab7e045de57c01795ce5d1dac92";
	private static final String COMMIT_TAG_1_1 = "77733d9c6ab6680c6cc460c631cd408a588a595c";
	private static final String COMMIT_TAG_1_2 = "53c6efa1dca64a87e536d8c5a3dcc3c12ad933b5";
	private static final String COMMIT_TAG_1_2_1 = "f8f13f4390f80d3dc8adb0a6167938a688ddb45e";
	private static final String COMMIT_TAG_1_3 = "745469e55fad8a801a92b0be96dc009acbe7e3fb";
	private static final String COMMIT_TAG_1_3_1 = "de69e957597d20d4be17fc7db2a0aa2fb3a414f7";
	private String interval = "";

	@Override
	protected String getTestPath() {
		return "target" + File.separator + "TeaStoreTest";
	}

	@Override
	protected String getRepositoryPath() {
		return "https://github.com/DescartesResearch/TeaStore";
	}

	@Override
	protected String getSettingsPath() {
		return "teastore-exec-files" + File.separator + "settings.properties";
	}
	
	@Override
	protected String getReferenceRepositoryModelDirectoryName() {
		return "88c4015eef95daf39b60e7c8a8fed1ca4a4f8a57" + File.separator + interval;
	}
	
	@BeforeEach
	public void setUp() {
		// The super.setUp is directly called by the test methods for explicit control. Therefore, nothing should be done.
	}
	
	@AfterEach
	public void tearDown() {
		// The super.tearDown is directly called by the test methods for explicit control. Therefore, nothing should be done.
	}
	
	private void propagateMultipleCommits(String firstCommit, String lastCommit)
			throws Exception {
		List<String> successfulCommits = new ArrayList<>();
		var commits = convertToStringList(this.controller.getCommitChangePropagator().getWrapper()
				.getAllCommitsBetweenTwoCommits(firstCommit, lastCommit));
		commits.add(0, firstCommit);
		int startIndex = 0;
		var oldCommit = commits.get(startIndex);
		successfulCommits.add(oldCommit);
		for (int idx = startIndex + 1; idx < commits.size(); idx++) {
			var newCommit = commits.get(idx);
			super.setUp();
			boolean result = executePropagationAndEvaluation(oldCommit, newCommit, idx);
			super.tearDown();
			if (result) {
				oldCommit = newCommit;
				successfulCommits.add(oldCommit);
				super.setUp();
				performIndependentEvaluation();
				super.tearDown();
				break;
			}
			Thread.sleep(5000);
		}
		for (String c : successfulCommits) {
			LOGGER.debug("Successful propagated: " + c);
		}
	}
	
	private List<String> convertToStringList(List<RevCommit> commits) {
		List<String> result = new ArrayList<>();
		for (RevCommit com : commits) {
			result.add(com.getId().getName());
		}
		return result;
	}

	@Disabled("Not tested")
	@Test
	@Order(1)
	public void testTeaStore1_0Integration() throws Exception {
		// Integrates TeaStore version 1.0.
		this.interval = "I0";
		super.setUp();
		executePropagationAndEvaluation(null, COMMIT_TAG_1_0, 0);
		super.tearDown();
	}
	
	@Disabled("Not tested")
	@Test
	@Order(2)
	public void evaluateTeaStore1_0Integration() throws Exception {
		super.setUp();
		performIndependentEvaluation();
		super.tearDown();
	}

	@Disabled("Not tested")
	@Test
	@Order(3)
	public void testTeaStore1_0To1_1Propagation() throws Exception {
		// Propagation of changes between TeaStore version 1.0 and 1.1.
		super.setUp();
		executePropagationAndEvaluation(COMMIT_TAG_1_0, COMMIT_TAG_1_1, 1);
		super.tearDown();
	}
	
	@Disabled("Not tested")
	@Test
	@Order(4)
	public void evaluateTeaStore1_0To1_1Propagation() throws Exception {
		super.setUp();
		performIndependentEvaluation();
		super.tearDown();
	}

	@Disabled("Not tested")
	@Test
	@Order(5)
	public void testTeaStoreWithMultipleCommits1_0To1_1() throws Exception {
		File target = new File(this.getTestPath());
		FileUtils.deleteDirectory(target);
		FileUtils.copyDirectory(new File(this.getTestPath() + "-0-" + COMMIT_TAG_1_0), target);
		propagateMultipleCommits(COMMIT_TAG_1_0, COMMIT_TAG_1_1);
		FileUtils.deleteDirectory(target);
	}

	@Disabled()
	@Test
	@Order(6)
	public void testTeaStore1_1Integration() throws Exception {
		// Integrates TeaStore version 1.1.
		this.interval = "I1";
		super.setUp();
		executePropagationAndEvaluation(null, COMMIT_TAG_1_1, 0);
		super.tearDown();
	}
	
	@Disabled()
	@Test
	@Order(7)
	public void evaluateTeaStore1_1Integration() throws Exception {
		super.setUp();
		performIndependentEvaluation();
		super.tearDown();
	}

	@Disabled()
	@Test
	@Order(8)
	public void testTeaStore1_1To1_2Propagation() throws Exception {
		// Propagation of changes between TeaStore version 1.1 and 1.2.
		super.setUp();
		executePropagationAndEvaluation(COMMIT_TAG_1_1, COMMIT_TAG_1_2, 1);
		super.tearDown();
	}
	
	@Disabled()
	@Test
	@Order(9)
	public void evaluateTeaStore1_1To1_2Propagation() throws Exception {
		super.setUp();
		performIndependentEvaluation();
		super.tearDown();
	}

	@Disabled()
	@Test
	@Order(10)
	public void testTeaStoreWithMultipleCommits1_1To_1_2() throws Exception {
		File target = new File(this.getTestPath());
		FileUtils.deleteDirectory(target);
		FileUtils.copyDirectory(new File(this.getTestPath() + "-0-" + COMMIT_TAG_1_1), target);
		propagateMultipleCommits(COMMIT_TAG_1_1, COMMIT_TAG_1_2);
		FileUtils.deleteDirectory(target);
	}

	@Disabled()
	@Test
	@Order(11)
	public void testTeaStore1_2Integration() throws Exception {
		// Integrates TeaStore version 1.2.
		this.interval = "I2";
		super.setUp();
		executePropagationAndEvaluation(null, COMMIT_TAG_1_2, 0);
		super.tearDown();
	}
	
	@Disabled()
	@Test
	@Order(12)
	public void evaluateTeaStore1_2Integration() throws Exception {
		super.setUp();
		performIndependentEvaluation();
		super.tearDown();
	}

	@Disabled()
	@Test
	@Order(13)
	public void testTeaStore1_2To_1_2_1Propagation() throws Exception {
		// Propagation of changes between TeaStore version 1.2 and 1.2.1.
		super.setUp();
		executePropagationAndEvaluation(COMMIT_TAG_1_2, COMMIT_TAG_1_2_1, 1);
		super.tearDown();
	}
	
	@Disabled()
	@Test
	@Order(14)
	public void evaluateTeaStore1_2To_1_2_1Propagation() throws Exception {
		super.setUp();
		performIndependentEvaluation();
		super.tearDown();
	}

	@Disabled()
	@Test
	@Order(15)
	public void testTeaStoreWithMultipleCommits1_2To1_2_1() throws Exception {
		File target = new File(this.getTestPath());
		FileUtils.deleteDirectory(target);
		FileUtils.copyDirectory(new File(this.getTestPath() + "-0-" + COMMIT_TAG_1_2), target);
		propagateMultipleCommits(COMMIT_TAG_1_2, COMMIT_TAG_1_2_1);
		FileUtils.deleteDirectory(target);
	}

	@Disabled()
	@Test
	@Order(16)
	public void testTeaStore1_2_1Integration() throws Exception {
		// Integrates TeaStore version 1.2.1.
		this.interval = "I3";
		super.setUp();
		executePropagationAndEvaluation(null, COMMIT_TAG_1_2_1, 0);
		super.tearDown();
	}
	
	@Disabled()
	@Test
	@Order(17)
	public void evaluateTeaStore1_2_1Integration() throws Exception {
		super.setUp();
		performIndependentEvaluation();
		super.tearDown();
	}

	@Disabled()
	@Test
	@Order(18)
	public void testTeaStore1_2_1To_1_3Propagation() throws Exception {
		// Propagation of changes between TeaStore version 1.2.1 and 1.3.
		super.setUp();
		executePropagationAndEvaluation(COMMIT_TAG_1_2_1, COMMIT_TAG_1_3, 1);
		super.tearDown();
	}
	
	@Disabled()
	@Test
	@Order(19)
	public void evaluateTeaStore1_2_1To_1_3Propagation() throws Exception {
		super.setUp();
		performIndependentEvaluation();
		super.tearDown();
	}

	@Disabled()
	@Test
	@Order(20)
	public void testTeaStoreWithMultipleCommits1_2_1To1_3() throws Exception {
		File target = new File(this.getTestPath());
		FileUtils.deleteDirectory(target);
		FileUtils.copyDirectory(new File(this.getTestPath() + "-0-" + COMMIT_TAG_1_2_1), target);
		propagateMultipleCommits(COMMIT_TAG_1_2_1, COMMIT_TAG_1_3);
		FileUtils.deleteDirectory(target);
	}

	@Disabled()
	@Test
	@Order(21)
	public void testTeaStore1_3Integration() throws Exception {
		// Integrates TeaStore version 1.3.
		this.interval = "I4";
		super.setUp();
		executePropagationAndEvaluation(null, COMMIT_TAG_1_3, 0);
		super.tearDown();
	}

	@Disabled()
	@Test
	@Order(22)
	public void evaluateTeaStore1_3Integration() throws Exception {
		super.setUp();
		performIndependentEvaluation();
		super.tearDown();
	}

	@Disabled()
	@Test
	@Order(23)
	public void testTeaStore1_3To_1_3_1Propagation() throws Exception {
		// Propagation of changes between TeaStore version 1.3 and 1.3.1.
		super.setUp();
		executePropagationAndEvaluation(COMMIT_TAG_1_3, COMMIT_TAG_1_3_1, 1);
		super.tearDown();
	}
	
	@Disabled()
	@Test
	@Order(24)
	public void evaluateTeaStore1_3To_1_3_1Propagation() throws Exception {
		super.setUp();
		performIndependentEvaluation();
		super.tearDown();
	}

	@Disabled()
	@Test
	@Order(25)
	public void testTeaStoreWithMultipleCommits1_3To1_3_1() throws Exception {
		File target = new File(this.getTestPath());
		FileUtils.deleteDirectory(target);
		FileUtils.copyDirectory(new File(this.getTestPath() + "-0-" + COMMIT_TAG_1_3), target);
		propagateMultipleCommits(COMMIT_TAG_1_3, COMMIT_TAG_1_3_1);
		FileUtils.deleteDirectory(target);
	}

	@Disabled()
	@Test
	@Order(26)
	public void testTeaStore1_3_1Integration() throws Exception {
		// Integrates TeaStore version 1.3.1.
		this.interval = "I5";
		super.setUp();
		executePropagationAndEvaluation(null, COMMIT_TAG_1_3_1, 0);
		super.tearDown();
	}
	
	@Disabled()
	@Test
	@Order(27)
	public void evaluateTeaStore1_3_1Integration() throws Exception {
		super.setUp();
		performIndependentEvaluation();
		super.tearDown();
	}

	@Override
	protected ChangePropagationSpecification getJavaPCMSpecification() {
		return new CommitIntegrationJavaPCMChangePropagationSpecification();
	}
}
