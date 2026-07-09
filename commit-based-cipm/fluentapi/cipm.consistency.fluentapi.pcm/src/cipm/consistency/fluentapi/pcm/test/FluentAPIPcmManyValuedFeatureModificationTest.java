package cipm.consistency.fluentapi.pcm.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.core.entity.EntityPackage;
import org.palladiosimulator.pcm.core.entity.ResourceRequiredRole;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.RepositoryPackage;

import cipm.consistency.fluentapi.pcm.api.ApiFactory;

/**
 * A test class containing test cases for the fluent api generated for the PCM
 * metamodel.
 * <p>
 * <p>
 * The main purpose of this test class is to show that many-valued feature
 * modifications (in PCM) are not problematic in realistic cases, although they
 * currently fail in metamodel tests of the fluent api for PCM.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIPcmManyValuedFeatureModificationTest {
	/**
	 * Ensures that adding an array of values to a many-valued PCM feature via its
	 * initialisation class works as intended.
	 */
	@Test
	public void testInit_WithAddedArray() {
		var api = ApiFactory.eINSTANCE.createFluentPcmAPI();
		var repo = api.newRepository()
				.withAddedComponents__Repository(
						new RepositoryComponent[] { api.createNewBasicComponent(), api.createNewBasicComponent() })
				.createNow();
		Assertions.assertEquals(2, repo.getComponents__Repository().size());
	}

	/**
	 * Ensures that adding an array of values to a many-valued PCM feature via the
	 * abstract (super) initialisation class works as intended.
	 */
	@Test
	public void testSuperInit_WithAddedArray() {
		var api = ApiFactory.eINSTANCE.createFluentPcmAPI();
		var repo = api.createNewRepository();
		api.modifyRepository(repo).xWithAddedFeat(RepositoryPackage.Literals.REPOSITORY__COMPONENTS_REPOSITORY,
				new RepositoryComponent[] { api.createNewBasicComponent(), api.createNewBasicComponent() });
		Assertions.assertEquals(2, repo.getComponents__Repository().size());
	}

	/**
	 * Ensures that adding an array of values to a many-valued PCM feature via the
	 * fluent api class works as intended.
	 */
	@Test
	public void testApi_WithAddedArray() {
		var api = ApiFactory.eINSTANCE.createFluentPcmAPI();
		var repo = api.createNewRepository();
		api.xWithAddedFeat(repo, RepositoryPackage.Literals.REPOSITORY__COMPONENTS_REPOSITORY,
				new RepositoryComponent[] { api.createNewBasicComponent(), api.createNewBasicComponent() });
		Assertions.assertEquals(2, repo.getComponents__Repository().size());
	}

	/**
	 * This is a failing test scenario in metamodel tests for fluent api for PCM,
	 * but it works here => The initial value of
	 * NewResourceInterfaceRequiringEntity.getResourceRequiredRoles__ResourceInterfaceRequiringEntity
	 * is not appropriate
	 */
	@Test
	public void testApi_WithAddedArray_Failing() {
		var api = ApiFactory.eINSTANCE.createFluentPcmAPI();
		var obj = api.createNewResourceInterfaceRequiringEntity();

		api.xWithAddedFeat(obj,
				EntityPackage.Literals.RESOURCE_INTERFACE_REQUIRING_ENTITY__RESOURCE_REQUIRED_ROLES_RESOURCE_INTERFACE_REQUIRING_ENTITY,
				new ResourceRequiredRole[] { api.createNewResourceRequiredRole(),
						api.createNewResourceRequiredRole() });
		Assertions.assertEquals(2, obj.getResourceRequiredRoles__ResourceInterfaceRequiringEntity().size());
	}

	/**
	 * This is a failing test scenario in metamodel tests for fluent api for PCM,
	 * but it works here => The initial value of
	 * NewResourceInterfaceRequiringEntity.getResourceRequiredRoles__ResourceInterfaceRequiringEntity
	 * is not appropriate
	 */
	@Test
	public void testSuperInit_WithAddedArray_Failing() {
		var api = ApiFactory.eINSTANCE.createFluentPcmAPI();
		var obj = api.createNewResourceInterfaceRequiringEntity();

		api.modifyResourceInterfaceRequiringEntity(obj).xWithAddedFeat(
				EntityPackage.Literals.RESOURCE_INTERFACE_REQUIRING_ENTITY__RESOURCE_REQUIRED_ROLES_RESOURCE_INTERFACE_REQUIRING_ENTITY,
				new ResourceRequiredRole[] { api.createNewResourceRequiredRole(),
						api.createNewResourceRequiredRole() });
		Assertions.assertEquals(2, obj.getResourceRequiredRoles__ResourceInterfaceRequiringEntity().size());
	}
}
