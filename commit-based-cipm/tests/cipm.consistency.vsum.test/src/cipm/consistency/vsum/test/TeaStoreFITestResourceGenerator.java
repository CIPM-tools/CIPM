package cipm.consistency.vsum.test;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import cipm.consistency.vsum.test.TeaStoreRepoSettings.TeaStoreCommitTag;

/**
 * The concrete implementation of {@link AbstractFITestResourceGenerator} for
 * TeaStore tests.
 * 
 * @author atora
 */
public class TeaStoreFITestResourceGenerator extends AbstractFITestResourceGenerator {
	private static final Logger LOGGER = Logger.getLogger("cipm." + TeaStoreFITestResourceGenerator.class.getSimpleName());
	
	@Disabled("Only one test case should run at once. Enable to generate resource")
	@Test
	public void testTeaStore1_0Integration() throws Exception {
		this.initResGen(TeaStoreCommitTag.COMMIT_1_0);
		this.generateResourcesFor(TeaStoreCommitTag.COMMIT_1_0);
	}

	@Disabled("Only one test case should run at once. Enable to generate resource")
	@Test
	public void testTeaStore1_1Integration() throws Exception {
		this.initResGen(TeaStoreCommitTag.COMMIT_1_1);
		this.generateResourcesFor(TeaStoreCommitTag.COMMIT_1_1);
	}

	@Override
	protected String getTestModelFileIdentifier(Object commitHashIdentifier) {
		return ((TeaStoreCommitTag) commitHashIdentifier).getTagName();
	}

	@Override
	protected HasRepoSettings initRepoSettings() {
		return new TeaStoreRepoSettings();
	}
}
