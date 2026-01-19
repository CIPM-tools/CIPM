package tools.cipm.views.measurements;

import tools.vitruv.framework.views.ChangeableViewSource;
import tools.vitruv.framework.views.ViewSelector;

public interface MeasurementsViewSelector extends ViewSelector {
	@Override
	MeasurementsView createView();
	
	ChangeableViewSource getViewSource();
}
