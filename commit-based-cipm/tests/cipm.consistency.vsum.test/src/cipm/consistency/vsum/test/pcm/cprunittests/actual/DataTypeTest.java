package cipm.consistency.vsum.test.pcm.cprunittests.actual;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.emftext.language.java.commons.CommonsPackage;
import org.emftext.language.java.containers.CompilationUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.core.entity.EntityPackage;
import org.palladiosimulator.pcm.core.entity.NamedElement;
import org.palladiosimulator.pcm.repository.DataType;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryFactory;

import cipm.consistency.cpr.pcmjava.JavaModelAccess;
import cipm.consistency.cpr.pcmjava.userinteraction.ConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.CorrespondenceInputConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.FeatureInputConflictResolutionStrategy;
import cipm.consistency.cpr.pcmjava.userinteraction.PcmUserInteractionManager;
import cipm.consistency.vsum.test.pcm.cprunittests.dummy.PcmCprAssertions;
import mir.reactions.allRepository.AllRepositoryChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

public class DataTypeTest extends AbstractClassifierTest {
	@Override
	protected List<ChangePropagationSpecification> getCPRs() {
		return List.of(new AllRepositoryChangePropagationSpecification());
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
		var persistedDataType = propagatedRepoResource.getEObject(dataTypeFragment);
		var persistedJavaClsCU = persistedJavaResource.getContents().stream()
				.filter((c) -> (c instanceof CompilationUnit)
						&& PcmCprAssertions.namespacesEqual(((CompilationUnit) c).getNamespaces(), expectedJavaClsNss))
				.map((c) -> ((CompilationUnit) c)).findFirst();
		Assertions.assertTrue(persistedJavaClsCU.isPresent());
		Assertions.assertEquals(1, persistedJavaClsCU.get().getClassifiers().size());
		var persistedJavaCls = persistedJavaClsCU.get().getClassifiers().get(0);
		Assertions.assertEquals(dataTypeName, persistedJavaCls.getName());

		PcmCprAssertions.assertForAllEqualResources((r) -> PcmCprAssertions.assertJavaClassLayoutCorrect(r),
				javaResource, persistedJavaResource);

		// Ensure that correspondences are persistent
		PcmCprAssertions.assertCorrespondenceInCorrespondenceView(getPcmVsumFacade(), persistedDataType,
				persistedJavaCls, "");
	}

	@SuppressWarnings("unchecked")
	private <T extends DataType & NamedElement> void forEachDataType(Consumer<T> r) {
		var dts = List.of(RepositoryFactory.eINSTANCE.createCompositeDataType());
		this.tearDown();
		for (var dt : dts) {
			this.setup();
			r.accept((T) dt);
			this.tearDown();
		}
	}

	@Test
	public void withoutExistingJavaClass() {
		var dtName = "pcmIfc";
		var nss = List.of("ns1", "ns2");

		forEachDataType((d) -> {
			final var dt = new DataType[1];
			this.dataTypeCreationTestTemplate(() -> d, (dataType) -> {
				dt[0] = dataType;
				return new ConflictResolutionStrategy[] { new FeatureInputConflictResolutionStrategy(List.of(dataType),
						null, List.of(CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES), List.of(nss)) };
			}, dtName, nss);

			PcmCprAssertions.assertFeatureValueInPcmManagerEquals(dt[0],
					CommonsPackage.Literals.NAMESPACE_AWARE_ELEMENT__NAMESPACES, nss);
		});
	}

	@Test
	public void withExistingJavaClass() {
		var listCls = ArrayList.class;
		var dtName = listCls.getSimpleName();
		var nss = List.of(listCls.getPackageName().split(namespaceSeparatorRegex));

		forEachDataType((d) -> {
			this.removePlaceholderInJavaModelResource();
			this.addClassToJavaModelResource(JavaModelAccess.getJavaModel(), dtName, nss);

			saveAndReloadJavaModelResource();

			var oldJavaResource = this.loadNewResourceInstance(JavaModelAccess.getJavaModel());

			this.dataTypeCreationTestTemplate(() -> d, null, dtName, nss);

			var newJavaResource = JavaModelAccess.getJavaModel();

			// Ensure that no new Java element is created
			PcmCprAssertions.assertAllContentsEqual(oldJavaResource, newJavaResource);
			PcmCprAssertions.assertNoFeaturesInPcmManager();
		});
	}

	@Test
	public void withMultipleExistingJavaClasses() {
		var listCls = ArrayList.class;
		var dtName = listCls.getSimpleName();
		var extraNs = "special";
		var listPacNss = List.of(listCls.getPackageName().split(namespaceSeparatorRegex));
		var nss1 = List.copyOf(listPacNss);
		var nss2 = new ArrayList<>(listPacNss);
		nss2.remove(nss2.size() - 1);
		nss2.add(extraNs);

		forEachDataType((d) -> {
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

			this.dataTypeCreationTestTemplate(() -> d,
					(dataType) -> new ConflictResolutionStrategy[] { new CorrespondenceInputConflictResolutionStrategy(
							List.of(dataType), List.of(cls1Final, cls2Final),
							Map.of(dataType.eResource().getURIFragment(dataType), List.of(cls2Fragment))) },
					dtName, nss2);

			var newJavaResource = JavaModelAccess.getJavaModel();

			// Ensure that no new Java element is created
			PcmCprAssertions.assertAllContentsEqual(oldJavaResource, newJavaResource);
		});
	}

	@Test
	public void withExistingPackages() {
		var dtName = "pcmIfc";
		var nss = List.of("ns1", "ns2");

		forEachDataType((d) -> {
			final var dt = new DataType[1];
			var parentPac = this.addPackageToJavaModelResource(JavaModelAccess.getJavaModel(), nss.subList(0, 1));
			var childPac = this.addPackageToJavaModelResource(JavaModelAccess.getJavaModel(), nss.subList(0, 2));

			var parentPacFragment = parentPac.eResource().getURIFragment(parentPac);
			var childPacFragment = childPac.eResource().getURIFragment(childPac);

			this.removePlaceholderInJavaModelResource();

			saveAndReloadJavaModelResource();

			this.dataTypeCreationTestTemplate(() -> d, (dataType) -> {
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
			Assertions.assertNotNull(JavaModelAccess.getJavaModel().getEObject(childPacFragment));
		});
	}

	@Test
	public void withExistingParentPackage() {
		var dtName = "pcmIfc";
		var nss = List.of("ns1", "ns2");

		forEachDataType((d) -> {
			final var dt = new DataType[1];

			var parentPac = this.addPackageToJavaModelResource(JavaModelAccess.getJavaModel(), nss.subList(0, 1));
			var parentPacFragment = parentPac.eResource().getURIFragment(parentPac);

			this.removePlaceholderInJavaModelResource();

			saveAndReloadJavaModelResource();

			this.dataTypeCreationTestTemplate(() -> d, (dataType) -> {
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
		});
	}

	@Test
	public void withExistingModule() {
		var dtName = "pcmIfc";
		var nss = List.of("ns1", "ns2");

		forEachDataType((d) -> {
			final var dt = new DataType[1];

			var mod = this.addModuleToJavaModelResource(JavaModelAccess.getJavaModel(), nss);
			this.removePlaceholderInJavaModelResource();

			var fullyQualifiedClsName = mod.getNamespacesAsString() + dtName;

			var modFragment = mod.eResource().getURIFragment(mod);
			saveAndReloadJavaModelResource();

			this.dataTypeCreationTestTemplate(() -> d, (dataType) -> {
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
			Assertions.assertEquals(dtName, ((org.emftext.language.java.containers.Module) JavaModelAccess
					.getJavaModel().getEObject(modFragment)).getConcreteClassifier(fullyQualifiedClsName).getName());
		});
	}
}
