package cipm.consistency.commitintegration.diff.util.pcm;

import org.splevo.diffing.match.HierarchicalMatchEngineFactory;
import org.splevo.jamopp.diffing.similarity.base.MapSimilarityToolboxFactory;

import cipm.consistency.commitintegration.diff.util.HierarchicalMatchEngineFactoryGenerator;

/**
 * A generator for HierarchicalMatchEngineFactories specific for PCM repository
 * models.
 * 
 * @author Martin Armbruster
 */
public final class PCMRepositoryMatchEngineFactoryGenerator {
	private PCMRepositoryMatchEngineFactoryGenerator() {
	}

	/**
	 * Generates a HierarchicalMatchEngineFactory specific for PCM repository
	 * models.
	 * 
	 * @return the generated factory.
	 */
	public static HierarchicalMatchEngineFactory generateMatchEngineFactory() {
		var builder = new PCMRepositorySimilarityToolboxBuilder();
		builder.setSimilarityToolboxFactory(new MapSimilarityToolboxFactory());
		
		var toolbox = builder.instantiate()
			.buildComparisonPairs()
			.build();
		
		return HierarchicalMatchEngineFactoryGenerator.generateMatchEngineFactory(new PCMRepositorySimilarityChecker(toolbox),
				"repository");
	}

	/**
	 * Generates a HierarchicalMatchEngineFactory specific for PCM repository models
	 * including the IDs of elements.
	 * 
	 * @return the generated factory.
	 */
	public static HierarchicalMatchEngineFactory generateIDBasedMatchEngineFactory() {
		var builder = new PCMRepositorySimilarityToolboxBuilder();
		builder.setSimilarityToolboxFactory(new MapSimilarityToolboxFactory());
		
		var toolbox = builder.instantiate()
			.buildIDBasedComparisonPairs()
			.build();
		
		return HierarchicalMatchEngineFactoryGenerator
				.generateMatchEngineFactory(new PCMRepositorySimilarityChecker(toolbox), "repository");
	}
}
