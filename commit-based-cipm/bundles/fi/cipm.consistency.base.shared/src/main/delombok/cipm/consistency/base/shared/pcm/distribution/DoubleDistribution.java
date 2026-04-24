package cipm.consistency.base.shared.pcm.distribution;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;

/**
 * Represents a double distribution which can be converted into a PCM stochastic expression.
 * 
 * @author David Monschein
 *
 */
public class DoubleDistribution {
    private static final int MAX_SIZE_DEFAULT = 500;

    /**
     * Counts the occurrences of the certain numbers.
     */
    private Map<Double, Long> occs;
    private int precision;

    private double sum;
    private int occSum;

    private int maxSize;

    /**
     * Creates a new distribution with a given accuracy.
     * 
     * @param precision
     *            the precision, expressed in digits after the decimal point
     */
    public DoubleDistribution(int precision) {
        this(precision, MAX_SIZE_DEFAULT);
    }

    public DoubleDistribution(int precision, int maxSize) {
        this.occs = new TreeMap<>();
        this.precision = precision;
        this.sum = 0;
        this.occSum = 0;
        this.maxSize = maxSize;
    }

    /**
     * Adds a new number to the distribution.
     * 
     * @param d
     *            number to add
     */
    public void put(double d) {
        double r = roundTo(d, precision);
        if (!occs.containsKey(r)) {
            occs.put(r, 1L);
        } else {
            occs.put(r, occs.get(r) + 1);
        }

        sum += d;
        occSum++;
    }

    /**
     * Multiplies the whole distribution with a given number.
     * 
     * @param factor
     *            the number to multiply the distribution with
     */
    public void multiply(double factor) {
        Map<Double, Long> copy = new HashMap<>();
        occs.entrySet()
            .forEach(et -> copy.put(roundTo(et.getKey() * factor, precision), et.getValue()));
        this.occs = copy;
    }

    /**
     * Divides the whole distribution by a given number.
     * 
     * @param factor
     *            the number to divide
     */
    public void divide(double factor) {
        this.multiply(1 / factor);
    }

    /**
     * Converts the distribution to a stochastic expression that can be used within PCM models.
     * 
     * @return stochastic expression
     */
    public PCMRandomVariable toStoex() {
        if (occs.size() == 1) {
            return buildDoubleLiteral(occs.entrySet()
                .stream()
                .findFirst()
                .get()
                .getKey());
        } else {
            double sum = occs.entrySet()
                .stream()
                .mapToLong(l -> l.getValue())
                .sum();
            StringBuilder builder = new StringBuilder();
            builder.append("DoublePMF[");
            for (Entry<Double, Long> entry : occs.entrySet()) {
                double prob = (double) entry.getValue() / sum;
                builder.append("(");
                builder.append(String.valueOf(entry.getKey()));
                builder.append(";");
                builder.append(String.valueOf(roundDouble(prob, 10)));
                builder.append(")");
            }
            builder.append("]");
            return buildPCMVar(builder.toString());
        }
    }

    private double roundDouble(double val, int chars) {
        double factor = Math.pow(10, chars);
        return Math.round(val * factor) / factor;
    }

    private PCMRandomVariable buildDoubleLiteral(double value) {
        return buildPCMVar(String.valueOf(value));
    }

    private PCMRandomVariable buildPCMVar(String value) {
        PCMRandomVariable ret = CoreFactory.eINSTANCE.createPCMRandomVariable();
        ret.setSpecification(value);
        return ret;
    }

    private double roundTo(double d, int precision) {
        double factor = Math.pow(10, precision);
        return Math.round(d * factor) / factor;
    }

    public double average() {
        return sum / (double) occSum;
    }

}