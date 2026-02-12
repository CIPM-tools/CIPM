package cipm.consistency.initialisers.jamopp.modifiers;

import java.util.Collection;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserPackage;

public class ModifiersInitialiserPackage implements IInitialiserPackage {
	@Override
	public Collection<IInitialiser> getInitialiserInstances() {
		return this.initCol(new IInitialiser[] { new AbstractInitialiser(), new DefaultInitialiser(),
				new FinalInitialiser(), new NativeInitialiser(), new OpenInitialiser(), new PrivateInitialiser(),
				new ProtectedInitialiser(), new PublicInitialiser(), new StaticInitialiser(), new StrictfpInitialiser(),
				new SynchronizedInitialiser(), new TransientInitialiser(), new TransitiveInitialiser(),
				new VolatileInitialiser(), });
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Class<? extends IInitialiser>> getInitialiserInterfaceTypes() {
		return this.initCol(new Class[] { IAbstractInitialiser.class, IAnnotableAndModifiableInitialiser.class,
				IAnnotationInstanceOrModifierInitialiser.class, IDefaultInitialiser.class, IFinalInitialiser.class,
				IModifiableInitialiser.class, IModifierInitialiser.class, IModuleRequiresModifierInitialiser.class,
				INativeInitialiser.class, IOpenInitialiser.class, IPrivateInitialiser.class,
				IProtectedInitialiser.class, IPublicInitialiser.class, IStaticInitialiser.class,
				IStrictfpInitialiser.class, ISynchronizedInitialiser.class, ITransientInitialiser.class,
				ITransitiveInitialiser.class, IVolatileInitialiser.class, });
	}
}
