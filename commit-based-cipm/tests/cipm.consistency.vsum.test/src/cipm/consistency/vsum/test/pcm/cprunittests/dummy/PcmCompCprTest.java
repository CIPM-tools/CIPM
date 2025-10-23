package cipm.consistency.vsum.test.pcm.cprunittests.dummy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.core.entity.EntityPackage;

import cipm.consistency.vsum.test.pcm.compchanges.DummyCompositeChangeMarker;
import cipm.consistency.vsum.test.pcm.cprunittests.AbstractPcmCprTest;
import mir.reactions.dummyPCMCompCPRs.DummyPCMCompCPRsChangePropagationSpecification;
import tools.vitruv.change.atomic.feature.attribute.AttributeFactory;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

public class PcmCompCprTest extends AbstractPcmCprTest {
	private static final Function<Resource, EObject> repoObjLocator = (r) -> r.getContents().get(0);

	@Override
	protected List<ChangePropagationSpecification> getCPRs() {
		var list = new ArrayList<ChangePropagationSpecification>();
		list.add(new DummyPCMCompCPRsChangePropagationSpecification());
		return list;
	}

	public void compCprTest(Resource pcmRepoResource) {
		var repoEObj = repoObjLocator.apply(pcmRepoResource);
		var repoEObjFragment = pcmRepoResource.getURIFragment(repoEObj);
		var repoEObjURI = pcmRepoResource.getURI().appendFragment(repoEObjFragment).toString();

		// Ensure that the URI fragments are accurate and lead to the desired object
		Assertions.assertEquals(repoEObj, pcmRepoResource.getEObject(repoEObjFragment));

		var attr = EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME;

		var compChangeMark = "compChange";
		var compChange1 = AttributeFactory.eINSTANCE.createReplaceSingleValuedEAttribute();
		compChange1.setAffectedEObject(repoEObj);
		compChange1.setAffectedEObjectID(repoEObjURI.toString());
		compChange1.setAffectedFeature(attr);
		compChange1.setOldValue(repoEObj.eGet(repoEObj.eClass().getEStructuralFeature(attr.getName())));
		compChange1.setNewValue("name1");
		DummyCompositeChangeMarker.markChange(compChange1, compChangeMark);

		var compChange2 = AttributeFactory.eINSTANCE.createReplaceSingleValuedEAttribute();
		compChange2.setAffectedEObject(repoEObj);
		compChange2.setAffectedEObjectID(repoEObjURI.toString());
		compChange2.setAffectedFeature(attr);
		compChange2.setOldValue(repoEObj.eGet(repoEObj.eClass().getEStructuralFeature(attr.getName())));
		compChange2.setNewValue("name2");
		DummyCompositeChangeMarker.markChange(compChange2, compChangeMark);

		var atomicChange = AttributeFactory.eINSTANCE.createReplaceSingleValuedEAttribute();
		atomicChange.setAffectedEObject(repoEObj);
		atomicChange.setAffectedEObjectID(repoEObjURI.toString());
		atomicChange.setAffectedFeature(attr);
		atomicChange.setOldValue(repoEObj.eGet(repoEObj.eClass().getEStructuralFeature(attr.getName())));
		atomicChange.setNewValue("name3");

		// Ensure that the change is not applied prior to propagation
		Assertions.assertEquals(1, pcmRepoResource.getContents().size());
		Assertions.assertEquals(repoEObj, pcmRepoResource.getContents().get(0));

		// Propagate the changes to repoRes, which results in applying the change to the
		// Resource in PcmFacade
		var props = propagatePcmChanges(pcmRepoResource, List.of(compChange1, compChange2, atomicChange));
		// The propagation DOES NOT change the passed Resource instance (repoRes here)
		// It instead applies the change to the Resource inside the PcmFacade

		// Ensure that no exceptions occurred during propagation
		Assertions.assertNull(props.getException());
		this.logPropagatedChanges(props);

		// Ensure that the change was actually applied
		var propagatedResource = this.getResourceFromPcmFacade(AbstractPcmCprTest.repositoryFileName);
		Assertions.assertEquals(1, propagatedResource.getContents().size());
		var propagatedRepoEObj = propagatedResource.getContents().get(0);

		// Ensure that the Resource is saved after changes are applied
		var res = this.loadNewResourceInstance(propagatedResource);

		// Ensure that the loaded Resource has the expected contents
		Assertions.assertEquals(1, res.getContents().size());
		var resRepoEObj = res.getContents().get(0);
		Assertions.assertTrue(EcoreUtil.equals(resRepoEObj, propagatedRepoEObj));

		DummyCompositeChangeMarker.reset();
	}

	@Test
	public void testCprTrigger_OnDifferentResourceInstance() {
		var pcmRes = this.getNewInstanceForResourceFromPcmFacade(AbstractPcmCprTest.repositoryFileName);
		compCprTest(pcmRes);
	}

	@Test
	public void testCprTrigger_OnSameResourceInstance() {
		var pcmRes = this.getResourceFromPcmFacade(AbstractPcmCprTest.repositoryFileName);
		compCprTest(pcmRes);
	}
}
