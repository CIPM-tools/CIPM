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
     * Checks the similarity of 2 variables. Similarity is checked by comparing their
     * names ({@link Variable#getName()}).
	 * 
	 * @param var1 The variable declaration to compare with the compare element.
	 * @return False if names are not equal, true otherwise.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseVariable(Variable var1) {
		this.logInfoMessage("caseVariable");

		Variable var2 = (Variable) this.getCompareElement();
		return JaMoPPNameComparisonUtil.namesEqual(var1, var2);
	}

    /**
     * Checks the similarity of 2 additional local variables. Similarity is checked by comparing their
     * names ({@link AdditionalLocalVariable#getName()}).
	 * 
	 * @param var1 The additional local variable to compare with the compare element.
	 * @return False if names are not equal, true otherwise.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseAdditionalLocalVariable(AdditionalLocalVariable var1) {
		this.logInfoMessage("caseAdditionalLocalVariable");

		AdditionalLocalVariable var2 = (AdditionalLocalVariable) this.getCompareElement();
		return JaMoPPNameComparisonUtil.namesEqual(var1, var2);
	}
}