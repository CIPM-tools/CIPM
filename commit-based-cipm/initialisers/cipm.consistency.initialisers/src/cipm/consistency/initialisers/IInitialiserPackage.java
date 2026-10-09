package cipm.consistency.initialisers;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An interface meant to be implemented by classes that provide access to groups
 * of instances, classes and interfaces that implement {@link IInitialiser}. Can
 * be used to discover which initialisers are present and to allow centralised
 * access to initialisers. <br>
 * <br>
 * Override {@link #getInitialiserInstances()},
 * {@link #getInitialiserInterfaceTypes()} and {@link #getSubPackages()} to
 * change what the implementors encompass. The default implementations of the
 * methods only return an empty collection of their respective return type.
 * 
 * @author Alp Torac Genc
 */
public interface IInitialiserPackage {
	/**
	 * @return A freshly created instance of each {@link IInitialiser} sub-type,
	 *         which this encompasses.
	 * 
	 * @see {@link #getAllInitialiserInstances()} for all such initialisers that are
	 *      accessible from sub-packages as well as this.
	 */
	public default Collection<IInitialiser> getInitialiserInstances() {
		return this.initCol();
	}

	/**
	 * @return Class objects of {@link IInitialiser} types that are contained in
	 *         this instance. Note that the returned types are not necessarily
	 *         concrete, i.e. they can be interfaces and/or abstract classes.
	 * 
	 * @see {@link #getAllInitialiserInterfaceTypes()} for all such initialiser
	 *      types that are accessible from this.
	 */
	public default Collection<Class<? extends IInitialiser>> getInitialiserInterfaceTypes() {
		return this.initCol();
	}

	/**
	 * @return All {@link IInitialiserPackage} instances nested in this, i.e. the
	 *         sub-packages of this.
	 */
	public default Collection<IInitialiserPackage> getSubPackages() {
		return this.initCol();
	}

	/**
	 * Meant to be used only by {@link IInitialiserPackage} implementors.
	 * 
	 * @return An empty collection that will be used to store objects of type T.
	 */
	public default <T extends Object> Collection<T> initCol() {
		return new ArrayList<T>();
	}

	/**
	 * Meant to be used only by {@link IInitialiserPackage} implementors. <br>
	 * <br>
	 * A variant of {@link #initCol()} that also adds the given elems to the created
	 * collection.
	 * 
	 * @return A collection containing elems.
	 */
	public default <T extends Object> Collection<T> initCol(T[] elems) {
		Collection<T> res = this.initCol();

		for (var e : elems) {
			res.add(e);
		}

		return res;
	}

	/**
	 * Recursively discovers all nested {@link IInitialiserPackage} instances
	 * reachable from this instance.
	 * 
	 * @return All {@link IInitialiserPackage} instances accessible from this.
	 * 
	 * @see {@link #getSubPackages()} for initialiser packages that are contained in
	 *      this directly.
	 */
	public default Collection<IInitialiserPackage> getAllSubPackages() {
		var result = this.getSubPackages();

		for (var pac : this.getSubPackages()) {
			result.addAll(pac.getSubPackages());
		}

		return result;
	}

	/**
	 * Recursively discovers all nested {@link IInitialiserPackage} instances
	 * reachable from this instance.
	 * 
	 * @return Freshly created {@link IInitialiser} instances for each initialiser
	 *         accessible from this.
	 * 
	 * @see {@link #getInitialiserInstances()} for initialiser instances that are
	 *      contained in this directly.
	 */
	public default Collection<IInitialiser> getAllInitialiserInstances() {
		var result = this.getInitialiserInstances();

		for (var pac : this.getAllSubPackages()) {
			result.addAll(pac.getInitialiserInstances());
		}

		return result;
	}

	/**
	 * Recursively discovers all nested {@link IInitialiserPackage} instances
	 * reachable from this instance.
	 * 
	 * @return All initialiser types that are accessible from this.
	 * 
	 * @see {@link #getInitialiserInterfaceTypes()} for initialiser types that are
	 *      contained in this directly.
	 */
	public default Collection<Class<? extends IInitialiser>> getAllInitialiserInterfaceTypes() {
		var result = this.getInitialiserInterfaceTypes();

		for (var pac : this.getAllSubPackages()) {
			result.addAll(pac.getInitialiserInterfaceTypes());
		}

		return result;
	}

	/**
	 * Recursively looks through all nested {@link IInitialiserPackage} instances
	 * for an {@link IInitialiser} type, which is capable of instantiating the given
	 * cls.
	 * 
	 * @return The class object of the {@link IInitialiser} type meant to
	 *         instantiate the given cls. Null, if there is no such
	 *         {@link IInitialiser} reachable from this.
	 */
	public default Class<? extends IInitialiser> getInitialiserInterfaceTypeFor(Class<?> cls) {
		var initClss = this.getAllInitialiserInterfaceTypes();

		for (var initCls : initClss) {
			if (IInitialiser.isInitialiserFor(initCls, cls)) {
				return initCls;
			}
		}

		return null;
	}

	/**
	 * Recursively looks for an {@link IInitialiser} instance, which is capable of
	 * instantiating the given cls.
	 * 
	 * @return An instance of the {@link IInitialiser} that is meant to instantiate
	 *         cls. Null, if there is no such {@link IInitialiser} reachable from
	 *         this.
	 */
	public default IInitialiser getInitialiserInstanceFor(Class<?> cls) {
		var init = this.getAllInitialiserInstances();

		for (var i : init) {
			if (i.isInitialiserFor(cls)) {
				return i;
			}
		}

		return null;
	}
}
