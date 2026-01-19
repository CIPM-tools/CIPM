package tools.cipm.views.measurements;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.framework.views.ChangeableViewSource;
import tools.vitruv.framework.views.ViewSelection;

public class FullMeasurementsViewSelector implements MeasurementsViewSelector {
	private BaseMeasurementsViewType viewType;
	private ChangeableViewSource viewSource;
	
	public FullMeasurementsViewSelector(BaseMeasurementsViewType viewType, ChangeableViewSource viewSource) {
		this.viewType = viewType;
		this.viewSource = viewSource;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public ViewSelection getSelection() {
		return new FullViewSelection();
	}

	@Override
	public Collection<EObject> getSelectableElements() {
		return Collections.emptySet();
	}

	@Override
	public boolean isSelected(EObject eObject) {
		return true;
	}

	@Override
	public boolean isSelectable(EObject eObject) {
		return false;
	}

	@Override
	public void setSelected(EObject eObject, boolean selected) {}

	@Override
	public boolean isViewObjectSelected(EObject eObject) {
		return true;
	}

	@Override
	public BaseMeasurementsView createView() {
		return this.viewType.createView(this);
	}
	
	@Override
	public ChangeableViewSource getViewSource() {
		return this.viewSource;
	}
}
