package cipm.consistency.base.shared.pcm.distribution;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;

/**
 * Represents a distribution of enum values represented as strings. The distribution can be
 * converted to a stochastic expression and used within PCM models.
 * 
 * @author David Monschein
 *
 */
public class EnumDistribution {
    /**
     * Number of occurences.
     */
    private Map<String, Long> distribution;

    /**
     * Creates a new, empty distribution.
     */
    public EnumDistribution() {
        this.distribution = new HashMap<>();
    }

    /**
     * Adds a new value to the distribution.
     * 
     * @param value
     *            the enum value
     * @param val
     *            the number of occurences
     */
    public void push(String value, long val) {
        if (!distribution.containsKey(value)) {
            distribution.put(value, val);
        } else {
            distribution.put(value, distribution.get(value) + val);
        }
    }

    /**
     * Adds a new value that occured once to the distribution.
     * 
     * @param value
     *            the enum value
     */
    public void push(String value) {
        this.push(value, 1L);
    }

    /**
     * Adds another enum distribution to this instance.
     * 
     * @param other
     *            the enum distribution to add
     */
    public void push(EnumDistribution other) {
        other.distribution.entrySet()
            .forEach(v -> this.push(v.getKey(), v.getValue()));
    }

    /**
     * Converts this distribution to a stochastic expression.
     * 
     * @return the generated stochastic expression
     */
    public PCMRandomVariable toStoex() {
        if (distribution.size() == 1) {
            return buildStringLiteral(distribution.entrySet()
                .stream()
                .findFirst()
                .get()
                .getKey());
        } else {
            long sum = distribution.entrySet()
                .stream()
                .mapToLong(l -> l.getValue())
                .sum();
            StringBuilder builder = new StringBuilder();
            builder.append("EnumPMF[");
            for (Entry<String, Long> entry : distribution.entrySet()) {
                double prob = (double) entry.getValue() / (double) sum;
                builder.append("(\"");
                builder.append(String.valueOf(entry.getKey()));
                builder.append("\";");
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

    private PCMRandomVariable buildStringLiteral(String key) {
        return buildPCMVar("\"" + key + "\"");
    }

    private PCMRandomVariable buildPCMVar(String value) {
        PCMRandomVariable ret = CoreFactory.eINSTANCE.createPCMRandomVariable();
        ret.setSpecification(value);
        return ret;
    }

}