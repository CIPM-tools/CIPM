package org.splevo.jamopp.diffing.similarity.switches;

import org.emftext.language.java.commons.NamedElement;
import org.emftext.language.java.commons.util.CommonsSwitch;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.util.JaMoPPNameComparisonUtil;

/**
 * Similarity decisions for commons elements.
 */
public class CommonsSimilaritySwitch extends CommonsSwitch<Boolean> implements ILoggableJavaSwitch, IJavaSimilarityPositionInnerSwitch {
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

    public CommonsSimilaritySwitch(IJavaSimilaritySwitch similaritySwitch, boolean checkStatementPosition) {
		this.similaritySwitch = similaritySwitch;
		this.checkStatementPosition = checkStatementPosition;
	}

	/**
     * Check named element. Similarity is defined by the names
     * of the elements ({@link NamedElement#getName()}).
     * 
     * @param element1
     *            The named element to compare with the compare element.
     * @return True if names are similar, false if not.
	 * 
	 * @see {@link #getCompareElement()}
     */
    @Override
    public Boolean caseNamedElement(NamedElement element1) {
    	this.logInfoMessage("caseNamedElement");
    	
        NamedElement element2 = (NamedElement) this.getCompareElement();
        return JaMoPPNameComparisonUtil.namesEqual(element1, element2);
    }
}