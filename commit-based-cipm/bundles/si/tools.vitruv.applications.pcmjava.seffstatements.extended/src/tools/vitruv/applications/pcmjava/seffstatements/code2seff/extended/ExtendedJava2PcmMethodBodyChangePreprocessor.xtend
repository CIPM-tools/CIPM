package tools.vitruv.applications.pcmjava.seffstatements.code2seff.extended

import org.emftext.language.java.members.Method
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.Java2PcmMethodBodyChangePreprocessor
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.ClassMethodBodyChangedTransformation
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.BasicComponentFinding
import tools.vitruv.domains.provider.java.AdjustedJavaDomainProvider
import tools.vitruv.domains.provider.pcm.ExtendedPcmDomainProvider
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.Code2SeffFactory

class ExtendedJava2PcmMethodBodyChangePreprocessor extends Java2PcmMethodBodyChangePreprocessor {
	boolean shouldGenerateInternalCallActions;

	new() {
		this(new CommitIntegrationCodeToSeffFactory)
	}
	
	new(Code2SeffFactory factory) {
		this(factory, true)		
	}
	
	new(Code2SeffFactory factory, boolean generateInternalCallActions) {
		super(factory,
			new AdjustedJavaDomainProvider().domain, new ExtendedPcmDomainProvider().domain)
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
