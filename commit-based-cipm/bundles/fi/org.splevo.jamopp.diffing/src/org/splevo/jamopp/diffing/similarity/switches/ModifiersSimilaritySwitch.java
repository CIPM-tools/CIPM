package org.splevo.jamopp.diffing.similarity.switches;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.modifiers.util.ModifiersSwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;

/**
 * Similarity decisions for modifier elements.
 * <p>
 * All modifier elements are strong typed with no identifying attributes or non-containment
 * references. Their location and runtime types are assumed to be checked before this switch is
 * called.
 * </p>
 */
public class ModifiersSimilaritySwitch extends ModifiersSwitch<Boolean> implements ILoggableJavaSwitch {
    @Override
    public Boolean defaultCase(EObject object) {
    	this.logMessage("defaultCase for Modifier");
    	
        return Boolean.TRUE;
    }
}