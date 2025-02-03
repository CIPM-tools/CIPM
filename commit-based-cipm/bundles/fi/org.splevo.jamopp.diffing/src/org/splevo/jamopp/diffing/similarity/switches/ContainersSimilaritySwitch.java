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
		this.logMessage("caseCompilationUnit");

		CompilationUnit unit2 = (CompilationUnit) this.getCompareElement();

		String name1 = Strings.nullToEmpty(unit1.getName());
		name1 = Strings.nullToEmpty(this.normalizeCompilationUnit(name1));
		name1 = Strings.nullToEmpty(this.normalizePackage(name1));

		String name2 = Strings.nullToEmpty(unit2.getName());

		if (!JaMoPPStringUtil.stringsEqual(name1, name2)) {
			return Boolean.FALSE;
		}

		String namespaceString1 = Strings.nullToEmpty(unit1.getNamespacesAsString());
		String namespaceString2 = Strings.nullToEmpty(unit2.getNamespacesAsString());
		namespaceString1 = Strings.nullToEmpty(this.normalizeNamespace(namespaceString1));

		if (!namespaceString1.equals(namespaceString2)) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
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
		this.logMessage("casePackage");

		Package package2 = (Package) this.getCompareElement();

		String packagePath1 = Strings.nullToEmpty(JaMoPPModelUtil.buildNamespacePath(package1));
		packagePath1 = Strings.nullToEmpty(this.normalizeNamespace(packagePath1));

		String packagePath2 = Strings.nullToEmpty(JaMoPPModelUtil.buildNamespacePath(package2));

		if (!packagePath1.equals(packagePath2)) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
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
		this.logMessage("caseModule");

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