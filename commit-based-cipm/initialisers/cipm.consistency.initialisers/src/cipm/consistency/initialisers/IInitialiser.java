package cipm.consistency.initialisers;

import java.util.function.BiFunction;

/**
 * An interface to be implemented by initialisers. Initialisers are interfaces
 * or classes, which are meant to instantiate objects (designated objects) via
 * {@link #instantiate()}. For intuition, their names can be used to denote what
 * they instantiate. <br>
 * <br>
 * Initialisers can also implement (default) methods that modify their
 * designated objects. It is suggested to have such modification methods return
 * something that indicates whether they ran as expected. <b>In general,
 * modification methods DO NOT check, if the object that is being modified
 * (modification target) is null. Attempting to use null as modification target
 * will result in EXCEPTIONS.</b> The reason behind this is the importance of
 * knowing if the modification target is null, as this could hint towards faulty
 * code.<br>
 * <br>
 * addSomething(...) methods have null checks for the passed parameters other
 * than the modification target (i.e. modification parameters), which are used
 * to perform modifications. By default, it is assumed that no null elements can
 * be added to modification targets via addSomething(...). If modification
 * parameters are null, no modification will be performed and the method will be
 * assumed to have run as expected, since no modification was performed and
 * failed. setSomething(...) methods, on the other hand, allow setting
 * attributes of the modification target to null. <br>
 * <br>
 * It is recommended to separate instantiation and initialisation (modification)
 * methods, as doing so will allow using the individual methods in sub-types.
 * Implementing initialisers similar to their designated objects, in terms of
 * type hierarchy and what modifications they allow from outside, may make
 * initialisers more flexible and ease implementing them. <br>
 * <br>
 * A further suggestion is to not declare attributes in the concrete
 * implementors, so that all functionality is present in form of methods. This
 * alleviates having to declare unnecessary attributes in sub-types and makes
 * overriding behaviour easier, since default methods can be overridden in
 * interfaces. If initialisation of the designated objects is complex, consider
 * realising it in form of initialiser adaptation strategies (see the links
 * below). <br>
 * <br>
 * This interface also contains some static utility methods.
 * 
 * @author Alp Torac Genc
 * @see {@link IInitialiserBase}
 * @see {@link IInitialiserAdapterStrategy}
 */
public interface IInitialiser {
	/**
	 * Can be used to create a (deep) copy of this.
	 * 
	 * @return A fresh instance of this initialiser's class.
	 */
	public IInitialiser newInitialiser();

	/**
	 * Attempts to instantiate the class this {@link IInitialiser} is designated
	 * for. Depending on the returned object, additional initialisation may be
	 * necessary.
	 */
	public Object instantiate();

	/**
	 * Attempts to initialise obj, so that it is "valid". <br>
	 * <br>
	 * <b>It is recommended to only use this method where necessary, as it may
	 * introduce additional modifications that are not obvious from outside.</b>
	 * 
	 * @param obj The object that will be made valid
	 */
	public boolean initialise(Object obj);

	/**
	 * Checks whether a given {@link IInitialiser} type directly declares any
	 * methods that modify given object instances. Returns false, if
	 * {@code initCls == null}.
	 */
	public static boolean declaresModificationMethods(Class<? extends IInitialiser> initCls) {
		if (initCls == null) {
			return false;
		}

		var methods = initCls.getDeclaredMethods();

		/*
		 * Instead of using a naming convention for modification methods, use the fact
		 * that modification methods take an object instance obj as a parameter, where
		 * initCls is capable of instantiating obj.
		 */
		for (var met : methods) {

			// A modification method must at least take obj as a parameter to modify it
			if (met.getParameterCount() <= 0)
				continue;

			// One of the parameters has to have the exact type of obj
			// initCls should thus be able to instantiate the type obj
			for (var p : met.getParameters()) {
				var pType = p.getType();
				if (isInitialiserFor(initCls, pType)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * A variant of {@link #declaresModificationMethods(Class)} for
	 * {@link IInitialiser} instances. <br>
	 * <br>
	 * Uses the type of the given parameter init.
	 */
	public static boolean declaresModificationMethods(IInitialiser init) {
		return init != null && declaresModificationMethods(init.getClass());
	}

	/**
	 * An initialiser class is assumed to be able to instantiate the class objClass,
	 * if it has a method (instantiation method), whose return type is objClass and
	 * which has no parameters. Methods inherited by initCls will also be
	 * inspected.<br>
	 * <br>
	 * For the result to be true, initCls has to be able to instantiate
	 * <i><b>exactly</b></i> objClass, i.e. the return type of the instantiation
	 * method has to be <i><b>exactly</b></i> objClass. <br>
	 * <br>
	 * Methods that are generated internally by Java (synthetic methods and bridge
	 * methods) are excluded.
	 * 
	 * @return True, if initCls is an initialiser type, which is meant to
	 *         instantiate objects of class objClass (sub-types of objClass do not
	 *         count).
	 */
	public static boolean isInitialiserFor(Class<? extends IInitialiser> initCls, Class<?> objClass) {
		if (initCls == null || objClass == null) {
			return false;
		}

		/*
		 * Count inherited methods as well, in order to allow initialisers to be
		 * extended without having to explicitly declare/override their instantiation
		 * method.
		 */
		var methods = initCls.getMethods();

		for (var m : methods) {
			/*
			 * Instead of using name checks or annotations, use the fact that the
			 * instantiation method should have the return type objClass and that it should
			 * take no parameters.
			 * 
			 * Also, make sure that the inspected methods are not generated internally by
			 * Java.
			 */
			if (!m.isBridge() && !m.isSynthetic() && m.getReturnType().equals(objClass)
					&& m.getParameters().length == 0) {
				return true;
			}
		}

		return false;
	}

	/**
	 * A variant of {@link #isInitialiserFor(Class, Class)}, where initCls is
	 * extracted from init. Returns false, if any parameter is null.
	 */
	public static boolean isInitialiserFor(IInitialiser init, Class<?> objClass) {
		return init != null && init.getInstanceClassOfInitialiser().equals(objClass);
	}

	/**
	 * Uses the return value of {@link #instantiate()} to determine the outcome.
	 * This method should not return null, as long as {@link #instantiate()} does
	 * not return null.
	 * 
	 * @return The {@link Class} object of the type this initialiser instantiates,
	 *         or null if {@link #instantiate()} returns null.
	 */
	public default Class<?> getInstanceClassOfInitialiser() {
		var obj = this.instantiate();
		return obj != null ? obj.getClass() : null;
	}

	/**
	 * The dynamic variant of {@link #declaresModificationMethods(Class)}. <br>
	 * <br>
	 * Uses the class of this instance.
	 */
	public default boolean declaresModificationMethods() {
		return declaresModificationMethods(this.getClass());
	}

	/**
	 * The dynamic variant of {@link #isInitialiserFor(IInitialiser, Class)}. <br>
	 * <br>
	 * Uses this initialiser as the initialiser parameter.
	 */
	public default boolean isInitialiserFor(Class<?> objClass) {
		return objClass != null && isInitialiserFor(this, objClass);
	}

	/**
	 * A helper method for implementors, which provides them with a template for
	 * versions of their modification methods, which take arrays of parameters
	 * rather than singular ones, and perform multiple modifications. The purpose of
	 * this method is to help keep consistency across the said versions of the
	 * modification methods.<br>
	 * <br>
	 * <b>If modificationFunction returns false for an element x of xs, the method
	 * will FAIL EARLY and return false.</b> This means, modificationFunction WILL
	 * NOT be called for the remaining xs once it fails. Because of this, it is
	 * important to perform modifications one by one, if performing the said
	 * modifications is expected to fail for some x. <br>
	 * <br>
	 * This method is not intended to be used directly from outside.
	 * 
	 * @param <T>                  The type of the object being modified
	 * @param <X>                  The parameter passed to the modification function
	 *                             (modificationFunction)
	 * @param obj                  The object being modified. {@code obj == null}
	 *                             will cause null pointer exceptions, if xs has at
	 *                             least one element.
	 * @param xs                   Array of parameters that will be passed to
	 *                             modificationFunction
	 * @param modificationFunction The modification function that will be run on
	 *                             obj, using xs as parameters (one
	 *                             modificationFunction call each x in xs)
	 * 
	 * @return
	 *         <ul>
	 *         <li>True, if either:
	 *         <ul>
	 *         <li>xs is null (because no modifications were performed and nothing
	 *         can fail)
	 *         <li>All modification method calls returned true (i.e. all
	 *         modifications were successfully performed)
	 *         </ul>
	 *         <li>Otherwise false, i.e. if {@code xs != null} and a modification
	 *         method call returned false.
	 *         </ul>
	 */
	public default <T extends Object, X extends Object> boolean doMultipleModifications(T obj, X[] xs,
			BiFunction<T, X, Boolean> modificationFunction) {
		if (xs != null) {
			for (var x : xs) {
				if (!modificationFunction.apply(obj, x))
					return false;
			}
		}

		return true;
	}
}