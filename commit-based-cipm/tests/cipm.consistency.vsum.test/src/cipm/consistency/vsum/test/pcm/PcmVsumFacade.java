package cipm.consistency.vsum.test.pcm;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import cipm.consistency.models.ModelFacade;
import cipm.consistency.vsum.Propagation;
import cipm.consistency.vsum.VsumDirLayout;
import cipm.consistency.vsum.test.pcm.newviews.IChangeAcceptingView;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.framework.views.CommittableView;
import tools.vitruv.framework.vsum.internal.InternalVirtualModel;

/**
 * An interface for the VSUM of the PCM to Java change propagation. This
 * interface is the counterpart of Java to PCM change propagation facade
 * {@link VsumFacade}.
 * 
 * @author Alp Torac Genc
 */
@SuppressWarnings("restriction")
public interface PcmVsumFacade extends AutoCloseable {

	/**
	 * @param models The models which should be loaded into the VSUM
	 * @param force  If true, we also load the model if its URI is already loaded in
	 *               the VSUM. This is needed if we changed a model manually.
	 */
	void loadModels(List<ModelFacade> models, boolean force);

	/**
	 * Forcefully reload all models
	 */
	void forceReload();

	/**
	 * Propagate a resource into the underlying vsum. Uses
	 * {@link #getChangeAcceptingView()} for propagation, meaning that
	 * {@link #getAllChanges()} will be propagated.
	 * 
	 * @param resource The propagated resource
	 * @return The propagated changes
	 */
	Propagation propagateResource(Resource resource);

	/**
	 * Propagate a resource into the underlying vsum. Uses
	 * {@link #getChangeAcceptingView()} for propagation, meaning that
	 * {@link #getAllChanges()} will be propagated.
	 * 
	 * @param resource           The propagated resource
	 * @param targetUri          The uri where vitruv persists the propagated
	 *                           resource
	 * @param changesToPropagate All changes that should be propagated to the
	 *                           underlying model
	 * @return The propagated changes
	 */
	Propagation propagateResource(Resource resource, URI targetUri);

	/**
	 * @return All changes stored in this instance. The next call to
	 *         {@link #getChangeAcceptingView()} will contain these changes, if they
	 *         are not removed from this instance in the meantime.
	 */
	Collection<EChange> getAllChanges();

	/**
	 * Adds the given change to this instance. The next call to
	 * {@link #getChangeAcceptingView()} will contain this change, if it is not
	 * removed from this instance in the meantime.
	 */
	void addChange(EChange change);

	/**
	 * Variant of {@link #addChange(EChange)} for change collections. Accounts for
	 * changesToAdd being null.
	 */
	default void addChanges(Collection<EChange> changesToAdd) {
		if (changesToAdd == null)
			return;

		changesToAdd.forEach((c) -> this.addChange(c));
	}

	/**
	 * @return Removes the given change instance from this instance. Returns true,
	 *         if the change is actually removed by this operation. Otherwise
	 *         returns false.
	 */
	boolean removeChange(EChange change);

	/**
	 * Cleans all model changes in this instance.
	 */
	void cleanChanges();

	/**
	 * @return The actual VSUM, to which this instance serves as a facade.
	 */
	InternalVirtualModel getVsum();

	/**
	 * @return An object that grants access to the {@link Correspondence}s inside
	 *         the VSUM.
	 */
	EditableCorrespondenceModelView<Correspondence> getCorrespondenceView();

	/**
	 * @return A {@link View} that is capable of taking model changes from outside
	 *         and applying them to the underlying {@link ModelFacade} instances.
	 */
	IChangeAcceptingView getChangeAcceptingView();

	/**
	 * @return A {@link View} that is capable of recording model changes applied to
	 *         {@link Resource} instances and applying them to the underlying
	 *         {@link ModelFacade} instances.
	 */
	CommittableView getChangeRecordingView();

	VsumDirLayout getDirLayout();

	/**
	 * Applies the given modifications to the {@link Resource} instance with the
	 * given URI and records all model changes associated with these modifications.
	 * Then applies those recorded changes to that Resource instance inside the VSUM
	 * and simultaneously propagates to all other {@link ModelFacade} instances.
	 * <p>
	 * <b>Only propagates the model changes emerging from the given modifications,
	 * DOES NOT propagate {@link #getAllChanges()}</b>. The model changes from the
	 * given modifications do not remain in this instance, but they are accessible
	 * from the return value of this method.
	 * 
	 * @return The resulting {@link Propagation} instance
	 */
	Propagation propagateResource(URI targetUri, Consumer<Resource> modifications);

	/**
	 * Closes all underlying {@link ModelFacade} instances.
	 */
	void closeAllModels();

	/**
	 * Cleans all PCM changes passed to this VSUM Facade to be propagated to the
	 * other {@link ModelFacade} instances. Shorthand for {@link #cleanChanges()}
	 * and {@link #closeAllModels()}.
	 */
	public default void close() {
		this.cleanChanges();
		this.closeAllModels();
	}
}
