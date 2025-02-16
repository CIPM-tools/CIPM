package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.members.ClassMethod;
import org.emftext.language.java.members.InterfaceMethod;

import cipm.consistency.initialisers.jamopp.members.ClassMethodInitialiser;
import cipm.consistency.initialisers.jamopp.members.InterfaceMethodInitialiser;

/**
 * An interface that can be implemented by tests, which work with {@link Method}
 * instances. <br>
 * <br>
 * Contains methods that can be used to create {@link Method} instances.
 */
public interface UsesMethods extends UsesStatements {
	/**
	 * A variant of {@link #createMinimalClsMethodWithNullReturn()} with the given
	 * parameter
	 * 
	 * @param methodName The name of the instance to be constructed
	 */
	public default ClassMethod createMinimalClsMethodWithNullReturn(String methodName) {
		var init = new ClassMethodInitialiser();
		ClassMethod result = this.createMinimalClsMethodWithNullReturn();
		init.setName(result, methodName);
		return result;
	}

	/**
	 * @return A {@link ClassMethod} instance with only one {@link Statement}
	 *         constructed by {@link #createMinimalNullReturn()}.
	 */
	public default ClassMethod createMinimalClsMethodWithNullReturn() {
		var init = new ClassMethodInitialiser();
		ClassMethod result = init.instantiate();
		init.addStatement(result, this.createMinimalNullReturn());
		return result;
	}

	/**
	 * A variant of {@link #createMinimalInterfaceMethodWithNullReturn()} with the
	 * given parameter
	 * 
	 * @param methodName The name of the instance to be constructed
	 */
	public default InterfaceMethod createMinimalInterfaceMethodWithNullReturn(String methodName) {
		var init = new InterfaceMethodInitialiser();
		InterfaceMethod result = this.createMinimalInterfaceMethodWithNullReturn();
		init.setName(result, methodName);
		return result;
	}

	/**
	 * @return A {@link InterfaceMethod} instance with only one {@link Statement}
	 *         constructed by {@link #createNullLiteral()}.
	 */
	public default InterfaceMethod createMinimalInterfaceMethodWithNullReturn() {
		var init = new InterfaceMethodInitialiser();
		InterfaceMethod result = init.instantiate();
		init.setDefaultValue(result, this.createNullLiteral());
		return result;
	}

}
