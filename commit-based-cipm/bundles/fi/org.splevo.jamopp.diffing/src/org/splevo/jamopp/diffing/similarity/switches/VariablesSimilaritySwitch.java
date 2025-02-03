package org.splevo.jamopp.diffing.similarity.switches;

import org.emftext.language.java.variables.AdditionalLocalVariable;
import org.emftext.language.java.variables.Variable;
import org.emftext.language.java.variables.util.VariablesSwitch;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.util.JaMoPPNameComparisonUtil;

/**
 * Similarity decisions for the variable elements.
 */
public class VariablesSimilaritySwitch extends VariablesSwitch<Boolean>
		implements ILoggableJavaSwitch, IJavaSimilarityInnerSwitch {
	private IJavaSimilaritySwitch similaritySwitch;

	@Override
	public ISimilarityRequestHandler getSimilarityRequestHandler() {
		return this.similaritySwitch;
	}

	@Override
	public IJavaSimilaritySwitch getContainingSwitch() {
		return this.similaritySwitch;
	}

	public VariablesSimilaritySwitch(IJavaSimilaritySwitch similaritySwitch) {
		this.similaritySwitch = similaritySwitch;
	}

	/**
	 * Check variable declaration similarity.<br>
	 * Similarity is checked by
	 * <ul>
	 * <li>variable name</li>
	 * <li>variable container (name space)</li>
	 * </ul>
	 * 
	 * @param var1 The variable declaration to compare with the compare element.
	 * @return True/False if the variable declarations are similar or not.
	 */
	@Override
	public Boolean caseVariable(Variable var1) {
		this.logInfoMessage("caseVariable");

		Variable var2 = (Variable) this.getCompareElement();
		return JaMoPPNameComparisonUtil.namesEqual(var1, var2);
	}

	@Override
	public Boolean caseAdditionalLocalVariable(AdditionalLocalVariable var1) {
		this.logInfoMessage("caseAdditionalLocalVariable");

		AdditionalLocalVariable var2 = (AdditionalLocalVariable) this.getCompareElement();
		return JaMoPPNameComparisonUtil.namesEqual(var1, var2);
	}
}