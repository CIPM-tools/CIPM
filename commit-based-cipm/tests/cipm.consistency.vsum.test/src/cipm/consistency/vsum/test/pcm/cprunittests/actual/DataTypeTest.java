package cipm.consistency.vsum.test.pcm.cprunittests.actual;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.core.entity.EntityPackage;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryFactory;

import cipm.consistency.vsum.test.pcm.cprunittests.AbstractPcmJavaCprTest;
import cipm.consistency.vsum.test.pcm.cprunittests.dummy.PcmCprAssertions;
import mir.reactions.allRepository.AllRepositoryChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

public class DataTypeTest extends AbstractPcmJavaCprTest {

	@Override
	protected List<ChangePropagationSpecification> getCPRs() {
		return List.of(new AllRepositoryChangePropagationSpecification());
	}

	@Test
	public void dataTypeCreationTest() {
		var javaResource = this.getJavaModelResourceFromJavaFacade();
//		Assertions.assertEquals(0, javaResource.getContents().size());

		final var dataTypeName = "pcmIfc";

		var originalRepoRes = this.getResourceFromPcmFacade(repositoryFileName);

		var changes = this.getEChangesFor(originalRepoRes, (r) -> {
			final var dataType = RepositoryFactory.eINSTANCE.createCollectionDataType();
			dataType.setEntityName(dataTypeName);
			var rRepoEObj = (Repository) r.getContents().get(0);
			rRepoEObj.getDataTypes__Repository().add(dataType);
		});

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

			Assertions.assertEquals(3, rContents.size());

			Assertions.assertEquals(2, rPacs.size());
			Assertions.assertTrue(rPacs.stream().anyMatch((p) -> p.getNamespacesAsString().equals("ns1.")));
			Assertions.assertTrue(rPacs.stream().anyMatch((p) -> p.getNamespacesAsString().equals("ns1.ns2.")));

			Assertions.assertEquals(1, rCUs.size());
			var cu = rCUs.get(0);
			Assertions.assertTrue(cu.getNamespacesAsString().equals("ns1.ns2."));
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
}
