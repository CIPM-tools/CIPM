package cipm.consistency.vsum.test;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class TEAMMATESCITest extends AbstractRepoTest {
	@Override
	protected HasRepoSettings initRepoSettings() {
		return new TeammatesRepoSettings();
	}
	
	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeammatesIntegration() throws Exception {
		// Integrates TEAMMATES version 8.0.0-rc.0.
		executePropagationAndEvaluation(null, this.getCommitHash(0), 0);
//		performIndependentEvaluation();
	}
	
	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeammatesFirstPropagation() throws Exception {
		executePropagationAndEvaluation(this.getCommitHash(0), this.getCommitHash(1), 1);
//		performIndependentEvaluation();
	}
	
	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeammatesSecondPropagation() throws Exception {
		executePropagationAndEvaluation(this.getCommitHash(1), this.getCommitHash(2), 1);
//		performIndependentEvaluation();
	}
	
	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeammatesThirdPropagation() throws Exception {
		executePropagationAndEvaluation(this.getCommitHash(2), this.getCommitHash(3), 1);
//		performIndependentEvaluation();
	}
	
	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeammatesFourthPropagation() throws Exception {
		executePropagationAndEvaluation(this.getCommitHash(3), this.getCommitHash(4), 1);
//		performIndependentEvaluation();
	}
}