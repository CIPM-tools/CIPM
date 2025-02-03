package org.splevo.jamopp.diffing.similarity.switches;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.instantiations.ExplicitConstructorCall;
import org.emftext.language.java.instantiations.NewConstructorCall;
import org.emftext.language.java.instantiations.util.InstantiationsSwitch;
import org.emftext.language.java.types.Type;
import org.emftext.language.java.types.TypeReference;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.util.JaMoPPBooleanUtil;
import org.splevo.jamopp.diffing.util.JaMoPPNullCheckUtil;

/**
 * Similarity decisions for object instantiation elements.
 */
public class InstantiationsSimilaritySwitch extends InstantiationsSwitch<Boolean>
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

	public InstantiationsSimilaritySwitch(IJavaSimilaritySwitch similaritySwitch, boolean checkStatementPosition) {
		this.similaritySwitch = similaritySwitch;
		this.checkStatementPosition = checkStatementPosition;
	}

	/**
	 * Check class instance creation similarity.<br>
	 * Similarity is checked by
	 * <ul>
	 * <li>instance type similarity</li>
	 * <li>number of constructor arguments</li>
	 * <li>types of constructor arguments</li>
	 * </ul>
	 * 
	 * @param call1 The class instance creation to compare with the compare element.
	 * @return True/False if the class instance creations are similar or not.
	 */
	@Override
	public Boolean caseExplicitConstructorCall(ExplicitConstructorCall call1) {
		this.logInfoMessage("caseExplicitConstructorCall");

		ExplicitConstructorCall call2 = (ExplicitConstructorCall) this.getCompareElement();

		// check the class instance types
		Boolean typeSimilarity = this.isSimilar(call1.getCallTarget(), call2.getCallTarget());
		if (JaMoPPBooleanUtil.isFalse(typeSimilarity)) {
			return Boolean.FALSE;
		}

		// check number of type arguments
		EList<Expression> cic1Args = call1.getArguments();
		EList<Expression> cic2Args = call2.getArguments();
		var cicArgsSimilarity = this.areSimilar(cic1Args, cic2Args);
		return JaMoPPBooleanUtil.isNotFalse(cicArgsSimilarity);
	}

	@Override
	public Boolean caseNewConstructorCall(NewConstructorCall call1) {
		this.logInfoMessage("caseNewConstructorCall");

		NewConstructorCall call2 = (NewConstructorCall) this.getCompareElement();

		TypeReference tref1 = call1.getTypeReference();
		TypeReference tref2 = call2.getTypeReference();

		// Null check to avoid NullPointerExceptions
		if (JaMoPPNullCheckUtil.onlyOneIsNull(tref1, tref2)) {
			return Boolean.FALSE;
		} else if (JaMoPPNullCheckUtil.allNonNull(tref1, tref2)) {
			Type type1 = tref1.getTarget();
			Type type2 = tref2.getTarget();
			Boolean typeSimilarity = this.isSimilar(type1, type2);
			if (JaMoPPBooleanUtil.isFalse(typeSimilarity)) {
				return Boolean.FALSE;
			}
		}

		EList<Expression> types1 = call1.getArguments();
		EList<Expression> types2 = call2.getArguments();
		var argsSimilarity = this.areSimilar(types1, types2);
		return JaMoPPBooleanUtil.isNotFalse(argsSimilarity);
	}

	@Override
	public Boolean defaultCase(EObject object) {
		this.logInfoMessage("defaultCase for Instantiation");

		return Boolean.TRUE;
	}
}