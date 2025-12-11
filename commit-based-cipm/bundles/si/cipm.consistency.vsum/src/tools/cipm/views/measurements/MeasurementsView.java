package tools.cipm.views.measurements;

import tools.vitruv.framework.views.CommittableView;

public interface MeasurementsView extends CommittableView {
	@Override
	MeasurementsViewType getViewType();
	
	void addMeasurement(Object measurement);
}
