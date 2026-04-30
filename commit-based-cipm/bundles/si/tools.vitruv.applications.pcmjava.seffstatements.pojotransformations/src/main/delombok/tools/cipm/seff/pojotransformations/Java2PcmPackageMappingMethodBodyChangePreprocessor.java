package tools.cipm.seff.pojotransformations;

import tools.cipm.seff.Java2PcmMethodBodyChangePreprocessor;
import tools.cipm.seff.pojotransformations.code2seff.PojoJava2PcmCodeToSeffFactory;
import tools.vitruv.change.interaction.UserInteractor;

public class Java2PcmPackageMappingMethodBodyChangePreprocessor extends Java2PcmMethodBodyChangePreprocessor {
  public Java2PcmPackageMappingMethodBodyChangePreprocessor(final UserInteractor userInteracting) {
    super(new PojoJava2PcmCodeToSeffFactory());
  }
}
