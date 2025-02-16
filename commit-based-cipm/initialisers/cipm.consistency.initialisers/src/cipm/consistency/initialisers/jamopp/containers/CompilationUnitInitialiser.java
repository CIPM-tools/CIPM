package cipm.consistency.initialisers.jamopp.containers;

import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.ContainersFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class CompilationUnitInitialiser extends AbstractInitialiserBase implements ICompilationUnitInitialiser {
	@Override
	public CompilationUnit instantiate() {
		var fac = ContainersFactory.eINSTANCE;
		return fac.createCompilationUnit();
	}

	@Override
	public ICompilationUnitInitialiser newInitialiser() {
		return new CompilationUnitInitialiser();
	}
}
