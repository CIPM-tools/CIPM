package cipm.consistency.fitests.similarity.jamopp.params;

import cipm.consistency.initialisers.IInitialiserBase;
import cipm.consistency.initialisers.jamopp.classifiers.ClassInitialiser;
import cipm.consistency.initialisers.jamopp.classifiers.IConcreteClassifierInitialiser;
import cipm.consistency.initialisers.jamopp.commons.INamedElementInitialiser;
import cipm.consistency.initialisers.jamopp.containers.CompilationUnitInitialiser;
import cipm.consistency.initialisers.jamopp.initadapters.BlockContainerInitialiserAdapter;
import cipm.consistency.initialisers.jamopp.initadapters.ClassMethodInitialiserAdapter;
import cipm.consistency.initialisers.jamopp.initadapters.ConcreteClassifierInitialiserAdapter;
import cipm.consistency.initialisers.jamopp.initadapters.IdentifierReferenceInitialiserAdapter;
import cipm.consistency.initialisers.jamopp.initadapters.MemberInitialiserAdapter;
import cipm.consistency.initialisers.jamopp.initadapters.NamedElementInitialiserAdapter;
import cipm.consistency.initialisers.jamopp.initadapters.NewConstructorCallInitialiserAdapter;
import cipm.consistency.initialisers.jamopp.instantiations.INewConstructorCallInitialiser;
import cipm.consistency.initialisers.jamopp.members.IClassMethodInitialiser;
import cipm.consistency.initialisers.jamopp.members.IMemberInitialiser;
import cipm.consistency.initialisers.jamopp.references.IIdentifierReferenceInitialiser;
import cipm.consistency.initialisers.jamopp.statements.BlockInitialiser;
import cipm.consistency.initialisers.jamopp.statements.IBlockContainerInitialiser;
import cipm.consistency.initialisers.jamopp.types.ClassifierReferenceInitialiser;
import cipm.consistency.fitests.similarity.params.IInitialiserParameterAdaptationStrategy;

/**
 * A concrete implementation of {@link IInitialiserParameterAdaptationStrategy}.
 * <br>
 * <br>
 * Adapts the given {@link IInitialiserBase} instances in a way that interface
 * tests can run without exceptions being thrown, due to objects they
 * instantiate missing certain components.
 * 
 * @author Alp Torac Genc
 */
public class JaMoPPInitialiserParameterAdaptationStrategy implements IInitialiserParameterAdaptationStrategy {
	@Override
	public void adaptAdaptableInitialiser(IInitialiserBase init) {
		if (INamedElementInitialiser.class.isAssignableFrom(init.getClass())) {
			init.addAdaptingStrategy(new NamedElementInitialiserAdapter());
		}
		if (IBlockContainerInitialiser.class.isAssignableFrom(init.getClass())) {
			init.addAdaptingStrategy(new BlockContainerInitialiserAdapter(new BlockInitialiser()));
		}
		if (IMemberInitialiser.class.isAssignableFrom(init.getClass())) {
			init.addAdaptingStrategy(new MemberInitialiserAdapter(new ClassInitialiser()));
		}
		if (IConcreteClassifierInitialiser.class.isAssignableFrom(init.getClass())) {
			init.addAdaptingStrategy(new ConcreteClassifierInitialiserAdapter(new CompilationUnitInitialiser()));
		}
		if (INewConstructorCallInitialiser.class.isAssignableFrom(init.getClass())) {
			init.addAdaptingStrategy(new NewConstructorCallInitialiserAdapter(new ClassifierReferenceInitialiser(),
					new ClassInitialiser()));
		}
		if (IClassMethodInitialiser.class.isAssignableFrom(init.getClass())) {
			init.addAdaptingStrategy(new ClassMethodInitialiserAdapter(new BlockInitialiser()));
		}
		if (IIdentifierReferenceInitialiser.class.isAssignableFrom(init.getClass())) {
			init.addAdaptingStrategy(new IdentifierReferenceInitialiserAdapter());
		}
	}
}
