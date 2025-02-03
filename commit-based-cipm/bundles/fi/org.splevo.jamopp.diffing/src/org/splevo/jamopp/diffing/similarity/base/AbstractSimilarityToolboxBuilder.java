package org.splevo.jamopp.diffing.similarity.base;

/**
 * An abstract class for concrete similarity {@link ISimilarityToolbox} builders
 * to extend. Complements {@link ISimilarityToolboxBuilder} with the integration
 * of {@link ISimilarityToolboxFactory}.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractSimilarityToolboxBuilder implements ISimilarityToolboxBuilder {
	/**
	 * The {@link ISimilarityToolboxFactory}, which determines the data structure
	 * used to store request-handler pairs within the {@link ISimilarityToolbox}
	 * instances built using this builder.
	 */
	private ISimilarityToolboxFactory stf;

	/**
	 * The current version of the {@link ISimilarityToolbox} instance being built.
	 */
	private ISimilarityToolbox st;

	@Override
	public void setSimilarityToolboxFactory(ISimilarityToolboxFactory stf) {
		this.stf = stf;
	}

	/**
	 * @return The {@link ISimilarityToolboxFactory}, which determines the data
	 *         structure used to store request-handler pairs within the
	 *         {@link ISimilarityToolbox} instances built using this builder.
	 */
	@Override
	public ISimilarityToolboxFactory getToolboxFactory() {
		return this.stf;
	}

	/**
	 * Meant to allow concrete implementors the access to the current version of the
	 * {@link ISimilarityToolbox} under construction.
	 * 
	 * @return The current version of the {@link ISimilarityToolbox} instance being
	 *         built.
	 */
	protected ISimilarityToolbox getCurrentToolbox() {
		return this.st;
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * <b>Resets the {@link ISimilarityToolbox} instance currently being built to
	 * null. </b>
	 */
	@Override
	public ISimilarityToolbox build() {
		var result = this.getCurrentToolbox();
		this.st = null;
		return result;
	}

	@Override
	public ISimilarityToolboxBuilder instantiate() {
		this.st = this.getToolboxFactory().createSimilarityToolbox();
		return this;
	}

	@Override
	public ISimilarityToolboxBuilder buildRequestHandlerPair(Class<? extends ISimilarityRequest> req,
			ISimilarityRequestHandler srh) {
		this.getCurrentToolbox().addRequestHandlerPair(req, srh);
		return this;
	}

}