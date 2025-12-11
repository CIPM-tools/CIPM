package tools.cipm.views.measurements;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.framework.views.ViewSelection;

public class FullViewSelection implements ViewSelection {
	@Override
	public boolean isViewObjectSelected(EObject eObject) {
		return true;
	}
}
