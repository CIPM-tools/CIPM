/**
 * This package contains dummy objects without members and initialisers for each
 * of those objects (both an initialiser class and an initialiser interface).
 * These objects and initialisers extend one another and form a type hierarchy.
 * <br>
 * <br>
 * The constructs defined within this package can be used to test interactions
 * of some {@link IInitialiser} methods with objects/initialisers that inherit
 * from one another. <br>
 * <br>
 * The current layout of the type hierarchy for the objects is as follows:
 * <ul>
 * <li>{@link DummyTopLevelObj}
 * <ul>
 * <li>{@link DummyNonTerminalObj}
 * <ul>
 * <li>{@link DummyTerminalObj}
 * </ul>
 * </ul>
 * </ul>
 * The current layout of the type hierarchy for the initialisers is as follows
 * (similar for initialiser interfaces):
 * <ul>
 * <li>{@link DummyTopLevelObjInitialiser}
 * <ul>
 * <li>{@link DummyNonTerminalObjInitialiser}
 * <ul>
 * <li>{@link DummyTerminalObjInitialiser}
 * </ul>
 * <li>{@link DummyAlternateInitialiser}
 * </ul>
 * </ul>
 */
package cipm.consistency.initialisers.tests.dummy.types.verticalhierarchy;