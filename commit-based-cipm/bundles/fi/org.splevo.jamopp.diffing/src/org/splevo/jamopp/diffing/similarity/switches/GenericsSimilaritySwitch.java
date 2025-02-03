package org.splevo.jamopp.diffing.similarity.switches;

import org.emftext.language.java.generics.ExtendsTypeArgument;
import org.emftext.language.java.generics.QualifiedTypeArgument;
import org.emftext.language.java.generics.SuperTypeArgument;
import org.emftext.language.java.generics.TypeParameter;
import org.emftext.language.java.generics.UnknownTypeArgument;
import org.emftext.language.java.generics.util.GenericsSwitch;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.util.JaMoPPBooleanUtil;
import org.splevo.jamopp.diffing.util.JaMoPPNameComparisonUtil;

/**
 * Similarity decisions for the generic elements.
 */
public class GenericsSimilaritySwitch extends GenericsSwitch<Boolean>
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

	public GenericsSimilaritySwitch(IJavaSimilaritySwitch similaritySwitch, boolean checkStatementPosition) {
		this.similaritySwitch = similaritySwitch;
		this.checkStatementPosition = checkStatementPosition;
	}

	@Override
	public Boolean caseQualifiedTypeArgument(QualifiedTypeArgument qta1) {
		this.logInfoMessage("caseQualifiedTypeArgument");

		QualifiedTypeArgument qta2 = (QualifiedTypeArgument) this.getCompareElement();
		return this.isSimilar(qta1.getTypeReference(), qta2.getTypeReference());
	}

	@Override
	public Boolean caseSuperTypeArgument(SuperTypeArgument sta1) {
		this.logInfoMessage("caseSuperTypeArgument");

		SuperTypeArgument sta2 = (SuperTypeArgument) this.getCompareElement();
		return this.isSimilar(sta1.getSuperType(), sta2.getSuperType());
	}

	@Override
	public Boolean caseExtendsTypeArgument(ExtendsTypeArgument eta1) {
		this.logInfoMessage("caseExtendsTypeArgument");

		ExtendsTypeArgument eta2 = (ExtendsTypeArgument) this.getCompareElement();
		return this.isSimilar(eta1.getExtendType(), eta2.getExtendType());
	}

	@Override
	public Boolean caseUnknownTypeArgument(UnknownTypeArgument arg) {
		this.logInfoMessage("caseUnknownTypeArgument");

		return Boolean.TRUE;
	}

	@Override
	public Boolean caseTypeParameter(TypeParameter param1) {
		this.logInfoMessage("caseTypeParameter");

		TypeParameter param2 = (TypeParameter) this.getCompareElement();

		var nameSimilarity = JaMoPPNameComparisonUtil.namesEqual(param1, param2);
		if (JaMoPPBooleanUtil.isFalse(nameSimilarity)) {
			return Boolean.FALSE;
		}

		return this.areSimilar(param1.getExtendTypes(), param2.getExtendTypes());
	}
}