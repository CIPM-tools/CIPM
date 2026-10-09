/**
 * Contains classes that help adapt initialisers in ways that lets them
 * instantiate valid objects without additional method calls. This is
 * particularly useful for parameterized tests, as there may be initialisers
 * extending mutual interfaces/classes, which may need to perform additional
 * modifications of the objects they instantiate. If this is the case, such
 * initialisers can be adapted to automatically perform those modifications,
 * without large if or instanceof blocks in tests.
 */
package cipm.consistency.initialisers.jamopp.initadapters;