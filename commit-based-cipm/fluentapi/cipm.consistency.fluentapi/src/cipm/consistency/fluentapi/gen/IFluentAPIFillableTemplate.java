package cipm.consistency.fluentapi.gen;

/**
 * A template interface for strings that contain string formatting flags that
 * should be filled with parameter values.
 * 
 * @see {@link IFluentAPITemplate}
 * @see {@link String#format(String, Object...)}
 * 
 * @author Alp Torac Genc
 */
public interface IFluentAPIFillableTemplate extends IFluentAPITemplate {
	/**
	 * Fills the template's string formatting flags with the given parameters.
	 * 
	 * @param params The parameters to plug to the stored template, in order to fill
	 *               in the flags. Superfluous parameters will be ignored.
	 * @return The string filled with the given params
	 * 
	 * @see {@link String#format(String, Object...)}
	 */
	public String getFor(Object... params);

	/**
	 * Returns the template string with all format specifiers replaced by empty
	 * strings.
	 * 
	 * @return The template string with all specifiers replaced by empty strings
	 */
	public String getEmpty();

	/**
	 * Combines the template's filled value with method call syntax.
	 * 
	 * @param templateParams The parameters to fill the template's flags with.
	 *                       Superfluous parameters will be ignored.
	 * @param methodParams   The parameters to include in the method call as
	 *                       arguments
	 * @return {@code .filledTemplate(methodParams0, methodParams1, ..., methodParamsN-1)}
	 */
	public default String callFor(Object[] templateParams, String... methodParams) {
		var callRoot = "." + getFor(templateParams);
		var args = "(";

		if (methodParams != null && methodParams.length > 0) {
			args += String.join(",", methodParams);
		}

		return callRoot + args + ")";
	}

	/**
	 * Combines the template's filled value with method call syntax to "this".
	 * 
	 * @param templateParams The parameters to fill the template's flags with.
	 *                       Superfluous parameters will be ignored.
	 * @param methodParams   The parameters to include in the method call as
	 *                       arguments
	 * @return {@code this.filledTemplate(methodParams0, methodParams1, ..., methodParamsN-1)}
	 */
	public default String thisCallFor(Object[] templateParams, String... methodParams) {
		return "this" + callFor(templateParams, methodParams);
	}
}
