package cipm.consistency.initialisers.jamopp.members;

import org.emftext.language.java.members.Constructor;
import org.emftext.language.java.statements.StatementListContainer;

import cipm.consistency.initialisers.jamopp.generics.ITypeParametrizableInitialiser;
import cipm.consistency.initialisers.jamopp.modifiers.IAnnotableAndModifiableInitialiser;
import cipm.consistency.initialisers.jamopp.parameters.IParametrizableInitialiser;
import cipm.consistency.initialisers.jamopp.statements.IBlockContainerInitialiser;
import cipm.consistency.initialisers.jamopp.statements.IStatementListContainerInitialiser;

public interface IConstructorInitialiser extends IAnnotableAndModifiableInitialiser, IBlockContainerInitialiser,
		IExceptionThrowerInitialiser, IMemberInitialiser, IParametrizableInitialiser,
		IStatementListContainerInitialiser, ITypeParametrizableInitialiser {

	@Override
	public Constructor instantiate();

	@Override
	public default boolean canContainStatements(StatementListContainer slc) {
		return ((Constructor) slc).getBlock() != null;
	}
}
