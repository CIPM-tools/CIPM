package org.splevo.jamopp.diffing.similarity.switches;

import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.EmptyModel;
import org.emftext.language.java.containers.Package;
import org.emftext.language.java.containers.util.ContainersSwitch;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.util.JaMoPPNameComparisonUtil;
import org.splevo.jamopp.diffing.util.JaMoPPStringUtil;
import org.splevo.jamopp.diffing.util.JaMoPPModelUtil;

/**
 * Similarity decisions for container elements.
 */
public class ContainersSimilaritySwitch extends ContainersSwitch<Boolean>
		implements ILoggableJavaSwitch, IJavaSimilarityPositionInnerSwitch {
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

	public ContainersSimilaritySwitch(IJavaSimilaritySwitch similaritySwitch, boolean checkStatementPosition) {
		this.similaritySwitch = similaritySwitch;
		this.checkStatementPosition = checkStatementPosition;
	}

	/**
	 * Check the similarity of two CompilationUnits.<br>
	 * Similarity is checked by
	 * <ul>
	 * <li>Comparing their names including renamings ({@link CompilationUnit#getName()})</li>
	 * <li>Comparing their namespaces' values including renamings ({@link CompilationUnit#getNamespacesAsString()})</li>
	 * </ul>
	 * Note: CompilationUnit names are full qualified. So it is important to apply
	 * classifier as well as package renaming normalizations to unit1.
	 * 
	 * @param unit1 The compilation unit to compare with the compareElement.
	 * @return False if not similar, true otherwise.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseCompilationUnit(CompilationUnit unit1) {
		this.logInfoMessage("caseCompilationUnit");

		CompilationUnit unit2 = (CompilationUnit) this.getCompareElement();

		String name1 = this.normalizePackage(this.normalizeCompilationUnit(unit1.getName()));
		String name2 = this.normalizePackage(this.normalizeCompilationUnit(unit2.getName()));

		if (!JaMoPPStringUtil.stringsEqual(name1, name2)) {
			return Boolean.FALSE;
		}

		String namespaceString1 = this.normalizeNamespace(unit1.getNamespacesAsString());
		String namespaceString2 = this.normalizeNamespace(unit2.getNamespacesAsString());

		return JaMoPPStringUtil.stringsEqual(namespaceString1, namespaceString2);
	}

	/**
	 * Check package similarity.<br>
	 * Similarity is checked by
	 * <ul>
	 * <li>full qualified package path </li>
	 * </ul>
	 * Note: Normalizations are applied to the full qualified package path of package1.
	 * 
	 * @param package1 The package to compare with the compare element.
	 * @return False if not similar, true otherwise.
	 * 
	 * @see {@link #getCompareElement()}
	 * @see {@link JaMoPPModelUtil#buildNamespacePath(org.eclipse.emf.ecore.EObject)}
	 * for more information on full qualified package path.
	 */
	@Override
	public Boolean casePackage(Package package1) {
		this.logInfoMessage("casePackage");

		Package package2 = (Package) this.getCompareElement();

		String packagePath1 = this.normalizeNamespace(JaMoPPModelUtil.buildNamespacePath(package1));
		String packagePath2 = this.normalizeNamespace(JaMoPPModelUtil.buildNamespacePath(package2));
		return JaMoPPStringUtil.stringsEqual(packagePath1, packagePath2);
	}

	/**
	 * Check module similarity.<br>
	 * Similarity is checked by
	 * <ul>
	 * <li>module names {@link org.emftext.language.java.containers.Module#getName()} </li>
	 * </ul>
	 * 
	 * @param module1 The module to compare with the compare element.
	 * @return False if names are not similar, true otherwise.
	 * 
	 * @see {@link #getCompareElement()}
	 */
	@Override
	public Boolean caseModule(org.emftext.language.java.containers.Module module1) {
		this.logInfoMessage("caseModule");

		org.emftext.language.java.containers.Module module2 = (org.emftext.language.java.containers.Module) this
				.getCompareElement();

		return JaMoPPNameComparisonUtil.namesEqual(module1, module2);
	}

	/**
	 * TODO Review this method to make sure it is correct.
	 * 
	 * <i><b>This method was added later, because comparing improperly
	 * initialised empty models could result in null otherwise.</b></i>
	 * <br><br>
	 * Empty models are considered to be similar.
	 * 
	 * @param emptyModule1 the empty model to compare with the compare element
	 * @return True
	 */
	@Override
	public Boolean caseEmptyModel(EmptyModel emptyModule1) {
		this.logInfoMessage("caseEmptyModel");

		return Boolean.TRUE;
	}
}