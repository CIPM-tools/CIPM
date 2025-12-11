package cipm.consistency.vsum.test.pcm.newviews;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceSetUtil;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.description.PropagatedChange;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.composite.description.VitruviusChangeFactory;
import tools.vitruv.change.composite.propagation.ChangePropagationListener;
import tools.vitruv.framework.views.CommittableView;
import tools.vitruv.framework.views.ViewSelection;
import tools.vitruv.framework.views.ViewSelector;
import tools.vitruv.framework.views.ViewType;
import tools.vitruv.framework.views.changederivation.StateBasedChangeResolutionStrategy;
import tools.vitruv.framework.vsum.internal.InternalVirtualModel;

/**
 * A {@link View} implementation, which accepts model changes (as
 * {@link EChange} instances) from outside.
 * 
 * <p>
 * TODO Move to tools.vitruv.framework.views
 * <p>
 * TODO Re-use existing methods in BasicView and other views once they are
 * accessible
 * 
 * @author Alp Torac Genc
 */
@SuppressWarnings("restriction")
public class ChangeAcceptingView implements IChangeAcceptingView, CommittableView, ChangePropagationListener {
	@Accessors({ AccessorType.PUBLIC_GETTER, AccessorType.PROTECTED_SETTER })
	private ViewSelection selection;

	@Accessors({ AccessorType.PUBLIC_GETTER, AccessorType.PROTECTED_SETTER })
	private ViewType<? extends ViewSelector> viewType;

	@Accessors({ AccessorType.PROTECTED_GETTER, AccessorType.PROTECTED_SETTER })
	private ResourceSet viewResourceSet;

	private InternalVirtualModel vsum;

	// TODO Change to VitruviusChange once composite changes are supported
	private final Collection<EChange> changes = new ArrayList<EChange>();

	// Mostly copied from BasicView
	public ChangeAcceptingView(final InternalVirtualModel vsum, final ViewType<? extends ViewSelector> viewType,
			final ViewSelection selection) {
		Preconditions.checkArgument((viewType != null), "view type must not be null");
		Preconditions.checkArgument((vsum != null), "vsum (view source) must not be null");
		Preconditions.checkArgument((selection != null), "view selection must not be null");
		this.viewType = viewType;
		this.selection = selection;
		this.vsum = vsum;
		this.vsum.addChangePropagationListener(this);
		this.viewResourceSet = ResourceSetUtil.withGlobalFactories(new ResourceSetImpl());
		this.update();
	}

	// Mostly copied from BasicView
	@Override
	public void update() {
		this.addChangeListeners(this.viewResourceSet);
	}

	// Mostly copied from BasicView
	private void addChangeListeners(final Notifier notifier) {
		boolean _matched = false;
		if (notifier instanceof ResourceSet) {
			_matched = true;
			final Consumer<Resource> _function = (Resource it) -> {
				this.addChangeListeners(it);
			};
			((ResourceSet) notifier).getResources().forEach(_function);
		}
		if (!_matched) {
			if (notifier instanceof Resource) {
				_matched = true;
				final Consumer<EObject> _function = (EObject it) -> {
					this.addChangeListeners(it);
				};
				((Resource) notifier).getContents().forEach(_function);
			}
		}
		if (!_matched) {
			if (notifier instanceof EObject) {
				_matched = true;
				final Consumer<EObject> _function = (EObject it) -> {
					this.addChangeListeners(it);
				};
				((EObject) notifier).eContents().forEach(_function);
			}
		}
	}

	// Mostly copied from ChangeRecordingView
	@Override
	public List<PropagatedChange> commitChanges() {
		if (this.changes.isEmpty())
			return List.of();

		var transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(this.changes);

		final List<PropagatedChange> propagatedChanges = vsum.propagateChange(transactionalChange);

		this.cleanChanges();

		return propagatedChanges;
	}

	// Adapted from BasicView
	@Override
	public void close() throws Exception {
		final Consumer<Resource> _function = (Resource it) -> {
			it.unload();
		};
		this.viewResourceSet.getResources().forEach(_function);
		this.viewResourceSet.getResources().clear();
		this.removeChangeListeners(this.viewResourceSet);
	}

	// Copied from BasicView
	private void removeChangeListeners(final ResourceSet resourceSet) {
		final Procedure1<Notifier> _function = (Notifier it) -> {
			it.eAdapters().clear();
		};
		IteratorExtensions.<Notifier>forEach(resourceSet.getAllContents(), _function);
	}

	@Override
	public CommittableView withChangeRecordingTrait() {
		return null;
	}

	@Override
	public CommittableView withChangeDerivingTrait(final StateBasedChangeResolutionStrategy changeResolutionStrategy) {
		return null;
	}

	// Adapted from BasicView
	public Collection<EObject> getRootObjects() {
		List<EObject> _xblockexpression = null;
		{
			final Function1<Resource, EList<EObject>> _function = (Resource it) -> {
				return it.getContents();
			};
			_xblockexpression = IterableExtensions.<EObject>toList(Iterables.<EObject>concat(
					ListExtensions.<Resource, EList<EObject>>map(this.viewResourceSet.getResources(), _function)));
		}
		return _xblockexpression;
	}

	public ViewSelection getSelection() {
		return this.selection;
	}

	public ViewType<? extends ViewSelector> getViewType() {
		return this.viewType;
	}

	/**
	 * @return Always returns false
	 */
	public boolean isClosed() {
		return false;
	}

	/**
	 * @return Always returns false
	 */
	public boolean isModified() {
		return false;
	}

	/**
	 * @return Always returns false
	 */
	public boolean isOutdated() {
		return false;
	}

	public void moveRoot(final EObject object, final URI newLocation) {
	}

	public void registerRoot(final EObject object, final URI persistAt) {
	}

	@Override
	public Collection<EChange> getAllChanges() {
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

	@Override
	public void startedChangePropagation(VitruviusChange changeToPropagate) {
	}

	@Override
	public void finishedChangePropagation(Iterable<PropagatedChange> propagatedChanges) {
	}
}
