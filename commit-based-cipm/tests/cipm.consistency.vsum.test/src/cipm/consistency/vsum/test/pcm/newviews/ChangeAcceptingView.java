package cipm.consistency.vsum.test.pcm.newviews;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend.lib.annotations.Delegate;

import com.google.common.base.Preconditions;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.description.PropagatedChange;
import tools.vitruv.change.composite.description.VitruviusChangeFactory;
import tools.vitruv.framework.views.CommittableView;
import tools.vitruv.framework.views.View;
import tools.vitruv.framework.views.ViewSelection;
import tools.vitruv.framework.views.ViewSelector;
import tools.vitruv.framework.views.ViewType;
import tools.vitruv.framework.views.changederivation.StateBasedChangeResolutionStrategy;
import tools.vitruv.framework.vsum.internal.InternalVirtualModel;

@SuppressWarnings("restriction")
public class ChangeAcceptingView implements IChangeAcceptingView, CommittableView {

	/*
	 * TODO Remove the underlying view, make operations on vsum instead
	 * 
	 * Only implement the methods that you actually need / use. For the rest, just
	 * implement placeholders and minimally for now. Do not throw
	 * UnsupportedOperationException, do nothing or do what you need to keep working
	 * instead.
	 * 
	 * If implementing those methods is not possible, change the visibility
	 * modifiers in tools.vitruv.framework.views.impl (submodule).
	 * 
	 * In the future, once access to BasicView is possible, extend it instead.
	 */

	/*
	 * TODO If changes create or introduce new EObjects, the view gets them (not the
	 * vsum)
	 * 
	 * Therefore, make sure to transfer those changes from that view to vsum.
	 */

	@Delegate
	private View view;

	private InternalVirtualModel vsum;

	// TODO Change to VitruviusChange once composite changes are supported
	private final EList<EChange> changes = new BasicEList<EChange>();

	public ChangeAcceptingView(final InternalVirtualModel vsum, final View view) {
		Preconditions.checkArgument((view != null), "view must not be null");
		boolean _isModified = view.isModified();
		boolean _not = (!_isModified);
		Preconditions.checkState(_not, "view must not be modified");
		this.view = view;
		this.vsum = vsum;
	}

	@Override
	public void update() {
		/*
		 * If the update() method from underlying view is used, IllegalStateException is
		 * thrown from a checkState call in BasicView.xtend, due to
		 * this.view.isModified() = true
		 */

		// TODO Ask about what should be done here

		this.view.update();
	}

	@Override
	public List<PropagatedChange> commitChanges() {
		if (this.isClosed()) {
			throw new IllegalStateException("The underlying view is closed");
		}
		if (this.changes.isEmpty())
			return List.of();

		final List<PropagatedChange> propagatedChanges = vsum
				.propagateChange(VitruviusChangeFactory.getInstance().createTransactionalChange(this.changes));

		this.cleanChanges();

		return propagatedChanges;
	}

	@Override
	public void close() throws Exception {
		this.view.close();
	}

	@Override
	public CommittableView withChangeRecordingTrait() {
		return this.view.withChangeRecordingTrait();
	}

	@Override
	public CommittableView withChangeDerivingTrait(final StateBasedChangeResolutionStrategy changeResolutionStrategy) {
		return this.view.withChangeDerivingTrait(changeResolutionStrategy);
	}

	public Collection<EObject> getRootObjects() {
		// TODO Should be no problem to do nothing here, if there are issues, copy from
		// existing method implementations
		return this.view.getRootObjects();
	}

	public <T extends Object> Collection<T> getRootObjects(final Class<T> clazz) {
		// TODO Should be no problem to do nothing here, if there are issues, copy from
		// existing method implementations
		return this.view.getRootObjects(clazz);
	}

	public ViewSelection getSelection() {
		return this.view.getSelection();
	}

	public ViewType<? extends ViewSelector> getViewType() {
		return this.view.getViewType();
	}

	public boolean isClosed() {
		return this.view.isClosed();
	}

	public boolean isModified() {
		return this.view.isModified();
	}

	public boolean isOutdated() {
		return this.view.isOutdated();
	}

	public void moveRoot(final EObject object, final URI newLocation) {
		this.view.moveRoot(object, newLocation);
	}

	public void registerRoot(final EObject object, final URI persistAt) {
		this.view.registerRoot(object, persistAt);
	}

	public CommittableView withChangeDerivingTrait() {
		return this.view.withChangeDerivingTrait();
	}

	@Override
	public List<EChange> getAllChanges() {
		return new ArrayList<EChange>(this.changes);
	}

	@Override
	public void addChange(EChange change) {
		this.changes.add(change);
	}

	@Override
	public boolean removeChange(EChange change) {
		return this.changes.remove(change);
	}

	@Override
	public void cleanChanges() {
		this.changes.clear();
	}
}
