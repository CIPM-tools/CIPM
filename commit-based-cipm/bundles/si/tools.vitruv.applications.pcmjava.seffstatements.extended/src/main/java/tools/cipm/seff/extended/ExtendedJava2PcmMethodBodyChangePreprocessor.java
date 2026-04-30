package tools.cipm.seff.extended;

import org.emftext.language.java.members.Method;
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy;
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory;
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding;
import tools.cipm.seff.BasicComponentFinding;
import tools.cipm.seff.ClassMethodBodyChangedTransformation;
import tools.cipm.seff.Code2SeffFactory;
import tools.cipm.seff.Java2PcmMethodBodyChangePreprocessor;

public class ExtendedJava2PcmMethodBodyChangePreprocessor extends Java2PcmMethodBodyChangePreprocessor {
  private boolean shouldGenerateInternalCallActions;

  public ExtendedJava2PcmMethodBodyChangePreprocessor() {
    this(new CommitIntegrationCodeToSeffFactory());
  }

  public ExtendedJava2PcmMethodBodyChangePreprocessor(final Code2SeffFactory factory) {
    this(factory, true);
  }

  public ExtendedJava2PcmMethodBodyChangePreprocessor(final Code2SeffFactory factory, final boolean generateInternalCallActions) {
    super(factory);
    this.shouldGenerateInternalCallActions = generateInternalCallActions;
  }

  protected ClassMethodBodyChangedTransformation createTransformation(final Method newMethod, final BasicComponentFinding basicComponentFinding, final AbstractFunctionClassificationStrategy classification, final InterfaceOfExternalCallFindingFactory interfaceOfExternalCallFinderFactory, final ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding) {
    return new ExtendedClassMethodBodyChangedTransformation(newMethod, basicComponentFinding, classification, interfaceOfExternalCallFinderFactory, resourceDemandingBehaviourForClassMethodFinding, 
      this.shouldGenerateInternalCallActions);
  }
}
