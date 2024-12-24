package cipm.consistency.vsum.test;

import java.io.File;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;

import cipm.consistency.commitintegration.JavaParserAndPropagatorUtils;
import cipm.consistency.commitintegration.detection.TEAMMATESComponentDetectionStrategy;
import cipm.consistency.cpr.javapcm.teammates.TeammatesJavaPCMChangePropagationSpecification;
import tools.vitruv.framework.propagation.ChangePropagationSpecification;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TEAMMATESCITest extends AbstractCITest {
	private static final String COMMIT_TAG_V_8_0_0_RC_0 = "648425746bb9434051647c8266dfab50a8f2d6a3";
	private static final String[] COMMIT_HASHES = {
		COMMIT_TAG_V_8_0_0_RC_0,
		"48b67bae03babf5a5e578aefce47f0285e8de8b4", 
		"83f518e279807dc7eb7023d008a4d1ab290fefee",
		"f33d0bcd5843678b832efd8ee2963e72a95ecfc9",
		"ce4463a8741840fd25a41b14801eab9193c7ed18"
	};
	// This version is the next one after the last commit in COMMIT_HASHES.
	private static final String COMMIT_TAG_V_8_0_0_RC_2 = "8a97db611be37ae1975715723e1913de4fd675e8";

	@BeforeAll
	public static void setUpForAll() {
		JavaParserAndPropagatorUtils.setConfiguration(new JavaParserAndPropagatorUtils.Configuration(false,
				new TEAMMATESComponentDetectionStrategy()));
	}
	
	@Override
	protected String getTestPath() {
		return "target" + File.separator + "TeammatesTest";
	}

	@Override
	protected String getRepositoryPath() {
		return "https://github.com/TEAMMATES/teammates";
	}

	@Override
	protected String getSettingsPath() {
		return "teammates-exec-files" + File.separator + "settings.properties";
	}
	
	@Override
	protected ChangePropagationSpecification getJavaPCMSpecification() {
		return new TeammatesJavaPCMChangePropagationSpecification();
	}

	@Test
	@Order(1)
	public void testTeammatesIntegration() throws Exception {
		// Integrates TEAMMATES version 8.0.0-rc.0.
		executePropagationAndEvaluation(null, COMMIT_HASHES[0], 0);
	}
	
	@Test
	@Order(2)
	public void evaluateTeammatesIntegration() throws Exception {
		performIndependentEvaluation();
	}
	
	@Test
	@Order(3)
	public void testTeammatesFirstPropagation() throws Exception {
		executePropagationAndEvaluation(COMMIT_HASHES[0], COMMIT_HASHES[1], 1);
	}
	
	@Order(4)
	@Test
	public void evaluateTeammatesFirstPropagation() throws Exception {
		performIndependentEvaluation();
	}
	
	@Order(5)
	@Test
	public void testTeammatesSecondPropagation() throws Exception {
		executePropagationAndEvaluation(COMMIT_HASHES[1], COMMIT_HASHES[2], 2);
	}
	
	@Order(6)
	@Test
	public void evaluateTeammatesSecondPropagation() throws Exception {
		performIndependentEvaluation();
	}
	
	@Test
	@Order(7)
	public void testTeammatesThirdPropagation() throws Exception {
		executePropagationAndEvaluation(COMMIT_HASHES[2], COMMIT_HASHES[3], 3);
	}
	
	@Order(8)
	@Test
	public void evaluateTeammatesThirdPropagation2() throws Exception {
		performIndependentEvaluation();
	}
	
	@Order(9)
	@Test
	public void testTeammatesFourthPropagation() throws Exception {
		executePropagationAndEvaluation(COMMIT_HASHES[3], COMMIT_HASHES[4], 4);
	}
	
	@Order(10)
	@Test
	public void evaluateTeammatesFourthPropagation() throws Exception {
		performIndependentEvaluation();
	}
	
	@Override
	protected String getReferenceRepositoryModelDirectoryName() {
		return "TEAMMATES";
	}
}