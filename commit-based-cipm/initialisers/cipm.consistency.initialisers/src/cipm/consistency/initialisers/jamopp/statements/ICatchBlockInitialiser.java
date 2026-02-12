package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.parameters.OrdinaryParameter;
import org.emftext.language.java.statements.CatchBlock;
import org.emftext.language.java.statements.StatementListContainer;

public interface ICatchBlockInitialiser extends IBlockContainerInitialiser, IStatementListContainerInitialiser {

	@Override
	public CatchBlock instantiate();

	public default boolean setParameter(CatchBlock cb, OrdinaryParameter param) {
		cb.setParameter(param);
		return (param == null && cb.getParameter() == null) || cb.getParameter().equals(param);
	}

	@Override
	public default boolean canContainStatements(StatementListContainer slc) {
		return ((CatchBlock) slc).getBlock() != null;
	}
}
