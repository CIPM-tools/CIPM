package cipm.consistency.vsum.test.pcm.cprunittests.dummy;

import java.util.List;

public final class PcmCPRDummyTestConstants {
	public static final String correspondenceTestPCMInterfaceName = "pcmIfc";

	public static final String userInteractionTestPCMInterfaceName = "userInteractionPcmIfc";
	public static final String userInteractionTestJavaInterfaceName = "userInteractionJavaIfc";

	public static final String componentContentDistributionTestDeletedComponentName = "toBeDeletedCMP";
	public static final String componentContentDistributionTestDeletedComponentClassOneName = "cls1";
	public static final String componentContentDistributionTestDeletedComponentClassTwoName = "cls2";
	public static final String componentContentDistributionTestPersistingComponentOneName = "cmp1";
	public static final String componentContentDistributionTestPersistingComponentTwoName = "cmp2";

	public static final String namespaceTestComponentName = "pcmNsCmp";
	public static final String namespaceTestComponentModuleName = "pcmNsCmpMod";
	public static final List<String> namespaceTestComponentModuleNamespaces = List.of("pcmNsCmpModNs1",
			"pcmNsCmpModNs2", "pcmNsCmpModNs3");
}
