package cipm.consistency.fitests.similarity.base.dummy;

/**
 * An exemplary custom request for comparing 2 objects using
 * {@code .equals(...)} method.
 * 
 * @author Alp Torac Genc
 */
public class EqualsCheckRequest extends DummySingleSimilarityRequest {
	public EqualsCheckRequest(Object elem1, Object elem2) {
		super(elem1, elem2);
	}
}
