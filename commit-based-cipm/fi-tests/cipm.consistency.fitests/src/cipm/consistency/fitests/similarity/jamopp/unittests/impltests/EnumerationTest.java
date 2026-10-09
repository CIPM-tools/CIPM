package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.classifiers.ClassifiersPackage;
import org.emftext.language.java.members.EnumConstant;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class EnumerationTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<EnumConstant> constant1 = () -> getAPI().newEnumConstant().withName("cst1").createNow();
	private final Supplier<EnumConstant> constant2 = () -> getAPI().newEnumConstant().withName("cst2").createNow();

	@Test
	public void testConstant() {
		this.testSimilarity(getAPI().newEnumeration().withAddedConstants(constant1.get()).createNow(),
				getAPI().newEnumeration().withAddedConstants(constant2.get()).createNow(),
				ClassifiersPackage.Literals.ENUMERATION__CONSTANTS);
	}

	@Test
	public void testConstantSize() {
		this.testSimilarity(
				getAPI().newEnumeration().withAddedConstants(new EnumConstant[] { constant1.get(), constant2.get() })
						.createNow(),
				getAPI().newEnumeration().withAddedConstants(constant1.get()).createNow(),
				ClassifiersPackage.Literals.ENUMERATION__CONSTANTS);
	}

	@Test
	public void testConstantNullCheck() {
		this.testSimilarityNullCheck(getAPI().newEnumeration().withAddedConstants(constant1.get()).createNow(),
				ClassifiersPackage.Literals.ENUMERATION__CONSTANTS);
	}
}
