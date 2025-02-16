package cipm.consistency.initialisers.jamopp.types;

import java.util.Collection;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserPackage;

public class TypesInitialiserPackage implements IInitialiserPackage {
	@Override
	public Collection<IInitialiser> getInitialiserInstances() {
		return this.initCol(new IInitialiser[] { new BooleanInitialiser(), new ByteInitialiser(), new CharInitialiser(),
				new ClassifierReferenceInitialiser(), new DoubleInitialiser(), new FloatInitialiser(),
				new InferableTypeInitialiser(), new IntInitialiser(), new LongInitialiser(),
				new NamespaceClassifierReferenceInitialiser(), new ShortInitialiser(), new VoidInitialiser(), });
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Class<? extends IInitialiser>> getInitialiserInterfaceTypes() {
		return this.initCol(new Class[] { IBooleanInitialiser.class, IByteInitialiser.class, ICharInitialiser.class,
				IClassifierReferenceInitialiser.class, IDoubleInitialiser.class, IFloatInitialiser.class,
				IInferableTypeInitialiser.class, IIntInitialiser.class, ILongInitialiser.class,
				INamespaceClassifierReferenceInitialiser.class, IPrimitiveTypeInitialiser.class,
				IShortInitialiser.class, ITypedElementExtensionInitialiser.class, ITypedElementInitialiser.class,
				ITypeInitialiser.class, ITypeReferenceInitialiser.class, IVoidInitialiser.class, });
	}
}
