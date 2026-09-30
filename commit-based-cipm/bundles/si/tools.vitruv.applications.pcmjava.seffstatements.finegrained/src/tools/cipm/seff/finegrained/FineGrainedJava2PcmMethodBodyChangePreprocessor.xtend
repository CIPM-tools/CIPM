package tools.cipm.seff.finegrained;

import org.emftext.language.java.members.Method
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy
import tools.cipm.seff.ClassMethodBodyChangedTransformation
import tools.cipm.seff.BasicComponentFinding
import tools.cipm.seff.extended.ExtendedJava2PcmMethodBodyChangePreprocessor

/**
 * Java2PcmMethodBodyChangePreprocessor variant that builds a
 * {@link FineGrainedClassMethodBodyChangedTransformation}, which diffs the new SEFF
 * against the existing one and merges only the changes, so unchanged SEFF elements keep
 * their identity and correspondences.
 */
class FineGrainedJava2PcmMethodBodyChangePreprocessor extends ExtendedJava2PcmMethodBodyChangePreprocessor {
	protected override ClassMethodBodyChangedTransformation createTransformation(Method newMethod,
		BasicComponentFinding basicComponentFinding, AbstractFunctionClassificationStrategy classification,
		InterfaceOfExternalCallFindingFactory interfaceOfExternalCallFindingFactory,
		ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding) {
		return new FineGrainedClassMethodBodyChangedTransformation(newMethod, basicComponentFinding,
			classification, interfaceOfExternalCallFindingFactory, resourceDemandingBehaviourForClassMethodFinding)
	}
}
