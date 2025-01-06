package tools.cipm.seff;

import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.members.ClassMethod;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.statements.StatementListContainer;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.SeffFactory;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.seff.StopAction;
import org.somox.gast2seff.visitors.FunctionCallClassificationVisitor;
import org.somox.gast2seff.visitors.IFunctionClassificationStrategy;
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory;
import org.somox.gast2seff.visitors.MethodCallFinder;
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding;
import org.somox.gast2seff.visitors.VisitorUtils;
import org.somox.sourcecodedecorator.SourceCodeDecoratorRepository;
import org.somox.sourcecodedecorator.SourcecodedecoratorFactory;

import com.google.common.collect.Lists;

import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.correspondence.CorrespondenceModelUtil;
import tools.vitruv.framework.userinteraction.UserInteractor;

/**
 * Class that keeps changes within a class method body consistent with the
 * architecture. Has dependencies to SoMoX as well as to Vitruvius. This is the
 * reason that the class is in its own plugin. The class is written in Java and
 * not in Xtend (we do not need the cool features of Xtend within the class).
 *
 * @author langhamm
 *
 */
public class ClassMethodBodyChangedTransformation {

	private static final Logger LOGGER = Logger.getLogger(ClassMethodBodyChangedTransformation.class.getSimpleName());

	private final Method newMethod;
	private final BasicComponentFinding basicComponentFinder;
	private final IFunctionClassificationStrategy iFunctionClassificationStrategy;

	private final InterfaceOfExternalCallFindingFactory interfaceOfExternalCallFinderFactory;

	private final ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding;
	
	private SourceCodeDecoratorRepository sourceCodeDecorator;

	public ClassMethodBodyChangedTransformation(final Method newMethod,
			final BasicComponentFinding basicComponentFinder,
			final IFunctionClassificationStrategy iFunctionClassificationStrategy,
			final InterfaceOfExternalCallFindingFactory interfaceOfExternalCallFindingFactory,
			final ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding) {
		this.newMethod = newMethod;
		this.basicComponentFinder = basicComponentFinder;
		this.iFunctionClassificationStrategy = iFunctionClassificationStrategy;
		this.interfaceOfExternalCallFinderFactory = interfaceOfExternalCallFindingFactory;
		this.resourceDemandingBehaviourForClassMethodFinding = resourceDemandingBehaviourForClassMethodFinding;
	}
	
	protected boolean generateInternalCallActions() {
		return true;
	}

	/**
	 * This method is called after a java method body has been changed. In order
	 * to keep the SEFF/ResourceDemandingInternalBehaviour consistent with the
	 * method we:
	 * 1) remove all AbstractActions corresponding to the Method from
	 * the SEFF/ResourceDemandingInternalBehaviour (which are currently all
	 * AbstractActions in the SEFF/ResourceDemandingInternalBehaviour) and from
	 * the correspondenceModel,
	 * 2) run the SoMoX SEFF extractor for this method,
	 * 3) reconnect the newly extracted SEFF elements with the old elements,
	 * 4) create new AbstractAction 2 Method correspondences for the new method
	 * (and its inner methods).
	 * 
	 * @param correspondenceModel the current correspondence model.
	 * @param userInteracting the user interactor.
	 */
	public void execute(final CorrespondenceModel correspondenceModel,
			final UserInteractor userInteracting) {
		if (!this.isArchitectureRelevantChange(correspondenceModel)) {
			LOGGER.debug("Change within the method: " + this.newMethod
					+ " is not an architecture relevant change");
			return;
		}
		
		// 1)
		this.removeCorrespondingAbstractActions(correspondenceModel);

		// 2)
		final ResourceDemandingBehaviour resourceDemandingBehaviour = this
				.findRdBehaviorToInsertElements(correspondenceModel);
		final BasicComponent basicComponent = this.basicComponentFinder.findBasicComponentForMethod(this.newMethod,
				correspondenceModel);
		this.executeSoMoXForMethod(basicComponent, resourceDemandingBehaviour);

		// 3)
		this.connectCreatedResourceDemandingBehaviour(resourceDemandingBehaviour, correspondenceModel);

		// 4)
		this.createNewCorrespondences(correspondenceModel, resourceDemandingBehaviour);
	}

	/**
	 * Checks whether the change is considered architecture relevant. This is
	 * the case if either the new or the old method does have a corresponding
	 * ResourceDemandingBehaviour.
	 *
	 * @param ci the current correspondence model.
	 * @return true if the method is architecture relevant. false otherwise.
	 */
	protected boolean isArchitectureRelevantChange(final CorrespondenceModel ci) {
		return this.isMethodArchitectureRelevant(this.newMethod, ci);
	}

	private boolean isMethodArchitectureRelevant(final Method method, final CorrespondenceModel ci) {
		if (null != method) {
			final Set<ResourceDemandingBehaviour> correspondingEObjectsByType = CorrespondenceModelUtil
					.getCorrespondingEObjects(ci, method, ResourceDemandingBehaviour.class);
			if (null != correspondingEObjectsByType && !correspondingEObjectsByType.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	protected void executeSoMoXForMethod(final BasicComponent basicComponent,
			final ResourceDemandingBehaviour targetResourceDemandingBehaviour) {
		sourceCodeDecorator = SourcecodedecoratorFactory.eINSTANCE.createSourceCodeDecoratorRepository();
		final MethodCallFinder methodCallFinder = new MethodCallFinder();
		final FunctionCallClassificationVisitor functionCallClassificationVisitor = new FunctionCallClassificationVisitor(
				this.iFunctionClassificationStrategy, methodCallFinder);
		if (this.newMethod instanceof ClassMethod) {
			// check whether the newMethod is a class method is done here. Could
			// be done eariler,
			// i.e. the class could only deal with ClassMethods, but this caused
			// problems when
			// changing an abstract method to a ClassMethod
			VisitorUtils.visitJaMoPPMethod(targetResourceDemandingBehaviour, basicComponent,
					(StatementListContainer) this.newMethod, sourceCodeDecorator,
					functionCallClassificationVisitor, this.interfaceOfExternalCallFinderFactory,
					this.resourceDemandingBehaviourForClassMethodFinding, methodCallFinder, this.generateInternalCallActions());
			for (var rdiLink : sourceCodeDecorator.getMethodLevelResourceDemandingInternalBehaviorLink()) {
				if (targetResourceDemandingBehaviour instanceof ResourceDemandingSEFF
						&& rdiLink.getResourceDemandingInternalBehaviour().eContainer() == null) {
					((ResourceDemandingSEFF) targetResourceDemandingBehaviour)
						.getResourceDemandingInternalBehaviours()
						.add(rdiLink.getResourceDemandingInternalBehaviour());
				}
			}
		} else {
			LOGGER.info("No SEFF recreated for method " + this.newMethod.getName()
					+ " because it is not a class method. Method " + this.newMethod);
		}

	}

	protected void createNewCorrespondences(final CorrespondenceModel ci,
			final ResourceDemandingBehaviour newResourceDemandingBehaviourElements) {
		for (final AbstractAction abstractAction : newResourceDemandingBehaviourElements.getSteps_Behaviour()) {
			ci.createAndAddCorrespondence(Lists.newArrayList(abstractAction), Lists.newArrayList(this.newMethod));
		}
	}

	private void connectCreatedResourceDemandingBehaviour(final ResourceDemandingBehaviour rdBehavior,
			final CorrespondenceModel ci) {
		final EList<AbstractAction> steps = rdBehavior.getSteps_Behaviour();
		final boolean addStartAction = 0 == steps.size() || !(steps.get(0) instanceof StartAction);
		final boolean addStopAction = 0 == steps.size() || !(steps.get(steps.size() - 1) instanceof StopAction);

		if (addStartAction) {
			rdBehavior.getSteps_Behaviour().add(0, SeffFactory.eINSTANCE.createStartAction());
		}
		if (addStopAction) {
			final AbstractAction stopAction = SeffFactory.eINSTANCE.createStopAction();
			rdBehavior.getSteps_Behaviour().add(stopAction);
		}
		VisitorUtils.connectActions(rdBehavior);
	}

	private void removeCorrespondingAbstractActions(final CorrespondenceModel ci) {
		final Set<AbstractAction> correspondingAbstractActions =
				CorrespondenceModelUtil.getCorrespondingEObjects(
						ci, this.newMethod, AbstractAction.class);
		if (null == correspondingAbstractActions) {
			return;
		}
		final ResourceDemandingBehaviour resourceDemandingBehaviour = this
				.getAndValidateResourceDemandingBehavior(correspondingAbstractActions);
		if (null == resourceDemandingBehaviour) {
			return;
		}
		if (resourceDemandingBehaviour instanceof ResourceDemandingSEFF) {
			((ResourceDemandingSEFF) resourceDemandingBehaviour).getResourceDemandingInternalBehaviours().clear();
		}
		for (final AbstractAction correspondingAbstractAction : correspondingAbstractActions) {
			ci.removeCorrespondencesFor(Lists.newArrayList(correspondingAbstractAction), null);
			EcoreUtil.remove(correspondingAbstractAction);
		}

		for (final AbstractAction abstractAction : resourceDemandingBehaviour.getSteps_Behaviour()) {
			if (!(abstractAction instanceof StartAction || abstractAction instanceof StopAction)) {
				LOGGER.warn(
						"The resource demanding behavior should be empty, "
						+ "but it contains at least following AbstractAction "
								+ abstractAction);
			}
		}
	}

	private ResourceDemandingBehaviour getAndValidateResourceDemandingBehavior(
			final Set<AbstractAction> correspondingAbstractActions) {
		ResourceDemandingBehaviour resourceDemandingBehaviour = null;
		for (final AbstractAction abstractAction : correspondingAbstractActions) {
			if (null == abstractAction.getResourceDemandingBehaviour_AbstractAction()) {
				LOGGER.warn("AbstractAction " + abstractAction
						+ " does not have a parent ResourceDemandingBehaviour - this should not happen.");
				continue;
			}
			if (null == resourceDemandingBehaviour) {
				// set resourceDemandingBehaviour in first cycle
				resourceDemandingBehaviour = abstractAction.getResourceDemandingBehaviour_AbstractAction();
				continue;
			}
			if (resourceDemandingBehaviour != abstractAction.getResourceDemandingBehaviour_AbstractAction()) {
				LOGGER.warn("resourceDemandingBehaviour " + resourceDemandingBehaviour
						+ " is different that current resourceDemandingBehaviour: "
						+ abstractAction.getResourceDemandingBehaviour_AbstractAction());
			}

		}
		return resourceDemandingBehaviour;
	}

	protected ResourceDemandingBehaviour findRdBehaviorToInsertElements(final CorrespondenceModel ci) {
		final Set<ResourceDemandingBehaviour> correspondingResourceDemandingBehaviours =
				CorrespondenceModelUtil.getCorrespondingEObjects(
						ci, this.newMethod, ResourceDemandingBehaviour.class);
		if (null == correspondingResourceDemandingBehaviours || correspondingResourceDemandingBehaviours.isEmpty()) {
			LOGGER.warn("No ResourceDemandingBehaviours found for method " + this.newMethod
					+ ". Could not create ResourceDemandingBehavoir to insert SEFF elements");
			return null;
		}
		return correspondingResourceDemandingBehaviours.iterator().next();
	}
	
	protected SourceCodeDecoratorRepository getSourceCodeDecoratorRepository() {
		return sourceCodeDecorator;
	}
}
