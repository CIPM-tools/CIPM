package cipm.consistency.fitests.repositorytests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.URI;

import cipm.consistency.fitests.similarity.jamopp.parser.AbstractJaMoPPParserSimilarityTestFactory;
import cipm.consistency.fitests.similarity.jamopp.parser.EAllContentSimilarityTestFactory;

/**
 * Contains repository parser tests for the "Teammates" repository
 * 
 * @author Alp Torac Genc
 */
public class TeammatesRepoTest extends AbstractJaMoPPParserRepoTest {
	private static final List<String> commitIDs = List.of("648425746bb9434051647c8266dfab50a8f2d6a3",
			"48b67bae03babf5a5e578aefce47f0285e8de8b4");

	@Override
	protected List<String> getCommitIDs() {
		return commitIDs;
	}

	@Override
	protected URI getRepoURI() {
		return URI.createURI("https://github.com/TEAMMATES/teammates");
	}

	@Override
	protected Collection<AbstractJaMoPPParserSimilarityTestFactory> getTestFactories() {
		var res = new ArrayList<AbstractJaMoPPParserSimilarityTestFactory>();
		res.add(new EAllContentSimilarityTestFactory(this.getSCC()));
		res.forEach((tf) -> tf.setExpectedSimilarityResultProvider(getExpectedSimilarityResultProviderForCommits()));
		return res;
	}
}
