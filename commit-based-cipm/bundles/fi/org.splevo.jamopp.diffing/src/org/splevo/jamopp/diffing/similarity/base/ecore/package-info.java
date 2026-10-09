/**
 * Complements {@link org.splevo.jamopp.diffing.similarity.base} with further
 * similarity checking elements for {@link EObject} and EMFtext. <br>
 * <br>
 * The way similarity checking works remains mostly the same as in
 * {@link org.splevo.jamopp.diffing.similarity.base}. Here, the similarity
 * checking logic mostly resides in {@link org.eclipse.emf.ecore.util.Switch}
 * implementations, which are nested in a
 * {@link org.eclipse.emf.ecore.util.ComposedSwitch}. The
 * {@link ISimilarityToolbox} instead provides the means to compute certain
 * operations during similarity checking. The use of {@link ISimilarityToolbox}
 * (as well as {@link ISimilarityRequest} and {@link ISimilarityRequestHandler})
 * makes extracting parameters regarding normalisation possible, for instance.
 * <br>
 * <br>
 * {@link AbstractComposedSwitchSimilarityChecker} and
 * {@link AbstractComposedSimilaritySwitchComparer} extend their correspondent
 * from the {@link org.splevo.jamopp.diffing.similarity.base} package. <br>
 * <br>
 * {@link IComposedSwitchAdapter} and {@link AbstractComposedSwitchAdapter} can
 * be used to make the switches similar to the {@link ISimilarityChecker} in
 * terms of how similarity checking methods are called. Without them,
 * {@link ISimilarityChecker} (as well as the elements it uses) would have to be
 * replicated for each {@link EObject} nested within the {@link EObject}
 * instances, whose similarity is being checked, due to the similarity checking
 * process here being hierarchical. <br>
 * <br>
 * {@link IInnerSwitch}, along with {@link AbstractComposedSimilaritySwitch},
 * helps integrate {@link ISimilarityRequestHandler} into switches, which allows
 * them to delegate certain similarity checking operations to
 * {@link ISimilarityRequestHandler} (such as an {@link ISimilarityToolbox}).
 * Additionally, it provides the switches with some default methods to spare
 * them from declaring the same methods. {@link IPositionInnerSwitch} extends
 * {@link IInnerSwitch} with further methods that can be used by switches, which
 * may need to compare nested {@link EObject} instances and/or account for the
 * positions of statements (in form of {@link EObject} instances). <br>
 * <br>
 * The concrete {@link ISimilarityRequest} implementations in this package can
 * be used by {@link AbstractComposedSwitchSimilarityChecker} to translate and
 * delegate incoming similarity checking method calls. The
 * {@link ISimilarityRequestHandler} implementations in this package can be used
 * in the underlying {@link ISimilarityToolbox} instance to handle the said
 * requests.
 */
package org.splevo.jamopp.diffing.similarity.base.ecore;