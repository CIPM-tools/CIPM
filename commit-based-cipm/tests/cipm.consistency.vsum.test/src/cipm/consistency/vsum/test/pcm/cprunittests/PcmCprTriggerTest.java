package cipm.consistency.vsum.test.pcm.cprunittests;

import java.io.IOException;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.repository.RepositoryPackage;

import cipm.consistency.models.pcm.PcmFacade;
import tools.vitruv.change.atomic.AtomicFactory;
import tools.vitruv.change.atomic.eobject.EobjectFactory;
import tools.vitruv.change.atomic.feature.FeatureFactory;
import tools.vitruv.change.atomic.feature.attribute.AttributeFactory;
import tools.vitruv.change.atomic.root.RootFactory;

public class PcmCprTriggerTest extends AbstractPcmCprTest {
	/**
	 * {@inheritDoc}
	 * 
	 * @implSpec Creates a minimal PCM without any correspondences
	 */
	@Override
	protected PcmFacade setupPcmFacade() {
		var pcmFacade = super.setupPcmFacade();
		pcmFacade.initialize(this.getPropagatedModelsRootPath());
		return pcmFacade;
	}

	@Test
	public void testCprTrigger() {
		var repoRes = this.getRepoRes();
		var repoEObj = repoRes.getContents().get(0);
		var repoEObjFragment = repoRes.getURIFragment(repoEObj);
		var repoEObjURI = repoRes.getURI().appendFragment(repoEObjFragment).toString();
		var repoEObjFragmentWithIndex = "/0";
		var repoEObjURIWithIndex = repoRes.getURI().appendFragment(repoEObjFragmentWithIndex).toString();
		Assertions.assertEquals(repoEObj, repoRes.getEObject(repoEObjFragment));
		Assertions.assertEquals(repoEObj, repoRes.getEObject(repoEObjFragmentWithIndex));

		var newEntityName = "en";

		var attrChange = AttributeFactory.eINSTANCE.createReplaceSingleValuedEAttribute();
		attrChange.setAffectedEObject(repoEObj);
		attrChange.setAffectedEObjectID(repoEObjURIWithIndex);
		var attr = (EAttribute) repoEObj.eClass().getEStructuralFeature(RepositoryPackage.REPOSITORY__ENTITY_NAME);
		attrChange.setAffectedFeature(attr);
		attrChange.setOldValue(repoEObj.eGet(attr));
		attrChange.setNewValue(newEntityName);

		Assertions.assertEquals(1, repoRes.getContents().size());
		Assertions.assertNotEquals(newEntityName, repoEObj.eGet(attr));

		var props = propagatePcmChanges(repoRes, List.of(attrChange));

		Assertions.assertNull(props.getException());
		this.logPropagatedChanges(props);

		var resSet = new ResourceSetImpl();
		var res = resSet.createResource(URI.createFileURI(repoRes.toString()));
		try {
			res.load(null);
		} catch (IOException e) {
			this.failTest(e);
		}

		Assertions.assertEquals(1, res.getContents().size());
		Assertions.assertEquals(newEntityName, repoEObj.eGet(attr));
	}

	private Resource getRepoRes() {
		return this.getPcmFacade().getResources().stream().filter((r) -> r.getURI().toString().contains(".repository"))
				.findFirst().get();
	}
}
