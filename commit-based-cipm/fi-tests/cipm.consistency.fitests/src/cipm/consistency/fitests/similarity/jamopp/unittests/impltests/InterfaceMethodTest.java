package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.annotations.AnnotationValue;
import org.emftext.language.java.members.MembersPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class InterfaceMethodTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<AnnotationValue> defaultValue1 = () -> getAPI().newDecimalIntegerLiteral(1);
	private final Supplier<AnnotationValue> defaultValue2 = () -> getAPI().newDecimalIntegerLiteral(2);

	@Test
	public void testDefaultValue() {
		this.testSimilarity(getAPI().newInterfaceMethod().withDefaultValue(defaultValue1.get()).createNow(),
				getAPI().newInterfaceMethod().withDefaultValue(defaultValue2.get()).createNow(),
				MembersPackage.Literals.INTERFACE_METHOD__DEFAULT_VALUE);
	}

	@Test
	public void testDefaultValueNullCheck() {
		this.testSimilarityNullCheck(getAPI().newInterfaceMethod().withDefaultValue(defaultValue1.get()).createNow(),
				MembersPackage.Literals.INTERFACE_METHOD__DEFAULT_VALUE);
	}
}
