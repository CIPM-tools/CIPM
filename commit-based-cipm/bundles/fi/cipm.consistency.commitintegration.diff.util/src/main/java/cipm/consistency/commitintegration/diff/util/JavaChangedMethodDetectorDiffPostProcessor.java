package cipm.consistency.commitintegration.diff.util;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.postprocessor.IPostProcessor;
import org.emftext.language.java.members.Method;

/**
 * A post processor for comparing Java models to detect changed methods.
 * 
 * @author Martin Armbruster
 */
public class JavaChangedMethodDetectorDiffPostProcessor implements IPostProcessor {
    private Set<Method> changedMethods = new HashSet<>();

    /**
     * Returns a set with all changed methods.
     * 
     * @return the set of changed methods.
     */
    public Set<Method> getChangedMethods() {
        return changedMethods;
    }

    @Override
    public void postMatch(Comparison comparison, Monitor monitor) {
    }

    @Override
    public void postDiff(Comparison comparison, Monitor monitor) {
        comparison.getMatches()
            .forEach(this::checkForMethods);
    }

    private void checkForMethods(Match m) {
        if (m.getLeft() instanceof Method && m.getRight() instanceof Method) {
            if (hasDifferences(m)) {
                changedMethods.add((Method) m.getRight());
            }
        } else {
            m.getSubmatches()
                .forEach(this::checkForMethods);
        }
    }

    private boolean hasDifferences(Match m) {
        return m.getDifferences()
            .size() > 0 || m.getSubmatches()
                .stream()
                .map(this::hasDifferences)
                .reduce(false, (a, b) -> a || b);
    }

    @Override
    public void postRequirements(Comparison comparison, Monitor monitor) {
    }

    @Override
    public void postEquivalences(Comparison comparison, Monitor monitor) {
    }

    @Override
    public void postConflicts(Comparison comparison, Monitor monitor) {
    }

    @Override
    public void postComparison(Comparison comparison, Monitor monitor) {
    }
}
