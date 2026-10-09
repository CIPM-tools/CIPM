package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import java.util.function.Supplier;

import org.emftext.language.java.imports.ImportsPackage;
import org.emftext.language.java.references.ReferenceableElement;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

public class StaticMemberImportTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<ReferenceableElement> staticMember1 = () -> getAPI().newClassMethod().withName("met1")
			.createNow();
	private final Supplier<ReferenceableElement> staticMember2 = () -> getAPI().newClassMethod().withName("met2")
			.createNow();

	@Test
	public void testStaticMember() {
		this.testSimilarity(getAPI().newStaticMemberImport().withAddedStaticMembers(staticMember1.get()).createNow(),
				getAPI().newStaticMemberImport().withAddedStaticMembers(staticMember2.get()).createNow(),
				ImportsPackage.Literals.STATIC_MEMBER_IMPORT__STATIC_MEMBERS);
	}

	@Test
	public void testStaticMemberSize() {
		this.testSimilarity(
				getAPI().newStaticMemberImport()
						.withAddedStaticMembers(new ReferenceableElement[] { staticMember1.get(), staticMember2.get() })
						.createNow(),
				getAPI().newStaticMemberImport().withAddedStaticMembers(staticMember1.get()).createNow(),
				ImportsPackage.Literals.STATIC_MEMBER_IMPORT__STATIC_MEMBERS);
	}

	@Test
	public void testStaticMemberNullCheck() {
		this.testSimilarityNullCheck(
				getAPI().newStaticMemberImport().withAddedStaticMembers(staticMember1.get()).createNow(),
				ImportsPackage.Literals.STATIC_MEMBER_IMPORT__STATIC_MEMBERS);
	}
}
