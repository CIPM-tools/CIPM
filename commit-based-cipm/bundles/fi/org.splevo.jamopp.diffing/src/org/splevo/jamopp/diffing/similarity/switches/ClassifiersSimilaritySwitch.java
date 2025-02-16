package org.splevo.jamopp.diffing.similarity.switches;

import org.emftext.language.java.classifiers.AnonymousClass;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.classifiers.util.ClassifiersSwitch;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;

import com.google.common.base.Strings;

/**
 * Similarity decisions for classifier elements.
 */
public class ClassifiersSimilaritySwitch extends ClassifiersSwitch<Boolean> implements ILoggableJavaSwitch, IJavaSimilarityPositionInnerSwitch {
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

    public ClassifiersSimilaritySwitch(IJavaSimilaritySwitch similaritySwitch, boolean checkStatementPosition) {
		this.similaritySwitch = similaritySwitch;
		this.checkStatementPosition = checkStatementPosition;
	}

    /**
     * Concrete classifiers such as classes and interface are identified by their location and
     * name. The location is considered implicitly.
     * 
     * @param classifier1
     *            the classifier to compare with the compareelement
     * @return True or false whether they are similar or not.
     */
    @Override
    public Boolean caseConcreteClassifier(ConcreteClassifier classifier1) {
    	this.logMessage("caseConcreteClassifier");
    	
        ConcreteClassifier classifier2 = (ConcreteClassifier) this.getCompareElement();

        String name1 = this.normalizeClassifier(classifier1.getQualifiedName());
        String name2 = Strings.nullToEmpty(classifier2.getQualifiedName());

        return (name1.equals(name2));
    }
    
    /**
     * Anonymous classes are considered to be similar.
     * 
     * @param anon the anonymous class to compare with the compare element.
     * @return true.
     */
    @Override
    public Boolean caseAnonymousClass(AnonymousClass anon) {
    	this.logMessage("caseAnonymousClass");
    	
    	return Boolean.TRUE;
    }

}