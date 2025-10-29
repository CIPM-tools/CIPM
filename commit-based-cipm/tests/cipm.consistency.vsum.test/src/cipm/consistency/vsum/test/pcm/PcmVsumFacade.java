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
import tools.vitruv.framework.vsum.internal.InternalVirtualModel;

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
	 * Propagate a resource into the underlying vsum
	 * 
	 * @param resource The propagated resource
	 * @return The propagated changes
	 */
	Propagation propagateResource(Resource resource);

	/**
	 * Propagate a resource into the underlying vsum
	 * 
	 * @param resource  The propagated resource
	 * @param targetUri The uri where vitruv persists the propagated resource
	 * @return The propagated changes
	 */
	Propagation propagateResource(Resource resource, URI targetUri);

	Collection<EChange> getAllChanges();

	void addChange(EChange change);

	default void addChanges(Collection<EChange> changesToAdd) {
		if (changesToAdd == null)
			return;

		changesToAdd.forEach((c) -> this.addChange(c));
	}

	boolean removeChange(EChange change);

	void cleanChanges();

	InternalVirtualModel getVsum();

	EditableCorrespondenceModelView<Correspondence> getCorrespondenceView();

	IChangeAcceptingView getChangeAcceptingView();

	VsumDirLayout getDirLayout();

	// FIXME Clarify whether ChangeRecordingView can be used like this
	Propagation propagateResource(URI targetUri, Consumer<Resource> modifications);

	/**
	 * Saving correspondences directly is currently not possible. It only triggers
	 * during change propagation. So, perform changes that do not lead to any
	 * effective changes and propagate them.
	 */
	void saveCorrespondences();

	void closeAllModels();

	public default void close() {
		this.cleanChanges();
		this.closeAllModels();
	}
}
