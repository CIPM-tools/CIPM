package cipm.consistency.fitests.repositorytests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.URI;

import cipm.consistency.fitests.similarity.jamopp.parser.AbstractJaMoPPParserSimilarityTestFactory;
import cipm.consistency.fitests.similarity.jamopp.parser.EAllContentSimilarityTestFactory;

/**
 * Contains repository parser tests for the "cwa-server" repository
 * 
 * @author Alp Torac Genc
 */
public class CWARepoTest extends AbstractJaMoPPParserRepoTest {
	/**
	 * The list of commits that will be parsed and compared to one another. All of
	 * them are pairwise different, i.e. all of them introduce code changes that
	 * break similarity (assuming test code is included).
	 */
	private static final List<String> commitIDs = List.of("7e1b610aa3334afb770eebef79ba60120e2169bc", // Version 2.25.0
			"6e97024a789fa4b68dd3a779ae81dadb3a67ab57", // Version 2.26.0
			"c22f9321075eb1a6754afe1917e149380243c835", // Version 2.27.0
			"33d1c90d58ddc25d9b596547acdf8246b51c4287", // Version 2.27.1
			"9323b87169bd54e5fb0015dcc7791d2fb70aa786", // Version 2.28.0
			"206e8c3b5aa25a99694bd157e0856b7d218ac65d", // Version 3.0.0
			"3977e6b06f72aa9585dee36025df9387eb5e9a7e", // Version 3.1.0
			"94bca6a6cf595ba0c9116d7fe1318fdc495a719f" // Version 3.2.0
	);

	@Override
	protected List<String> getCommitIDs() {
		return commitIDs;
	}

	@Override
	protected URI getRepoURI() {
		return URI.createURI("https://github.com/corona-warn-app/cwa-server");
	}

	@Override
	protected Collection<AbstractJaMoPPParserSimilarityTestFactory> getTestFactories() {
		var res = new ArrayList<AbstractJaMoPPParserSimilarityTestFactory>();
		res.add(new EAllContentSimilarityTestFactory(this.getSCC()));
		res.forEach((tf) -> tf.setExpectedSimilarityResultProvider(getExpectedSimilarityResultProviderForCommits()));
		return res;
	}
}
