package org.splevo.jamopp.diffing.similarity.switches;

import org.emftext.language.java.imports.ClassifierImport;
import org.emftext.language.java.imports.StaticMemberImport;
import org.emftext.language.java.imports.util.ImportsSwitch;
import org.emftext.language.java.references.ReferenceableElement;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;

import com.google.common.base.Strings;

/**
 * Similarity decisions for the import elements.
 */
public class ImportsSimilaritySwitch extends ImportsSwitch<Boolean> implements ILoggableJavaSwitch, IJavaSimilarityPositionInnerSwitch {
	private IJavaSimilaritySwitch similaritySwitch;
	private boolean checkStatementPosition;

	@Override
	public ISimilarityRequestHandler getSimilarityRequestHandler() {
		return this.similaritySwitch;
	}

	@Override
	public boolean shouldCheckStatementPosition() {
		return this.checkStatementPosition;
	}
	
	@Override
	public IJavaSimilaritySwitch getContainingSwitch() {
		return this.similaritySwitch;
	}

    public ImportsSimilaritySwitch(IJavaSimilaritySwitch similaritySwitch, boolean checkStatementPosition) {
		this.similaritySwitch = similaritySwitch;
		this.checkStatementPosition = checkStatementPosition;
	}

	@Override
    public Boolean caseClassifierImport(ClassifierImport import1) {
		this.logMessage("caseClassifierImport");

        ClassifierImport import2 = (ClassifierImport) this.getCompareElement();

        Boolean similarity = this.isSimilar(import1.getClassifier(), import2.getClassifier());
        if (similarity == Boolean.FALSE) {
            return Boolean.FALSE;
        }

        String namespace1 = Strings.nullToEmpty(import1.getNamespacesAsString());
        String namespace2 = Strings.nullToEmpty(import2.getNamespacesAsString());
        return (namespace1.equals(namespace2));
    }

    @Override
    public Boolean caseStaticMemberImport(StaticMemberImport import1) {
    	this.logMessage("caseStaticMemberImport");

        StaticMemberImport import2 = (StaticMemberImport) this.getCompareElement();

        if (import1.getStaticMembers().size() != import2.getStaticMembers().size()) {
            return Boolean.FALSE;
        }
        for (int i = 0; i < import1.getStaticMembers().size(); i++) {
            ReferenceableElement member1 = import1.getStaticMembers().get(i);
            ReferenceableElement member2 = import2.getStaticMembers().get(i);
            Boolean similarity = this.isSimilar(member1, member2);
            if (similarity == Boolean.FALSE) {
                return Boolean.FALSE;
            }
        }

        String namespace1 = Strings.nullToEmpty(import1.getNamespacesAsString());
        String namespace2 = Strings.nullToEmpty(import2.getNamespacesAsString());
        return (namespace1.equals(namespace2));
    }
}