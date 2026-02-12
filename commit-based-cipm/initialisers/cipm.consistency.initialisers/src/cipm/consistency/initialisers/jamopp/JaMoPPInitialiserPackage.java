package cipm.consistency.initialisers.jamopp;

import java.util.Collection;

import cipm.consistency.initialisers.IInitialiserPackage;
import cipm.consistency.initialisers.jamopp.annotations.AnnotationsInitialiserPackage;
import cipm.consistency.initialisers.jamopp.arrays.ArraysInitialiserPackage;
import cipm.consistency.initialisers.jamopp.classifiers.ClassifierInitialiserPackage;
import cipm.consistency.initialisers.jamopp.commons.CommonsInitialiserPackage;
import cipm.consistency.initialisers.jamopp.containers.ContainersInitialiserPackage;
import cipm.consistency.initialisers.jamopp.expressions.ExpressionsInitialiserPackage;
import cipm.consistency.initialisers.jamopp.generics.GenericsInitialiserPackage;
import cipm.consistency.initialisers.jamopp.imports.ImportsInitialiserPackage;
import cipm.consistency.initialisers.jamopp.instantiations.InstantiationsInitialiserPackage;
import cipm.consistency.initialisers.jamopp.literals.LiteralsInitialiserPackage;
import cipm.consistency.initialisers.jamopp.members.MembersInitialiserPackage;
import cipm.consistency.initialisers.jamopp.modifiers.ModifiersInitialiserPackage;
import cipm.consistency.initialisers.jamopp.modules.ModulesInitialiserPackage;
import cipm.consistency.initialisers.jamopp.operators.OperatorsInitialiserPackage;
import cipm.consistency.initialisers.jamopp.parameters.ParametersInitialiserPackage;
import cipm.consistency.initialisers.jamopp.references.ReferencesInitialiserPackage;
import cipm.consistency.initialisers.jamopp.statements.StatementsInitialiserPackage;
import cipm.consistency.initialisers.jamopp.types.TypesInitialiserPackage;
import cipm.consistency.initialisers.jamopp.variables.VariablesInitialiserPackage;

/**
 * The topmost implementor of {@link IInitialiserPackage} for EObject
 * implementors used by JaMoPP.
 * 
 * @author Alp Torac Genc
 */
public class JaMoPPInitialiserPackage implements IInitialiserPackage {
	@Override
	public Collection<IInitialiserPackage> getSubPackages() {
		return this.initCol(new IInitialiserPackage[] { new AnnotationsInitialiserPackage(),
				new ArraysInitialiserPackage(), new ClassifierInitialiserPackage(), new CommonsInitialiserPackage(),
				new ContainersInitialiserPackage(), new ExpressionsInitialiserPackage(),
				new GenericsInitialiserPackage(), new ImportsInitialiserPackage(),
				new InstantiationsInitialiserPackage(), new LiteralsInitialiserPackage(),
				new MembersInitialiserPackage(), new ModifiersInitialiserPackage(), new ModulesInitialiserPackage(),
				new OperatorsInitialiserPackage(), new ParametersInitialiserPackage(),
				new ReferencesInitialiserPackage(), new StatementsInitialiserPackage(), new TypesInitialiserPackage(),
				new VariablesInitialiserPackage() });
	}
}
