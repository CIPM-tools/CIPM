package cipm.consistency.initialisers.jamopp.references;

import java.util.Collection;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserPackage;

public class ReferencesInitialiserPackage implements IInitialiserPackage {
	@Override
	public Collection<IInitialiser> getInitialiserInstances() {
		return this.initCol(new IInitialiser[] { new IdentifierReferenceInitialiser(), new MethodCallInitialiser(),
				new PackageReferenceInitialiser(), new PrimitiveTypeReferenceInitialiser(),
				new ReflectiveClassReferenceInitialiser(), new SelfReferenceInitialiser(),
				new StringReferenceInitialiser(), new TextBlockReferenceInitialiser(), });
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Class<? extends IInitialiser>> getInitialiserInterfaceTypes() {
		return this.initCol(new Class[] { IArgumentableInitialiser.class, IElementReferenceInitialiser.class,
				IIdentifierReferenceInitialiser.class, IMethodCallInitialiser.class, IPackageReferenceInitialiser.class,
				IPrimitiveTypeReferenceInitialiser.class, IReferenceableElementInitialiser.class,
				IReferenceInitialiser.class, IReflectiveClassReferenceInitialiser.class,
				ISelfReferenceInitialiser.class, IStringReferenceInitialiser.class,
				ITextBlockReferenceInitialiser.class, });
	}
}
