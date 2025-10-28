package cipm.consistency.vsum.test.pcm.experiment;

import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.Test;

import cipm.consistency.commitintegration.diff.util.ComparisonBasedJaccardCoefficientCalculator;
import cipm.consistency.commitintegration.diff.util.JavaModelComparator;
import cipm.consistency.commitintegration.diff.util.ComparisonBasedJaccardCoefficientCalculator.JaccardCoefficientResult;
import cipm.consistency.commitintegration.diff.util.pcm.PCMModelComparator;

public class PcmToJavaChangePropagationTest {
	private PcmToJavaChangePropagationDirLayout dirLayout;

	@Test
	public void pcmToJavaChangePropagationTestTemplate() {
		/*
		 * Before test: Run the existing Teammates tests (via
		 * TeammatesChangeGeneratingTest) that propagate Java to PCM (and Java -> PCM ->
		 * IM). Let them evaluate their results as per usual. Save the saved Java and
		 * PCM changes.
		 * 
		 * TODO: Evaluate Jaccard Coefficient for old/newPcmRepoModel.
		 */

		/*
		 * TODO
		 * 
		 * During test: Propagate all PCM changes to oldPcmRepoModel; which results in
		 * newPcmRepoModel, newJavaCodeModel, newImModel.
		 */

		/*
		 * TODO
		 * 
		 * After test: Compute Jaccard Coefficient for old/newjavaCodeModel and Jaccard
		 * Coefficient for old/newPcmRepoModel. Compute F1-Score for old/newImModel.
		 * Compare with results from existing Java -> PCM (-> IM) tests.
		 */
	}

	private JaccardCoefficientResult computeJCForJavaInPcmToJavaPropagation(Resource newJavaModel,
			Resource oldJavaModel) {
		return ComparisonBasedJaccardCoefficientCalculator.calculateJaccardCoefficient(
				JavaModelComparator.compareJavaModels(newJavaModel, oldJavaModel, null, null, null));
	}

	private JaccardCoefficientResult computeJCForPcmInPcmToJavaPropagation(Resource newPcmRepo, Resource oldPcmRepo) {
		return ComparisonBasedJaccardCoefficientCalculator
				.calculateJaccardCoefficient(PCMModelComparator.compareRepositoryModels(newPcmRepo, oldPcmRepo));
	}
}
