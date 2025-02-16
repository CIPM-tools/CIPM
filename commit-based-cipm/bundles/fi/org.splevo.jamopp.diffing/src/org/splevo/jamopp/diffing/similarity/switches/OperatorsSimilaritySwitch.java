package org.splevo.jamopp.diffing.similarity.switches;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.operators.util.OperatorsSwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;

/**
 * Similarity decisions for operator elements.
 * <p>
 * All operator elements are strong typed with no identifying attributes or non-containment
 * references. Their location and runtime types are assumed to be checked before this switch is
 * called.
 * </p>
 */
public class OperatorsSimilaritySwitch extends OperatorsSwitch<Boolean> implements ILoggableJavaSwitch {
    @Override
    public Boolean defaultCase(EObject object) {
    	this.logMessage("defaultCase for Operator");
    	
        return Boolean.TRUE;
    }
}