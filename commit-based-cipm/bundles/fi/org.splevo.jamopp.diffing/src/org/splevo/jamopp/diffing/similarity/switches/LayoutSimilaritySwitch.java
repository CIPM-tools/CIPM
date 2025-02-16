package org.splevo.jamopp.diffing.similarity.switches;

import org.eclipse.emf.ecore.EObject;
import org.emftext.commons.layout.util.LayoutSwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;

/**
 * Similarity Decisions for layout information is always true as they are not considered for
 * now.
 */
public class LayoutSimilaritySwitch extends LayoutSwitch<Boolean> implements ILoggableJavaSwitch {
    @Override
    public Boolean defaultCase(EObject object) {
    	this.logMessage("defaultCase for Layout");
    	
        return Boolean.TRUE;
    }
}