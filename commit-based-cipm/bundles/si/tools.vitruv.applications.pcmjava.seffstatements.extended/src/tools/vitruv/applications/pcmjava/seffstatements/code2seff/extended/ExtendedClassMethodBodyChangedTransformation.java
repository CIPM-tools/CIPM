package tools.vitruv.applications.pcmjava.seffstatements.code2seff.extended;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.statements.Statement;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingInternalBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.somox.gast2seff.visitors.IFunctionClassificationStrategy;
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory;
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding;
import org.somox.sourcecodedecorator.SeffElementSourceCodeLink;
import org.somox.sourcecodedecorator.SourceCodeDecoratorRepository;

import com.google.common.collect.Lists;

import tools.vitruv.applications.pcmjava.seffstatements.code2seff.BasicComponentFinding;
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.ClassMethodBodyChangedTransformation;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.userinteraction.UserInteractor;

/**
 * Class that keeps changes within a class method body consistent with the
 * architecture.
 *
 * @author Noureddine Dahmane
 * @author Martin Armbruster
 */
public class ExtendedClassMethodBodyChangedTransformation extends ClassMethodBodyChangedTransformation {
	private boolean shouldGenerateInternalCallActions;
	
	public ExtendedClassMethodBodyChangedTransformation(final Method newMethod,
			final BasicComponentFinding basicComponentFinder,
			final IFunctionClassificationStrategy iFunctionClassificationStrategy,
			final InterfaceOfExternalCallFindingFactory interfaceOfExternalCallFindingFactory,
			final ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding) {
		this(newMethod, basicComponentFinder, iFunctionClassificationStrategy, interfaceOfExternalCallFindingFactory,
				resourceDemandingBehaviourForClassMethodFinding, true);
	}
	
	public ExtendedClassMethodBodyChangedTransformation(final Method newMethod,
			final BasicComponentFinding basicComponentFinder,
			final IFunctionClassificationStrategy iFunctionClassificationStrategy,
			final InterfaceOfExternalCallFindingFactory interfaceOfExternalCallFindingFactory,
			final ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding,
			final boolean shouldGenerateInternalCallActions) {
		super(newMethod, basicComponentFinder, iFunctionClassificationStrategy, interfaceOfExternalCallFindingFactory,
				resourceDemandingBehaviourForClassMethodFinding);
		this.shouldGenerateInternalCallActions = shouldGenerateInternalCallActions;
	}
	
	@Override
	protected boolean generateInternalCallActions() {
		return this.shouldGenerateInternalCallActions;
	}

	/**
	 * Extends the process to keep Java method bodies consistent with SEFFs by introducing the following step:
	 * 5) Link the abstract actions with the corresponding statements which are needed
	 * during the instrumentation process.
	 */
	@Override
	public void execute(final CorrespondenceModel correspondenceModel,
			final UserInteractor userInteracting) {
		super.execute(correspondenceModel, userInteracting);
		var decorator = super.getSourceCodeDecoratorRepository();
		if (decorator != null) {
			// 5) Link the abstract actions with their corresponding statements.
			this.bindAbstractActionsAndStatements(decorator, correspondenceModel);
		}
	}
	
	private void bindAbstractActionsAndStatements(SourceCodeDecoratorRepository sourceCodeDecorator,
			CorrespondenceModel correspondenceModel) {
		List<SeffElementSourceCodeLink> seffElementSourceCodeLinks =
				sourceCodeDecorator.getSeffElementsSourceCodeLinks();

        for (SeffElementSourceCodeLink seffElementSourceCodeLink : seffElementSourceCodeLinks) {
            if (seffElementSourceCodeLink.getSeffElement() instanceof AbstractAction) {
            	AbstractAction ab = (AbstractAction) seffElementSourceCodeLink.getSeffElement();
            	// Actions in ResourceDemandingInternalBehaviours are ignored.
            	if (this.isContainedInResourceDemandingInternalBehaviour(ab)) {
            		continue;
            	}
            	List<EObject> actionList = Lists.newArrayList(ab);
	            for (Statement statement : seffElementSourceCodeLink.getStatement()) {
                    correspondenceModel.createAndAddCorrespondence(actionList, Lists.newArrayList(statement));
	            }
            }
        }
	}
	
	/**
	 * Checks if an action is contained within a ResourceDemandingInternalBehaviour.
	 * 
	 * @param action the action to check.
	 * @return true if the action is contained within a ResourceDemandingInternalBehaviour. false otherwise.
	 */
	private boolean isContainedInResourceDemandingInternalBehaviour(AbstractAction action) {
		EObject obj = action.eContainer();
		while (obj != null) {
			if (obj instanceof ResourceDemandingInternalBehaviour) {
				return true;
			} else if (obj instanceof ResourceDemandingSEFF) {
				return false;
			}
			obj = obj.eContainer();
		}
		return false;
	}
}
