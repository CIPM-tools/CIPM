package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.members.ExceptionThrower;
import org.emftext.language.java.members.MembersPackage;
import org.emftext.language.java.types.NamespaceClassifierReference;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class ExceptionThrowerTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<NamespaceClassifierReference> exceptions1 = () -> getAPI().newNamespaceClassifierReference()
			.withAddedNamespaces("ns1").createNow();
	private final Supplier<NamespaceClassifierReference> exceptions2 = () -> getAPI().newNamespaceClassifierReference()
			.withAddedNamespaces("ns2").createNow();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(ExceptionThrower.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testExceptions(Class<?> cls, String displayName) {
		this.testSimilarity(getAPI().newX(cls)
				.xWithAddedFeat(MembersPackage.Literals.EXCEPTION_THROWER__EXCEPTIONS, exceptions1.get()).createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(MembersPackage.Literals.EXCEPTION_THROWER__EXCEPTIONS, exceptions2.get())
						.createNow(),
				MembersPackage.Literals.EXCEPTION_THROWER__EXCEPTIONS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testExceptionsSize(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(MembersPackage.Literals.EXCEPTION_THROWER__EXCEPTIONS,
								new NamespaceClassifierReference[] { exceptions1.get(), exceptions2.get() })
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(MembersPackage.Literals.EXCEPTION_THROWER__EXCEPTIONS, exceptions2.get())
						.createNow(),
				MembersPackage.Literals.EXCEPTION_THROWER__EXCEPTIONS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testExceptionsNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(getAPI().newX(cls)
				.xWithAddedFeat(MembersPackage.Literals.EXCEPTION_THROWER__EXCEPTIONS, exceptions1.get()).createNow(),
				MembersPackage.Literals.EXCEPTION_THROWER__EXCEPTIONS);
	}
}
