package cipm.consistency.fluentapi.test.metamodel;

import java.util.List;
import java.util.function.Function;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;

/**
 * A class that encapsulates which methods the metamodel-related tests should
 * consider. Those tests should take an instance of this class and use it as
 * information source.
 * <p>
 * <p>
 * Note that several settable attributes of this class have default values for
 * convenience purposes, in cases where they are irrelevant.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIMethodTestData {
	private List<EClass> eClssToCheckFor;

	private Function<EClass, String> methodNamePrefixFunc;
	private Function<EClass, EClassifier> returnTypeOfOpFunc = (eCls) -> eCls;
	private Function<EClass, List<String>> paramNameFunc = (eCls) -> List.of();
	private Function<EClass, List<EClassifier>> paramTypeFunc = (eCls) -> List.of();
	private Function<EClass, Boolean> expectMultiValueVariantsFunc = (eCls) -> false;
	private Function<EClass, Boolean> expectBigNumberVariantsFunc = (eCls) -> false;

	/**
	 * @param eCls A given EClass
	 * @return The name of the considered method for the given EClass
	 * 
	 * @see {@link #setMethodNamePrefixFunc(Function)}
	 */
	public String getMethodName(EClass eCls) {
		return methodNamePrefixFunc.apply(eCls);
	}

	/**
	 * Sets the logic of retrieving methods' names for the considered EClasses, in
	 * cases where the method's name changes based on the individual EClasses.
	 * 
	 * @param methodNamePrefixFunc A function for retrieving the considered method's
	 *                             name for the considered EClasses
	 */
	public void setMethodNamePrefixFunc(Function<EClass, String> methodNamePrefixFunc) {
		this.methodNamePrefixFunc = methodNamePrefixFunc;
	}

	/**
	 * @param eCls A given EClass
	 * @return The return type of the considered method for the given EClass
	 * 
	 * @see {@link #setReturnTypeOfOpFunc(Function)}
	 */
	public EClassifier getReturnTypeOfOp(EClass eCls) {
		return returnTypeOfOpFunc.apply(eCls);
	}

	/**
	 * Sets the logic of retrieving methods' return types for the considered
	 * EClasses, in cases where the method return types change based on the
	 * individual EClasses.
	 * 
	 * @param returnTypeOfOpFunc A function for retrieving the considered method's
	 *                           return type for the considered EClasses
	 */
	public <T extends EClassifier> void setReturnTypeOfOpFunc(Function<EClass, T> returnTypeOfOpFunc) {
		this.returnTypeOfOpFunc = (eCls) -> (T) returnTypeOfOpFunc.apply(eCls);
	}

	/**
	 * @param eCls A given EClass
	 * @return The parameter names of the considered method for the given EClass
	 * 
	 * @see {@link #setParamNameFunc(Function)}
	 */
	public List<String> getParamNames(EClass eCls) {
		return paramNameFunc.apply(eCls);
	}

	/**
	 * Sets the logic of retrieving methods' parameters' names for the considered
	 * EClasses, in cases where they change based on the individual EClasses.
	 * 
	 * @param paramNameFunc A function for retrieving the considered method's
	 *                      parameter names for the considered EClasses
	 */
	public void setParamNameFunc(Function<EClass, List<String>> paramNameFunc) {
		this.paramNameFunc = paramNameFunc;
	}

	/**
	 * @param eCls A given EClass
	 * @return The parameter types of the considered method for the given EClass
	 * 
	 * @see {@link #setParamTypeFunc(Function)}
	 */
	public List<EClassifier> getParamTypes(EClass eCls) {
		return paramTypeFunc.apply(eCls);
	}

	/**
	 * Sets the logic of retrieving methods' parameters' types for the considered
	 * EClasses, in cases where they change based on the individual EClasses.
	 * 
	 * @param paramTypeFunc A function for retrieving the considered method's
	 *                      parameter types for the considered EClasses
	 */
	public void setParamTypeFunc(Function<EClass, List<EClassifier>> paramTypeFunc) {
		this.paramTypeFunc = paramTypeFunc;
	}

	/**
	 * @see {@link #setEClssToCheckFor(List)}
	 */
	public List<EClass> getEClssToCheckFor() {
		return eClssToCheckFor;
	}

	/**
	 * @param eClssToCheckFor A list of EClasses that should be considered in the
	 *                        scope denoted by this instance.
	 */
	public void setEClssToCheckFor(List<EClass> eClssToCheckFor) {
		this.eClssToCheckFor = eClssToCheckFor;
	}

	/**
	 * @param eCls A given EClass
	 * @return Whether the corresponding initialisation class should contain
	 *         overloading modification methods (with array and collection types)
	 */
	public Boolean getExpectMultiValueVariants(EClass eCls) {
		return expectMultiValueVariantsFunc.apply(eCls);
	}

	/**
	 * Sets the logic of determining, whether overloading modification methods in
	 * the initialisation classes for the considered EClasses should be expected.
	 * 
	 * @param expectMultiValueVariantsFunc A function for determining whether the
	 *                                     initialisation class for the considered
	 *                                     EClasses should have any overloading
	 *                                     modification methods (with array and
	 *                                     collection types)
	 */
	public void setExpectMultiValueVariantsFunc(Function<EClass, Boolean> expectMultiValueVariantsFunc) {
		this.expectMultiValueVariantsFunc = expectMultiValueVariantsFunc;
	}

	/**
	 * @param eCls A given EClass
	 * @return Whether the corresponding initialisation class should contain
	 *         overloading modification methods (with primitive types, such as int
	 *         or long)
	 */
	public Boolean getExpectBigNumberVariants(EClass eCls) {
		return expectBigNumberVariantsFunc.apply(eCls);
	}

	/**
	 * Sets the logic of determining, whether overloading modification methods in
	 * the initialisation classes for the considered EClasses should be expected.
	 * 
	 * @param expectBigNumberVariantsFunc A function for determining whether the
	 *                                    initialisation class for the considered
	 *                                    EClasses should have any overloading
	 *                                    modification methods (with primitive
	 *                                    types, such as int or long)
	 */
	public void setExpectBigNumberVariantsFunc(Function<EClass, Boolean> expectBigNumberVariantsFunc) {
		this.expectBigNumberVariantsFunc = expectBigNumberVariantsFunc;
	}

}