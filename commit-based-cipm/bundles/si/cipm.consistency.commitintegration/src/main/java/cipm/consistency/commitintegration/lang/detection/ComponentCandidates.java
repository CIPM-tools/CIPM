package cipm.consistency.commitintegration.lang.detection;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Stores the component candidates.
 * 
 * @author Martin Armbruster
 */
public class ComponentCandidates {
    private EnumMap<ComponentState, Map<String, Set<Resource>>> candidates;

    public ComponentCandidates() {
        candidates = new EnumMap<>(ComponentState.class);
        for (var value : ComponentState.values()) {
            candidates.put(value, new HashMap<>());
        }
    }

    /**
     * Adds a classifier in a module.
     * 
     * @param state
     *            state of the module.
     * @param moduleName
     *            name of the module.
     * @param cu
     *            Resource with the classifier in the module.
     */
    // TODO Burgey: the java stuff uses this, but i don't need it, so im not refactoring it.
    public void addModuleClassifier(ComponentState state, String moduleName, Resource cu) {
        Map<String, Set<Resource>> classMap = getModulesInState(state);
        Set<Resource> classSet;
        if (classMap.containsKey(moduleName)) {
            classSet = classMap.get(moduleName);
        } else {
            classSet = new HashSet<>();
            classMap.put(moduleName, classSet);
        }
        classSet.add(cu);
    }

    /**
     * Returns all modules in a certain state.
     * 
     * @param state
     *            the state for which all modules are returned.
     * @return all modules in the specified state.
     */
    public Map<String, Set<Resource>> getModulesInState(ComponentState state) {
        return candidates.get(state);
    }

    /**
     * Updates the state of a module.
     * 
     * @param oldState
     *            the old state of the module.
     * @param newState
     *            the new state of the module.
     * @param moduleName
     *            the name of the module.
     */
    public void updateState(ComponentState oldState, ComponentState newState, String moduleName) {
        Map<String, Set<Resource>> map = getModulesInState(oldState);
        Set<Resource> classes = map.remove(moduleName);
        map = getModulesInState(newState);
        map.put(moduleName, classes);
    }

    /**
     * Removes a module.
     * 
     * @param state
     *            state of the module.
     * @param moduleName
     *            name of the module.
     */
    public void removeModule(ComponentState state, String moduleName) {
        Map<String, Set<Resource>> map = getModulesInState(state);
        map.remove(moduleName);
    }

    /**
     * Determines the state of a module.
     * 
     * @param modName
     *            name of the module.
     * @return the state of the module.
     */
    public ComponentState getStateOfModule(String modName) {
        for (var entry : candidates.entrySet()) {
            for (var subEntry : entry.getValue()
                .entrySet()) {
                if (subEntry.getKey()
                    .equals(modName)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        candidates.entrySet()
            .forEach(e -> {
                var stateString = String.format("%s:\n\t%s", e.getKey()
                    .toString(),
                        e.getValue()
                            .keySet()
                            .stream()
                            .collect(Collectors.joining(", ")));
                sb.append(stateString + "\n");
            });
        return sb.toString();
    }
}
