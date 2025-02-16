package org.splevo.jamopp.diffing.similarity.switches;

import org.emftext.language.java.modules.AccessProvidingModuleDirective;
import org.emftext.language.java.modules.ModuleReference;
import org.emftext.language.java.modules.ProvidesModuleDirective;
import org.emftext.language.java.modules.RequiresModuleDirective;
import org.emftext.language.java.modules.UsesModuleDirective;
import org.emftext.language.java.modules.util.ModulesSwitch;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;

/**
 * Similarity Decisions for module elements.
 */
public class ModulesSimilaritySwitch extends ModulesSwitch<Boolean> implements ILoggableJavaSwitch, IJavaSimilarityPositionInnerSwitch {
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

    public ModulesSimilaritySwitch(IJavaSimilaritySwitch similaritySwitch, boolean checkStatementPosition) {
		this.similaritySwitch = similaritySwitch;
		this.checkStatementPosition = checkStatementPosition;
	}

	/**
     * Check ModuleReference similarity.<br>
     * Similarity is checked by
     * <ul>
     * <li>module names</li>
     * </ul>
     * 
     * @param modRef1 The module reference to compare with the compare element.
     * @return True/False if the module references are similar or not.
     */
	@Override
	public Boolean caseModuleReference(ModuleReference modRef1) {
		this.logMessage("caseModuleReference");
		
		ModuleReference modRef2 = (ModuleReference) this.getCompareElement();
		if (this.compareNamespacesByPart(modRef1, modRef2)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	/**
     * Check similarity for access providing module directives.<br>
     * Similarity is checked by
     * <ul>
     * <li>the provided package</li>
     * </ul>
     * 
     * @param dir1 The access providing module directive to compare with the compare element.
     * @return True/False if the module directives are similar or not.
     */
	@Override
	public Boolean caseAccessProvidingModuleDirective(AccessProvidingModuleDirective dir1) {
		this.logMessage("caseAccessProvidingModuleDirective");
		
		AccessProvidingModuleDirective dir2 = (AccessProvidingModuleDirective) this.getCompareElement();
		if (!this.compareNamespacesByPart(dir1, dir2)) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
	
	/**
     * Check similarity for require module directives.<br>
     * Similarity is checked by
     * <ul>
     * <li>required modules</li>
     * </ul>
     * 
     * @param dir1 The require module directive to compare with the compare element.
     * @return True/False if the module directives are similar or not.
     */
	@Override
	public Boolean caseRequiresModuleDirective(RequiresModuleDirective dir1) {
		this.logMessage("caseRequiresModuleDirective");
		
		RequiresModuleDirective dir2 = (RequiresModuleDirective) this.getCompareElement();
		return this.isSimilar(dir1.getRequiredModule(), dir2.getRequiredModule());
	}
	
	/**
     * Check similarity for provide module directives.<br>
     * Similarity is checked by
     * <ul>
     * <li>provided types</li>
     * </ul>
     * 
     * @param dir1 The provide module directive to compare with the compare element.
     * @return True/False if the module directives are similar or not.
     */
	@Override
	public Boolean caseProvidesModuleDirective(ProvidesModuleDirective dir1) {
		this.logMessage("caseProvidesModuleDirective");
		
		ProvidesModuleDirective dir2 = (ProvidesModuleDirective) this.getCompareElement();
		return this.isSimilar(dir1.getTypeReference(), dir2.getTypeReference());
	}
	
	/**
     * Check similarity for use module directives.<br>
     * Similarity is checked by
     * <ul>
     * <li>used types</li>
     * </ul>
     * 
     * @param dir1 The use module directive to compare with the compare element.
     * @return True/False if the module directives are similar or not.
     */
	@Override
	public Boolean caseUsesModuleDirective(UsesModuleDirective dir1) {
		this.logMessage("caseUsesModuleDirective");
		
		UsesModuleDirective dir2 = (UsesModuleDirective) this.getCompareElement();
		return this.isSimilar(dir1.getTypeReference(), dir2.getTypeReference());
	}
}