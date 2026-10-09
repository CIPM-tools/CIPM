package cipm.consistency.fitests.similarity.base.dummy;

import org.splevo.jamopp.diffing.similarity.base.ISimilarityRequest;

/**
 * An exemplary custom request for similarity checking 2 objects. <br>
 * <br>
 * Note 1: It is possible to re-use requests via inheritance. However, it is
 * possible only if doing so does not alter the behaviour of the toolbox, which
 * will contain the corresponding request-handler pair. <br>
 * <br>
 * Note 2: Requests are meant to be taken care of by handlers. Therefore, they
 * should not contain their own handling logic. They should only contain
 * information.
 * 
 * @author Alp Torac Genc
 */
public class DummySingleSimilarityRequest implements ISimilarityRequest {
	private Object elem1;
	private Object elem2;

	public DummySingleSimilarityRequest(Object elem1, Object elem2) {
		this.elem1 = elem1;
		this.elem2 = elem2;
	}

	@Override
	public Object getParams() {
		return new Object[] { elem1, elem2 };
	}
}
