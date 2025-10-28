package cipm.consistency.vsum.test.pcm;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.jupiter.api.Assertions;

import cipm.consistency.vsum.Propagation;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.description.VitruviusChange;

public class ChangeSaver {
	// TODO Extract this part to some DirLayout class
	
	private static final String changesDirName = "changes";
	private static final String javaChangesFileName = "javaChanges.changes";
	private static final String pcmChangesFileName = "pcmChanges.changes";

	private final Path rootPath;
	private final Path changesPath;
	private final Path javaChangesFilePath;
	private final Path pcmChangesFilePath;

	public ChangeSaver(Path rootPath) {
		this.rootPath = rootPath;
		this.changesPath = rootPath.resolve(changesDirName);
		this.javaChangesFilePath = this.changesPath.resolve(javaChangesFileName);
		this.pcmChangesFilePath = this.changesPath.resolve(pcmChangesFileName);
	}

	public void saveChanges(Propagation prop, boolean isJavaConsequential) {
		if (isJavaConsequential) {
			saveChanges(getAllConsequentialEChangesInOrder(prop), getAllOriginalEChangesInOrder(prop));
		} else {
			saveChanges(getAllOriginalEChangesInOrder(prop), getAllConsequentialEChangesInOrder(prop));
		}
	}

	public void saveChanges(List<EChange> javaChanges, List<EChange> pcmChanges) {
		var javaChangesResSet = new ResourceSetImpl();
		var javaChangesRes = javaChangesResSet
				.createResource(URI.createFileURI(javaChangesFilePath.toFile().getAbsolutePath()));
		javaChangesRes.getContents().addAll(javaChanges);

		var pcmChangesResSet = new ResourceSetImpl();
		var pcmChangesRes = pcmChangesResSet
				.createResource(URI.createFileURI(pcmChangesFilePath.toFile().getAbsolutePath()));
		pcmChangesRes.getContents().addAll(pcmChanges);

		try {
			javaChangesRes.save(null);
			pcmChangesRes.save(null);
		} catch (IOException e) {
			e.printStackTrace();
			Assertions.fail(e);
		}
	}

	/**
	 * @return All original changes in {@code prop.getChanges().get(0)}, since
	 *         changes are duplicated in {@code prop.getChanges().get(1)}
	 */
	public List<EChange> getAllOriginalEChangesInOrder(Propagation prop) {
		return getAllEChangesInOrder(prop.getChanges().get(0).getOriginalChange());
	}

	/**
	 * @return All consequential changes in {@code prop.getChanges().get(0)}, since
	 *         changes are duplicated in {@code prop.getChanges().get(1)}
	 */
	public List<EChange> getAllConsequentialEChangesInOrder(Propagation prop) {
		return getAllEChangesInOrder(prop.getChanges().get(0).getConsequentialChanges());
	}

	public List<EChange> getAllEChangesInOrder(VitruviusChange change) {
		return change.getEChanges();
	}
}
