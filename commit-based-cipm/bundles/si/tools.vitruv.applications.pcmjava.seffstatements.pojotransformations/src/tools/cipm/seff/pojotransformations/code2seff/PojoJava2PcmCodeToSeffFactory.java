package tools.cipm.seff.pojotransformations.code2seff;

import org.palladiosimulator.pcm.repository.BasicComponent;
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy;
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFinding;
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory;
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding;
import org.somox.sourcecodedecorator.SourceCodeDecoratorRepository;

import tools.cipm.seff.BasicComponentFinding;
import tools.cipm.seff.Code2SeffFactory;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

public class PojoJava2PcmCodeToSeffFactory implements Code2SeffFactory {

	@Override
	public BasicComponentFinding createBasicComponentFinding() {
		return new BasicComponentForPackageMappingFinder();
	}

	@Override
	public InterfaceOfExternalCallFindingFactory createInterfaceOfExternalCallFindingFactory(
			final EditableCorrespondenceModelView<Correspondence> correspondenceModel, final BasicComponent basicComponent) {
		return new InterfaceOfExternalCallFindingFactory() {
			public InterfaceOfExternalCallFinding createInterfaceOfExternalCallFinding(
					SourceCodeDecoratorRepository sourceCodeDecoratorRepository,
					BasicComponent basicComponent) {
				return new InterfaceOfExternalCallFinderForPackageMapping(correspondenceModel, basicComponent);
			}
		};
	}

	@Override
	public ResourceDemandingBehaviourForClassMethodFinding createResourceDemandingBehaviourForClassMethodFinding(
			final EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
		return new ResourceDemandingBehaviourForClassMethodFinderForPackageMapping(correspondenceModel);
	}

	@Override
	public AbstractFunctionClassificationStrategy createAbstractFunctionClassificationStrategy(
			final BasicComponentFinding basicComponentFinding, final EditableCorrespondenceModelView<Correspondence> correspondenceModel,
			final BasicComponent basicComponent) {
		return new FunctionClassificationStrategyForPackageMapping(basicComponentFinding, correspondenceModel,
				basicComponent);
	}

}
