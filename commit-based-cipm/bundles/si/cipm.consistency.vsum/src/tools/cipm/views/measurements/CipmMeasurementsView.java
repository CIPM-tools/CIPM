package tools.cipm.views.measurements;

import tools.vitruv.framework.views.ChangeableViewSource;

public class CipmMeasurementsView extends BaseMeasurementsView {
	protected CipmMeasurementsView(BaseMeasurementsViewType viewType, ChangeableViewSource viewSource) {
		super(viewType, viewSource);
	}

	@Override
	public void addMeasurement(Object measurement) {
		// Add here logic to add the actual measurement to the measurements model.
	}
}
