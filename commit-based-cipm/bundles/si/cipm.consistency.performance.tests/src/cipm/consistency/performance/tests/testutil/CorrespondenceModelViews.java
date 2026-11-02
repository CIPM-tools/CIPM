package cipm.consistency.performance.tests.testutil;

import java.nio.file.Path;

import org.eclipse.emf.common.util.URI;

import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.model.CorrespondenceModelFactory;
import tools.vitruv.change.correspondence.model.PersistableCorrespondenceModel;
import tools.vitruv.change.correspondence.view.CorrespondenceModelViewFactory;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

/**
 * Builds a real (not faked) correspondence model view backed by a temporary file. Several of
 * the propagation specs under test read their correspondence-model view even when a change
 * ultimately isn't handled by anything, so a real, cheap, non-null view is needed rather than
 * a null/faked one.
 */
public final class CorrespondenceModelViews {

	private CorrespondenceModelViews() {
	}

	/**
	 * Creates a fresh, empty, in-memory-backed correspondence view. Pass a JUnit 5
	 * {@code @TempDir}-provided directory; a new backing file is placed inside it.
	 */
	public static EditableCorrespondenceModelView<Correspondence> newEditableView(Path tempDir) {
		URI modelUri = URI.createFileURI(tempDir.resolve("correspondences.xmi").toString());
		PersistableCorrespondenceModel model = CorrespondenceModelFactory.createPersistableCorrespondenceModel(modelUri);
		return CorrespondenceModelViewFactory.createEditableCorrespondenceModelView(model);
	}
}
