package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.members.AdditionalField;
import org.emftext.language.java.members.Field;
import org.emftext.language.java.members.MembersPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesAdditionalFields;
import cipm.consistency.initialisers.jamopp.members.FieldInitialiser;

public class FieldTest extends AbstractJaMoPPSimilarityTest implements UsesAdditionalFields {
	protected Field initElement(AdditionalField[] additionalFields) {
		var fieldInit = new FieldInitialiser();
		var field = fieldInit.instantiate();
		Assertions.assertTrue(fieldInit.addAdditionalFields(field, additionalFields));
		return field;
	}

	@Test
	public void testAdditionalField() {
		var objOne = this.initElement(new AdditionalField[] { this.createMinimalAF("af1") });
		var objTwo = this.initElement(new AdditionalField[] { this.createMinimalAF("af2") });

		this.testSimilarity(objOne, objTwo, MembersPackage.Literals.FIELD__ADDITIONAL_FIELDS);
	}

	@Test
	public void testAdditionalFieldSize() {
		var objOne = this
				.initElement(new AdditionalField[] { this.createMinimalAF("af1"), this.createMinimalAF("af2") });
		var objTwo = this.initElement(new AdditionalField[] { this.createMinimalAF("af1") });

		this.testSimilarity(objOne, objTwo, MembersPackage.Literals.FIELD__ADDITIONAL_FIELDS);
	}

	@Test
	public void testAdditionalFieldNullCheck() {
		this.testSimilarityNullCheck(this.initElement(new AdditionalField[] { this.createMinimalAF("af1") }),
				new FieldInitialiser(), false, MembersPackage.Literals.FIELD__ADDITIONAL_FIELDS);
	}
}
