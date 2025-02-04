/**
 * Contains interfaces for initialiser implementations (such as
 * {@link cipm.consistency.initialisers.IInitialiser} as well as for classes
 * that can be used to adapt them. Also has an interface
 * {@link cipm.consistency.initialisers.IInitialiserPackage}, which can be
 * implemented to access initialiser types and instances easier.
 * {@link cipm.consistency.initialisers.IInitialiserPackage} defines a nestable
 * structure that enables finding the proper initialisers as well as certain
 * groups of initialisers. It is recommended to implement that interface for
 * each package containing initialisers and to add all initialisers to that
 * implementation. <br>
 * <br>
 * Initialisers are classes/interfaces, whose purpose is to instantiate,
 * initialise and modify certain objects. Implementing initialisers similar to
 * their designated objects, may help making the initialisers more flexible and
 * can ease adding further initialisers later on. It is strongly recommended to
 * define atomic modification methods in initialisers and re-using them, rather
 * than defining complex modification methods. <br>
 * <br>
 * In order to make parameterised tests using initialisers as parameters
 * possible, one can use
 * {@link cipm.consistency.initialisers.IInitialiserAdapterStrategy} to adapt
 * certain initialisers. This way, they can be used in such tests without
 * throwing exceptions and without the need of type checking. The most common
 * cause of such exceptions is initialisers not setting the required attributes
 * while instantiating objects. In such cases, the said adapters can be used to
 * have them set those attributes, so that using the instances in tests do not
 * throw exceptions, due to their essential attributes not being set.
 */
package cipm.consistency.initialisers;