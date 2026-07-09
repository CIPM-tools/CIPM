/**
 * Contains extension classes, which offer static methods to the fluent api. The
 * purpose of the extension classes is to both keep the generated fluent api
 * code less verbose and to make fluent api more metamodel agnostic (through
 * EMF-Reflection for instance).
 * <p>
 * <p>
 * Note: Changing any public member within this package (i.e. classes or methods
 * of the classes) requires adapting the generation of fluent api. This is due
 * to Java limitations, which do not allow dynamically adjusting static
 * elements, such as method or class names.
 */
package cipm.consistency.fluentapi.extensions;