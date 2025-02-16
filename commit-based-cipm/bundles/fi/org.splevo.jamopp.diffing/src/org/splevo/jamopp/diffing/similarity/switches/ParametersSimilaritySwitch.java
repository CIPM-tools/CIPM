package org.splevo.jamopp.diffing.similarity.switches;

import org.emftext.language.java.parameters.Parameter;
import org.emftext.language.java.parameters.util.ParametersSwitch;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;

import com.google.common.base.Strings;

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

	@Override
    public Boolean caseParameter(Parameter param1) {
		this.logMessage("caseParameter");
		
        Parameter param2 = (Parameter) this.getCompareElement();
        String name1 = Strings.nullToEmpty(param1.getName());
        String name2 = Strings.nullToEmpty(param2.getName());
        return (name1.equals(name2));
    }
}