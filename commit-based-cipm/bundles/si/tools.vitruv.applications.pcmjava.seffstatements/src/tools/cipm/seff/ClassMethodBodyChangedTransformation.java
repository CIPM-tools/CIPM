package tools.cipm.seff;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
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

import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.interaction.UserInteractor;

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

	protected final Method newMethod;
	protected final BasicComponentFinding basicComponentFinder;
	private final IFunctionClassificationStrategy iFunctionClassificationStrategy;

	private final InterfaceOfExternalCallFindingFactory interfaceOfExternalCallFindingFactory;

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
		this.interfaceOfExternalCallFindingFactory = interfaceOfExternalCallFindingFactory;
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
	 * 3) reconnect the newly extracted SEFF elements with the old elements.
	 * 
	 * @param correspondenceModel the current correspondence model.
	 * @param userInteractor the user interactor.
	 */
	public void execute(final EditableCorrespondenceModelView<Correspondence> correspondenceModel,
			final UserInteractor userInteractor) {
		if (!this.isArchitectureRelevantChange(correspondenceModel)) {
			LOGGER.debug("Change within the method: " + this.newMethod
					+ " is not an architecture relevant change");
			return;
		}
		
		// 1)
		this.emptyCorrespondingSeffs(correspondenceModel);

		// 2)
		final ResourceDemandingBehaviour resourceDemandingBehaviour = this
				.findRdBehaviorToInsertElements(correspondenceModel);
		if (resourceDemandingBehaviour == null) {
			return;
		}
		final BasicComponent basicComponent = this.basicComponentFinder.findBasicComponentForMethod(this.newMethod,
				correspondenceModel);
		this.executeSoMoXForMethod(basicComponent, resourceDemandingBehaviour);

		// 3)
		this.connectCreatedResourceDemandingBehaviour(resourceDemandingBehaviour);
	}

	/**
	 * Checks whether the change is considered architecture relevant. This is
	 * the case if either the new or the old method does have a corresponding
	 * ResourceDemandingBehaviour.
	 *
	 * @param correspondenceModel the current correspondence model.
	 * @return true if the method is architecture relevant. false otherwise.
	 */
	protected boolean isArchitectureRelevantChange(final EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
		return this.isMethodArchitectureRelevant(this.newMethod, correspondenceModel);
	}

	private boolean isMethodArchitectureRelevant(final Method method, final EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
		if (null != method) {
			final List<ResourceDemandingBehaviour> correspondingEObjectsByType = CorrespondenceModelUtil
				.getCorrespondingEObjects(correspondenceModel, method, ResourceDemandingBehaviour.class);
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
					functionCallClassificationVisitor, this.interfaceOfExternalCallFindingFactory,
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

	/**
	 * Ensures that the given ResourceDemandingBehaviour starts with a StartAction
	 * and ends with a StopAction, adding whichever of the two is missing. Any
	 * actions already present are left untouched.
	 *
	 * Callers that also need the actions to be linked to each other have to invoke
	 * {@link VisitorUtils#connectActions(ResourceDemandingBehaviour)} themselves,
	 * because they differ in when that has to happen.
	 *
	 * @param rdBehavior the behaviour to complete.
	 */
	protected static void ensureStartAndStopAction(final ResourceDemandingBehaviour rdBehavior) {
		final EList<AbstractAction> steps = rdBehavior.getSteps_Behaviour();
		final boolean addStartAction = 0 == steps.size() || !(steps.get(0) instanceof StartAction);
		final boolean addStopAction = 0 == steps.size() || !(steps.get(steps.size() - 1) instanceof StopAction);

		if (addStartAction) {
			steps.add(0, SeffFactory.eINSTANCE.createStartAction());
		}
		if (addStopAction) {
			steps.add(SeffFactory.eINSTANCE.createStopAction());
		}
	}

	private void connectCreatedResourceDemandingBehaviour(final ResourceDemandingBehaviour rdBehavior) {
		ensureStartAndStopAction(rdBehavior);
		VisitorUtils.connectActions(rdBehavior);
	}

	private void emptyCorrespondingSeffs(final EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
		var correspondingSeff = CorrespondenceModelUtil
				.getCorrespondingEObjects(correspondenceModel, this.newMethod, ResourceDemandingSEFF.class);

		if (correspondingSeff == null || correspondingSeff.isEmpty()) {
			return;
		}
		if (correspondingSeff.size() > 1) {
			LOGGER.warn("More than one SEFF corresponding to a method.");
		}

		for (var seff : correspondingSeff) {
			for (var action : new ArrayList<>(seff.getSteps_Behaviour())) {
				removeObjectWithChildren(correspondenceModel, action);
			}
			for (var rdBehavior : new ArrayList<>(seff.getResourceDemandingInternalBehaviours())) {
				removeObjectWithChildren(correspondenceModel, rdBehavior);
			}
		}
	}

	private void removeObjectWithChildren(EditableCorrespondenceModelView<Correspondence> correspondenceModel, EObject obj) {
		for (var child : new ArrayList<>(obj.eContents())) {
			removeObjectWithChildren(correspondenceModel, child);
		}
		CorrespondenceModelUtil.removeCorrespondencesFor(correspondenceModel, obj);
		EcoreUtil.remove(obj);
	}

	/**
	 * Finds the RD behaviour that newly extracted SEFF elements for {@link #newMethod}
	 * should be inserted into.
	 *
	 * @param correspondenceModel the current correspondence model.
	 * @return the corresponding RD behaviour, or {@code null} if the method has none
	 *         (a warning is logged in that case).
	 */
	protected ResourceDemandingBehaviour findRdBehaviorToInsertElements(final EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
		final List<ResourceDemandingBehaviour> correspondingResourceDemandingBehaviours =
			CorrespondenceModelUtil.getCorrespondingEObjects(correspondenceModel, this.newMethod, ResourceDemandingBehaviour.class);
		if (null == correspondingResourceDemandingBehaviours || correspondingResourceDemandingBehaviours.isEmpty()) {
			LOGGER.warn("No ResourceDemandingBehaviours found for method " + this.newMethod
					+ ". Could not create ResourceDemandingBehaviour to insert SEFF elements");
			return null;
		}
		return correspondingResourceDemandingBehaviours.iterator().next();
	}
	
	/**
	 * Returns the source-code-decorator repository produced while running SoMoX for this
	 * method. It is {@code null} until {@link #execute} has run SoMoX (i.e. only meaningful
	 * after {@code execute} has processed an architecture-relevant change).
	 */
	protected SourceCodeDecoratorRepository getSourceCodeDecoratorRepository() {
		return sourceCodeDecorator;
	}
}
