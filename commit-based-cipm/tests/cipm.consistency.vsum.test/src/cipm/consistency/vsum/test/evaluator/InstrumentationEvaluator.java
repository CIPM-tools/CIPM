package cipm.consistency.vsum.test.evaluator;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.emftext.language.java.statements.Return;
import org.emftext.language.java.statements.Statement;
import org.palladiosimulator.pcm.repository.OperationSignature;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.commitintegration.CommitIntegrationController;
import cipm.consistency.commitintegration.CommitIntegrationState;
import cipm.consistency.models.code.CodeModelFacade;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import cipm.consistency.tools.evaluation.data.InstrumentationEvaluationData;
import tools.vitruv.change.correspondence.Correspondence;
//import tools.vitruv.domains.java.tuid.JamoppStringOperations;
//import tools.vitruv.changes.correspondence.model.CorrespondenceModel;
//import tools.vitruv.change.correspondence.util.CorrespondenceModelUtil;
import tools.vitruv.change.correspondence.view.CorrespondenceModelView;

/**
 * Evaluates the instrumentation.
 * 
 * @param <CM>
 *            The code model which is instrumented
 * 
 * @author Martin Armbruster
 * @author Lukas Burgey
 */
public class InstrumentationEvaluator<CM extends CodeModelFacade> extends CommitIntegrationController<CM> {
    private final int numberAdditionalStatements = 10;
    private final int numberServiceStatements = 7;
    private final int numberStatementsPerParameter = 1;
    private final int numberExternalCallStatements = 1;
    private final int numberBranchStatements = 1;
    private final int numberLoopStatements = 3;
    private final int numberInternalActionStatements = 2;
    private final int numberInternalActionStatementsPerReturnStatement = 2;

//    private CM codeModel;
//    private CM instrumentedModel;

    /**
     * Evaluates the instrumented code model. It is assumed to be executed directly after the
     * instrumentation.
     * 
     * @param state
     *            The commit integration state.
     * @param instrumentedCodeModel
     *            The instrumented code model. The actual code model is still contained in the
     *            state.
     */
    // TODO what does evaluating "dependently" mean?
    public void evaluateInstrumentationDependently(CommitIntegrationState<CM> state,
            CodeModelFacade instrumentedCodeModel) {
        if (instrumentedCodeModel == null || instrumentedCodeModel.getResource()
            .getContents()
            .isEmpty()) {
            return;
        }
        // count statements in the
        InstrumentationEvaluationData insEvalData = EvaluationDataContainer.get()
            .getInstrumentationData();
        int codeModelStatements = countStatements(state.getCodeModelFacade()
            .getResource());
        int instrumStatements = countStatements(instrumentedCodeModel.getResource());
        insEvalData.setStatementDifferenceCount(instrumStatements - codeModelStatements);

        var lowerDifferenceCount = countExpectedStatements(state.getImFacade()
            .getModel(),
                state.getVsumFacade()
                    .getCorrespondenceView(),
                true);
        insEvalData.setExpectedLowerStatementDifferenceCount(lowerDifferenceCount);

        var upperDifferenceCount = countExpectedStatements(state.getImFacade()
            .getModel(),
                state.getVsumFacade()
                    .getCorrespondenceView(),
                false);
        insEvalData.setExpectedUpperStatementDifferenceCount(upperDifferenceCount);
    }

    /**
     * Reloads the instrumented code and evaluates it.
     * 
     * @param im
     *            the extended IM.
     * @param javaModel
     *            the original Java model.
     * @param fileLayout
     *            the Java file layout.
     * @param cm
     *            the correspondence model.
     */
    // TODO this method duplicates some code from the previous method. We could add a parameter "dependent" and merge them into one
//    public void evaluateInstrumentationIndependently(CommitIntegrationState<CM> state) {
//
////    public void evaluateInstrumentationIndependently(InstrumentationModel im, Resource javaModel,
////            CommitIntegrationState<LuaModel> state, CorrespondenceModelView<Correspondence> cmv) {
//
//        var im = state.getImFacade()
//            .getModel();
//        var cmv = state.getVsumFacade()
//            .getCorrespondenceView();
//        var codeModel = state.getCodeModelFacade()
//            .getResource();
//
//        InstrumentationEvaluationData insEvalData = EvaluationDataContainer.getGlobalContainer()
//            .getInstrumentationData();
//        insEvalData.setExpectedLowerStatementDifferenceCount(countExpectedStatements(im, cmv, true));
//        insEvalData.setExpectedUpperStatementDifferenceCount(countExpectedStatements(im, cmv, false));
//        Resource reloadedModel = JavaParserAndPropagatorUtils.parseJavaCodeIntoOneModel(state.getImFacade()
//            .getDirLayout()
//            .getInstrumentationDirPath(),
//                state.getCodeModelFacade()
//                    .getDirLayout()
//                    .getRootDirPath()
//                    .resolve("ins.javaxmi"),
//                state.getCodeModelFacade()
//                    .getDirLayout()
//                    .getModuleConfigurationPath());
//
//        var potentialProxies = EcoreUtil.ProxyCrossReferencer.find(reloadedModel);
//
//        int javaStatements = countStatements(codeModel);
//        int instrumStatements = countStatements(reloadedModel);
//        insEvalData.setReloadedStatementDifferenceCount(instrumStatements - javaStatements);
//        if (!potentialProxies.isEmpty()) {
//            insEvalData.getUnmatchedChangedMethods()
//                .add("Reloaded model contains proxy objects.");
//            return;
//        }
//        compareModels(reloadedModel, codeModel);
//        Set<Method> changed = new HashSet<>(postProcessor.getChangedMethods());
//        insEvalData.setNumberChangedMethods(changed.size());
//        for (var sip : instrumentedModel.getPoints()) {
//            var corMeth = cmv.getCorrespondingEObjects(sip.getService(), null);
//            boolean success = changed.remove(corMeth.stream()
//                .findFirst()
//                .get());
//            if (!success) {
//                insEvalData.getUnmatchedIPs()
//                    .add(sip.getId());
//            }
//        }
//        for (Method m : changed) {
//            insEvalData.getUnmatchedChangedMethods()
//                .add(convertToString(m));
//        }
//    }

//    private static void compareModels(Resource reloadedModel, Resource codeModel) {
//        // TODO lua case
//        if (reloadedModel instanceof Notifier) {
//            var javaReloadedModel = (Notifier) reloadedModel;
//            var javaCodeModel = (Notifier) codeModel;
//
//            var postProcessor = new JavaChangedMethodDetectorDiffPostProcessor();
//            JavaModelComparator.compareJavaModels(javaReloadedModel, javaCodeModel, null, null, postProcessor);
//        }
//
//    }

    private static int countStatements(Resource model) {
        int statements = 0;
        for (var iter = model.getAllContents(); iter.hasNext();) {
            EObject obj = iter.next();
            if (obj instanceof Statement) {
                statements++;
            }
        }
        return statements;
    }

    private int countExpectedStatements(InstrumentationModel im, CorrespondenceModelView<Correspondence> cmv,
            boolean lowerCount) {
        int statements = numberAdditionalStatements;
        for (var sip : im.getPoints()) {
            statements += numberServiceStatements;
            statements += ((OperationSignature) sip.getService()
                .getDescribedService__SEFF()).getParameters__OperationSignature()
                    .size() * numberStatementsPerParameter;
            for (var aip : sip.getActionInstrumentationPoints()) {
                if (!aip.isActive()) {
                    continue;
                }
                switch (aip.getType()) {
                case BRANCH:
                    statements += numberBranchStatements;
                    break;
                case INTERNAL:
                case INTERNAL_CALL:
                    statements += numberInternalActionStatements;
                    if (!lowerCount) {
                        Set<EObject> stats = cmv.getCorrespondingEObjects(aip.getAction(), null);
                        for (EObject s : stats) {
                            if (s instanceof Return) {
                                statements += numberInternalActionStatementsPerReturnStatement;
                            }
                            statements += numberInternalActionStatementsPerReturnStatement
                                    * ((Statement) s).getChildrenByType(Return.class)
                                        .size();
                        }
                    }
                    break;
                case EXTERNAL_CALL:
                    statements += numberExternalCallStatements;
                    break;
                case LOOP:
                default:
                    statements += numberLoopStatements;
                    break;
                }
            }
        }
        return statements;
    }

//    private String convertToString(Method method) {
//        StringBuilder builder = new StringBuilder();
//		builder.append(method.getContainingConcreteClassifier().getQualifiedName());
//		builder.append("::");
//		builder.append(method.getName());
//		builder.append("(");
//		for (var param : method.getParameters()) {
//			builder.append(JamoppStringOperations.getStringRepresentation(param));
//			builder.append(",");
//		}
//		builder.append(")");
//		builder.append(JamoppStringOperations.getStringRepresentation(method.getTypeReference().getTarget(),
//				method.getTypeReference().getArrayDimension()));
//        return builder.toString();
//    }
}
