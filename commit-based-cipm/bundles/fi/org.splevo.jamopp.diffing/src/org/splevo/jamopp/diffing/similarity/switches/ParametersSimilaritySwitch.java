package org.splevo.jamopp.diffing.similarity.switches;

import org.emftext.language.java.parameters.Parameter;
import org.emftext.language.java.parameters.util.ParametersSwitch;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.util.JaMoPPNameComparisonUtil;

/**
 * Similarity decisions for parameter elements.
 * <p>
 * Parameters are variables and for this named elements. So their names must be checked but no
 * more identifying attributes or references exist.
 * </p>
 */
public class ParametersSimilaritySwitch extends ParametersSwitch<Boolean> implements ILoggableJavaSwitch, IJavaSimilarityInnerSwitch {
	private IJavaSimilaritySwitch similaritySwitch;

	@Override
	public ISimilarityRequestHandler getSimilarityRequestHandler() {
		return this.similaritySwitch;
	}
	
	@Override
	public IJavaSimilaritySwitch getContainingSwitch() {
		return this.similaritySwitch;
	}

    public ParametersSimilaritySwitch(IJavaSimilaritySwitch similaritySwitch) {
		this.similaritySwitch = similaritySwitch;
	}

	/**
	 * Checks the similarity of 2 parameters. Similarity is checked by comparing
	 * their names ({@link Parameter#getName()}).
	 * 
	 * @param param1 The parameter to compare with compareElement
	 * @return True if the names are similar, false if not.
	 * 
	 * @see {@link #getCompareElement()}
     */
	@Override
    public Boolean caseParameter(Parameter param1) {
		this.logInfoMessage("caseParameter");
		
        Parameter param2 = (Parameter) this.getCompareElement();
        return JaMoPPNameComparisonUtil.namesEqual(param1, param2);
    }
}