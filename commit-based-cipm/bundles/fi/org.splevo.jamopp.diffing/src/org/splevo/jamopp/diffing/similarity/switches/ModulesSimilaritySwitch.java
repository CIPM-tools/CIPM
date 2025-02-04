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
import org.splevo.jamopp.diffing.util.JaMoPPBooleanUtil;

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
     * <li>Individual parts of namespace (comparing all elements pairwise in {@link ModuleReference#getNamespaces()})
     * </ul>
     * 
     * @param modRef1 The module reference to compare with the compare element.
     * @return True/False if the module references are similar or not.
	 * 
	 * @see {@link #getCompareElement()}
     */
	@Override
	public Boolean caseModuleReference(ModuleReference modRef1) {
		this.logInfoMessage("caseModuleReference");
		
		ModuleReference modRef2 = (ModuleReference) this.getCompareElement();
		return JaMoPPBooleanUtil.isTrue(this.compareNamespacesByPart(modRef1, modRef2));
	}
	
	/**
     * Check similarity for access providing module directives.<br>
     * Similarity is checked by
     * <ul>
     * <li>Individual parts of namespace (comparing all elements pairwise in {@link AccessProvidingModuleDirective#getNamespaces()})
     * </ul>
     * 
     * @param dir1 The access providing module directive to compare with the compare element.
     * @return True/False if the module directives are similar or not.
	 * 
	 * @see {@link #getCompareElement()}
     */
	@Override
	public Boolean caseAccessProvidingModuleDirective(AccessProvidingModuleDirective dir1) {
		this.logInfoMessage("caseAccessProvidingModuleDirective");
		
		AccessProvidingModuleDirective dir2 = (AccessProvidingModuleDirective) this.getCompareElement();
		return JaMoPPBooleanUtil.isTrue(this.compareNamespacesByPart(dir1, dir2));
	}
	
	/**
     * Check similarity for require module directives.<br>
     * Similarity is checked by
     * <ul>
     * <li>required module ({@link RequiresModuleDirective#getRequiredModule()})
     * </ul>
     * 
     * @param dir1 The require module directive to compare with the compare element.
     * @return Result of similarity checking the required modules.
	 * 
	 * @see {@link #getCompareElement()}
     */
	@Override
	public Boolean caseRequiresModuleDirective(RequiresModuleDirective dir1) {
		this.logInfoMessage("caseRequiresModuleDirective");
		
		RequiresModuleDirective dir2 = (RequiresModuleDirective) this.getCompareElement();
		return this.isSimilar(dir1.getRequiredModule(), dir2.getRequiredModule());
	}
	
	/**
     * Check similarity for provide module directives.<br>
     * Similarity is checked by
     * <ul>
     * <li>provided types ({@link ProvidesModuleDirective#getTypeReference()})
     * </ul>
     * 
     * @param dir1 The provide module directive to compare with the compare element.
     * @return Result of similarity checking the provided types.
	 * 
	 * @see {@link #getCompareElement()}
     */
	@Override
	public Boolean caseProvidesModuleDirective(ProvidesModuleDirective dir1) {
		this.logInfoMessage("caseProvidesModuleDirective");
		
		ProvidesModuleDirective dir2 = (ProvidesModuleDirective) this.getCompareElement();
		return this.isSimilar(dir1.getTypeReference(), dir2.getTypeReference());
	}
	
	/**
     * Check similarity for use module directives.<br>
     * Similarity is checked by
     * <ul>
     * <li>used types ({@link ProvidesModuleDirective#getTypeReference()})
     * </ul>
     * 
     * @param dir1 The uses module directive to compare with the compare element.
     * @return Result of similarity checking used types.
	 * 
	 * @see {@link #getCompareElement()}
     */
	@Override
	public Boolean caseUsesModuleDirective(UsesModuleDirective dir1) {
		this.logInfoMessage("caseUsesModuleDirective");
		
		UsesModuleDirective dir2 = (UsesModuleDirective) this.getCompareElement();
		return this.isSimilar(dir1.getTypeReference(), dir2.getTypeReference());
	}
}