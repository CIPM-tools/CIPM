package cipm.consistency.commitintegration.diff.util;

import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.ecore.EObject;

/**
 * A calculator for the Jaccard coefficient based on the comparison of EMF Compare.
 * 
 * @author Martin Armbruster
 */
public final class ComparisonBasedJaccardCoefficientCalculator {
    private ComparisonBasedJaccardCoefficientCalculator() {
    }

    /**
     * Represents the result of the calculation of the Jaccard coefficient.
     * 
     * @author Martin Armbruster
     */
    public static class JaccardCoefficientResult {
        private int unionCardinality;
        private int intersectionCardinality;
        private double jc;
        private List<EObject> oldUnmatched;
        private List<EObject> newUnmatched;

        JaccardCoefficientResult(int union, int intersection, List<EObject> oldUnmatched, List<EObject> newUnmatched) {
            this.unionCardinality = union;
            this.intersectionCardinality = intersection;
            if (union == 0) {
                this.jc = -1;
            } else {
                this.jc = (double) intersection / union;
            }
            this.oldUnmatched = oldUnmatched;
            this.newUnmatched = newUnmatched;
        }

        public int getUnionCardinality() {
            return unionCardinality;
        }

        public int getIntersectionCardinality() {
            return intersectionCardinality;
        }

        public double getJC() {
            return jc;
        }

        public List<EObject> getOldUnmatched() {
            return oldUnmatched;
        }

        public List<EObject> getNewUnmatched() {
            return newUnmatched;
        }
    }

    /**
     * Calculates the Jaccard coefficient for a comparison.
     * 
     * @param result
     *            the comparison.
     * @return the resulting JC.
     */
    public static JaccardCoefficientResult calculateJaccardCoefficient(Comparison result) {
        int unionCardinality = calculateUnionCardinality(result.getMatches());
        List<EObject> leftUnmatched = new ArrayList<>();
        List<EObject> rightUnmatched = new ArrayList<>();
        int intersectionCardinality = calculateIntersectionCardinality(result.getMatches(), leftUnmatched,
                rightUnmatched);
        return new JaccardCoefficientResult(unionCardinality, intersectionCardinality, rightUnmatched, leftUnmatched);
    }

    private static int calculateIntersectionCardinality(List<Match> matches, List<EObject> leftUnmatched,
            List<EObject> rightUnmatched) {
        int card = 0;
        for (Match m : matches) {
            if (m.getLeft() != null && m.getRight() != null) {
                card++;
            } else if (m.getLeft() != null) {
                leftUnmatched.add(m.getLeft());
            } else {
                rightUnmatched.add(m.getRight());
            }
            card += calculateIntersectionCardinality(m.getSubmatches(), leftUnmatched, rightUnmatched);
        }
        return card;
    }

    private static int calculateUnionCardinality(List<Match> matches) {
        int card = 0;
        for (Match m : matches) {
            card += Iterators.size(m.getAllSubmatches()
                .iterator()) + 1;
        }
        return card;
    }
}
