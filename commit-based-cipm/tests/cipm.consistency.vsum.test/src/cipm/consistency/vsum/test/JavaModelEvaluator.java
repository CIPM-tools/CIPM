package cipm.consistency.vsum.test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;

import cipm.consistency.commitintegration.JavaParserAndPropagatorUtils;
import cipm.consistency.commitintegration.diff.util.ComparisonBasedJaccardCoefficientCalculator;
import cipm.consistency.commitintegration.diff.util.JavaModelComparator;
import cipm.consistency.tools.evaluation.data.JavaEvaluationData;

/**
 * Evaluates the update of the Java model.
 * 
 * @author Martin Armbruster
 */
public class JavaModelEvaluator {
	private JavaEvaluationData currentEvalResult;

	public void evaluateJavaModels(Resource javaModel, Path srcDir, JavaEvaluationData evalData, Path configPath) {
		currentEvalResult = evalData;
		Path referenceModelPath = Paths.get(javaModel.getURI().toFileString());
		Resource parsed = JavaParserAndPropagatorUtils.parseJavaCodeIntoOneModel(srcDir, referenceModelPath,
				configPath);
		currentEvalResult.setNewElementsCount(ModelElementsCounter.countModelElements(parsed));
		currentEvalResult.setOldElementsCount(ModelElementsCounter.countModelElements(javaModel));
		
		var result = JavaModelComparator.compareJavaModels(parsed, javaModel, List.of(parsed), List.of(javaModel),
				null);
		var jc = ComparisonBasedJaccardCoefficientCalculator.calculateJaccardCoefficient(result);
		currentEvalResult.setUnionCardinality(jc.getUnionCardinality());
		currentEvalResult.setIntersectionCardinality(jc.getIntersectionCardinality());
		currentEvalResult.setJc(jc.getJC());
	}
}
