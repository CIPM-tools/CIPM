package cipm.consistency.initialisers.jamopp.members;

import java.util.Collection;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserPackage;

public class MembersInitialiserPackage implements IInitialiserPackage {
	@Override
	public Collection<IInitialiser> getInitialiserInstances() {
		return this.initCol(new IInitialiser[] { new AdditionalFieldInitialiser(), new ClassMethodInitialiser(),
				new ConstructorInitialiser(), new EmptyMemberInitialiser(), new EnumConstantInitialiser(),
				new FieldInitialiser(), new InterfaceMethodInitialiser(), });
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Class<? extends IInitialiser>> getInitialiserInterfaceTypes() {
		return this.initCol(new Class[] { IAdditionalFieldInitialiser.class, IClassMethodInitialiser.class,
				IConstructorInitialiser.class, IEmptyMemberInitialiser.class, IEnumConstantInitialiser.class,
				IExceptionThrowerInitialiser.class, IFieldInitialiser.class, IInterfaceMethodInitialiser.class,
				IMemberContainerInitialiser.class, IMemberInitialiser.class, IMethodInitialiser.class, });
	}
}
