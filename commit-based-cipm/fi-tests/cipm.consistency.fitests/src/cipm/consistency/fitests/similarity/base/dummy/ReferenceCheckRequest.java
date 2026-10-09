package cipm.consistency.fitests.similarity.base.dummy;

/**
 * An exemplary custom request for comparing 2 objects using reference checking
 * ({@code ==}).
 * 
 * @author Alp Torac Genc
 */
public class ReferenceCheckRequest extends DummySingleSimilarityRequest {
	public ReferenceCheckRequest(Object elem1, Object elem2) {
		super(elem1, elem2);
	}
}
