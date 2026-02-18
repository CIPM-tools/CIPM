package tools.cipm.seff;

import com.google.common.base.Objects;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.JavaPackage;
import org.emftext.language.java.commons.CommonsPackage;
import org.emftext.language.java.members.Method;
import org.palladiosimulator.pcm.PcmPackage;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy;
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory;
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.interaction.UserInteractor;
import tools.vitruv.change.propagation.ResourceAccess;
import tools.vitruv.change.propagation.impl.AbstractChangePropagationSpecification;

public class Java2PcmMethodBodyChangePreprocessor extends AbstractChangePropagationSpecification {
  private final Code2SeffFactory code2SeffFactory;

  public Java2PcmMethodBodyChangePreprocessor(final Code2SeffFactory code2SEFFfactory) {
    this(code2SEFFfactory, MetamodelDescriptor.of(JavaPackage.eINSTANCE), MetamodelDescriptor.of(PcmPackage.eINSTANCE));
  }

  public Java2PcmMethodBodyChangePreprocessor(final Code2SeffFactory code2SEFFfactory, final MetamodelDescriptor sourceDomain, final MetamodelDescriptor targetDomain) {
    super(sourceDomain, targetDomain);
    this.code2SeffFactory = code2SEFFfactory;
  }

  @Override
  public void propagateChange(final EChange change, final EditableCorrespondenceModelView<Correspondence> correspondenceModel, final ResourceAccess resourceAccess) {
    if (this.doesHandleChange(change, correspondenceModel)) {
      final ReplaceSingleValuedEAttribute<?, ?> attrChange = ((ReplaceSingleValuedEAttribute<?, ?>) change);
      EObject _affectedEObject = attrChange.getAffectedEObject();
      final Method meth = ((Method) _affectedEObject);
      this.executeClassMethodBodyChangeRefiner(correspondenceModel, this.getUserInteractor(), meth);
    }
  }

  @Override
  public boolean doesHandleChange(final EChange change, final EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
    if ((!(change instanceof ReplaceSingleValuedEAttribute))) {
      return false;
    }
    final ReplaceSingleValuedEAttribute<?, ?> attrChange = ((ReplaceSingleValuedEAttribute<?, ?>) change);
    return (((attrChange.getAffectedEObject() instanceof Method) && Objects.equal(attrChange.getAffectedFeature(), CommonsPackage.Literals.NAMED_ELEMENT__NAME)) && (!attrChange.getNewValue().equals("")));
  }

  private void executeClassMethodBodyChangeRefiner(final EditableCorrespondenceModelView<Correspondence> correspondenceModel, final UserInteractor userInteracting, final Method newMethod) {
    final BasicComponentFinding basicComponentFinding = this.code2SeffFactory.createBasicComponentFinding();
    final BasicComponent myBasicComponent = basicComponentFinding.findBasicComponentForMethod(newMethod, correspondenceModel);
    final AbstractFunctionClassificationStrategy classification = this.code2SeffFactory.createAbstractFunctionClassificationStrategy(basicComponentFinding, correspondenceModel, myBasicComponent);
    final InterfaceOfExternalCallFindingFactory interfaceOfExternalCallFinderFactory = this.code2SeffFactory.createInterfaceOfExternalCallFindingFactory(correspondenceModel, myBasicComponent);
    final ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding = this.code2SeffFactory.createResourceDemandingBehaviourForClassMethodFinding(correspondenceModel);
    final ClassMethodBodyChangedTransformation methodBodyChanged = this.createTransformation(newMethod, basicComponentFinding, classification, interfaceOfExternalCallFinderFactory, resourceDemandingBehaviourForClassMethodFinding);
    methodBodyChanged.execute(correspondenceModel, userInteracting);
  }

  protected ClassMethodBodyChangedTransformation createTransformation(final Method newMethod, final BasicComponentFinding basicComponentFinding, final AbstractFunctionClassificationStrategy classification, final InterfaceOfExternalCallFindingFactory interfaceOfExternalCallFinderFactory, final ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding) {
    return new ClassMethodBodyChangedTransformation(newMethod, basicComponentFinding, classification, interfaceOfExternalCallFinderFactory, resourceDemandingBehaviourForClassMethodFinding);
  }
}
