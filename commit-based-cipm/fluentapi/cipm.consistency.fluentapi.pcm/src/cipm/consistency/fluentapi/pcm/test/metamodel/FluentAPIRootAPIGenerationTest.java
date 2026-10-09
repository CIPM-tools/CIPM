package cipm.consistency.fluentapi.pcm.test.metamodel;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import cipm.consistency.fluentapi.test.metamodel.AbstractFluentAPIRootAPIGenerationTest;

/**
 * Implementation of {@link AbstractFluentAPIRootAPIGenerationTest} for PCM.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIRootAPIGenerationTest extends AbstractFluentAPIRootAPIGenerationTest
		implements IFluentPcmAPIMetamodelTest {
	@Disabled("There are no elements with only one modifiable feature in PCM metamodel")
	@Override
	@Test
	public void methodTest_API_NewX_WithParameter_SingleModifiableFeature() {
	}
}
