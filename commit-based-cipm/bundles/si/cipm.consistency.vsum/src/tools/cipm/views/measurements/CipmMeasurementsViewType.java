package tools.cipm.views.measurements;

import java.util.function.Predicate;

import org.eclipse.emf.ecore.resource.Resource;

public class CipmMeasurementsViewType extends BaseMeasurementsViewType {
	public CipmMeasurementsViewType(String name) {
		super(name);
	}

	@Override
	public CipmMeasurementsView createView(MeasurementsViewSelector selector) {
		return new CipmMeasurementsView(this, selector.getViewSource());
	}
	
	@Override
	protected Predicate<Resource> getMeasurementsModelFilter() {
		return (resource) -> true; // Here should be a more strict filter.
	}
}
