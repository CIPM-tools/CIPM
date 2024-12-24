package cipm.consistency.vsum.test;

import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.statements.Return;
import org.emftext.language.java.statements.Statement;
import org.palladiosimulator.pcm.repository.OperationSignature;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.commitintegration.JavaFileSystemLayout;
import cipm.consistency.commitintegration.JavaParserAndPropagatorUtils;
import cipm.consistency.commitintegration.diff.util.JavaChangedMethodDetectorDiffPostProcessor;
import cipm.consistency.commitintegration.diff.util.JavaModelComparator;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import cipm.consistency.tools.evaluation.data.InstrumentationEvaluationData;
import tools.vitruv.domains.java.tuid.JamoppStringOperations;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.correspondence.CorrespondenceModelUtil;

/**
 * Evaluates the instrumentation.
 * 
 * @author Martin Armbruster
 */
@SuppressWarnings("restriction")
public class InstrumentationEvaluator {
	private final int numberAdditionalStatements = 10;
	private final int numberServiceStatements = 7;
	private final int numberStatementsPerParameter = 1;
	private final int numberExternalCallStatements = 1;
	private final int numberBranchStatements = 1;
	private final int numberLoopStatements = 3;
	private final int numberInternalActionStatements = 2;
	private final int numberInternalActionStatementsPerReturnStatement = 2;
	
	/**
	 * Evaluates the instrumented model. It is assumed to be executed directly after the instrumentation.
	 * 
	 * @param im the extended IM.
	 * @param javaModel the original Java model.
	 * @param instrumentedModel the instrumented Java model.
	 * @param cm the correspondence model.
	 */
	public void evaluateInstrumentationDependently(InstrumentationModel im, Resource javaModel,
			Resource instrumentedModel, CorrespondenceModel cm) {
		if (instrumentedModel == null || instrumentedModel.getContents().isEmpty()) {
			return;
		}
		InstrumentationEvaluationData insEvalData = EvaluationDataContainer
				.getGlobalContainer().getInstrumentationData();
		int javaStatements = countStatements(javaModel);
		int instrumStatements = countStatements(instrumentedModel);
		insEvalData.setExpectedLowerStatementDifferenceCount(countExpectedStatements(im, cm, true));
		insEvalData.setExpectedUpperStatementDifferenceCount(countExpectedStatements(im, cm, false));
		insEvalData.setStatementDifferenceCount(instrumStatements - javaStatements);
	}
	
	/**
	 * Reloads the instrumented code and evaluates it.
	 * 
	 * @param im the extended IM.
	 * @param javaModel the original Java model.
	 * @param fileLayout the Java file layout.
	 * @param cm the correspondence model.
	 */
	public void evaluateInstrumentationIndependently(InstrumentationModel im, Resource javaModel,
			JavaFileSystemLayout fileLayout, CorrespondenceModel cm) {
		if (Files.notExists(fileLayout.getInstrumentationCopy())) {
			return;
		}
		InstrumentationEvaluationData insEvalData = EvaluationDataContainer
				.getGlobalContainer().getInstrumentationIndependentData();
		insEvalData.setExpectedLowerStatementDifferenceCount(countExpectedStatements(im, cm, true));
		insEvalData.setExpectedUpperStatementDifferenceCount(countExpectedStatements(im, cm, false));
		Resource reloadedModel = JavaParserAndPropagatorUtils.parseJavaCodeIntoOneModel(
				fileLayout.getInstrumentationCopy(),
				fileLayout.getJavaModelFile().resolveSibling("ins.javaxmi"),
				fileLayout.getModuleConfiguration());
		var potentialProxies = EcoreUtil.ProxyCrossReferencer.find(reloadedModel);
		
		int javaStatements = countStatements(javaModel);
		int instrumStatements = countStatements(reloadedModel);
		insEvalData.setReloadedStatementDifferenceCount(instrumStatements - javaStatements);
		if (!potentialProxies.isEmpty()) {
			insEvalData.getUnmatchedChangedMethods().add("Reloaded model contains proxy objects.");
			return;
		}
		var postProcessor = new JavaChangedMethodDetectorDiffPostProcessor();
		JavaModelComparator.compareJavaModels(reloadedModel, javaModel,
				null, null, postProcessor);
		Set<Method> changed = new HashSet<>(postProcessor.getChangedMethods());
		insEvalData.setNumberChangedMethods(changed.size());
		for (var sip : im.getPoints()) {
			var corMeth = CorrespondenceModelUtil.getCorrespondingEObjects(cm, sip.getService(), Method.class);
			boolean success = changed.remove(corMeth.stream().findFirst().get());
			if (!success) {
				insEvalData.getUnmatchedIPs().add(sip.getId());
			}
		}
		for (Method m : changed) {
			insEvalData.getUnmatchedChangedMethods().add(convertToString(m));
		}
	}
	
	private int countStatements(Resource model) {
		int statements = 0;
		for (var iter = model.getAllContents(); iter.hasNext();) {
			EObject obj = iter.next();
			if (obj instanceof Statement) {
				statements++;
			}
		}
		return statements;
	}
	
	private int countExpectedStatements(InstrumentationModel im, CorrespondenceModel cm, boolean lowerCount) {
		int statements = numberAdditionalStatements;
		for (var sip : im.getPoints()) {
			statements += numberServiceStatements;
			statements += ((OperationSignature) sip.getService().getDescribedService__SEFF())
					.getParameters__OperationSignature().size() * numberStatementsPerParameter;
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
							var stats = CorrespondenceModelUtil.getCorrespondingEObjects(cm,
									aip.getAction(), Statement.class);
							for (Statement s : stats) {
								if (s instanceof Return) {
									statements +=
										numberInternalActionStatementsPerReturnStatement;
								}
								statements += numberInternalActionStatementsPerReturnStatement
										* s.getChildrenByType(Return.class).size();
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
	
	private String convertToString(Method method) {
		StringBuilder builder = new StringBuilder();
		builder.append(method.getContainingConcreteClassifier().getQualifiedName());
		builder.append("::");
		builder.append(method.getName());
		builder.append("(");
		for (var param : method.getParameters()) {
			builder.append(JamoppStringOperations.getStringRepresentation(param));
			builder.append(",");
		}
		builder.append(")");
		builder.append(JamoppStringOperations.getStringRepresentation(method.getTypeReference().getTarget(),
				method.getTypeReference().getArrayDimension()));
		return builder.toString();
	}
}
