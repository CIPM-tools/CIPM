package cipm.consistency.commitintegration.diff.util.pcm;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.EMFCompare;
import org.palladiosimulator.pcm.repository.RepositoryPackage;
import org.splevo.diffing.match.HierarchicalMatchEngineFactory;

import cipm.consistency.commitintegration.diff.util.HierarchicalMatchEngineFactoryGenerator;
import cipm.consistency.commitintegration.diff.util.ResourceListFilteringComparisonScope;

/**
 * This class enables the comparison of PCM repository models.
 * 
 * @author Martin Armbruster
 */
public final class PCMModelComparator {
    private PCMModelComparator() {
    }

    /**
     * Compares PCM repository models using EMF Compare.
     * 
     * @param newState
     *            contains the new state: a repository or a repository resource.
     * @param currentState
     *            contains the current or old state compared to the new state: a repository or a
     *            repository resource.
     * @return the result of the comparison.
     */
    public static Comparison compareRepositoryModels(Notifier newState, Notifier currentState) {
        return internalCompareRepositoryModels(newState, currentState,
                PCMRepositoryMatchEngineFactoryGenerator.generateMatchEngineFactory());
    }

    /**
     * Compares PCM repository models using EMF Compare and considering the IDs of elements.
     * 
     * @param newState
     *            contains the new state: a repository or a repository resource.
     * @param currentState
     *            containes the current or old state compared to the new state: a repository or a
     *            repository resource.
     * @return the result of the comparison.
     */
    public static Comparison compareRepositoryModelsIDBased(Notifier newState, Notifier currentState) {
        return internalCompareRepositoryModels(newState, currentState,
                PCMRepositoryMatchEngineFactoryGenerator.generateIDBasedMatchEngineFactory());
    }

    private static Comparison internalCompareRepositoryModels(Notifier newState, Notifier currentState,
            HierarchicalMatchEngineFactory engineFactory) {
        var scope = new ResourceListFilteringComparisonScope(newState, currentState, null, null);
        scope.getNsURIs()
            .add(RepositoryPackage.eNS_URI);

        return EMFCompare.builder()
            .setMatchEngineFactoryRegistry(
                    HierarchicalMatchEngineFactoryGenerator.generateMatchEngineRegistry(engineFactory))
            .build()
            .compare(scope);
    }
}
