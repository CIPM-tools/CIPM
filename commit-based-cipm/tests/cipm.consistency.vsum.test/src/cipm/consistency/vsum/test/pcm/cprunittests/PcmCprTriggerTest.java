package cipm.consistency.vsum.test.pcm.cprunittests;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryPackage;

import cipm.consistency.models.pcm.PcmFacade;
import tools.vitruv.change.atomic.feature.attribute.AttributeFactory;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;

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
	public void testCprTrigger_OnDifferentResourceInstance_WithoutView() {
		var repoRes = this.getNewInstanceForResourceFromPcmFacade(AbstractPcmCprTest.repositoryFileName);
		var repoEObj = repoRes.getContents().get(0);
		var repoEObjFragment = repoRes.getURIFragment(repoEObj);
		var repoEObjURI = repoRes.getURI().appendFragment(repoEObjFragment).toString();
		var repoEObjFragmentWithIndex = "/0";
		var repoEObjURIWithIndex = repoRes.getURI().appendFragment(repoEObjFragmentWithIndex).toString();

		// Ensure that the URI fragments are accurate and lead to the desired object
		Assertions.assertEquals(repoEObj, repoRes.getEObject(repoEObjFragment));
		Assertions.assertEquals(repoEObj, repoRes.getEObject(repoEObjFragmentWithIndex));

		var newEntityName = "en";

		var attrChange = AttributeFactory.eINSTANCE.createReplaceSingleValuedEAttribute();
		attrChange.setAffectedEObject(repoEObj);

		// TODO Clarify why only repoEObjURIWithIndex works as affectedEObjectID and not
		// repoEObjURI or repoEObjFragment
		attrChange.setAffectedEObjectID(repoEObjURIWithIndex);
		var attr = (EAttribute) repoEObj.eClass().getEStructuralFeature(RepositoryPackage.REPOSITORY__ENTITY_NAME);
		attrChange.setAffectedFeature(attr);
		attrChange.setOldValue(repoEObj.eGet(attr));
		attrChange.setNewValue(newEntityName);

		// Ensure that the change is not applied prior to propagation
		Assertions.assertEquals(1, repoRes.getContents().size());
		Assertions.assertEquals(repoEObj, repoRes.getContents().get(0));
		Assertions.assertNotEquals(newEntityName, repoEObj.eGet(attr));

		// Propagate the changes to repoRes, which results in applying the change to the
		// Resource in PcmFacade
		var props = propagatePcmChanges(repoRes, List.of(attrChange));
		// The propagation DOES NOT change the passed Resource instance (repoRes here)
		// It instead applies the change to the Resource inside the PcmFacade

		// Ensure that no exceptions occurred during propagation
		Assertions.assertNull(props.getException());
		this.logPropagatedChanges(props);

		// Ensure that the change was actually applied
		var propagatedResource = this.getResourceFromPcmFacade(AbstractPcmCprTest.repositoryFileName);
		Assertions.assertEquals(1, propagatedResource.getContents().size());
		var propagatedRepoEObj = propagatedResource.getContents().get(0);
		Assertions.assertEquals(newEntityName, propagatedRepoEObj.eGet(attr));

		// Ensure that the Resource is saved after changes are applied
		var res = this.loadNewResourceInstance(propagatedResource);

		// Ensure that the loaded Resource has the expected contents
		Assertions.assertEquals(1, res.getContents().size());
		var resRepoEObj = res.getContents().get(0);
		Assertions.assertTrue(EcoreUtil.equals(resRepoEObj, propagatedRepoEObj));
		Assertions.assertEquals(newEntityName, resRepoEObj.eGet(attr));
	}

	@Test
	public void testCprTrigger_OnSameResourceInstance_WithoutView() {
		var repoRes = this.getResourceFromPcmFacade(AbstractPcmCprTest.repositoryFileName);
		var repoEObj = repoRes.getContents().get(0);
		var repoEObjFragment = repoRes.getURIFragment(repoEObj);
		var repoEObjURI = repoRes.getURI().appendFragment(repoEObjFragment).toString();
		var repoEObjFragmentWithIndex = "/0";
		var repoEObjURIWithIndex = repoRes.getURI().appendFragment(repoEObjFragmentWithIndex).toString();

		// Ensure that the URI fragments are accurate and lead to the desired object
		Assertions.assertEquals(repoEObj, repoRes.getEObject(repoEObjFragment));
		Assertions.assertEquals(repoEObj, repoRes.getEObject(repoEObjFragmentWithIndex));

		var newEntityName = "en";

		var attrChange = AttributeFactory.eINSTANCE.createReplaceSingleValuedEAttribute();
		attrChange.setAffectedEObject(repoEObj);

		// TODO Clarify why only repoEObjURIWithIndex works as affectedEObjectID and not
		// repoEObjURI or repoEObjFragment
		attrChange.setAffectedEObjectID(repoEObjURIWithIndex);
		var attr = (EAttribute) repoEObj.eClass().getEStructuralFeature(RepositoryPackage.REPOSITORY__ENTITY_NAME);
		attrChange.setAffectedFeature(attr);
		attrChange.setOldValue(repoEObj.eGet(attr));
		attrChange.setNewValue(newEntityName);

		// Ensure that the change is not applied prior to propagation
		Assertions.assertEquals(1, repoRes.getContents().size());
		Assertions.assertEquals(repoEObj, repoRes.getContents().get(0));
		Assertions.assertNotEquals(newEntityName, repoEObj.eGet(attr));

		// Propagate the changes to repoRes, which results in applying the change to the
		// Resource in PcmFacade
		var props = propagatePcmChanges(repoRes, List.of(attrChange));
		// The propagation DOES NOT change the passed Resource instance (repoRes here)
		// It instead applies the change to the Resource inside the PcmFacade

		// Ensure that no exceptions occurred during propagation
		Assertions.assertNull(props.getException());
		this.logPropagatedChanges(props);

		// Ensure that the change was actually applied
		var propagatedResource = this.getResourceFromPcmFacade(AbstractPcmCprTest.repositoryFileName);
		Assertions.assertEquals(1, propagatedResource.getContents().size());
		var propagatedRepoEObj = propagatedResource.getContents().get(0);
		Assertions.assertEquals(newEntityName, propagatedRepoEObj.eGet(attr));

		// Ensure that the Resource is saved after changes are applied
		var res = this.loadNewResourceInstance(propagatedResource);

		// Ensure that the loaded Resource has the expected contents
		Assertions.assertEquals(1, res.getContents().size());
		var resRepoEObj = res.getContents().get(0);
		Assertions.assertTrue(EcoreUtil.equals(resRepoEObj, propagatedRepoEObj));
		Assertions.assertEquals(newEntityName, resRepoEObj.eGet(attr));
	}

	@Test
	public void testCprTrigger_OnSameResourceInstance_WithView() {
		final var newEntityName = "en";
		var mods = new HashMap<URI, Consumer<Resource>>();
		var repoRes = this.getResourceFromPcmFacade(AbstractPcmCprTest.repositoryFileName);
		mods.put(repoRes.getURI(), (r) -> {
			var repoEObj = (Repository) repoRes.getContents().get(0);
			repoEObj.setEntityName(newEntityName);
		});

		var propList = this.getPcmVsumFacade().modifyEObjects(getPcmFacade(), mods);

		// Ensure that no exceptions occurred during propagation
		Assertions.assertEquals(1, propList.size());
		var prop = propList.iterator().next();
		Assertions.assertNull(prop.getException());
		this.logPropagatedChanges(prop);

		// Ensure that there is only one original change (the one made above) and is the
		// expected one
		Assertions.assertEquals(1, prop.getOriginalChangeCount());
		var change = prop.getChanges().get(0).getOriginalChange().getEChanges().get(0);
		Assertions.assertTrue(ReplaceSingleValuedEAttribute.class.isAssignableFrom(change.getClass()));

		// Ensure that the change was actually applied
		var propagatedResource = this.getResourceFromPcmFacade(AbstractPcmCprTest.repositoryFileName);
		Assertions.assertEquals(1, propagatedResource.getContents().size());
		var propagatedRepoEObj = propagatedResource.getContents().get(0);
		Assertions.assertEquals(newEntityName, propagatedRepoEObj
				.eGet(propagatedRepoEObj.eClass().getEStructuralFeature(RepositoryPackage.REPOSITORY__ENTITY_NAME)));

		// Ensure that the Resource is saved after changes are applied
		var res = this.loadNewResourceInstance(propagatedResource);

		// Ensure that the loaded Resource has the expected contents
		Assertions.assertEquals(1, res.getContents().size());
		var resRepoEObj = res.getContents().get(0);
		Assertions.assertTrue(EcoreUtil.equals(resRepoEObj, propagatedRepoEObj));
		Assertions.assertEquals(newEntityName, resRepoEObj
				.eGet(resRepoEObj.eClass().getEStructuralFeature(RepositoryPackage.REPOSITORY__ENTITY_NAME)));
	}
}
