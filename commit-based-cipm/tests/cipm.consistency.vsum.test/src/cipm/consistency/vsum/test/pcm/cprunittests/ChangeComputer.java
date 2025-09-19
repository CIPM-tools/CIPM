package cipm.consistency.vsum.test.pcm.cprunittests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.framework.views.changederivation.DefaultStateBasedChangeResolutionStrategy;

public class ChangeComputer {
	/**
	 * Creates, loads and returns a Resource instance for the given URI.
	 */
	protected Resource loadResource(URI uri) {
		var resSet = new ResourceSetImpl();
		return this.loadResource(resSet.createResource(uri));
	}

	/**
	 * Creates, loads and returns a new Resource instance for the same URI. Can be
	 * used to create a separate Resource instance for the given resource.
	 */
	protected Resource loadNewResourceInstance(Resource resource) {
		return this.loadResource(resource.getURI());
	}

	/**
	 * Loads and returns the given resource. Does not create a new resource
	 * instance.
	 */
	protected Resource loadResource(Resource resource) {
		try {
			resource.load(null);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		return resource;
	}

	public Resource getEmptyResourceInstance() {
		return new ResourceSetImpl()
				.createResource(URI.createFileURI(new File("").toPath().resolve("res.repository").toString()));
	}

	public Resource getResourceCopy(Resource res) {
		var newRes = this.getEmptyResourceInstance();
		res.getContents().forEach((c) -> newRes.getContents().add(EcoreUtil.copy(c)));
		return newRes;
	}

	public List<EChange> getEChangesFor(List<Consumer<Resource>> modifications) {
		var changes = new ArrayList<EChange>();
		var res = this.getEmptyResourceInstance();
		for (var mod : modifications) {
			changes.addAll(this.getEChangesFor(res, mod));
		}
		return changes;
	}

	public List<EChange> getEChangesFor(Consumer<Resource> modifications) {
		return this.getEChangesFor(this.getEmptyResourceInstance(), modifications);
	}

	/**
	 * Modifies oldRes along the way
	 */
	public List<EChange> getEChangesFor(Resource oldRes, Consumer<Resource> modifications) {
		var unmodifiedResDupl = this.getResourceCopy(oldRes);
		modifications.accept(oldRes);
		var d = new DefaultStateBasedChangeResolutionStrategy(UseIdentifiers.WHEN_AVAILABLE);
		var changes = d.getChangeSequenceBetween(oldRes, unmodifiedResDupl).getEChanges();
		return new ArrayList<>(changes);
	}
}
