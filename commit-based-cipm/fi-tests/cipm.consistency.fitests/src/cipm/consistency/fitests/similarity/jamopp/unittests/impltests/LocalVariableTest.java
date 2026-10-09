package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.variables.AdditionalLocalVariable;
import org.emftext.language.java.variables.VariablesPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class LocalVariableTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<AdditionalLocalVariable> additionalLocalVariable1 = () -> getAPI()
			.newAdditionalLocalVariable().withName("alv1").createNow();
	private final Supplier<AdditionalLocalVariable> additionalLocalVariable2 = () -> getAPI()
			.newAdditionalLocalVariable().withName("alv2").createNow();

	@Test
	public void testAdditionalLocalVariable() {
		this.testSimilarity(
				getAPI().newLocalVariable().withAddedAdditionalLocalVariables(additionalLocalVariable1.get())
						.createNow(),
				getAPI().newLocalVariable().withAddedAdditionalLocalVariables(additionalLocalVariable2.get())
						.createNow(),
				VariablesPackage.Literals.LOCAL_VARIABLE__ADDITIONAL_LOCAL_VARIABLES);
	}

	@Test
	public void testAdditionalLocalVariableSize() {
		this.testSimilarity(
				getAPI().newLocalVariable()
						.withAddedAdditionalLocalVariables(new AdditionalLocalVariable[] {
								additionalLocalVariable1.get(), additionalLocalVariable2.get() })
						.createNow(),
				getAPI().newLocalVariable().withAddedAdditionalLocalVariables(additionalLocalVariable1.get())
						.createNow(),
				VariablesPackage.Literals.LOCAL_VARIABLE__ADDITIONAL_LOCAL_VARIABLES);
	}

	@Test
	public void testAdditionalLocalVariableNullCheck() {
		this.testSimilarityNullCheck(getAPI().newLocalVariable()
				.withAddedAdditionalLocalVariables(additionalLocalVariable1.get()).createNow(),
				VariablesPackage.Literals.LOCAL_VARIABLE__ADDITIONAL_LOCAL_VARIABLES);
	}
}
