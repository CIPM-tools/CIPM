package org.splevo.jamopp.diffing.similarity.base;

/**
 * An interface implemented by classes that help build a
 * {@link ISimilarityToolbox} dynamically. The implementors use the builder
 * pattern and may include further building methods. <br>
 * <br>
 * To build a {@link ISimilarityToolbox}:
 * <ol>
 * <li>Set the {@link ISimilarityToolboxFactory} to specify the internal data
 * structure that will be used in the end product to store request-handler pairs
 * using {@link #setSimilarityToolboxFactory(ISimilarityToolboxFactory)}.
 * <li>Instantiate a {@link ISimilarityToolbox} within the
 * {@link ISimilarityToolboxBuilder} using {@link #instantiate()}.
 * <li>Use the provided building methods, such as
 * {@link #buildRequestHandlerPair(Class, ISimilarityRequestHandler)}.
 * <li>Return the end product by calling {@link #build()}.
 * </ol>
 * Concrete implementors may provide additional building methods, such as those
 * that summarise multiple
 * {@link #buildRequestHandlerPair(Class, ISimilarityRequestHandler)} calls into
 * one method. In such cases, it is recommended to use those methods instead, in
 * order to keep the building process tidier.
 * 
 * @author atora
 */
public interface ISimilarityToolboxBuilder {
	/**
	 * Sets the {@link ISimilarityToolboxFactory}, which specifies the internal data
	 * structure that will be used in the end product to store request-handler
	 * pairs. Currently used {@link ISimilarityToolboxFactory} can be changed by
	 * using this method as well. <br>
	 * <br>
	 * <b>Note that changing the {@link ISimilarityToolboxFactory} after calling
	 * {@link #instantiate()} has no effect on the {@link ISimilarityToolbox} that
	 * is currently being built. For this change to take effect,
	 * {@link #instantiate()} can be called to start the building process anew with
	 * the new {@link ISimilarityToolboxFactory}. </b>
	 * 
	 * @param stf The new {@link ISimilarityToolboxFactory} that this builder will
	 *            use
	 */
	public void setSimilarityToolboxFactory(ISimilarityToolboxFactory stf);

	/**
	 * @return Currently used {@link ISimilarityToolboxFactory}.
	 */
	public ISimilarityToolboxFactory getToolboxFactory();

	/**
	 * Returns the current version of the {@link ISimilarityToolbox} being built.
	 * <br>
	 * <br>
	 * <b>Depending on the concrete implementation, calling this method may set the
	 * current version of the {@link ISimilarityToolbox} to null after
	 * returning.</b>
	 * 
	 * @return The resulting {@link ISimilarityToolbox}.
	 */
	public ISimilarityToolbox build();

	/**
	 * Creates a {@link ISimilarityToolbox} using the current
	 * {@link ISimilarityToolboxFactory} within this builder. Can also be used to
	 * reset the current version of the {@link ISimilarityToolbox}.
	 * 
	 * @return this
	 */
	public ISimilarityToolboxBuilder instantiate();

	/**
	 * Adds the given request-handler pair (reqClass, srh) to the
	 * {@link ISimilarityToolbox}.
	 * 
	 * @param reqClass The class of the {@link ISimilarityRequest} instances, which
	 *                 should be processed using the given
	 *                 {@link ISimilarityRequestHandler} instance.
	 * @param srh      The {@link ISimilarityRequestHandler} instance, which should
	 *                 be used to process the {@link ISimilarityRequest} instances
	 *                 of the given class.
	 * 
	 * @return this
	 */
	public ISimilarityToolboxBuilder buildRequestHandlerPair(Class<? extends ISimilarityRequest> reqClass,
			ISimilarityRequestHandler srh);
}