package tools.cipm.seff

import org.emftext.language.java.members.Method
import org.palladiosimulator.pcm.repository.BasicComponent
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding
import tools.vitruv.change.interaction.UserInteractor
import tools.vitruv.change.propagation.impl.AbstractChangePropagationSpecification
import tools.vitruv.change.propagation.ResourceAccess
import tools.vitruv.change.atomic.EChange
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute
import org.emftext.language.java.commons.CommonsPackage
import tools.vitruv.change.composite.MetamodelDescriptor
import org.emftext.language.java.JavaPackage
import org.palladiosimulator.pcm.PcmPackage
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView
import tools.vitruv.change.correspondence.Correspondence

/**
 * Change-propagation preprocessor that reconstructs a method's SEFF when the method
 * changes. It builds a {@link ClassMethodBodyChangedTransformation} from the given
 * {@link Code2SeffFactory} and runs it. Subclasses override {@link #createTransformation}
 * to supply a more specific transformation (see the extended and fine-grained variants).
 */
class Java2PcmMethodBodyChangePreprocessor extends AbstractChangePropagationSpecification {
	val Code2SeffFactory code2SeffFactory;
	
	new(Code2SeffFactory code2SeffFactory) {
		this(code2SeffFactory, MetamodelDescriptor.of(JavaPackage.eINSTANCE), MetamodelDescriptor.of(PcmPackage.eINSTANCE));
	}
	
	new(Code2SeffFactory code2SeffFactory, MetamodelDescriptor sourceDomain, MetamodelDescriptor targetDomain) {
		super(sourceDomain, targetDomain)
		this.code2SeffFactory = code2SeffFactory
	}

	override propagateChange(EChange change, EditableCorrespondenceModelView<Correspondence> correspondenceModel, ResourceAccess resourceAccess) {
		if (doesHandleChange(change, correspondenceModel)) {
			val attrChange = change as ReplaceSingleValuedEAttribute<?, ?>;
			val meth = attrChange.affectedEObject as Method;
			executeClassMethodBodyChangeRefiner(correspondenceModel, userInteractor, meth);
		}
	}

	override doesHandleChange(EChange change, EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
		if (!(change instanceof ReplaceSingleValuedEAttribute)) {
			return false;
		}
		val attrChange = change as ReplaceSingleValuedEAttribute<?, ?>;
		return attrChange.affectedEObject instanceof Method
			&& attrChange.affectedFeature == CommonsPackage.Literals.NAMED_ELEMENT__NAME
			&& !attrChange.newValue.equals("")
	}

	private def void executeClassMethodBodyChangeRefiner(EditableCorrespondenceModelView<Correspondence> correspondenceModel,
		UserInteractor userInteractor, Method newMethod) {
		val basicComponentFinding = code2SeffFactory.createBasicComponentFinding
		val BasicComponent basicComponent = basicComponentFinding.findBasicComponentForMethod(newMethod,
			correspondenceModel);
		val classification = code2SeffFactory.createAbstractFunctionClassificationStrategy(correspondenceModel,
			basicComponentFinding, basicComponent);
		val InterfaceOfExternalCallFindingFactory interfaceOfExternalCallFindingFactory = code2SeffFactory.
			createInterfaceOfExternalCallFindingFactory(correspondenceModel, basicComponent);
		val ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding =
			code2SeffFactory.createResourceDemandingBehaviourForClassMethodFinding(correspondenceModel);
		val ClassMethodBodyChangedTransformation methodBodyChanged = createTransformation(
			newMethod, basicComponentFinding, classification, interfaceOfExternalCallFindingFactory,
			resourceDemandingBehaviourForClassMethodFinding);
		methodBodyChanged.execute(correspondenceModel, userInteractor);
	}
	
	protected def ClassMethodBodyChangedTransformation createTransformation(Method newMethod,
		BasicComponentFinding basicComponentFinding, AbstractFunctionClassificationStrategy classification,
		InterfaceOfExternalCallFindingFactory interfaceOfExternalCallFindingFactory,
		ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding) {
		return new ClassMethodBodyChangedTransformation(newMethod, basicComponentFinding, classification,
			interfaceOfExternalCallFindingFactory, resourceDemandingBehaviourForClassMethodFinding)
	}
}
