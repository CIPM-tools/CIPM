package cipm.consistency.vsum.test.pcm.cprunittests.actual;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.resource.Resource;
import org.emftext.language.java.classifiers.ClassifiersFactory;
import org.emftext.language.java.commons.CommonsPackage;
import org.emftext.language.java.containers.ContainersFactory;
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
			List<String> expectedJavaClsNss) {

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

		var dataTypeFragment = dataType[0].eResource().getURIFragment(dataType[0]);

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
			var rDataType = r.getEObject(dataTypeFragment);
			PcmCprAssertions.assertFeatureValueEquals(rDataType, EntityPackage.Literals.NAMED_ELEMENT__ENTITY_NAME,
					dataTypeName);
		}, originalRepoRes, propagatedRepoResource, loadedRepoResource);

		// Ensure that consequential changes to Java are done too
		this.removePlaceholderInJavaModelResource();
		var persistedJavaResource = this.getJavaModelResourceFromJavaFacade();

		final var clsFragment = new String[1];

		PcmCprAssertions.assertForAllEqualResources((r) -> {
			var rContents = r.getContents();
			var rPacs = rContents.stream().filter((c) -> c instanceof org.emftext.language.java.containers.Package)
					.map((c) -> (org.emftext.language.java.containers.Package) c)
					.collect(Collectors.toUnmodifiableList());
			var rCUs = rContents.stream()
					.filter((c) -> c instanceof org.emftext.language.java.containers.CompilationUnit)
					.map((c) -> (org.emftext.language.java.containers.CompilationUnit) c)
					.collect(Collectors.toUnmodifiableList());

			// Ensure that all necessary packages exist
			for (int i = 0; i < expectedJavaClsNss.size(); i++) {
				var expectedNs = expectedJavaClsNss.subList(0, i + 1);
				Assertions.assertTrue(rPacs.stream().anyMatch((p) -> namespacesEqual(p.getNamespaces(), expectedNs)));
			}

			// Ensure that the corresponding class exists
			var cuOpt = rCUs.stream().filter((cu) -> namespacesEqual(cu.getNamespaces(), expectedJavaClsNss))
					.findFirst();
			Assertions.assertTrue(cuOpt.isPresent());
			Assertions.assertEquals(dataTypeName, cuOpt.get().getName());
			Assertions.assertEquals(1, cuOpt.get().getClassifiers().size());
			var cls = cuOpt.get().getClassifiers().get(0);
			Assertions.assertEquals(dataTypeName, cls.getName());
			clsFragment[0] = cls.eResource().getURIFragment(cls);
		}, javaResource, persistedJavaResource);

		// Ensure that correspondences are persistent
		var persistedDataType = propagatedRepoResource.getEObject(dataTypeFragment);
		var persistedJavaCls = persistedJavaResource.getEObject(clsFragment[0]);

		PcmCprAssertions.assertCorrespondenceInCorrespondenceView(getPcmVsumFacade(), persistedDataType,
				persistedJavaCls, "");
	}

	@Test
	public void withoutExistingJavaClass() {
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
	public void withExistingJavaClass() {
		var listCls = List.class;
		var dtName = listCls.getSimpleName();
		var nss = List.of(listCls.getPackageName().split("\\."));

		this.removePlaceholderInJavaModelResource();
		this.addClassToJavaModelResource(JavaModelAccess.getJavaModel(), dtName, nss);

		saveAndReloadJavaModelResource();

		var oldJavaResource = this.loadNewResourceInstance(JavaModelAccess.getJavaModel());

		this.dataTypeCreationTestTemplate(() -> RepositoryFactory.eINSTANCE.createCollectionDataType(), null, dtName,
				nss);

		var newJavaResource = JavaModelAccess.getJavaModel();

		// Ensure that no new Java element is created
		PcmCprAssertions.assertAllContentsEqual(oldJavaResource, newJavaResource);
		PcmCprAssertions.assertNoFeaturesInPcmManager();
	}

	@Test
	public void withMultipleExistingJavaClasses() {
		var listCls = List.class;
		var dtName = listCls.getSimpleName();

		var listPacNss = List.of(listCls.getPackageName().split("\\."));
		var nss1 = List.copyOf(listPacNss);
		var nss2 = new ArrayList<>(listPacNss);
		nss2.remove(nss2.size() - 1);
		nss2.add("special");

		this.removePlaceholderInJavaModelResource();

		// Add Java clss to a resource, so that their URI fragments can be found

		var cls1 = this.addClassToJavaModelResource(JavaModelAccess.getJavaModel(), dtName, nss1);
		var cls2 = this.addClassToJavaModelResource(JavaModelAccess.getJavaModel(), dtName, nss2);

		var cls1Fragment = cls1.eResource().getURIFragment(cls1);
		var cls2Fragment = cls2.eResource().getURIFragment(cls2);

		saveAndReloadJavaModelResource();

		final var cls1Final = (org.emftext.language.java.classifiers.Class) JavaModelAccess.getJavaModel()
				.getEObject(cls1Fragment);
		final var cls2Final = (org.emftext.language.java.classifiers.Class) JavaModelAccess.getJavaModel()
				.getEObject(cls2Fragment);

		var oldJavaResource = this.loadNewResourceInstance(JavaModelAccess.getJavaModel());

		this.dataTypeCreationTestTemplate(() -> RepositoryFactory.eINSTANCE.createCollectionDataType(),
				(dataType) -> new ConflictResolutionStrategy[] { new CorrespondenceInputConflictResolutionStrategy(
						List.of(dataType), List.of(cls1Final, cls2Final),
						Map.of(dataType.eResource().getURIFragment(dataType), List.of(cls2Fragment))) },
				dtName, nss2);

		var newJavaResource = JavaModelAccess.getJavaModel();

		// Ensure that no new Java element is created
		PcmCprAssertions.assertAllContentsEqual(oldJavaResource, newJavaResource);
	}

	@Test
	public void withExistingPackages() {
		var dtName = "pcmIfc";
		var nss = List.of("ns1", "ns2");
		final var dt = new DataType[1];

		var parentPac = this.addPackageToJavaModelResource(JavaModelAccess.getJavaModel(), nss.subList(0, 1));
		var childPac = this.addPackageToJavaModelResource(JavaModelAccess.getJavaModel(), nss.subList(0, 2));

		var parentPacFragment = parentPac.eResource().getURIFragment(parentPac);
		var childPacFragment = childPac.eResource().getURIFragment(childPac);

		this.removePlaceholderInJavaModelResource();

		saveAndReloadJavaModelResource();

		this.dataTypeCreationTestTemplate(() -> RepositoryFactory.eINSTANCE.createCollectionDataType(), (dataType) -> {
			dt[0] = dataType;
			return new ConflictResolutionStrategy[] { new FeatureInputConflictResolutionStrategy(List.of(dataType),
					null, List.of(CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES), List.of(nss)) };
		}, dtName, nss);

		PcmCprAssertions.assertFeatureValueInPcmManagerEquals(dt[0],
				CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES, nss);

		// There should be 2 packages and 1 compilation unit as root content
		Assertions.assertEquals(3, JavaModelAccess.getJavaModel().getContents().size());
		Assertions.assertEquals(2, JavaModelAccess.getJavaModel().getContents().stream()
				.filter((c) -> c instanceof org.emftext.language.java.containers.Package).count());
		Assertions.assertEquals(1, JavaModelAccess.getJavaModel().getContents().stream()
				.filter((c) -> c instanceof org.emftext.language.java.containers.CompilationUnit).count());

		Assertions.assertNotNull(JavaModelAccess.getJavaModel().getEObject(parentPacFragment));
		Assertions.assertEquals(dtName, ((org.emftext.language.java.containers.Package) JavaModelAccess.getJavaModel()
				.getEObject(parentPacFragment)).getClassifiers().get(0).getName());
		Assertions.assertNotNull(JavaModelAccess.getJavaModel().getEObject(childPacFragment));
	}

	@Test
	public void withExistingParentPackage() {
		var dtName = "pcmIfc";
		var nss = List.of("ns1", "ns2");
		final var dt = new DataType[1];

		var parentPac = this.addPackageToJavaModelResource(JavaModelAccess.getJavaModel(), nss.subList(0, 1));
		var parentPacFragment = parentPac.eResource().getURIFragment(parentPac);

		this.removePlaceholderInJavaModelResource();

		saveAndReloadJavaModelResource();

		this.dataTypeCreationTestTemplate(() -> RepositoryFactory.eINSTANCE.createCollectionDataType(), (dataType) -> {
			dt[0] = dataType;
			return new ConflictResolutionStrategy[] { new FeatureInputConflictResolutionStrategy(List.of(dataType),
					null, List.of(CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES), List.of(nss)) };
		}, dtName, nss);

		PcmCprAssertions.assertFeatureValueInPcmManagerEquals(dt[0],
				CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES, nss);

		// There should be 2 packages and 1 compilation unit as root content
		Assertions.assertEquals(3, JavaModelAccess.getJavaModel().getContents().size());
		Assertions.assertEquals(2, JavaModelAccess.getJavaModel().getContents().stream()
				.filter((c) -> c instanceof org.emftext.language.java.containers.Package).count());
		Assertions.assertEquals(1, JavaModelAccess.getJavaModel().getContents().stream()
				.filter((c) -> c instanceof org.emftext.language.java.containers.CompilationUnit).count());

		Assertions.assertNotNull(JavaModelAccess.getJavaModel().getEObject(parentPacFragment));
		Assertions.assertEquals(dtName, ((org.emftext.language.java.containers.Package) JavaModelAccess.getJavaModel()
				.getEObject(parentPacFragment)).getClassifiers().get(0).getName());
	}

	@Test
	public void withExistingModule() {
		var dtName = "pcmIfc";
		var nss = List.of("ns1", "ns2");
		final var dt = new DataType[1];

		var mod = this.addModuleToJavaModelResource(JavaModelAccess.getJavaModel(), nss.subList(0, 1), nss.get(1));
		this.removePlaceholderInJavaModelResource();
		// Segment building results in "/", if the module is the only content of the
		// resource fix it by appending a "0" to it, so that it contains the index of
		// the module "/0"
		var modFragment = mod.eResource().getURIFragment(mod) + "0";
		saveAndReloadJavaModelResource();

		this.dataTypeCreationTestTemplate(() -> RepositoryFactory.eINSTANCE.createCollectionDataType(), (dataType) -> {
			dt[0] = dataType;
			return new ConflictResolutionStrategy[] { new FeatureInputConflictResolutionStrategy(List.of(dataType),
					null, List.of(CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES), List.of(nss)) };
		}, dtName, nss);

		PcmCprAssertions.assertFeatureValueInPcmManagerEquals(dt[0],
				CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES, nss);

		// There should be 1 module, 2 packages and 1 compilation unit as root content
		Assertions.assertEquals(4, JavaModelAccess.getJavaModel().getContents().size());
		Assertions.assertEquals(1, JavaModelAccess.getJavaModel().getContents().stream()
				.filter((c) -> c instanceof org.emftext.language.java.containers.Module).count());
		Assertions.assertEquals(2, JavaModelAccess.getJavaModel().getContents().stream()
				.filter((c) -> c instanceof org.emftext.language.java.containers.Package).count());
		Assertions.assertEquals(1, JavaModelAccess.getJavaModel().getContents().stream()
				.filter((c) -> c instanceof org.emftext.language.java.containers.CompilationUnit).count());

		Assertions.assertNotNull(JavaModelAccess.getJavaModel().getEObject(modFragment));
		Assertions.assertEquals(dtName,
				((org.emftext.language.java.containers.Module) JavaModelAccess.getJavaModel().getEObject(modFragment))
						.getClassifiersInSamePackage().get(0).getName());
	}

	private void saveAndReloadJavaModelResource() {
		JavaModelAccess.saveJavaModel();
		this.getJavaFacade().reload();
		JavaModelAccess.setJavaModel(this.getJavaFacade().getResource());
	}

	private org.emftext.language.java.classifiers.Class addClassToJavaModelResource(Resource modelRes, String className,
			List<String> classNss) {
		var cls = ClassifiersFactory.eINSTANCE.createClass();
		cls.setName(className);
		var jrs = PcmJavaCPRUtils.addJavaClassifierIntoResource(modelRes, cls, classNss);
		modelRes.getContents().addAll(jrs);
		return cls;
	}

	private org.emftext.language.java.containers.Package addPackageToJavaModelResource(Resource modelRes,
			List<String> pacNss) {
		var pac = ContainersFactory.eINSTANCE.createPackage();
		pac.getNamespaces().addAll(pacNss);
		modelRes.getContents().add(pac);
		return pac;
	}

	private org.emftext.language.java.containers.Module addModuleToJavaModelResource(Resource modelRes,
			List<String> modNss, String modName) {
		var mod = ContainersFactory.eINSTANCE.createModule();
		mod.setName(modName);
		mod.getNamespaces().addAll(modNss);
		modelRes.getContents().add(mod);
		return mod;
	}
}
