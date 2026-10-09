package cipm.consistency.fluentapi.gen;

import java.util.Collections;

/**
 * Concrete implementation of {@link IFluentAPIFillableTemplate} that stores a
 * template string with formatting flags.
 * 
 * @see IFluentAPIFillableTemplate
 * @see {@link String#format(String, Object...)}
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIFillableTemplate implements IFluentAPIFillableTemplate {
	private final String template;

	public FluentAPIFillableTemplate(String template) {
		this.template = template;
	}

	@Override
	public String get() {
		return this.template;
	}

	@Override
	public String getFor(Object... params) {
		return String.format(this.template, params);
	}

	@Override
	public String getEmpty() {
		// Over-approximate the amount of flags, since passing more parameters does not
		// matter
		return getFor(Collections.nCopies(this.template.length(), "").toArray());
	}

}
