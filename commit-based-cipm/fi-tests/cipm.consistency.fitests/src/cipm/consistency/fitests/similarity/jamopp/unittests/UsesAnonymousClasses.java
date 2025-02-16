package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.classifiers.AnonymousClass;

import cipm.consistency.initialisers.jamopp.classifiers.AnonymousClassInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link AnonymousClass} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link AnonymousClass} instances.
 */
public interface UsesAnonymousClasses extends UsesMethods {
	/**
	 * @param methodName See {@link #createMinimalClsMethodWithNullReturn(String)}
	 * 
	 * @return An {@link AnonymousClass} instance with a {@link Member} constructed
	 *         using {@link #createMinimalClsMethodWithNullReturn(String)}.
	 * 
	 */
	public default AnonymousClass createMinimalAnonymousClassWithMethod(String methodName) {
		var acInit = new AnonymousClassInitialiser();
		var ac = this.createMinimalAnonymousClass();
		acInit.addMember(ac, this.createMinimalClsMethodWithNullReturn(methodName));
		return ac;
	}

	/**
	 * @return A minimal {@link AnonymousClass} instance.
	 */
	public default AnonymousClass createMinimalAnonymousClass() {
		var acInit = new AnonymousClassInitialiser();
		var ac = acInit.instantiate();
		return ac;
	}

}
