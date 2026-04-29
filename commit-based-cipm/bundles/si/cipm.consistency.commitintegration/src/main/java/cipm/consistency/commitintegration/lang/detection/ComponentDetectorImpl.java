package cipm.consistency.commitintegration.lang.detection;

import cipm.consistency.commitintegration.lang.detection.strategy.ComponentDetectionStrategy;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.emf.ecore.resource.ResourceSet;
import tools.vitruv.change.interaction.InternalUserInteractor;
import tools.vitruv.change.interaction.UserInteractionFactory;

public class ComponentDetectorImpl implements ComponentDetector {
    protected Set<ComponentDetectionStrategy> strategies = new HashSet<>();

    @Override
    public void addComponentDetectionStrategy(ComponentDetectionStrategy strategy) {
        strategies.add(strategy);
    }

    /**
     * Detects the components of a resourceset based on the detection strategies
     */
    @Override
    public ComponentCandidates detectModuleCandidates(ResourceSet resourceSet, Path projectRoot) {
        var candidates = new ComponentCandidates();

        for (var resource : resourceSet.getResources()) {
            for (var strategy : strategies) {
                strategy.detectComponent(resource, projectRoot, candidates);
            }
        }

        return candidates;
    }

    private void updateConfig(ComponentStatePreferences config, ComponentCandidates candidates, ComponentState state) {
        var candidateMap = new HashMap<>(candidates.getModulesInState(state));
        candidateMap.forEach((k, v) -> config.getModuleClassification()
            .put(k, state));
    }

    protected List<ComponentState> getEnabledModuleStates() {
        return List.of(ComponentState.REGULAR_COMPONENT, ComponentState.PART_OF_COMPONENT, ComponentState.NO_COMPONENT);
    }

    protected void resolveModuleCandidates(ComponentCandidates candidates, Path configPath) {
        InternalUserInteractor userInteractor = UserInteractionFactory.instance.createDialogUserInteractor();

        // Apply the stored configuration on the found modules.
        var modCandidates = new HashMap<>(candidates.getModulesInState(ComponentState.COMPONENT_CANDIDATE));

        ComponentStatePreferences config = new ComponentStatePreferences(configPath);
        // Decide the state for component candidates.
        modCandidates.forEach((k, v) -> {
            if (config.getModuleClassification()
                .containsKey(k)) {
                candidates.updateState(ComponentState.COMPONENT_CANDIDATE, config.getModuleClassification()
                    .get(k), k);
            }
        });
        modCandidates = new HashMap<>(candidates.getModulesInState(ComponentState.PART_OF_COMPONENT));
        // Merge modules which are part of other modules.
        modCandidates.forEach((k, v) -> {
            if (config.getSubModuleMapping()
                .containsKey(k)) {
                candidates.removeModule(ComponentState.PART_OF_COMPONENT, k);
                String otherMod = config.getSubModuleMapping()
                    .get(k);
                candidates.getModulesInState(candidates.getStateOfModule(otherMod))
                    .get(otherMod)
                    .addAll(v);
            }
        });
        // The configuration is reset to the current state to exclude removed modules.
        config.clear();
        updateConfig(config, candidates, ComponentState.MICROSERVICE_COMPONENT);
        updateConfig(config, candidates, ComponentState.REGULAR_COMPONENT);
        updateConfig(config, candidates, ComponentState.NO_COMPONENT);
        // Ask the developer to decide the type of the remaining component candidates.

        var stateList = getEnabledModuleStates();
        var stateNames = stateList.stream()
            .map(s -> s.toString())
            .collect(Collectors.toList());
        modCandidates = new HashMap<>(candidates.getModulesInState(ComponentState.COMPONENT_CANDIDATE));
        modCandidates.forEach((k, v) -> {
            int r = userInteractor.getSingleSelectionDialogBuilder()
                .message("Detected the potential component / module: " + k + "\nWhich type of a component is it?")
//                .choices(List.of("Microservice component", "Regular component", "Part of another component",
//                        "No component"))
                .choices(stateNames)
                .startInteraction();
            ComponentState newState;
            if (0 <= r && r < stateList.size()) {
                newState = stateList.get(r);
            } else {
                newState = ComponentState.NO_COMPONENT;
            }
            candidates.updateState(ComponentState.COMPONENT_CANDIDATE, newState, k);
            config.getModuleClassification()
                .put(k, newState);
        });

        // Ask the developer to decide which module is part of which other module.
        modCandidates = new HashMap<>(candidates.getModulesInState(ComponentState.PART_OF_COMPONENT));
        modCandidates.forEach((k, v) -> {
            ArrayList<String> allPossibleModules = new ArrayList<>();
            allPossibleModules.addAll(candidates.getModulesInState(ComponentState.MICROSERVICE_COMPONENT)
                .keySet());
            int mscSize = allPossibleModules.size();
            allPossibleModules.addAll(candidates.getModulesInState(ComponentState.REGULAR_COMPONENT)
                .keySet());
            int r = userInteractor.getSingleSelectionDialogBuilder()
                .message("The component / module candidate " + k + " is part of which component / module?")
                .choices(allPossibleModules)
                .startInteraction();
            String newMod = allPossibleModules.get(r);
            candidates.removeModule(ComponentState.PART_OF_COMPONENT, k);
            ComponentState stateToUse;
            if (r < mscSize) {
                stateToUse = ComponentState.MICROSERVICE_COMPONENT;
            } else {
                stateToUse = ComponentState.REGULAR_COMPONENT;
            }
            candidates.getModulesInState(stateToUse)
                .get(newMod)
                .addAll(v);
            config.getSubModuleMapping()
                .put(k, newMod);
        });
        try {
            config.save();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ComponentCandidates detectModules(ResourceSet resourceSet, Path projectRoot, Path configPath) {
        var candidates = detectModuleCandidates(resourceSet, projectRoot);
        resolveModuleCandidates(candidates, configPath);
        return candidates;
    }
}