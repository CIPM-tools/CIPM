package cipm.consistency.vsum.test;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * The concrete implementation of {@link AbstractFITestResourceGenerator} for
 * Teammates tests.
 * 
 * @author atora
 */
public class TeammatesFITestResourceGenerator extends AbstractFITestResourceGenerator {
	private static final Logger LOGGER = Logger.getLogger("cipm." + TeammatesFITestResourceGenerator.class.getSimpleName());
	
	@Disabled("Only one test case should run at once. Enable to generate resource")
	@Test
	public void testTeammatesIntegration_0() throws Exception {
		this.initResGen(0);
		this.generateResourcesFor(0);
	}
	
	@Disabled("Only one test case should run at once. Enable to generate resource")
	@Test
	public void testTeammatesIntegration_1() throws Exception {
		this.initResGen(1);
		this.generateResourcesFor(1);
	}

	@Override
	protected String getTestModelFileIdentifier(Object commitHashIdentifier) {
		return String.valueOf(commitHashIdentifier);
	}

	@Override
	protected HasRepoSettings initRepoSettings() {
		return new TeammatesRepoSettings();
	}
}
