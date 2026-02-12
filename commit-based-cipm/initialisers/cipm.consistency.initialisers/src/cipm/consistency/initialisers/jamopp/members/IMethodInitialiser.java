package cipm.consistency.initialisers.jamopp.members;

import org.emftext.language.java.members.Method;

import cipm.consistency.initialisers.jamopp.generics.ITypeParametrizableInitialiser;
import cipm.consistency.initialisers.jamopp.modifiers.IAnnotableAndModifiableInitialiser;
import cipm.consistency.initialisers.jamopp.parameters.IParametrizableInitialiser;
import cipm.consistency.initialisers.jamopp.references.IReferenceableElementInitialiser;
import cipm.consistency.initialisers.jamopp.statements.IStatementContainerInitialiser;
import cipm.consistency.initialisers.jamopp.types.ITypedElementInitialiser;

public interface IMethodInitialiser extends IAnnotableAndModifiableInitialiser, IExceptionThrowerInitialiser,
		IMemberInitialiser, IReferenceableElementInitialiser, IParametrizableInitialiser,
		IStatementContainerInitialiser, ITypedElementInitialiser, ITypeParametrizableInitialiser {
	@Override
	public Method instantiate();
}
