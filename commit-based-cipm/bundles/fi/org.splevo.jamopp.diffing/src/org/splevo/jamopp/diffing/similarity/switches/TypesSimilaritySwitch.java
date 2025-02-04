package org.splevo.jamopp.diffing.similarity.switches;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.types.ClassifierReference;
import org.emftext.language.java.types.InferableType;
import org.emftext.language.java.types.NamespaceClassifierReference;
import org.emftext.language.java.types.PrimitiveType;
import org.emftext.language.java.types.TypeReference;
import org.emftext.language.java.types.util.TypesSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.util.JaMoPPBooleanUtil;
import org.splevo.jamopp.diffing.util.JaMoPPNamespaceUtil;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;

/**
 * Similarity decisions for elements of the types package.
 */
public class TypesSimilaritySwitch extends TypesSwitch<Boolean> implements ILoggableJavaSwitch, IJavaSimilarityPositionInnerSwitch {
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

    public TypesSimilaritySwitch(IJavaSimilaritySwitch similaritySwitch, boolean checkStatementPosition) {
		this.similaritySwitch = similaritySwitch;
		this.checkStatementPosition = checkStatementPosition;
	}

	/**
     * Checks the similarity of 2 classifier references.<br>
     * 
     * Similarity is checked by the target (the method called). Everything else are containment references
     * checked indirectly.
     * 
     * @param ref1
     *            The classifier reference to compare with the compare element.
     * @return False if targets are not similar, true otherwise.
	 * 
	 * @see {@link #getCompareElement()}
     */
    @Override
    public Boolean caseClassifierReference(ClassifierReference ref1) {
    	this.logInfoMessage("caseClassifierReference");
    	
        ClassifierReference ref2 = (ClassifierReference) this.getCompareElement();

        Boolean targetSimilarity = this.isSimilar(ref1.getTarget(), ref2.getTarget());
        return JaMoPPBooleanUtil.isNotFalse(targetSimilarity);
    }

    /**
     * Checks the similarity of 2 type references. Similarity is checked by comparing
     * their targets ({@link TypeReference#getTarget()}.
     * 
     * @param ref1 The type reference to compare with compareElement
     * @return False if targets are not similar, true otherwise.
	 * 
	 * @see {@link #getCompareElement()}
     */
    @Override
    public Boolean caseTypeReference(TypeReference ref1) {
    	this.logInfoMessage("caseTypeReference");

        TypeReference ref2 = (TypeReference) this.getCompareElement();

        Boolean targetSimilarity = this.isSimilar(ref1.getTarget(), ref2.getTarget());
        return JaMoPPBooleanUtil.isNotFalse(targetSimilarity);
    }

    /**
     * Checks the similarity of 2 namespace classifier references. Similarity is checked by comparing:
     * <ol>
     * <li> Namespaces ({@link NamespaceClassifierReference#getNamespacesAsString()})
     * <li> Pure classifier reference ({@link NamespaceClassifierReference#getPureClassifierReference})
     * </ol>
     * 
     * @param ref1 The namespace classifier reference to compare with compareElement
     * @return False if namespaces are not similar, otherwise returns the result of similarity checking
     * pure classifier references.
	 * 
	 * @see {@link #getCompareElement()}
     */
    @Override
    public Boolean caseNamespaceClassifierReference(NamespaceClassifierReference ref1) {
    	this.logInfoMessage("caseNamespaceClassifierReference");

        NamespaceClassifierReference ref2 = (NamespaceClassifierReference) this.getCompareElement();

        var namespaceSimilarity = JaMoPPNamespaceUtil.compareNamespacesAsString(ref1, ref2);
        if (JaMoPPBooleanUtil.isFalse(namespaceSimilarity)) {
            return Boolean.FALSE;
        }

        ClassifierReference pureRef1 = ref1.getPureClassifierReference();
        ClassifierReference pureRef2 = ref2.getPureClassifierReference();

        return this.isSimilar(pureRef1, pureRef2);
    }

    /**
     * Primitive types are always similar as their class similarity is assumed.
     * <br><br>
     * Note: The fall back to the default case is not sufficient here, as the common
     * TypeReference case would be used before, leading to a loop.
     * 
     * @param type
     *            The primitive type to compare with compareElement.
     * @return TRUE
	 * 
	 * @see {@link #getCompareElement()}
     */
    @Override
    public Boolean casePrimitiveType(PrimitiveType type) {
    	this.logInfoMessage("casePrimitiveType");
    	
        return Boolean.TRUE;
    }
    
    /**
     * Inferable types are considered to be similar.
     * 
     * @param type The inferable type to compare with the compare element.
     * @return true.
	 * 
	 * @see {@link #getCompareElement()}
     */
    @Override
    public Boolean caseInferableType(InferableType type) {
    	this.logInfoMessage("caseInferableType");
    	
    	return Boolean.TRUE;
    }

    @Override
    public Boolean defaultCase(EObject object) {
    	this.logInfoMessage("defaultCase for Type");
    	
        return Boolean.TRUE;
    }
}