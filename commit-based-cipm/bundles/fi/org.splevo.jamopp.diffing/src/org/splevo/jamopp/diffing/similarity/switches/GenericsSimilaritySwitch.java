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

	/**
	 * Checks the similarity of 2 qualified type arguments. Similarity is checked by comparing:
	 * <ol>
	 * <li> The type reference ({@link QualifiedTypeArgument#getTypeReference()})
	 * </ol>
	 * 
	 * @param qta1 The qualified type argument to compare with compareElement
	 * @return Result of similarity checking of type references.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseQualifiedTypeArgument(QualifiedTypeArgument qta1) {
		this.logInfoMessage("caseQualifiedTypeArgument");

		QualifiedTypeArgument qta2 = (QualifiedTypeArgument) this.getCompareElement();
		return this.isSimilar(qta1.getTypeReference(), qta2.getTypeReference());
	}

	/**
	 * Checks the similarity of 2 super type arguments. Similarity is checked by comparing:
	 * <ol>
	 * <li> The super type (the lower bound) ({@link SuperTypeArgument#getSuperType()})
	 * </ol>
	 * 
	 * @param sta1 The super type argument to compare with compareElement
	 * @return Result of similarity checking of super types.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseSuperTypeArgument(SuperTypeArgument sta1) {
		this.logInfoMessage("caseSuperTypeArgument");

		SuperTypeArgument sta2 = (SuperTypeArgument) this.getCompareElement();
		return this.isSimilar(sta1.getSuperType(), sta2.getSuperType());
	}

	/**
	 * Checks the similarity of 2 extends type arguments. Similarity is checked by comparing:
	 * <ol>
	 * <li> The extend type argument (the upper bound) ({@link ExtendsTypeArgument#getExtendType()})
	 * </ol>
	 * 
	 * @param eta1 The extend type argument to compare with compareElement
	 * @return Result of similarity checking of extend type arguments.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseExtendsTypeArgument(ExtendsTypeArgument eta1) {
		this.logInfoMessage("caseExtendsTypeArgument");

		ExtendsTypeArgument eta2 = (ExtendsTypeArgument) this.getCompareElement();
		return this.isSimilar(eta1.getExtendType(), eta2.getExtendType());
	}

	/**
	 * Unknown type arguments are considered to be similar.
	 * 
	 * @param arg The unknown type argument to compare with compareElement
	 * @return true
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseUnknownTypeArgument(UnknownTypeArgument arg) {
		this.logInfoMessage("caseUnknownTypeArgument");

		return Boolean.TRUE;
	}

	/**
	 * Checks the similarity of 2 type parameters. Similarity is checked by comparing:
	 * <ol>
	 * <li> The name ({@link TypeParameter#getName()})
	 * <li> The extend types ({@link TypeParameter#getExtendTypes()})
	 * </ol>
	 * 
	 * @param param1 The type parameter to compare with compareElement
	 * @return False if not similar, result of similarity checking in 2. otherwise.
	 * 
	 * @see {@link #getCompareElement()}
	 */
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