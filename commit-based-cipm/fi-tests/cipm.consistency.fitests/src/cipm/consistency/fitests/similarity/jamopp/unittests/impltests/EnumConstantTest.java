package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.classifiers.AnonymousClass;
import org.emftext.language.java.members.MembersPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class EnumConstantTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<AnonymousClass> anonymousClass1 = () -> getAPI().newAnonymousClass()
			.withAddedMembers(getAPI().newClass().withName("cls1").createNow()).createNow();
	private final Supplier<AnonymousClass> anonymousClass2 = () -> getAPI().newAnonymousClass()
			.withAddedMembers(getAPI().newClass().withName("cls2").createNow()).createNow();

	@Test
	public void testAnonymousClass() {
		this.testSimilarity(getAPI().newEnumConstant().withAnonymousClass(anonymousClass1.get()).createNow(),
				getAPI().newEnumConstant().withAnonymousClass(anonymousClass2.get()).createNow(),
				MembersPackage.Literals.ENUM_CONSTANT__ANONYMOUS_CLASS);
	}

	@Test
	public void testAnonymousClassNullCheck() {
		this.testSimilarityNullCheck(getAPI().newEnumConstant().withAnonymousClass(anonymousClass1.get()).createNow(),
				MembersPackage.Literals.ENUM_CONSTANT__ANONYMOUS_CLASS);
	}
}
