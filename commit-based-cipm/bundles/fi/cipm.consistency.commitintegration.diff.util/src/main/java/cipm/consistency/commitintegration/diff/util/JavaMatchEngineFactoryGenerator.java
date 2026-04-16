package cipm.consistency.commitintegration.diff.util;

import org.splevo.diffing.match.HierarchicalMatchEngineFactory;
import org.splevo.jamopp.diffing.similarity.SimilarityChecker;

/**
 * A generator for HierarchicalMatchEngineFactories specific to Java models.
 * 
 * @author Martin Armbruster
 */
public final class JavaMatchEngineFactoryGenerator {
    private JavaMatchEngineFactoryGenerator() {
    }

    /**
     * Generates the HierarchicalMatchEngineFactory.
     * 
     * @return the generated factory.
     */
    public static HierarchicalMatchEngineFactory generateMatchEngineFactory() {
        return HierarchicalMatchEngineFactoryGenerator.generateMatchEngineFactory(new SimilarityChecker(), "javaxmi");
    }
}
