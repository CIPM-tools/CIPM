package cipm.consistency.vsum.test.pcm.cprunittests.actual;

import java.util.List;

import cipm.consistency.vsum.test.pcm.cprunittests.AbstractPcmJavaCprTest;
import mir.reactions.allRepository.AllRepositoryChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

public class DataTypeTest extends AbstractPcmJavaCprTest {

	@Override
	protected List<ChangePropagationSpecification> getCPRs() {
		return List.of(new AllRepositoryChangePropagationSpecification());
	}

	public void dataTypeCreationTest
}
