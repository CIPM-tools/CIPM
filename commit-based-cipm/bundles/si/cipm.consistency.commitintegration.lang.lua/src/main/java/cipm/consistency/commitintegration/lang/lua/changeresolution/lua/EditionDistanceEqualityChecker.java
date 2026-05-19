package cipm.consistency.commitintegration.lang.lua.changeresolution.lua;

import java.util.ArrayList;

import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.eobject.EditionDistance;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.eobject.ProximityEObjectMatcher;
import org.eclipse.emf.compare.match.eobject.ProximityEObjectMatcher.DistanceFunction;
import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Lists;

public class EditionDistanceEqualityChecker {
    private IComparisonFactory comparisonFactory;
    private DistanceFunction distanceFunction;
    private IEObjectMatcher eObjectMatcher;

    public EditionDistanceEqualityChecker() {
//        var equalityHelperFactory = new DefaultEqualityHelperFactory();

//        comparisonFactory = new DefaultComparisonFactory(() -> equalityHelper);
        comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory());
        // TODO the edition distance is not cutting it with its default
        // settings. Try to use its builder to make it work
        distanceFunction = EditionDistance.builder()
            .uri(0)
            .build();
//        distanceFunction = new EditionDistance();
        eObjectMatcher = new ProximityEObjectMatcher(distanceFunction);
    }

    /**
     * Do objects match based on their edition distance
     * @param left
     * @param right
     * @return
     */
    public Boolean match(EObject left, EObject right) {
        var comparison = comparisonFactory.createComparison();
        comparison.setThreeWay(false);

//        var comparison = equalityHelper.getTarget();

        var monitor = new BasicMonitor();

        var leftIterator = Lists.newArrayList(left)
            .iterator();
        var rightIterator = Lists.newArrayList(right)
            .iterator();
        var originIterator = new ArrayList<EObject>().iterator();

        eObjectMatcher.createMatches(comparison, leftIterator, rightIterator, originIterator, monitor);

        // we search for matches of the right object in the comparison
        var rightMatch = comparison.getMatch(right);

        return rightMatch.getLeft() != null && rightMatch.getRight() != null;
    }
}
