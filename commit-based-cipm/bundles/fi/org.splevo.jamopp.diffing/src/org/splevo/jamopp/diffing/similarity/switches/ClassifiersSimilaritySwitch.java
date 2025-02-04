package org.splevo.jamopp.diffing.similarity.switches;

import org.emftext.language.java.classifiers.AnonymousClass;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.classifiers.util.ClassifiersSwitch;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.util.JaMoPPStringUtil;

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
     * name. The location is considered implicitly. Precisely, similarity is checked by comparing
     * the qualified names ({@link ConcreteClassifier#getQualifiedName()})
     * <br><br>
     * Note: Classifier normalizations are applied to the qualified name of
     * classifier1 before comparing.
     * 
     * @param classifier1
     *            the classifier to compare with the compareElement
     * @return True or false whether they are similar or not.
     * 
     * @see {@link #getCompareElement()}
     */
    @Override
    public Boolean caseConcreteClassifier(ConcreteClassifier classifier1) {
    	this.logInfoMessage("caseConcreteClassifier");
    	
        ConcreteClassifier classifier2 = (ConcreteClassifier) this.getCompareElement();

        String name1 = this.normalizeClassifier(classifier1.getQualifiedName());
        String name2 = this.normalizeClassifier(classifier2.getQualifiedName());

        return JaMoPPStringUtil.stringsEqual(name1, name2);
    }
    
    /**
     * Anonymous classes are considered to be similar.
     * 
     * @param anon the anonymous class to compare with the compare element.
     * @return true.
     * 
     * @see {@link #getCompareElement()}
     */
    @Override
    public Boolean caseAnonymousClass(AnonymousClass anon) {
    	this.logInfoMessage("caseAnonymousClass");
    	
    	return Boolean.TRUE;
    }

}