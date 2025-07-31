package cipm.consistency.vsum.test.pcm.newviews;

import java.lang.reflect.InvocationTargetException;
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
import tools.vitruv.framework.views.ChangeableViewSource;
import tools.vitruv.framework.views.CommittableView;
import tools.vitruv.framework.views.View;
import tools.vitruv.framework.views.ViewSelection;
import tools.vitruv.framework.views.ViewSelector;
import tools.vitruv.framework.views.ViewType;
import tools.vitruv.framework.views.changederivation.StateBasedChangeResolutionStrategy;

public class ChangeAcceptingView implements IChangeAcceptingView, CommittableView {
	@Delegate
	private View view;

	// TODO Change to VitruviusChange once composite changes are supported
	private final EList<EChange> changes = new BasicEList<EChange>();

	public ChangeAcceptingView(final View view) {
		Preconditions.checkArgument((view != null), "view must not be null");
		boolean _isModified = view.isModified();
		boolean _not = (!_isModified);
		Preconditions.checkState(_not, "view must not be modified");
		this.view = view;
	}

	@Override
	public void update() {
		this.view.update();
	}

	@Override
	@SuppressWarnings("all")
	public List<PropagatedChange> commitChanges() {
		if (this.isClosed()) {
			throw new IllegalStateException("The underlying view is closed");
		}

		ChangeableViewSource cvs = null;

		/*
		 * Forcefully access the "getViewSource" method from ModifiableView interface,
		 * which is not accessible from here. Needed to retrieve the underlying object,
		 * which has the means to propagate.
		 * 
		 * TODO Find a better way to access "getViewSource" without using reflection
		 */

		try {
			cvs = (ChangeableViewSource) this.view.getClass().getMethod("getViewSource", null).invoke(this.view, null);
		} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException
				| SecurityException e) {
			throw new IllegalStateException(e);
		}

		final List<PropagatedChange> propagatedChanges = cvs
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
		return this.view.getRootObjects();
	}

	public <T extends Object> Collection<T> getRootObjects(final Class<T> clazz) {
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
