package cipm.consistency.cpr.javaim

import org.emftext.language.java.members.Method
import tools.vitruv.framework.correspondence.CorrespondenceModel
import tools.vitruv.framework.userinteraction.UserInteractor
import tools.vitruv.framework.change.echange.EChange
import tools.vitruv.framework.propagation.ResourceAccess
import tools.vitruv.framework.correspondence.CorrespondenceModelUtil
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelPackage
import tools.vitruv.framework.change.echange.feature.attribute.ReplaceSingleValuedEAttribute
import cipm.consistency.models.instrumentation.InstrumentationModelUtil
import tools.cipm.seff.extended.ExtendedJava2PcmMethodBodyChangePreprocessor

/**
 * Propagates changes in method bodies to the extended instrumentation model.
 * 
 * @author Noureddine Dahmane
 * @author Martin Armbruster
 */
class Java2ImChangePropagationSpecification extends ExtendedJava2PcmMethodBodyChangePreprocessor {
// The change propagation between Java->PCM and Java->IM should be separated. However, there is a temporal constraint
// for Java->IM: the changes can only be propagated after the SEFF has been reconstructed because, otherwise, there are
// no actions for which instrumentation points can be generated. As a result, to ensure that the temporal constraint is
// met, the propagation rules for Java->IM extend the rules for Java->PCM and are executed after them.
	
	override propagateChange(EChange change, CorrespondenceModel correspondenceModel, ResourceAccess resourceAccess) {
		super.propagateChange(change, correspondenceModel, resourceAccess)
		if (super.doesHandleChange(change, correspondenceModel)) {
			val attrChange = change as ReplaceSingleValuedEAttribute<?, ?>;
			val meth = attrChange.affectedEObject as Method;
			executeJava2ImTransformation(correspondenceModel, userInteractor, meth)
		}
	}
	
	private def executeJava2ImTransformation(CorrespondenceModel correspondenceModel,
		UserInteractor userInteracting, Method newMethod) {
		
		val correspondingSEFFs = CorrespondenceModelUtil.getCorrespondingEObjects(
			correspondenceModel, newMethod, ResourceDemandingSEFF)
		val im = CorrespondenceModelUtil.getCorrespondingEObjects(correspondenceModel,
			InstrumentationModelPackage.Literals.INSTRUMENTATION_MODEL, InstrumentationModel).last
		
		if (!correspondingSEFFs.empty) {
			val seff = correspondingSEFFs.last
			var sip = im.points.filter[it.service == seff].last
			if (sip !== null) {
				sip.actionInstrumentationPoints.clear
				InstrumentationModelUtil.recursiveBuildImm(seff, sip)
			} else {
				sip = InstrumentationModelUtil.recursiveBuildImm(seff)
				im.points.add(sip)
			}
		}
	}
}
