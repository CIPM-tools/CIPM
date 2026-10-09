package cipm.consistency.fluentapi.gen;

import org.apache.commons.lang.StringUtils;

/**
 * A template interface for EMF feature names that provides utility methods for
 * generating common getter and setter patterns.
 * <p>
 * This interface is primarily used in the fluent API code generation to
 * generate proper method calls for features stored within the generated model.
 * 
 * @see IFluentAPITemplate
 * 
 * @author Alp Torac Genc
 */
public interface IFluentAPIFeatureTemplate extends IFluentAPITemplate {
	/**
	 * Prefix for generating "this" references
	 */
	static final String THIS_PREFIX = "this.";
	/**
	 * Prefix for getter method names
	 */
	static final String GETTER_PREFIX = "get";
	/**
	 * Prefix for setter method names
	 */
	static final String SETTER_PREFIX = "set";

	/**
	 * Direct access to the feature of "this"
	 * 
	 * @return {@code this.featureName}
	 */
	public default String inThis() {
		return THIS_PREFIX + this.get();
	}

	/**
	 * The (supposed) name of the getter function of the feature.
	 * 
	 * @return {@code getFeatureName}
	 */
	public default String getter() {
		return GETTER_PREFIX + StringUtils.capitalize(this.get());
	}

	/**
	 * The (supposed) name of the setter function of the feature.
	 * 
	 * @return {@code this.setFeatureName}
	 */
	public default String setter() {
		return SETTER_PREFIX + StringUtils.capitalize(this.get());
	}

	/**
	 * Combines the feature's getter function with method call syntax.
	 * 
	 * @return {@code .getFeatureName()}
	 */
	public default String getterCall() {
		return "." + getter() + "()";
	}

	/**
	 * Combines the feature's setter function with method call syntax and the given
	 * argument.
	 * 
	 * @return {@code .setFeatureName(param)}
	 */
	public default String setterCall(String param) {
		return "." + setter() + "(" + param + ")";
	}

	/**
	 * Combines the feature's getter function with method call syntax on "this".
	 * 
	 * @return {@code this.getFeatureName()}
	 */
	public default String thisGetterCall() {
		return "this" + getterCall();
	}

	/**
	 * Combines the feature's setter function with method call syntax and the given
	 * argument on "this".
	 * 
	 * @return {@code this.setFeatureName(param)}
	 */
	public default String thisSetterCall(String param) {
		return "this" + setterCall(param);
	}

	/**
	 * @return {@link #get()} with the first letter capitalised.
	 */
	public default String getCapitalised() {
		return StringUtils.capitalize(this.get());
	}
}
