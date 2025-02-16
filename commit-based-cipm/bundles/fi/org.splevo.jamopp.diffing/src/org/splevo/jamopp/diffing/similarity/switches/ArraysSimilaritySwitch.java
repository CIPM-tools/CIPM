package org.splevo.jamopp.diffing.similarity.switches;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.arrays.util.ArraysSwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;

/**
 * Similarity decision for array elements.
 * <p>
 * All array elements are strongly typed. They have no identifying attributes. Their location
 * and runtime type are assumed to be checked before this switch is called. So nothing to check
 * here.
 */
public class ArraysSimilaritySwitch extends ArraysSwitch<Boolean> implements ILoggableJavaSwitch {
	@Override
    public Boolean defaultCase(EObject object) {
		this.logMessage("defaultCase for Array");
		
        return Boolean.TRUE;
    }
}