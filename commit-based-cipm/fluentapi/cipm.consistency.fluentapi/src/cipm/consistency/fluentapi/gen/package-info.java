/**
 * Contains the generator class responsible for generating the EMF model
 * elements of the fluent API, as well as other constructs assisting this
 * process.
 * <p>
 * <p>
 * The generation-related code within the generator classes is written in a
 * modular way, such that modifying {@link ModelConstants} via refactoring
 * operations should reflect MOST of the changes to the generated model.
 * <b><i>Due to the potential usage of static methods SM within the generated
 * fluent API code, it is NOT NECESSARILY guaranteed to generate valid methods
 * after changes to the generation code: If the name of any SM changes, their
 * occurrences within the generation code must be changed MANUALLY.</i></b> As
 * Java currently does not allow controlling names of static methods through
 * constant variables in the code, this issue cannot be addressed without
 * generating all SM alongside fluent API.
 * <p>
 * <p>
 * In the current version, the FluentAPIGenerator class is the top-most
 * generator, which by working with other generators creates the EMF model of
 * the fluent API:
 * <ul>
 * <li>{@link cipm.consistency.fluentapi.gen.rootapi}: Contains the generator
 * classes associated with the fluent API's facade class, which allows using the
 * fluent API from outside conveniently.
 * <li>{@link cipm.consistency.fluentapi.gen.superinit}: Contains the generator
 * classes associated with the super type of all initialisation classes.
 * <li>{@link cipm.consistency.fluentapi.gen.init}: Contains the generator
 * classes associated with the concrete implementations of initialisation
 * classes, each responsible for creating and modifying elements of certain
 * types.
 * </ul>
 * <p>
 * <p>
 * Note: Some of the generator classes may implement multiple methods for
 * generation purposes. If that is the case, refer to their documentation for
 * further details / instructions.
 */
package cipm.consistency.fluentapi.gen;