/**
 * Contains the generator classes responsible for generating the EMF model
 * elements of the Initialisation classes of the fluent API, i.e. the classes
 * that facilitate the creation and modification of elements of a certain type.
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
 * In the current version, the FluentAPIInitialisationEClassGenerator class is
 * the top-most generator within this package. It is responsible for generating
 * the EClass of each Initialisation class. The rest of the classes within this
 * package are responsible for generating EOperation instances of various
 * methods of the Initialisation classes. Names of these classes denote which
 * methods they are responsible for.
 */
package cipm.consistency.fluentapi.gen.init;