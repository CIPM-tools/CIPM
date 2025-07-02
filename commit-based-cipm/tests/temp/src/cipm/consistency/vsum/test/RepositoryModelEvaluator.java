package cipm.consistency.vsum.test;

import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.palladiosimulator.pcm.repository.Repository;

import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;

public class RepositoryModelEvaluator {
	public void evaluateRepositoryModel(Path pathToReferenceModel, Repository vsumRepository) {
		if (Files.notExists(pathToReferenceModel)) {
			return;
		}
		var referenceModel = this.loadRepository(pathToReferenceModel);
		
		var result = EvaluationDataContainer.getGlobalContainer().getRepoComparisonResult();
		result.setOldElementsCount(ModelElementsCounter.countModelElements(vsumRepository.eResource()));
		result.setNewElementsCount(ModelElementsCounter.countModelElements(referenceModel.eResource()));
		
		var jc = PCMComparison.compareRepositories(vsumRepository, referenceModel);
		result.setIntersectionCardinality(jc.getIntersectionCardinality());
		result.setUnionCardinality(jc.getUnionCardinality());
		result.setJc(jc.getJC());
	}
	
	private Repository loadRepository(Path repoPath) {
		var resourceSet = new ResourceSetImpl();
		var resource = resourceSet.getResource(URI.createFileURI(repoPath.toAbsolutePath().toString()), true);
		if (resource.getContents().isEmpty()) {
			throw new IllegalStateException("Repository model in '" + repoPath + "' is empty.");
		}
		if (!(resource.getContents().get(0) instanceof Repository)) {
			throw new IllegalStateException("No repository model found in '" + repoPath 
				+ "'. Instead, it contains '" + resource.getContents().get(0).getClass().getCanonicalName() + "'.");
		}
		return (Repository) resource.getContents().get(0);
	}
}
