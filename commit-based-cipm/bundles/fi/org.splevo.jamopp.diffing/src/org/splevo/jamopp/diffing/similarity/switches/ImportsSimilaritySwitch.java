package org.splevo.jamopp.diffing.similarity.switches;

import org.emftext.language.java.imports.ClassifierImport;
import org.emftext.language.java.imports.PackageImport;
import org.emftext.language.java.imports.StaticClassifierImport;
import org.emftext.language.java.imports.StaticMemberImport;
import org.emftext.language.java.imports.util.ImportsSwitch;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.util.JaMoPPBooleanUtil;
import org.splevo.jamopp.diffing.util.JaMoPPNamespaceUtil;

/**
 * Similarity decisions for the import elements.
 */
public class ImportsSimilaritySwitch extends ImportsSwitch<Boolean>
		implements ILoggableJavaSwitch, IJavaSimilarityPositionInnerSwitch {
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
		this.logInfoMessage("caseClassifierImport");

		ClassifierImport import2 = (ClassifierImport) this.getCompareElement();

		Boolean similarity = this.isSimilar(import1.getClassifier(), import2.getClassifier());
		if (JaMoPPBooleanUtil.isFalse(similarity)) {
			return Boolean.FALSE;
		}

		return JaMoPPNamespaceUtil.compareNamespacesAsString(import1, import2);
	}

	@Override
	public Boolean caseStaticMemberImport(StaticMemberImport import1) {
		this.logInfoMessage("caseStaticMemberImport");

		StaticMemberImport import2 = (StaticMemberImport) this.getCompareElement();

		var stMems1 = import1.getStaticMembers();
		var stMems2 = import2.getStaticMembers();
		var stMemsSimilarity = this.areSimilar(stMems1, stMems2);
		if (JaMoPPBooleanUtil.isFalse(stMemsSimilarity)) {
			return Boolean.FALSE;
		}

		return JaMoPPNamespaceUtil.compareNamespacesAsString(import1, import2);
	}

	/**
	 * TODO Review this method to make sure it is correct.
	 * 
	 * <i><b>This method was added later, because comparing improperly
	 * initialised package imports could result in null otherwise.</b></i>
	 * <br><br>
	 * 
	 * Package imports are considered similar, if their namespaces
	 * ({@link PackageImport#getNamespacesAsString()} are equal.
	 * 
	 * @param import1 the package import to compare with the compare element
	 * @return True if namespaces are equal, false otherwise.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean casePackageImport(PackageImport import1) {
		this.logInfoMessage("casePackageImport");
		
		PackageImport import2 = (PackageImport) this.getCompareElement();
		
		return JaMoPPNamespaceUtil.compareNamespacesAsString(import1, import2);
	}

	/**
	 * TODO Review this method to make sure it is correct.
	 * 
	 * <i><b>This method was added later, because comparing improperly
	 * initialised static classifier imports could result in null otherwise.</b></i>
	 * <br><br>
	 * 
	 * Static classifier imports are considered similar, if:
	 * <ul>
	 * <li> Their classifiers ({@link StaticClassifierImport#getClassifier()}) are similar
	 * <li> Their namespaces ({@link StaticClassifierImport#getNamespacesAsString()}) are equal
	 * </ul>
	 * 
	 * @param import1 the package import to compare with the compare element
	 * @return False if not similar, true otherwise.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseStaticClassifierImport(StaticClassifierImport import1) {
		this.logInfoMessage("caseStaticClassifierImport");

		StaticClassifierImport import2 = (StaticClassifierImport) this.getCompareElement();

		Boolean similarity = this.isSimilar(import1.getClassifier(), import2.getClassifier());
		if (JaMoPPBooleanUtil.isFalse(similarity)) {
			return Boolean.FALSE;
		}

		return JaMoPPNamespaceUtil.compareNamespacesAsString(import1, import2);
	}
}