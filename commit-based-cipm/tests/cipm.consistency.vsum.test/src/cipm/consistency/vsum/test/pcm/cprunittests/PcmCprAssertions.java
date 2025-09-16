package cipm.consistency.vsum.test.pcm.cprunittests;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Assertions;

import cipm.consistency.vsum.test.pcm.PcmVsumFacade;
import cipm.consistency.vsum.test.pcm.userinteraction.CorrespondenceEntry;
import cipm.consistency.vsum.test.pcm.userinteraction.PcmUserInteractionManager;

public final class PcmCprAssertions {
	/**
	 * {@code obj.feat =?= expectedValue}
	 */
	public static void assertFeatureValueEquals(EObject obj, EStructuralFeature feat, Object expectedValue) {
		var objFeatVal = obj.eGet(feat);
		if (!feat.isMany()) {
			Assertions.assertEquals(expectedValue, objFeatVal);
		} else {
			var expectedFeatVal = (List<?>) expectedValue;
			var castedObjFeatVal = (List<?>) objFeatVal;
			Assertions.assertEquals(expectedFeatVal.size(), castedObjFeatVal.size());
			Assertions.assertTrue(expectedFeatVal.containsAll(castedObjFeatVal));
		}
	}

	/**
	 * <ul>
	 * <li>
	 * {@code PcmUserInteractionManager.(triggeringEObj, feat) =?= expectedValue}
	 * <li>
	 * {@code PcmUserInteractionManager.(triggeringEObj, feat) =?= correspondent.feat}
	 * </ul>
	 */
	public static void assertFeatureValueSetViaPcmManager(EObject triggeringEObj, EObject correspondent,
			EStructuralFeature feat, Object expectedValue) {
		Assertions.assertTrue(PcmUserInteractionManager.hasDesiredFeatureValue(triggeringEObj, feat));
		assertFeatureValueInPcmManagerEquals(triggeringEObj, feat, expectedValue);
		assertActualFeatureValueAndPcmManagerConsistent(triggeringEObj, correspondent, feat);
	}

	/**
	 * {@code PcmUserInteractionManager.(triggeringEObj, feat) =?= expectedValue}
	 */
	public static void assertFeatureValueInPcmManagerEquals(EObject triggeringEObj, EStructuralFeature feat,
			Object expectedValue) {
		var pcmManagerFeatVal = PcmUserInteractionManager.getDesiredFeatureValue(triggeringEObj, feat, false);
		if (!feat.isMany()) {
			Assertions.assertEquals(expectedValue, pcmManagerFeatVal);
		} else {
			var expectedFeatVal = (List<?>) expectedValue;
			var castedPcmManagerFeatVal = (List<?>) pcmManagerFeatVal;
			Assertions.assertEquals(expectedFeatVal.size(), castedPcmManagerFeatVal.size());
			Assertions.assertTrue(expectedFeatVal.containsAll(castedPcmManagerFeatVal));
		}
	}

	/**
	 * {@code PcmUserInteractionManager.(triggeringEObj, feat) =?= correspondent.feat}
	 */
	public static void assertActualFeatureValueAndPcmManagerConsistent(EObject triggeringEObj, EObject correspondent,
			EStructuralFeature feat) {
		var pcmManagerFeatVal = PcmUserInteractionManager.getDesiredFeatureValue(triggeringEObj, feat, false);
		if (!feat.isMany()) {
			assertFeatureValueEquals(correspondent, feat, pcmManagerFeatVal);
		} else {
			var correspondentFeatVal = (List<?>) correspondent.eGet(feat);
			var castedPcmManagerFeatVal = (List<?>) pcmManagerFeatVal;
			Assertions.assertEquals(castedPcmManagerFeatVal.size(), correspondentFeatVal.size());
			Assertions.assertTrue(castedPcmManagerFeatVal.containsAll(correspondentFeatVal));
		}
	}

	/**
	 * Ensures that both collections contain equal, unique (wrt.
	 * {@link EcoreUtil#equals(Object)}) EObjects
	 */
	public static void assertContainsEqualEObjects(Collection<EObject> col1, Collection<EObject> col2) {
		Assertions.assertEquals(col1.size(), col2.size());
		for (var obj1 : col1) {
			assertContainsOnlyOneEqualEObject(obj1, col2);
		}
		for (var obj2 : col2) {
			assertContainsOnlyOneEqualEObject(obj2, col1);
		}
	}

	/**
	 * Ensures that there is only one EObject equal (wrt.
	 * {@link EcoreUtil#equals(Object)}) to seekEqualFor in the given col.
	 */
	public static void assertContainsOnlyOneEqualEObject(EObject seekEqualFor, Collection<EObject> col) {
		var matches = col.stream().filter((o) -> EcoreUtil.equals(seekEqualFor, o)).toArray(EObject[]::new);
		if (matches.length == 0) {
			Assertions.fail("Given col contains no equal EObject");
		} else if (matches.length > 1) {
			Assertions.fail("Given col contains multiple equal EObjects");
		}
	}

	/**
	 * Ensures that the given correspondences and the ones stored in
	 * PcmUserInteractionManager are equal
	 */
	public static void assertCorrespondenceViewAndPcmManagerConsistent(PcmVsumFacade pcmVsum,
			Collection<CorrespondenceEntry> cors) {
		var postPropCorView = pcmVsum.getCorrespondenceView();
		if (cors.isEmpty()) {
			assertNoCorrespondencesSaved(pcmVsum);
			assertNoCorrespondencesInPcmManager();
			return;
		}

		for (var corEntry : cors) {
			var knownSide = corEntry.getKnownElement();
			var correspondents = corEntry.getCorrespondentsForKnownElement();
			var tag = corEntry.getTag();

			var knownSidePcmManagerCorEntry = PcmUserInteractionManager.getDesiredCorrespondence(knownSide, tag, false);
			var knownSideCorViewCors = postPropCorView.getCorrespondingEObjects(knownSide);

			if (knownSidePcmManagerCorEntry == null) {
				Assertions.assertTrue(knownSideCorViewCors.isEmpty());
				Assertions.assertTrue(correspondents.isEmpty());
				continue;
			}

			var knownSidePcmManagerCors = knownSidePcmManagerCorEntry.getCorrespondentsForKnownElement();
			Assertions.assertEquals(correspondents.size(), knownSidePcmManagerCors.size());
			Assertions.assertEquals(correspondents.size(), knownSideCorViewCors.size());
			assertContainsEqualEObjects(knownSideCorViewCors, knownSidePcmManagerCors);

			assertCorrespondenceViewAndPcmManagerConsistent(pcmVsum, knownSide, tag);
		}
	}

	public static void assertCorrespondenceViewAndPcmManagerConsistent(PcmVsumFacade pcmVsum, EObject knownSide,
			String tag) {
		var knownSidePcmManagerCorEntry = PcmUserInteractionManager.getDesiredCorrespondence(knownSide, tag, false);
		var knownSideCorView = pcmVsum.getCorrespondenceView();
		if (knownSidePcmManagerCorEntry == null) {
			Assertions.assertTrue(knownSideCorView.getCorrespondingEObjects(knownSide, tag).isEmpty());
			return;
		}

		var knownSidePcmManagerCors = knownSidePcmManagerCorEntry.getCorrespondentsForKnownElement();
		var knownSideCorViewCors = knownSideCorView.getCorrespondingEObjects(knownSide);
		assertContainsEqualEObjects(knownSideCorViewCors, knownSidePcmManagerCors);
	}

	/**
	 * Ensures that there are no complete correspondences in
	 * PcmUserInteractionManager
	 */
	public static void assertNoCorrespondencesInPcmManager() {
		Assertions.assertEquals(0, PcmUserInteractionManager.getAllCompleteCorrespondences());
	}

	/**
	 * Ensures that there are no assigned features in PcmUserInteractionManager
	 */
	public static void assertNoFeaturesInPcmManager() {
		Assertions.assertEquals(0, PcmUserInteractionManager.getAllAssignedFeatures());
	}

	/**
	 * Ensures that there are no persisted correspondences. Checks the Resource file
	 * for correspondences.
	 */
	public static void assertNoCorrespondencesSaved(PcmVsumFacade pcmVsum) {
		var correspondencesURI = pcmVsum.getDirLayout().getVsumCorrespondenceModelUri();
		var correspondencesPath = pcmVsum.getDirLayout().getVsumCorrespondenceModelPath();

		// No correspondence file => No correspondences saved
		if (!correspondencesPath.toFile().exists())
			return;

		var corRes = new ResourceSetImpl().createResource(correspondencesURI);
		try {
			corRes.load(null);
		} catch (IOException e) {
			Assertions.fail(e);
		}

		/*
		 * The correspondence Resource file may have a single
		 * "correspondence:Correspondences" object as root. If it does, check whether
		 * there are any correspondences nested within. If there are nested
		 * correspondences, fail assertions.
		 */
		var corResContent = corRes.getContents();
		if (corResContent.size() == 1) {
			var correspondencesObj = corResContent.get(0);
			Assertions.assertTrue(correspondencesObj.eContents().isEmpty());
		} else if (corResContent.size() > 1) {
			Assertions.fail("More than one root content detected in correspondence Resource");
		}

		// Unload the created Resource instance to free up memory space
		corRes.unload();
	}

}
