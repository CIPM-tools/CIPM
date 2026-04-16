package cipm.consistency.commitintegration.diff.util;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.compare.diff.DiffBuilder;
import org.eclipse.emf.compare.diff.DefaultDiffEngine;
import org.eclipse.emf.compare.diff.FeatureFilter;
import org.eclipse.emf.compare.postprocessor.BasicPostProcessorDescriptorImpl;
import org.eclipse.emf.compare.postprocessor.PostProcessorDescriptorRegistryImpl;
import java.util.regex.Pattern;
import java.util.List;
import org.emftext.language.java.JavaPackage;
import org.splevo.jamopp.diffing.scope.PackageIgnoreChecker;
import org.splevo.jamopp.diffing.diff.JaMoPPFeatureFilter;
import org.eclipse.emf.compare.postprocessor.IPostProcessor;

/**
 * This class provides methods for comparing Java models.
 * 
 * @author Timur Saglam
 * @author Ilia Chupakhin
 * @author Martin Armbruster
 */
public final class JavaModelComparator {
    private JavaModelComparator() {
    }

    /**
     * Compares Java models using EMF Compare.
     * 
     * @param newState
     *            contains the new state.
     * @param currentState
     *            contains the current or old state compared to the new state.
     * @param newResources
     *            a list of Resources which represent the new state of Java models. If the newState
     *            is a ResourceSet, this list is used to filter the Resources in the newState.
     * @param currentResources
     *            a list of Resources which represent the current or old state of Java models. If
     *            the currentState is a ResourceSet, this list is used to filter the Resources in
     *            the currentState.
     * @param postProcessor
     *            an optional post processor for the comparison process.
     * @return the comparison result.
     */
    public static Comparison compareJavaModels(Notifier newState, Notifier currentState, List<Resource> newResources,
            List<Resource> currentResources, IPostProcessor postProcessor) {

        var scope = new ResourceListFilteringComparisonScope(newState, currentState, newResources, currentResources);
        scope.getNsURIs()
            .add(JavaPackage.eNS_URI);

        var jamoppFeatureFilter = new JaMoPPFeatureFilter(new PackageIgnoreChecker(List.of()));
        var diffProcessor = new DiffBuilder();
        var diffEngine = new DefaultDiffEngine(diffProcessor) {
            @Override
            protected FeatureFilter createFeatureFilter() {
                return jamoppFeatureFilter;
            }
        };

        var engineRegistry = HierarchicalMatchEngineFactoryGenerator
            .generateMatchEngineRegistry(JavaMatchEngineFactoryGenerator.generateMatchEngineFactory());

        var builder = EMFCompare.builder()
            .setMatchEngineFactoryRegistry(engineRegistry)
            .setDiffEngine(diffEngine);

        if (postProcessor != null) {
            var processorDescriptor = new BasicPostProcessorDescriptorImpl(postProcessor, Pattern.compile(".*"), null);
            var processorRegistry = new PostProcessorDescriptorRegistryImpl<String>();
            processorRegistry.put("java", processorDescriptor);
            builder.setPostProcessorRegistry(processorRegistry);
        }

        return builder.build()
            .compare(scope);
    }
}
