package tools.cipm.seff.extended;

import org.emftext.language.java.members.Method
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy
import tools.cipm.seff.Java2PcmMethodBodyChangePreprocessor
import tools.cipm.seff.ClassMethodBodyChangedTransformation
import tools.cipm.seff.BasicComponentFinding
import tools.cipm.seff.Code2SeffFactory

class ExtendedJava2PcmMethodBodyChangePreprocessor extends Java2PcmMethodBodyChangePreprocessor {
	boolean shouldGenerateInternalCallActions;

	new() {
		this(new CommitIntegrationCodeToSeffFactory)
	}
	
	new(Code2SeffFactory factory) {
		this(factory, true)		
	}
	
	new(Code2SeffFactory factory, boolean generateInternalCallActions) {
		super(factory)
		this.shouldGenerateInternalCallActions = generateInternalCallActions;		
	}

	protected override ClassMethodBodyChangedTransformation createTransformation(Method newMethod,
		BasicComponentFinding basicComponentFinding, AbstractFunctionClassificationStrategy classification,
		InterfaceOfExternalCallFindingFactory interfaceOfExternalCallFinderFactory,
		ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding) {
		return new ExtendedClassMethodBodyChangedTransformation(newMethod, basicComponentFinding,
			classification, interfaceOfExternalCallFinderFactory, resourceDemandingBehaviourForClassMethodFinding,
			this.shouldGenerateInternalCallActions);
	}
}
