package org.splevo.jamopp.diffing.similarity.switches;

import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.Package;
import org.emftext.language.java.containers.util.ContainersSwitch;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.util.JaMoPPNameComparisonUtil;
import org.splevo.jamopp.diffing.util.JaMoPPStringUtil;
import org.splevo.jamopp.diffing.util.JaMoPPModelUtil;

import com.google.common.base.Strings;

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
	 * <li>Comparing their names (including renamings)</li>
	 * <li>Comparing their namespaces' values (including renamings)</li>
	 * </ul>
	 * Note: CompilationUnit names are full qualified. So it is important to apply
	 * classifier as well as package renaming normalizations to them.
	 * 
	 * @param unit1 The compilation unit to compare with the compareElement.
	 * @return True/False whether they are similar or not.
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
	 * <li>full qualified package path</li>
	 * </ul>
	 * 
	 * @param package1 The package to compare with the compare element.
	 * @return True/False if the packages are similar or not.
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
	 * <li>module names</li>
	 * </ul>
	 * 
	 * @param module1 The module to compare with the compare element.
	 * @return True/False if the modules are similar or not.
	 */
	@Override
	public Boolean caseModule(org.emftext.language.java.containers.Module module1) {
		this.logInfoMessage("caseModule");

		org.emftext.language.java.containers.Module module2 = (org.emftext.language.java.containers.Module) this
				.getCompareElement();

		var name1 = Strings.nullToEmpty(module1.getName());
		var name2 = Strings.nullToEmpty(module2.getName());

		if (!name1.equals(name2)) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
}