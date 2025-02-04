package cipm.consistency.fitests.similarity.jamopp.unittests.complextests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.initialisers.jamopp.members.ClassMethodInitialiser;
import cipm.consistency.initialisers.jamopp.statements.LocalVariableStatementInitialiser;

/**
 * A test class aiming to test how similarity checking interacts with containers
 * of {@link LocalVariableStatement} instances
 * ({@code lvStatement.eContainer()}).
 * 
 * @author Alp Torac Genc
 */
public class LocalVariableStatementContainerTest extends AbstractJaMoPPSimilarityTest {

	/**
	 * Ensures that differences in containers of {@link LocalVariableStatement}
	 * instances break their similarity.
	 */
	@Test
	public void testDifferentContainers() {
		var stInit = new LocalVariableStatementInitialiser();
		var clsMetInit = new ClassMethodInitialiser();
		var clsMet1 = clsMetInit.instantiate();

		var st11 = stInit.instantiate();
		var st21 = stInit.instantiate();

		Assertions.assertTrue(clsMetInit.setStatement(clsMet1, st11));

		// Ensure that the containers of both statements are different
		Assertions.assertEquals(st11.eContainer(), clsMet1);
		Assertions.assertNull(st21.eContainer());
		Assertions.assertNotEquals(st11.eContainer(), st21.eContainer());

		// Similarity checking for LocalVariableStatement cares about their container
		this.testSimilarity(st11, st21, false);
	}
}
