package org.splevo.jamopp.diffing.similarity.base;

/**
 * An interface for similarity comparers, which can be used as a layer of
 * indirection between {@link ISimilarityChecker} and
 * {@link ISimilarityToolbox}. This allows to integrate additional similarity
 * checking related constructs without bloating the implementors of
 * {@link ISimilarityChecker}. <br>
 * <br>
 * {@link AbstractSimilarityComparer} contains further useful methods for
 * similarity comparers. It also integrates {@link ISimilarityToolbox} with
 * {@link ISimilarityComparer}. It is therefore recommended to extend
 * {@link AbstractSimilarityComparer} for similarity comparer classes rather
 * than implementing this interface alone. <br>
 * <br>
 * Implements {@link ISimilarityRequestHandler} because this increases the
 * flexibility of similarity comparers. This is important, as they are thought
 * as a layer of indirection. For example, a similarity comparer could be
 * implemented to work with multiple {@link ISimilarityToolbox} instances, so
 * that {@link ISimilarityToolbox} instances with different purposes can be
 * separated. Alternatively, one can choose to implement
 * {@link ISimilarityComparer} with means other than {@link ISimilarityToolbox}
 * to deal with {@link ISimilarityRequest} instances, although doing so would
 * deviate from {@link AbstractSimilarityComparer} and
 * {@link AbstractSimilarityChecker}.
 * 
 * @see {@link AbstractSimilarityChecker}, {@link AbstractSimilarityComparer},
 *      {@link ISimilarityToolbox}
 * @author Alp Torac Genc
 */
public interface ISimilarityComparer extends ISimilarityRequestHandler {

}