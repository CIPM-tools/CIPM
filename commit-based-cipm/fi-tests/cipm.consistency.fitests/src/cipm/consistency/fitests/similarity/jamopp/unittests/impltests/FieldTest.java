package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.members.AdditionalField;
import org.emftext.language.java.members.MembersPackage;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class FieldTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<AdditionalField> additionalField1 = () -> getAPI().newAdditionalField().withName("af1")
			.createNow();
	private final Supplier<AdditionalField> additionalField2 = () -> getAPI().newAdditionalField().withName("af2")
			.createNow();

	@Test
	public void testAdditionalField() {
		this.testSimilarity(getAPI().newField().withAddedAdditionalFields(additionalField1.get()).createNow(),
				getAPI().newField().withAddedAdditionalFields(additionalField2.get()).createNow(),
				MembersPackage.Literals.FIELD__ADDITIONAL_FIELDS);
	}

	@Test
	public void testAdditionalFieldSize() {
		this.testSimilarity(
				getAPI().newField()
						.withAddedAdditionalFields(
								new AdditionalField[] { additionalField1.get(), additionalField2.get() })
						.createNow(),
				getAPI().newField().withAddedAdditionalFields(additionalField1.get()).createNow(),
				MembersPackage.Literals.FIELD__ADDITIONAL_FIELDS);
	}

	@Test
	public void testAdditionalFieldNullCheck() {
		this.testSimilarityNullCheck(getAPI().newField().withAddedAdditionalFields(additionalField1.get()).createNow(),
				MembersPackage.Literals.FIELD__ADDITIONAL_FIELDS);
	}
}
