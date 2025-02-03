package org.splevo.jamopp.diffing.similarity.base;

/**
 * An interface for encapsulating parameters required by similarity checking
 * operations. Similarity requests are intended to be used by their matching
 * {@link ISimilarityRequestHandler}. <br>
 * <br>
 * It is recommended to not override the return type of {@link #getParams()},
 * especially if the concrete similarity requests are planned to be extended.
 * 
 * @see {@link ISimilarityToolbox}, {@link ISimilarityRequestHandler}
 * @author Alp Torac Genc
 */
public interface ISimilarityRequest {
	/**
	 * @return All parameters encapsulated by this instance. Unless specified
	 *         otherwise, the return type is an Object array of all parameters
	 *         passed to the constructor of the concrete implementation in the same
	 *         order. If the said constructor only takes one parameter, the return
	 *         type of this method is the type of that parameter in the constructor.
	 */
	public Object getParams();
}
