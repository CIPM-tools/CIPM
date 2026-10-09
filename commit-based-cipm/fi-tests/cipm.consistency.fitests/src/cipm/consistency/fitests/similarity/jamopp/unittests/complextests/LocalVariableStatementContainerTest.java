package cipm.consistency.fitests.similarity.jamopp.unittests.complextests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

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
		var st11 = getAPI().createNewLocalVariableStatement();
		var st21 = getAPI().createNewLocalVariableStatement();

		var clsMet1 = getAPI().newClassMethod().withStatement(st11).createNow();

		// Ensure that the containers of both statements are different
		Assertions.assertEquals(st11.eContainer(), clsMet1);
		Assertions.assertNull(st21.eContainer());
		Assertions.assertNotEquals(st11.eContainer(), st21.eContainer());

		// Similarity checking for LocalVariableStatement cares about their container
		this.testSimilarity(st11, st21, false);
	}
}
