package cipm.consistency.initialisers.jamopp.members;

import org.emftext.language.java.members.ExceptionThrower;
import org.emftext.language.java.types.NamespaceClassifierReference;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface IExceptionThrowerInitialiser extends ICommentableInitialiser {
	@Override
	public ExceptionThrower instantiate();

	public default boolean addException(ExceptionThrower extt, NamespaceClassifierReference exception) {
		if (exception != null) {
			extt.getExceptions().add(exception);
			return extt.getExceptions().contains(exception);
		}
		return true;
	}

	public default boolean addExceptions(ExceptionThrower extt, NamespaceClassifierReference[] exceptions) {
		return this.doMultipleModifications(extt, exceptions, this::addException);
	}
}
