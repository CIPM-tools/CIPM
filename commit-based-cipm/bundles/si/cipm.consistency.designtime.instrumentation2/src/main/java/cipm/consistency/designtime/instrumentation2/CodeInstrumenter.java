package cipm.consistency.designtime.instrumentation2;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementListContainer;

import tools.cipm.models.instrumentation.InstrumentationModel.ActionInstrumentationPoint;
import tools.cipm.models.instrumentation.InstrumentationModel.InstrumentationType;
import tools.cipm.models.instrumentation.InstrumentationModel.ServiceInstrumentationPoint;
import cipm.consistency.commitintegration.CommitIntegrationState;
import cipm.consistency.designtime.instrumentation2.instrumenter.MinimalMonitoringEnvironmentModelGenerator;
import cipm.consistency.designtime.instrumentation2.instrumenter.ServiceInstrumentationPointInstrumenter;
import cipm.consistency.models.code.CodeModelFacade;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.CorrespondenceModelView;

/**
 * An instrumenter for the source code based on the instrumentation points in the instrumentation
 * model.
 * 
 * @author Martin Armbruster
 */
public final class CodeInstrumenter {
    private static final Logger LOGGER = Logger.getLogger("cipm." + CodeInstrumenter.class.getSimpleName());

    private CodeInstrumenter() {
    }

    public static <CM extends CodeModelFacade> Resource instrument(CommitIntegrationState<CM> state, boolean adaptive) {
        LOGGER.debug("Executing the " + (adaptive ? "adaptive" : "full") + " instrumentation.");
        LOGGER.trace("Copying the code model.");

        var im = state.getImFacade()
            .getModel();
        Resource codeModel = state.getCodeModelFacade()
            .getResource();

        ResourceSet targetSet = new ResourceSetImpl();
        Resource copy = targetSet.createResource(codeModel.getURI());
        copy.getContents()
            .addAll(EcoreUtil.copyAll(codeModel.getContents()));

        LOGGER.trace("Generating the minimal monitoring environment.");
        MinimalMonitoringEnvironmentModelGenerator gen = new MinimalMonitoringEnvironmentModelGenerator(copy);
        ServiceInstrumentationPointInstrumenter sipIns = new ServiceInstrumentationPointInstrumenter(gen);

        for (ServiceInstrumentationPoint sip : im.getPoints()) {
            LOGGER.trace("Instrumenting the service " + sip.getService()
                .getDescribedService__SEFF()
                .getEntityName());

//            Method service = (Method) cmv.getCorrespondingEObjects(sip.getService(), null)
//                .iterator()
//                .next();

            // get the code model element that corresponds to the SIP

            var corrSet = state.getVsumFacade()
                .getCorrespondenceView()
                .getCorrespondingEObjects(sip);
            if (corrSet.size() != 1) {
                throw new IllegalStateException("Multiple code model elements correspond to a SIP");
            }

            EObject codeModelServiceElement = corrSet.iterator()
                .next();
            EObject copiedService = findCopiedEObject(targetSet, codeModelServiceElement);

            ActionStatementMapping statementMap = createActionStatementMapping(targetSet, sip, state.getVsumFacade()
                .getCorrespondenceView());

            // TODO we need to dispatch to the language specific instrumenter
            sipIns.instrument((Method) copiedService, sip, statementMap, adaptive);
        }

        LOGGER.trace("Saving the instrumented code.");

        var path = state.getDirLayout()
            .getRootDirPath()
            .resolve("instrumented-code");
        ModelSaverInRepositoryCopy.saveModels(targetSet, copy, path, state.getGitRepositoryWrapper()
            .getWorkTree()
            .toPath(), gen);

        LOGGER.trace("Finished the instrumentation.");
        return copy;
    }

    private static <C extends Correspondence> ActionStatementMapping createActionStatementMapping(
            ResourceSet copyContainer, ServiceInstrumentationPoint sip, CorrespondenceModelView<C> cmv) {
        ActionStatementMapping statementMap = new ActionStatementMapping();
        for (ActionInstrumentationPoint aip : sip.getActionInstrumentationPoints()) {
//			Set<Statement> correspondingStatements = CorrespondenceModelUtil.getCorrespondingEObjects(cm,
//					aip.getAction(), Statement.class);

            // TODO I cannot cast Set<EObject> to Set<Statement> here?!
            Set<EObject> correspondingStatements = cmv.getCorrespondingEObjects(aip.getAction(), null);
            Statement firstStatement;
            if (aip.getType() == InstrumentationType.INTERNAL || aip.getType() == InstrumentationType.INTERNAL_CALL) {
                Statement lastStatement = findFirstOrLastStatement(correspondingStatements, false);
                statementMap.getAbstractActionToLastStatementMapping()
                    .put(aip.getAction(), findCopiedEObject(copyContainer, lastStatement));
                firstStatement = findFirstOrLastStatement(correspondingStatements, true);
            } else {
                try {
                    firstStatement = (Statement) correspondingStatements.iterator()
                        .next();
                } catch (NoSuchElementException e) {
                    continue;
                }
            }
            Statement copiedFirstStatement = findCopiedEObject(copyContainer, firstStatement);
            statementMap.put(aip.getAction(), copiedFirstStatement);
        }
        return statementMap;
    }

    @SuppressWarnings("unchecked")
    private static <T extends EObject> T findCopiedEObject(ResourceSet copyContainer, T original) {
        EObject potResult = copyContainer.getEObject(EcoreUtil.getURI(original), false);
        if (potResult != null && original.eClass()
            .isInstance(potResult)) {
            return (T) potResult;
        }
        return null;
    }

    private static Statement findFirstOrLastStatement(Set<EObject> statements, boolean findFirst) {
        if (statements.size() == 1) {
            return (Statement) statements.iterator()
                .next();
        }
        EObject container = statements.iterator()
            .next()
            .eContainer();
        if (container instanceof StatementListContainer) {
            EList<Statement> stats = ((StatementListContainer) container).getStatements();
            if (findFirst) {
                for (Statement next : stats) {
                    if (statements.contains(next)) {
                        return next;
                    }
                }
            }
            Set<EObject> copiedStatements = new HashSet<>(statements);
            for (Statement next : stats) {
                copiedStatements.remove(next);
                if (copiedStatements.size() == 1) {
                    return (Statement) copiedStatements.iterator()
                        .next();
                }
            }
        }
        return null;
    }
}
