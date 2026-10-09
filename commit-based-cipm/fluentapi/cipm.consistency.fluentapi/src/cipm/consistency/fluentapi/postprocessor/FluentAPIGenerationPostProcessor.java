package cipm.consistency.fluentapi.postprocessor;

/**
 * An interface for classes that are meant to post-process the generated fluent
 * API model. Refer to the documentation of individual concrete implementors for
 * more information.
 * <p>
 * <p>
 * Note: The concrete implementors of this class are likely to be affected by
 * changes to the fluent API model, therefore they might require adaptations.
 * 
 * @author Alp Torac Genc
 */
public interface FluentAPIGenerationPostProcessor {
	/**
	 * Applies this post-processor to model elements passed to this instance. Refer
	 * to the documentation of the concrete implementor for more details.
	 */
	public void apply();
}
