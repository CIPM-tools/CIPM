package tools.cipm.seff.pojotransformations;

import tools.cipm.seff.Java2PcmMethodBodyChangePreprocessor
import tools.cipm.seff.pojotransformations.code2seff.PojoJava2PcmCodeToSeffFactory
import tools.vitruv.framework.userinteraction.UserInteractor

class Java2PcmPackageMappingMethodBodyChangePreprocessor extends Java2PcmMethodBodyChangePreprocessor {
	new(UserInteractor userInteracting) {
		super(new PojoJava2PcmCodeToSeffFactory());
	}
}
