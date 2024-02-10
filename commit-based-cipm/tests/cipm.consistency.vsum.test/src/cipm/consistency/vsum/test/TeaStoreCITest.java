package cipm.consistency.vsum.test;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import cipm.consistency.commitintegration.diff.util.ComparisonBasedJaccardCoefficientCalculator;
import cipm.consistency.commitintegration.diff.util.pcm.PCMModelComparator;
import cipm.consistency.vsum.test.TeaStoreRepoSettings.TeaStoreCommitTag;

/**
 * A test class for the TeaStore.
 * 
 * @author Martin Armbruster
 */
public class TeaStoreCITest extends AbstractRepoTest {

	@Override
	protected HasRepoSettings initRepoSettings() {
		return new TeaStoreRepoSettings();
	}
	
	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_0Integration() throws Exception {
		// Integrates TeaStore version 1.0.
		executePropagationAndEvaluation(null, this.getCommitHash(TeaStoreCommitTag.COMMIT_1_0), 0);
//		performIndependentEvaluation();
	}

	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_0To1_1Propagation() throws Exception {
		// Propagation of changes between TeaStore version 1.0 and 1.1.
		executePropagationAndEvaluation(this.getCommitHash(TeaStoreCommitTag.COMMIT_1_0),
				this.getCommitHash(TeaStoreCommitTag.COMMIT_1_1), 1);
//		performIndependentEvaluation();
	}

	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStoreWithMultipleCommits1_0To1_1() throws GitAPIException, IOException, InterruptedException {
		propagateMultipleCommits(this.getCommitHash(TeaStoreCommitTag.COMMIT_1_0),
				this.getCommitHash(TeaStoreCommitTag.COMMIT_1_1));
	}

	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_1Integration() throws Exception {
		// Integrates TeaStore version 1.1.
		executePropagationAndEvaluation(null, this.getCommitHash(TeaStoreCommitTag.COMMIT_1_1), 0);
//		performIndependentEvaluation();
	}

	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_1To1_2Propagation() throws Exception {
		// Propagation of changes between TeaStore version 1.1 and 1.2.
		executePropagationAndEvaluation(this.getCommitHash(TeaStoreCommitTag.COMMIT_1_1),
				this.getCommitHash(TeaStoreCommitTag.COMMIT_1_2), 1);
//		performIndependentEvaluation();
	}

	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStoreWithMultipleCommits1_1To_1_2() throws Exception {
		propagateMultipleCommits(this.getCommitHash(TeaStoreCommitTag.COMMIT_1_1),
				this.getCommitHash(TeaStoreCommitTag.COMMIT_1_2));
	}

	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_2Integration() throws Exception {
		// Integrates TeaStore version 1.2.
		executePropagationAndEvaluation(null, this.getCommitHash(TeaStoreCommitTag.COMMIT_1_2), 0);
//		performIndependentEvaluation();
	}

	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_2To_1_2_1Propagation() throws Exception {
		// Propagation of changes between TeaStore version 1.2 and 1.2.1.
		executePropagationAndEvaluation(this.getCommitHash(TeaStoreCommitTag.COMMIT_1_2),
				this.getCommitHash(TeaStoreCommitTag.COMMIT_1_2_1), 1);
//		performIndependentEvaluation();
	}

	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStoreWithMultipleCommits1_2To1_2_1() throws GitAPIException, IOException, InterruptedException {
		propagateMultipleCommits(this.getCommitHash(TeaStoreCommitTag.COMMIT_1_2),
				this.getCommitHash(TeaStoreCommitTag.COMMIT_1_2_1));
	}

	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_2_1Integration() throws Exception {
		// Integrates TeaStore version 1.2.1.
		executePropagationAndEvaluation(null, this.getCommitHash(TeaStoreCommitTag.COMMIT_1_2_1), 0);
//		performIndependentEvaluation();
	}

	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_2_1To_1_3Propagation() throws Exception {
		// Propagation of changes between TeaStore version 1.2.1 and 1.3.
		executePropagationAndEvaluation(this.getCommitHash(TeaStoreCommitTag.COMMIT_1_2_1),
				this.getCommitHash(TeaStoreCommitTag.COMMIT_1_3), 1);
//		performIndependentEvaluation();
	}

	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStoreWithMultipleCommits1_2_1To1_3() throws GitAPIException, IOException, InterruptedException {
		propagateMultipleCommits(this.getCommitHash(TeaStoreCommitTag.COMMIT_1_2_1),
				this.getCommitHash(TeaStoreCommitTag.COMMIT_1_3));
	}

	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_3Integration() throws Exception {
		// Integrates TeaStore version 1.3.
		executePropagationAndEvaluation(null, this.getCommitHash(TeaStoreCommitTag.COMMIT_1_3), 0);
//		performIndependentEvaluation();
	}

	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_3To_1_3_1Propagation() throws Exception {
		// Propagation of changes between TeaStore version 1.3 and 1.3.1.
		executePropagationAndEvaluation(this.getCommitHash(TeaStoreCommitTag.COMMIT_1_3),
				this.getCommitHash(TeaStoreCommitTag.COMMIT_1_3_1), 1);
//		performIndependentEvaluation();
	}

	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStoreWithMultipleCommits1_3To1_3_1() throws GitAPIException, IOException, InterruptedException {
		propagateMultipleCommits(this.getCommitHash(TeaStoreCommitTag.COMMIT_1_3),
				this.getCommitHash(TeaStoreCommitTag.COMMIT_1_3_1));
	}

	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_3_1Integration() throws Exception {
		// Integrates TeaStore version 1.3.1.
		executePropagationAndEvaluation(null, this.getCommitHash(TeaStoreCommitTag.COMMIT_1_3_1), 0);
//		performIndependentEvaluation();
	}

//	@Test
	public void testTemplateForPCMRepositoryComparison() {
		ResourceSet set = new ResourceSetImpl();
		Resource res1 = set.getResource(URI.createFileURI("path1"), true);
		Resource res2 = set.getResource(URI.createFileURI("path2"), true);
		var comp = PCMModelComparator.compareRepositoryModels(res1, res2);
		var res = ComparisonBasedJaccardCoefficientCalculator.calculateJaccardCoefficient(comp);
		System.out.println(res);
	}
}
