package org.splevo.jamopp.diffing.similarity.switches;

import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.Package;
import org.emftext.language.java.containers.util.ContainersSwitch;
import org.splevo.jamopp.diffing.similarity.IJavaSimilaritySwitch;
import org.splevo.jamopp.diffing.similarity.ILoggableJavaSwitch;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequestHandler;
import org.splevo.jamopp.diffing.util.JaMoPPModelUtil;

import com.google.common.base.Strings;

/**
 * Similarity decisions for container elements.
 */
public class ContainersSimilaritySwitch extends ContainersSwitch<Boolean> implements ILoggableJavaSwitch, IJavaSimilarityPositionInnerSwitch {
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
     * Note: CompilationUnit names are full qualified. So it is important to apply classifier as
     * well as package renaming normalizations to them.
     * 
     * @param unit1
     *            The compilation unit to compare with the compareElement.
     * @return True/False whether they are similar or not.
     */
    @Override
    public Boolean caseCompilationUnit(CompilationUnit unit1) {
    	this.logMessage("caseCompilationUnit");
    	
        CompilationUnit unit2 = (CompilationUnit) this.getCompareElement();
        this.logComparison(unit1.getName(), unit2.getName(), CompilationUnit.class.getSimpleName());
        
        String name1 = this.normalizeCompilationUnit(unit1.getName());
        name1 = this.normalizePackage(name1);
        String name2 = unit2.getName();
        
        this.logResult(name1.equals(name2), "compilation unit name");
        if (!name1.equals(name2)) {
            return Boolean.FALSE;
        }
        
        String namespaceString1 = this.normalizeNamespace(unit1.getNamespacesAsString());
        String namespaceString2 = Strings.nullToEmpty(unit2.getNamespacesAsString());
        
        this.logResult(namespaceString1.equals(namespaceString2), "compilation unit namespace");
        
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
     * @param package1
     *            The package to compare with the compare element.
     * @return True/False if the packages are similar or not.
     */
    @Override
    public Boolean casePackage(Package package1) {
    	this.logMessage("casePackage");
    	
        Package package2 = (Package) this.getCompareElement();
        this.logComparison(package1, package2, Package.class.getSimpleName());
        
        String packagePath1 = JaMoPPModelUtil.buildNamespacePath(package1);
        packagePath1 = this.normalizeNamespace(packagePath1);
        String packagePath2 = JaMoPPModelUtil.buildNamespacePath(package2);
        
        this.logComparison(packagePath1, packagePath2, "package namespace");
        this.logResult(packagePath1.equals(packagePath2), "package path");
        
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
    	
    	org.emftext.language.java.containers.Module module2 =
    			(org.emftext.language.java.containers.Module) this.getCompareElement();
    	
    	this.logResult(module1.getName().equals(module2.getName()), org.emftext.language.java.containers.Module.class.getSimpleName());
    	if (!module1.getName().equals(module2.getName())) {
    		return Boolean.FALSE;
    	}
    	return Boolean.TRUE;
    }
}