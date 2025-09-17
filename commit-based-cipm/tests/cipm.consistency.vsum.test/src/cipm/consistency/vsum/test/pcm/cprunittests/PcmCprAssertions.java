package cipm.consistency.vsum.test.pcm.cprunittests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Assertions;

import cipm.consistency.vsum.test.pcm.PcmVsumFacade;
import cipm.consistency.vsum.test.pcm.userinteraction.CorrespondenceEntry;
import cipm.consistency.vsum.test.pcm.userinteraction.PcmUserInteractionManager;

public final class PcmCprAssertions {

	// TODO Add logging to all methods, so that assertion flow is clear

	private static final Logger LOGGER = Logger.getLogger(PcmCprAssertions.class);

	/**
	 * Ensures that the correspondence (obj1, obj2, tag) = (obj2, obj1, tag) exists
	 * within the correspondence view.
	 */
	public static void assertCorrespondenceInCorrespondenceView(PcmVsumFacade pcmVsum, EObject obj1, EObject obj2,
			String tag) {
		var corView = pcmVsum.getCorrespondenceView();
		var obj1Correspondents = corView.getCorrespondingEObjects(obj1, tag);
		var obj2Correspondents = corView.getCorrespondingEObjects(obj2, tag);

		Assertions.assertTrue(corView.hasCorrespondences(obj1));
		Assertions.assertTrue(obj2Correspondents.contains(obj1));

		Assertions.assertTrue(corView.hasCorrespondences(obj2));
		Assertions.assertTrue(obj1Correspondents.contains(obj2));
	}

	/**
	 * Ensures that all contents of the given resources are equal (wrt.
	 * {@link EcoreUtil#equals(List, List)}). Accounts for order differences as
	 * well.
	 */
	public static void assertResourceInstancesEqual(Resource res1, Resource res2) {
		LOGGER.debug(String.format("Computing content (and content order) equality of: %s vs %s", res1.getURI(),
				res2.getURI()));
		var res1Content = new ArrayList<EObject>();
		res1.getAllContents().forEachRemaining(res1Content::add);
		var res2Content = new ArrayList<EObject>();
		res2.getAllContents().forEachRemaining(res2Content::add);

		Assertions.assertEquals(res1Content.size(), res2Content.size());
		Assertions.assertTrue(EcoreUtil.equals(res1Content, res2Content));
		LOGGER.debug("Content (and content order) equal");
	}

	public static void assertAllResourceInstancesEqual(Resource... resources) {
		for (int i = 0; i < resources.length - 1; i++) {
			assertResourceInstancesEqual(resources[i], resources[i + 1]);
		}
	}

	/**
	 * Performs the (same) given assertions for all given Resource instances.
	 */
	public static void assertForAllResources(Consumer<Resource> assertions, Resource... resources) {
		for (var r : resources) {
			assertions.accept(r);
		}
	}

	/**
	 * Shorthand for {@link #assertForAllResources(Consumer, Resource...)} then
	 * {@link #assertAllResourceInstancesEqual(Resource...)}
	 */
	public static void assertForAllEqualResources(Consumer<Resource> assertions, Resource... resources) {
		assertForAllResources(assertions, resources);
		assertAllResourceInstancesEqual(resources);
	}

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
	 * {@code PcmUserInteractionManager.(triggeringPCMElement, affectedJavaElementFeat) =?= expectedValue}
	 * <li>
	 * {@code PcmUserInteractionManager.(triggeringPCMElement, affectedJavaElementFeat) =?= affectedJavaElement.affectedJavaElementFeat}
	 * </ul>
	 */
	public static void assertFeatureValueSetViaPcmManager(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature affectedJavaElementFeat, Object expectedValue) {
		assertFeatureValueInPcmManagerEquals(triggeringPCMElement, affectedJavaElementFeat, expectedValue);
		assertFeatureValueInPcmManagerConsistent(triggeringPCMElement, affectedJavaElement, affectedJavaElementFeat);
	}

	/**
	 * {@code PcmUserInteractionManager.(triggeringPCMElement, affectedJavaElementFeat) =?= expectedValue}
	 */
	public static void assertFeatureValueInPcmManagerEquals(EObject triggeringPCMElement,
			EStructuralFeature affectedJavaElementFeat, Object expectedValue) {
		Assertions.assertTrue(
				PcmUserInteractionManager.hasDesiredFeatureValue(triggeringPCMElement, affectedJavaElementFeat));
		var pcmManagerFeatVal = PcmUserInteractionManager.getDesiredFeatureValue(triggeringPCMElement,
				affectedJavaElementFeat, false);
		if (!affectedJavaElementFeat.isMany()) {
			Assertions.assertEquals(expectedValue, pcmManagerFeatVal);
		} else {
			var expectedFeatVal = (List<?>) expectedValue;
			var castedPcmManagerFeatVal = (List<?>) pcmManagerFeatVal;
			Assertions.assertEquals(expectedFeatVal.size(), castedPcmManagerFeatVal.size());
			Assertions.assertTrue(expectedFeatVal.containsAll(castedPcmManagerFeatVal));
		}
	}

	/**
	 * {@code PcmUserInteractionManager.(triggeringPCMElement, affectedJavaElementFeat) =?= affectedJavaElement.affectedJavaElementFeat}
	 */
	public static void assertFeatureValueInPcmManagerConsistent(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature affectedJavaElementFeat) {
		Assertions.assertTrue(
				PcmUserInteractionManager.hasDesiredFeatureValue(triggeringPCMElement, affectedJavaElementFeat));
		var pcmManagerFeatVal = PcmUserInteractionManager.getDesiredFeatureValue(triggeringPCMElement,
				affectedJavaElementFeat, false);

		Assertions.assertTrue(PcmUserInteractionManager.hasDesiredFeatureValue(triggeringPCMElement,
				affectedJavaElement, affectedJavaElementFeat));
		var fullPcmManagerFeatVal = PcmUserInteractionManager.getDesiredFeatureValue(triggeringPCMElement,
				affectedJavaElement, affectedJavaElementFeat, false);

		Assertions.assertEquals(pcmManagerFeatVal, fullPcmManagerFeatVal);

		if (!affectedJavaElementFeat.isMany()) {
			assertFeatureValueEquals(affectedJavaElement, affectedJavaElementFeat, pcmManagerFeatVal);
		} else {
			var correspondentFeatVal = (List<?>) affectedJavaElement.eGet(affectedJavaElementFeat);
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
			var tag = corEntry.getCorrespondenceTag();

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
