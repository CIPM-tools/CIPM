package cipm.consistency.base.shared.pcm.distribution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;

/**
 * Describes a distribution of integer numbers.
 * 
 * @author David Monschein
 *
 */
public class IntDistribution {
    /**
     * Number of occurences of the numbers.
     */
    private Map<Integer, Long> distribution;

    /**
     * Creates a new, empty distribution.
     */
    public IntDistribution() {
        this.distribution = new HashMap<>();
    }

    /**
     * Converts this instance to a stochastic expression that can be used within PCM models.
     * 
     * @return the generated stochastic expression
     */
    public PCMRandomVariable toStochasticExpression() {
        if (distribution.size() == 1) {
            return buildIntLiteral(distribution.entrySet()
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
            builder.append("IntPMF[");
            for (Entry<Integer, Long> entry : distribution.entrySet()) {
                double prob = (double) entry.getValue() / (double) sum;
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

    /**
     * Adds an integer to the distribution.
     * 
     * @param iterations
     *            integer to add
     */
    public void push(int iterations) {
        this.push(iterations, 1L);
    }

    /**
     * Adds an integer to the distribution which occured a certain number of times
     * 
     * @param iterations
     *            the integer to add
     * @param value
     *            the number of occurences of the integer
     */
    public void push(int iterations, long value) {
        if (distribution.containsKey(iterations)) {
            distribution.put(iterations, distribution.get(iterations) + value);
        } else {
            distribution.put(iterations, value);
        }
    }

    /**
     * Adds another distribution to this instance.
     * 
     * @param iterations
     *            the distribution to add
     */
    public void push(IntDistribution iterations) {
        iterations.distribution.entrySet()
            .forEach(it -> {
                this.push(it.getKey(), it.getValue());
            });
    }

    private PCMRandomVariable buildIntLiteral(int value) {
        return buildPCMVar(String.valueOf(value));
    }

    private PCMRandomVariable buildPCMVar(String value) {
        PCMRandomVariable ret = CoreFactory.eINSTANCE.createPCMRandomVariable();
        ret.setSpecification(value);
        return ret;
    }

    private double roundDouble(double val, int chars) {
        double factor = Math.pow(10, chars);
        return Math.round(val * factor) / factor;
    }

    public void pushAll(List<Integer> iterations) {
        iterations.forEach(i -> push(i));
    }

}