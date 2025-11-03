package cipm.consistency.vsum.test.pcm.cprunittests.dummy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.junit.jupiter.api.Assertions;

import cipm.consistency.cpr.pcmjava.userinteraction.CorrespondenceEntry;
import cipm.consistency.cpr.pcmjava.userinteraction.PcmUserInteractionManager;
import cipm.consistency.vsum.test.pcm.PcmVsumFacade;

public final class PcmCprAssertions {

	// TODO Add logging to all methods, so that assertion flow is clear

	private static final Logger LOGGER = Logger.getLogger(PcmCprAssertions.class);

	public static void assertJavaClassLayoutCorrect(Resource r) {
		var allClss = new HashSet<ConcreteClassifier>();
		var it = r.getAllContents();
		while (it.hasNext()) {
			var currentElem = it.next();
			if (currentElem instanceof ConcreteClassifier) {
				allClss.add((ConcreteClassifier) currentElem);
			}
		}
		for (var cls : allClss) {
			Assertions.assertNotNull(cls.getContainingCompilationUnit());
			assertJavaClassLayoutCorrectForJavaClassifier(r, cls.getContainingCompilationUnit().getNamespaces(),
					cls.getName());
		}
	}

	public static void assertJavaClassLayoutCorrectForJavaClassifier(Resource r, List<String> expectedNss,
			String expectedJavaClsName) {
		var rContents = r.getContents();

		var rPacs = rContents.stream().filter((c) -> c instanceof org.emftext.language.java.containers.Package)
				.map((c) -> (org.emftext.language.java.containers.Package) c).collect(Collectors.toUnmodifiableList());

		var rCUs = rContents.stream().filter((c) -> c instanceof org.emftext.language.java.containers.CompilationUnit)
				.map((c) -> (org.emftext.language.java.containers.CompilationUnit) c)
				.collect(Collectors.toUnmodifiableList());

		// Ensure that all necessary packages exist
		for (int i = 0; i < expectedNss.size(); i++) {
			var expectedNs = expectedNss.subList(0, i + 1);
			Assertions.assertTrue(rPacs.stream().anyMatch((p) -> namespacesEqual(p.getNamespaces(), expectedNs)));
		}

		// Ensure that the corresponding class exists
		var cuOpt = rCUs.stream().filter((cu) -> namespacesEqual(cu.getNamespaces(), expectedNss)).findFirst();
		Assertions.assertTrue(cuOpt.isPresent());
		Assertions.assertEquals(expectedJavaClsName, cuOpt.get().getName());
		Assertions.assertEquals(1, cuOpt.get().getClassifiers().size());
		var cls = cuOpt.get().getClassifiers().get(0);
		Assertions.assertEquals(expectedJavaClsName, cls.getName());
	}

	public static boolean namespacesEqual(List<String> nss1, List<String> nss2) {
		if (nss1.size() != nss2.size())
			return false;

		for (int i = 0; i < nss1.size(); i++) {
			if (!nss1.get(i).equals(nss2.get(i)))
				return false;
		}

		return true;
	}

	/**
	 * Ensures that eAllContent() of both resources yield equal EObjects in equal
	 * order.
	 */
	public static void assertAllContentsEqual(Resource res1, Resource res2) {
		var it1 = res1.getAllContents();
		var it2 = res2.getAllContents();

		var idx = 0;
		while (it1.hasNext() || it2.hasNext()) {
			if (it1.hasNext() ^ it2.hasNext())
				Assertions.fail("Given resources have differing eAllContents size");

			Assertions.assertTrue(EcoreUtil.equals(it1.next(), it2.next()),
					"Given resources eAllContents differ at iteration: " + idx);
			idx++;
		}
	}

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

	/**
	 * Ensures that all contents of the given resources are equal (wrt.
	 * {@link EcoreUtil#equals(List, List)}). Accounts for order differences as
	 * well.
	 */
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
		var featInObj = obj.eClass().getEStructuralFeature(feat.getName());
		if (featInObj == null)
			Assertions.fail(String.format("Given feature %s is not inside the given object of type %s", feat.getName(),
					obj.getClass()));

		var objFeatVal = obj.eGet(featInObj);
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
	public static void assertFeatureValueInPcmManagerConsistent(EObject triggeringPCMElement,
			EObject affectedJavaElement, EStructuralFeature affectedJavaElementFeat) {
		Assertions.assertTrue(
				PcmUserInteractionManager.hasDesiredFeatureValue(triggeringPCMElement, affectedJavaElementFeat));
		var pcmManagerFeatVal = PcmUserInteractionManager.getDesiredFeatureValue(triggeringPCMElement,
				affectedJavaElementFeat, false);

		Assertions.assertTrue(PcmUserInteractionManager.hasDesiredFeatureValue(triggeringPCMElement,
				affectedJavaElement, affectedJavaElementFeat));
		var fullPcmManagerFeatVal = PcmUserInteractionManager.getDesiredFeatureValue(triggeringPCMElement,
				affectedJavaElement, affectedJavaElementFeat, false);

		Assertions.assertEquals(pcmManagerFeatVal, fullPcmManagerFeatVal);

		// TODO Move inside if-block, in case of errors
		assertFeatureValueEquals(affectedJavaElement, affectedJavaElementFeat, pcmManagerFeatVal);

//		if (!affectedJavaElementFeat.isMany()) {
//		} else {
//			var correspondentFeatVal = (List<?>) affectedJavaElement
//					.eGet(affectedJavaElement.eClass().getEStructuralFeature(affectedJavaElementFeat.getName()));
//			var castedPcmManagerFeatVal = (List<?>) pcmManagerFeatVal;
//			Assertions.assertEquals(castedPcmManagerFeatVal.size(), correspondentFeatVal.size());
//			Assertions.assertTrue(castedPcmManagerFeatVal.containsAll(correspondentFeatVal));
//		}
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

	/**
	 * Ensures that the given correspondence and its counterpart stored in
	 * PcmUserInteractionManager are equal
	 */
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
		Assertions.assertTrue(PcmUserInteractionManager.getAllCompleteCorrespondences().isEmpty());
	}

	/**
	 * Ensures that there are no assigned features in PcmUserInteractionManager
	 */
	public static void assertNoFeaturesInPcmManager() {
		Assertions.assertTrue(PcmUserInteractionManager.getAllAssignedFeatures().isEmpty());
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
