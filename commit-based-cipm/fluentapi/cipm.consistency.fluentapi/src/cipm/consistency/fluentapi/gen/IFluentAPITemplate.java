package cipm.consistency.fluentapi.gen;

/**
 * Base interface for template classes that hold string templates used in fluent
 * API code generation.
 * <p>
 * Templates represent string patterns (such as; method names, method calls,
 * documentation fragments, EMF feature names) that can be retrieved and
 * combined to form complete code strings. The template string itself is
 * obtained via {@link #get()}, while the helper methods
 * {@link #call(String...)} and {@link #thisCall(String...)} facilitate
 * composing method invocation strings by appending the template to a
 * dot-prefixed method call with optional parameters.
 * <p>
 * There are two kinds of templates:
 * <ul>
 * <li><b>Fixed templates</b> ({@link FluentAPIFixTemplate}): Holds a template
 * as a string that does not require any further parameters</li>
 * <li><b>Fillable templates</b> ({@link IFluentAPIFillableTemplate}): Holds a
 * (partial) template as a string, which requires further parameters to be
 * complete</li>
 * </ul>
 * 
 * @author Alp Torac Genc
 */
public interface IFluentAPITemplate {
	/**
	 * @return The raw template string, may contain string formatting flags
	 * @see {@link String#format(String, Object...)}
	 */
	public String get();

	/**
	 * Combines the template with method call syntax, uses the given string
	 * parameters as its arguments.
	 * 
	 * @param params Method call parameters, can be null if the method should take
	 *               no arguments
	 * @return {@code .storedTemplate(param0, param1, ..., paramN-1)}
	 */
	public default String call(String... params) {
		var callRoot = "." + get();
		var args = "(";

		if (params != null && params.length > 0) {
			args += String.join(",", params);
		}

		return callRoot + args + ")";
	}

	/**
	 * Combines the template with a "this" prefix and method call syntax.
	 * 
	 * @param params Method call parameters, can be null if the method should take
	 *               no arguments
	 * @return {@code this.storedTemplate(param0, param1, ..., paramN-1)}
	 */
	public default String thisCall(String... params) {
		return "this" + call(params);
	}
}
