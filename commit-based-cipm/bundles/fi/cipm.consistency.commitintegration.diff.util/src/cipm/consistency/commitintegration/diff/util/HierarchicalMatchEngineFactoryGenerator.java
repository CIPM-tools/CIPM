package cipm.consistency.commitintegration.diff.util;

import java.util.List;

import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.eobject.EqualityHelperExtensionProvider;
import org.eclipse.emf.compare.match.eobject.EqualityHelperExtensionProviderDescriptorRegistryImpl;
import org.eclipse.emf.compare.rcp.EMFCompareRCPPlugin;
import org.eclipse.emf.compare.utils.EqualityHelper;
import org.splevo.diffing.match.HierarchicalMatchEngineFactory;
import org.splevo.diffing.match.HierarchicalStrategyResourceMatcher;
import org.splevo.jamopp.diffing.match.JaMoPPEqualityStrategy;
import org.splevo.jamopp.diffing.match.JaMoPPIgnoreStrategy;
import org.splevo.jamopp.diffing.scope.PackageIgnoreChecker;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityChecker;

import com.google.common.cache.CacheBuilder;

/**
 * A generator for HierarchicalMatchEngineFactories.
 * 
 * @author Martin Armbruster
 */
public final class HierarchicalMatchEngineFactoryGenerator {
	private HierarchicalMatchEngineFactoryGenerator() {
	}
	
	/**
	 * Generates a HierarchicalMatchEngineFactory.
	 * 
	 * @param simChecker the SimilarityChecker which is internally used to compare model elements.
	 * @param key a key to identify the compared models.
	 * @return the generated factory.
	 */
	public static HierarchicalMatchEngineFactory generateMatchEngineFactory(ISimilarityChecker simChecker, String key) {
		EqualityHelperExtensionProvider.Descriptor.Registry descRegistryImpl =
				EqualityHelperExtensionProviderDescriptorRegistryImpl.createStandaloneInstance();
		descRegistryImpl.put(key, new SimilarityCheckerBasedEqualityHelperExtensionProviderDescriptor(simChecker));
		return new HierarchicalMatchEngineFactory(
				new EqualityHelper(EqualityHelper.createDefaultCache(
						CacheBuilder.newBuilder()), descRegistryImpl),
				new JaMoPPEqualityStrategy(simChecker),
				new JaMoPPIgnoreStrategy(new PackageIgnoreChecker(List.of())),
				new HierarchicalStrategyResourceMatcher());
	}
	
	/**
	 * Generates a registry with a specific match engine.
	 * 
	 * @param engineFactory the MatchEngineFactory which is registered in the generated registry.
	 * @return the generated registry.
	 */
	public static IMatchEngine.Factory.Registry generateMatchEngineRegistry(HierarchicalMatchEngineFactory engineFactory) {
		engineFactory.setRanking(20);
		var engineRegistry = EMFCompareRCPPlugin.getDefault().getMatchEngineFactoryRegistry();
		engineRegistry.add(engineFactory);
		return engineRegistry;
	}
}
