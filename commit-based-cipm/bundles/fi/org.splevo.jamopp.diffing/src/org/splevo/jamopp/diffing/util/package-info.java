/**
 * Utility package to handle JaMoPP models. The classes within contain methods
 * that can work with null parameters. <br>
 * <br>
 * Similarity checking requires comparing a multitude of objects with one
 * another throughout the process. During similarity checking, comparing said
 * objects to one another with the comparison methods is very important, in
 * order to ensure consistency. Therefore, several utility classes are extracted
 * from similarity checking mechanisms, so that the same comparison methods can
 * be used in multiple places. Additionally, this makes changing the comparison
 * logic easier, since changing the methods within utility classes is
 * sufficient.
 */
package org.splevo.jamopp.diffing.util;
