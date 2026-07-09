package cipm.consistency.fluentapi.gen;

/**
 * A template class that holds a fixed string value, without any string
 * formatting flags.
 * 
 * @see IFluentAPITemplate
 * @see FluentAPIFillableTemplate
 * 
 * @see {@link String#format(String, Object...)}
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIFixTemplate implements IFluentAPITemplate {
	private final String template;

	public FluentAPIFixTemplate(String template) {
		this.template = template;
	}

	@Override
	public String get() {
		return this.template;
	}
}
