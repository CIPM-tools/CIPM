package cipm.consistency.vsum.test;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.palladiosimulator.pcm.repository.Repository;

import cipm.consistency.commitintegration.diff.util.ComparisonBasedJaccardCoefficientCalculator;
import cipm.consistency.commitintegration.diff.util.ComparisonBasedJaccardCoefficientCalculator.JaccardCoefficientResult;
import cipm.consistency.commitintegration.diff.util.pcm.PCMModelComparator;

/**
 * This class supports the comparison of PCM models by providing appropriate utility methods.
 * 
 * @author Martin Armbruster
 */
public class PCMComparison {
	private PCMComparison() {}
	
	/**
	 * Compares two PCM Repository models.
	 * 
	 * @param path1 File path to the first PCM Repository model.
	 * @param path2 File path to the second PCM Repository model.
	 * @return The result of the Repository model comparison.
	 */
	public static JaccardCoefficientResult compareRepositories(String path1, String path2) {
		ResourceSet set = new ResourceSetImpl();
		Resource res1 = set.getResource(URI.createFileURI(path1), true);
		Resource res2 = set.getResource(URI.createFileURI(path2), true);
		return compareRepositories((Repository) res1.getContents().get(0), (Repository) res2.getContents().get(0));
	}
	
	public static JaccardCoefficientResult compareRepositories(Repository repo1, Repository repo2) {
		var comp = PCMModelComparator.compareRepositoryModels(repo1, repo2);
		return ComparisonBasedJaccardCoefficientCalculator.calculateJaccardCoefficient(comp);
	}
}
