package cipm.consistency.vsum.test.pcm.cprunittests.actual;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.resource.Resource;
import org.emftext.language.java.classifiers.ClassifiersFactory;
import org.emftext.language.java.commons.CommonsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.core.entity.EntityPackage;
import org.palladiosimulator.pcm.core.entity.NamedElement;
import org.palladiosimulator.pcm.repository.DataType;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryFactory;

import cipm.consistency.cpr.pcmjava.JavaModelAccess;
import cipm.consistency.cpr.pcmjava.PcmJavaCPRUtils;
import cipm.consistency.cpr.pcmjava.userinteraction.ConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.CorrespondenceInputConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.FeatureInputConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.PcmUserInteractionManager;
import cipm.consistency.vsum.test.pcm.cprunittests.AbstractPcmJavaCprTest;
import cipm.consistency.vsum.test.pcm.cprunittests.dummy.PcmCprAssertions;
import mir.reactions.allRepository.AllRepositoryChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

public class DataTypeTest extends AbstractPcmJavaCprTest {

	@Override
	protected List<ChangePropagationSpecification> getCPRs() {
		return List.of(new AllRepositoryChangePropagationSpecification());
	}

	// TODO Test creating a new Java class with pre-existing packages
	//
	// TODO Test creating a new Java class with pre-existing modules

	private boolean namespacesEqual(List<String> nss1, List<String> nss2) {
		if (nss1.size() != nss2.size())
			return false;

		for (int i = 0; i < nss1.size(); i++) {
			if (!nss1.get(i).equals(nss2.get(i)))
				return false;
		}

		return true;
	}

	public <T extends DataType & NamedElement> void dataTypeCreationTestTemplate(Supplier<T> dataTypeFac,
			Function<T, ConflictResolutionStrategy[]> createdDataTypeToStratFunc, String dataTypeName,
			List<String> javaClsNss) {

		var javaResource = this.getJavaModelResourceFromJavaFacade();

		final var dataType = new DataType[1];

		var originalRepoRes = this.getResourceFromPcmFacade(repositoryFileName);

		var changes = this.getEChangesFor(originalRepoRes, (r) -> {
			var dt = dataTypeFac.get();
			dt.setEntityName(dataTypeName);
			var rRepoEObj = (Repository) r.getContents().get(0);
			rRepoEObj.getDataTypes__Repository().add(dt);
			dataType[0] = dt;
		});

		if (createdDataTypeToStratFunc != null) {
			@SuppressWarnings("unchecked")
			var strats = createdDataTypeToStratFunc.apply((T) dataType[0]);
			for (var strat : strats) {
				PcmUserInteractionManager.addConflictResolutionStrategy(strat);
			}
		}

		this.propagateChangesToResource(originalRepoRes, changes);

		var propagatedRepoResource = this.getResourceFromPcmFacade(repositoryFileName);
		var loadedRepoResource = this.loadNewResourceInstance(propagatedRepoResource);

		// Ensure that the change was actually applied to PCM
		// Ensure that the Resource is saved after changes are applied
		// Ensure that the loaded Resource has the expected contents
		PcmCprAssertions.assertForAllEqualResources((r) -> {
			Assertions.assertEquals(1, r.getContents().size());
			var rRepoEObj = r.getContents().get(0);
			Assertions.assertEquals(1, rRepoEObj.eContents().size());
			var rDataType = rRepoEObj.eContents().get(0);
			PcmCprAssertions.assertFeatureValueEquals(rDataType, EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME,
					dataTypeName);
		}, originalRepoRes, propagatedRepoResource, loadedRepoResource);

		// Ensure that consequential changes to Java are done too
		this.removePlaceholderInJavaModelResource();
		var persistedJavaResource = this.getJavaModelResourceFromJavaFacade();

		PcmCprAssertions.assertForAllEqualResources((r) -> {
			var rContents = r.getContents();
			var rPacs = rContents.stream().filter((c) -> c instanceof org.emftext.language.java.containers.Package)
					.map((c) -> (org.emftext.language.java.containers.Package) c)
					.collect(Collectors.toUnmodifiableList());
			var rCUs = rContents.stream()
					.filter((c) -> c instanceof org.emftext.language.java.containers.CompilationUnit)
					.map((c) -> (org.emftext.language.java.containers.CompilationUnit) c)
					.collect(Collectors.toUnmodifiableList());

			Assertions.assertEquals(javaClsNss.size(), rPacs.size());
			for (int i = 0; i < javaClsNss.size(); i++) {
				var expectedNs = javaClsNss.subList(0, i + 1);
				Assertions.assertTrue(rPacs.stream().anyMatch((p) -> namespacesEqual(p.getNamespaces(), expectedNs)));
			}

			Assertions.assertEquals(1, rCUs.size());
			var cu = rCUs.get(0);
			Assertions.assertTrue(namespacesEqual(cu.getNamespaces(), javaClsNss));
			Assertions.assertEquals(1, cu.getClassifiers().size());

			var cls = cu.getClassifiers().get(0);
			Assertions.assertTrue(cls.getName().equals(dataTypeName));
		}, javaResource, persistedJavaResource);

		// Ensure that correspondences are persistent
		var persistedDataType = propagatedRepoResource.getContents().get(0).eContents().get(0);
		var persistedJavaCls = persistedJavaResource.getContents().stream()
				.filter((c) -> c instanceof org.emftext.language.java.containers.CompilationUnit)
				.map((c) -> (org.emftext.language.java.containers.CompilationUnit) c).findFirst().get().getClassifiers()
				.get(0);

		PcmCprAssertions.assertCorrespondenceInCorrespondenceView(getPcmVsumFacade(), persistedDataType,
				persistedJavaCls, "");

	}

	@Test
	public void collectionDataTypeCreationTest_WithoutExistingJavaClass() {
		var dtName = "pcmIfc";
		var nss = List.of("ns1", "ns2");
		final var dt = new DataType[1];

		this.dataTypeCreationTestTemplate(() -> RepositoryFactory.eINSTANCE.createCollectionDataType(), (dataType) -> {
			dt[0] = dataType;
			return new ConflictResolutionStrategy[] { new FeatureInputConflictResolutionStrategy(List.of(dataType),
					null, List.of(CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES), List.of(nss)) };
		}, dtName, nss);

		PcmCprAssertions.assertFeatureValueInPcmManagerEquals(dt[0],
				CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES, nss);
	}

	@Test
	public void collectionDataTypeCreationTest_WithExistingJavaClass() {
		var listCls = List.class;
		var dtName = listCls.getSimpleName();
		var nss = List.of(listCls.getPackageName().split("\\."));

		this.removePlaceholderInJavaModelResource();
		this.addClassToJavaModelResource(JavaModelAccess.getJavaModel(), dtName, nss);
		JavaModelAccess.saveJavaModel();
		this.getJavaFacade().reload();

		var oldJavaResource = this.loadNewResourceInstance(JavaModelAccess.getJavaModel());

		this.dataTypeCreationTestTemplate(() -> RepositoryFactory.eINSTANCE.createCollectionDataType(), null, dtName,
				nss);

		var newJavaResource = JavaModelAccess.getJavaModel();

		// Ensure that no new Java element is created
		PcmCprAssertions.assertAllContentsEqual(oldJavaResource, newJavaResource);
		PcmCprAssertions.assertNoFeaturesInPcmManager();
	}

	@Test
	public void collectionDataTypeCreationTest_WithMultipleExistingJavaClasses() {
		var listCls = List.class;
		var dtName = listCls.getSimpleName();

		var nss1 = List.of(listCls.getPackageName().split("\\."));
		var nss2 = List.of("java", "util", "special");

		this.removePlaceholderInJavaModelResource();
		
		// Add Java clss to a resource, so that their URI fragments can be found
		
		var cls1 = this.addClassToJavaModelResource(JavaModelAccess.getJavaModel(), dtName, nss1);
		var cls2 = this.addClassToJavaModelResource(JavaModelAccess.getJavaModel(), dtName, nss2);
		JavaModelAccess.saveJavaModel();
		this.getJavaFacade().reload();

		var oldJavaResource = this.loadNewResourceInstance(JavaModelAccess.getJavaModel());

		this.dataTypeCreationTestTemplate(() -> RepositoryFactory.eINSTANCE.createCollectionDataType(),
				(dataType) -> new ConflictResolutionStrategy[] { new CorrespondenceInputConflictResolutionStrategy(
						List.of(dataType), List.of(cls1, cls2), Map.of(dataType.eResource().getURIFragment(dataType),
								List.of(cls2.eResource().getURIFragment(cls2)))) },
				dtName, nss2);

		var newJavaResource = JavaModelAccess.getJavaModel();

		// Ensure that no new Java element is created
		PcmCprAssertions.assertAllContentsEqual(oldJavaResource, newJavaResource);
	}

	private org.emftext.language.java.classifiers.Class addClassToJavaModelResource(Resource modelRes, String className,
			List<String> classNss) {
		var cls = ClassifiersFactory.eINSTANCE.createClass();
		cls.setName(className);
		var jrs = PcmJavaCPRUtils.addJavaClassifierIntoResource(modelRes, cls, classNss);
		modelRes.getContents().addAll(jrs);
		return cls;
	}
}
