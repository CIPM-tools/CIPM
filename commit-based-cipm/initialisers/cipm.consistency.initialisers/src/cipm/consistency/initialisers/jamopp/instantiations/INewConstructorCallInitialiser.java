package cipm.consistency.initialisers.jamopp.instantiations;

import org.emftext.language.java.classifiers.AnonymousClass;
import org.emftext.language.java.instantiations.NewConstructorCall;

import cipm.consistency.initialisers.jamopp.types.ITypedElementInitialiser;

public interface INewConstructorCallInitialiser extends IInstantiationInitialiser, ITypedElementInitialiser {

	@Override
	public NewConstructorCall instantiate();

	public default boolean setAnonymousClass(NewConstructorCall ncc, AnonymousClass anonymousCls) {
		ncc.setAnonymousClass(anonymousCls);
		return (anonymousCls == null && ncc.getAnonymousClass() == null)
				|| ncc.getAnonymousClass().equals(anonymousCls);
	}
}
