package cipm.consistency.base.shared;

import java.util.List;

import org.eclipse.net4j.util.collection.Pair;

import com.google.common.collect.Lists;

/**
 * Basic Utility that is used to simplify some JSON operations.
 * 
 * @author David Monschein
 *
 */
public class JsonUtil {

    /**
     * Wraps a single key-value pair as JSON object.
     * 
     * @param attribute
     *            key of the attribute
     * @param value
     *            value of the attribute
     * @param string
     *            whether to wrap it as string or not
     * @return valid JSON string that represents an object which contains the given key-value pair
     */
    public static String wrapAsObject(String attribute, Object value, boolean string) {
        return wrapAsObject(Lists.newArrayList(new Pair<>(attribute, new Pair<>(value, string))));
    }

    /**
     * Wraps a list of key-value pairs as JSON object.
     * 
     * @param values
     *            triples which are structured as follows: (key, value, wrap as string)
     * @return valid JSON string that represents an object which contains the given key-value pairs
     */
    public static String wrapAsObject(List<Pair<String, Pair<Object, Boolean>>> values) {
        StringBuilder outputJson = new StringBuilder();
        outputJson.append("{");
        for (Pair<String, Pair<Object, Boolean>> attr : values) {
            outputJson.append("\"");
            outputJson.append(attr.getElement1());
            outputJson.append("\"");
            outputJson.append(" : ");
            if (attr.getElement2().getElement2()) {
                outputJson.append("\"");
            }
            outputJson.append(String.valueOf(attr.getElement2().getElement1()));
            if (attr.getElement2().getElement2()) {
                outputJson.append("\"");
            }
            outputJson.append(",");
        }
        if (values.size() > 0) {
            outputJson.setLength(outputJson.length() - 1);
        }
        outputJson.append("}");

        return outputJson.toString();
    }

    /**
     * Generates a string which represents an empty JSON object.
     * 
     * @return string which represents an empty JSON object
     */
    public static String emptyObject() {
        return "{}";
    }

    /**
     * Generates a string which represents an empty JSON array.
     * 
     * @return string which represents an empty JSON array
     */
    public static String emptyArray() {
        return "[]";
    }

}
