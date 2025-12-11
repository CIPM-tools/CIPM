package tools.cipm.views.measurements;

import tools.vitruv.framework.views.impl.ChangeRecordingView;

import java.util.Collection;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

import tools.vitruv.framework.views.ChangeableViewSource;
import tools.vitruv.framework.views.CommittableView;
import tools.vitruv.framework.views.changederivation.StateBasedChangeResolutionStrategy;
import tools.vitruv.framework.views.impl.BasicView;

public abstract class BaseMeasurementsView extends ChangeRecordingView implements MeasurementsView {
	private Collection<EObject> readOnlyRoots;
	
	protected BaseMeasurementsView(BaseMeasurementsViewType viewType, ChangeableViewSource viewSource) {
		super(new BasicView(viewType, viewSource, new FullViewSelection()));
	}
	
	@Override
	public Collection<EObject> getRootObjects() {
		this.getView().checkNotClosed();
		if (this.readOnlyRoots == null) {
			this.readOnlyRoots = EcoreUtil.copyAll(this.getView().getRootObjects());
		}
		return this.readOnlyRoots;
	}
	
	@Override
	public void modifyContents(Procedure1<? super ResourceSet> modificationFunction) {
		this.readOnlyRoots = null;
		super.modifyContents(modificationFunction);
	}
	
	@Override
	public void registerRoot(EObject object, URI persistAt) {
		// Do nothing for now.
	}
	
	@Override
	public void moveRoot(EObject object, URI newLocation) {
		// Do nothing for now.
	}
	
	@Override
	public MeasurementsViewType getViewType() {
		return (MeasurementsViewType) super.getViewType();
	}
	
	@Override
	public CommittableView withChangeDerivingTrait() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public CommittableView withChangeDerivingTrait(StateBasedChangeResolutionStrategy changeResolutionStrategy) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public CommittableView withChangeRecordingTrait() {
		throw new UnsupportedOperationException();
	}
}
