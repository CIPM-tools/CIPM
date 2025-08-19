package cipm.consistency.vsum.test.pcm.newviews;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.BasicEList;
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

@SuppressWarnings("restriction")
public class ChangeAcceptingView implements IChangeAcceptingView, CommittableView, ChangePropagationListener {
	@Accessors({ AccessorType.PUBLIC_GETTER, AccessorType.PROTECTED_SETTER })
	private ViewSelection selection;

	@Accessors({ AccessorType.PUBLIC_GETTER, AccessorType.PROTECTED_SETTER })
	private ViewType<? extends ViewSelector> viewType;

	@Accessors({ AccessorType.PROTECTED_GETTER, AccessorType.PROTECTED_SETTER })
	private ResourceSet viewResourceSet;

	private ResourceSet originalStateViewResourceSet;

	private HashMap<Resource, Resource> originalStateResourceMapping;

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

	private InternalVirtualModel vsum;

	// TODO Change to VitruviusChange once composite changes are supported
	private final EList<EChange> changes = new BasicEList<EChange>();

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
		this.setupReferenceState();
		this.update();
	}

	private void setupReferenceState() {
		ResourceSetImpl _resourceSetImpl = new ResourceSetImpl();
		this.originalStateViewResourceSet = _resourceSetImpl;
		ResourceCopier.copyViewResources(this.viewResourceSet.getResources(), this.originalStateViewResourceSet);
		HashMap<Resource, Resource> _hashMap = new HashMap<Resource, Resource>();
		this.originalStateResourceMapping = _hashMap;
		final Consumer<Resource> _function = (Resource resource) -> {
			final Function1<Resource, Boolean> _function_1 = (Resource it) -> {
				URI _uRI = it.getURI();
				URI _uRI_1 = resource.getURI();
				return Boolean.valueOf((_uRI == _uRI_1));
			};
			this.originalStateResourceMapping.put(resource, IterableExtensions
					.<Resource>findFirst(this.originalStateViewResourceSet.getResources(), _function_1));
		};
		this.viewResourceSet.getResources().forEach(_function);
	}

	@Override
	public void update() {
		this.closeOriginalState();

		// Copy paste of ViewCreatingViewType.updateView(this)
		this.viewResourceSet.getResources().forEach(Resource::unload);
		this.viewResourceSet.getResources().clear();
		var viewSources = this.vsum.getViewSourceModels();
		var resourcesWithSelectedElements = new ArrayList<Resource>();
		viewSources.stream()
				.filter((vs) -> vs.getContents().stream().anyMatch((c) -> this.getSelection().isViewObjectSelected(c)))
				.forEach((r) -> resourcesWithSelectedElements.add(r));
		ResourceCopier.copyViewSourceResources(resourcesWithSelectedElements, this.viewResourceSet, (c) -> this.getSelection().isViewObjectSelected(c));

		this.addChangeListeners(this.viewResourceSet);
		this.setupReferenceState();
	}

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

	@Override
	public List<PropagatedChange> commitChanges() {
		if (this.changes.isEmpty())
			return List.of();

		final List<PropagatedChange> propagatedChanges = vsum
				.propagateChange(VitruviusChangeFactory.getInstance().createTransactionalChange(this.changes));

		this.cleanChanges();

		return propagatedChanges;
	}

	private void closeOriginalState() {
		final Consumer<Resource> _function = (Resource it) -> {
			it.unload();
		};
		this.originalStateViewResourceSet.getResources().forEach(_function);
		this.originalStateViewResourceSet.getResources().clear();
	}

	@Override
	public void close() throws Exception {
		final Consumer<Resource> _function = (Resource it) -> {
			it.unload();
		};
		this.viewResourceSet.getResources().forEach(_function);
		this.viewResourceSet.getResources().clear();
		this.removeChangeListeners(this.viewResourceSet);
	}

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
		Preconditions.checkArgument((object != null), "object to move must not be null");
		Preconditions.checkState(this.getRootObjects().contains(object), "view must contain element %s to move",
				object);
		Preconditions.checkArgument((newLocation != null), "URI for new location of root must not be null");
		final Function1<Resource, Boolean> _function = (Resource it) -> {
			return Boolean.valueOf(it.getContents().contains(object));
		};
		Resource _findFirst = IterableExtensions.<Resource>findFirst(this.viewResourceSet.getResources(), _function);
		_findFirst.setURI(newLocation);
	}

	public void registerRoot(final EObject object, final URI persistAt) {
		Preconditions.checkArgument((object != null), "object to register as root must not be null");
		Preconditions.checkArgument((persistAt != null), "URI for root to register must not be null");
		Resource viewResource = this.viewResourceSet.getResource(persistAt, false);
		if ((viewResource == null)) {
			viewResource = this.viewResourceSet.createResource(persistAt);
		}
		viewResource.getContents().add(object);
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

	@Override
	public void startedChangePropagation(VitruviusChange changeToPropagate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finishedChangePropagation(Iterable<PropagatedChange> propagatedChanges) {
		// TODO Auto-generated method stub

	}
}
