/**
 * Contains unit tests for {@link EObject} types, which have a concrete
 * implementation in form of a non-abstract class in the {@link Commentable}
 * type hierarchy. In the said tests, {@link EObject} instances are constructed
 * programmatically and are checked for similarity. <br>
 * <br>
 * The tests within this package are meant to be minimal and only to test
 * similarity checking with respect to individual attributes of {@link EObject}
 * instances, in an isolated fashion. This means, each test performs similarity
 * checking on 2 {@link EObject} instances, whose attributes are equal except
 * for one of them. This way, one can pinpoint basic similarity checking errors
 * regarding certain attributes of certain {@link EObject} implementors.<br>
 * <br>
 * It is highly recommended to make the construction of the "main"
 * {@link EObject} instances as obvious and visible as possible, as their
 * construction can get complicated and not knowing all the construction steps
 * can lead to tests not fulfilling their goal. <br>
 * <br>
 * See {@link cipm.consistency.fitests.similarity.jamopp.unittests} to find out
 * more about what test methods do.
 */
package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;