package cipm.consistency.fluentapi.gen.init;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;

import cipm.consistency.fluentapi.gen.FluentAPIGenerationContext;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationUtil;
import cipm.consistency.fluentapi.gen.FluentAPIMethodsUtil;
import cipm.consistency.fluentapi.gen.IFluentAPIMethodGenerator;
import cipm.consistency.fluentapi.gen.ModelConstants;

public class FluentAPIInitialisationWithOperationGenerator implements IFluentAPIMethodGenerator {
	private static final String withXFeatMethodBodyTemplate = FluentAPIMethodsUtil
			.joinLOC(ModelConstants.SuperInitialisation.CurrentElement.NAME.thisGetterCall() + ".eSet("
					+ ModelConstants.SuperInitialisation.CurrentElement.NAME.thisGetterCall()
					// %s: Feature name
					// %s: Feature value parameter (although the parameter name is known, there are
					// overloading methods process the parameter)
					+ ".eClass().getEStructuralFeature(\"%s\"), %s)", "return this");

	private static final String withoutXFeatMethodBodyTemplate = FluentAPIMethodsUtil
			.joinLOC(ModelConstants.SuperInitialisation.CurrentElement.NAME.thisGetterCall() + ".eUnset("
					+ ModelConstants.SuperInitialisation.CurrentElement.NAME.thisGetterCall()
					// %s: Feature name
					+ ".eClass().getEStructuralFeature(\"%s\"))", "return this");

	private static final String withAddedXFeatMethodBodyTemplate = FluentAPIMethodsUtil
			.joinLOC("((org.eclipse.emf.common.util.EList) "
					+ ModelConstants.SuperInitialisation.CurrentElement.NAME.thisGetterCall() + ".eGet("
					+ ModelConstants.SuperInitialisation.CurrentElement.NAME.thisGetterCall()
					// %s: Feature name
					+ ".eClass().getEStructuralFeature(\"%s\"))).add("
					+ ModelConstants.Initialiation.WithAdded.PARAMETER_NAME.get() + ")", "return this");

	private static final String withRemovedXFeatMethodBodyTemplate = FluentAPIMethodsUtil
			.joinLOC("((org.eclipse.emf.common.util.EList) "
					+ ModelConstants.SuperInitialisation.CurrentElement.NAME.thisGetterCall() + ".eGet("
					+ ModelConstants.SuperInitialisation.CurrentElement.NAME.thisGetterCall()
					// %s: Feature name
					+ ".eClass().getEStructuralFeature(\"%s\"))).remove("
					+ ModelConstants.Initialiation.WithRemoved.PARAMETER_NAME.get() + ")", "return this");

	private static final String cleanXFeatMethodBodyTemplate = FluentAPIMethodsUtil
			.joinLOC("var list = (org.eclipse.emf.common.util.EList) "
					+ ModelConstants.SuperInitialisation.CurrentElement.NAME.thisGetterCall() + ".eGet("
					+ ModelConstants.SuperInitialisation.CurrentElement.NAME.thisGetterCall()
					// %s: Feature name
					+ ".eClass().getEStructuralFeature(\"%s\"))", "list.clear()", "return this");

	private List<EStructuralFeature> getAllEligibleFeats(FluentAPIGenerationContext context, EClass elemToInit) {
		return elemToInit.getEAllStructuralFeatures().stream()
				.filter((feat) -> context.getTargetMetamodelFilter().isFeatureEligible(elemToInit, feat))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public List<EOperation> generateAllWithOperationsFor(FluentAPIGenerationContext context, EClass initECls,
			EClass elemToInit) {
		var feats = this.getAllEligibleFeats(context, elemToInit);
		var ops = new ArrayList<EOperation>();

		for (var feat : feats) {
			if (!feat.isMany()) {
				ops.addAll(this.generateWithXFeat(context, initECls, elemToInit, feat));
				ops.add(this.generateWithoutXFeat(initECls, elemToInit, feat));
			} else {
				ops.add(this.generateWithAddedXFeat(context, initECls, elemToInit, feat));
				ops.add(this.generateWithRemovedXFeat(context, initECls, elemToInit, feat));
				ops.add(this.generateCleanXFeat(context, initECls, elemToInit, feat));
			}
		}
		return ops;
	}

	private List<EOperation> generateWithXFeat(FluentAPIGenerationContext context, EClass initECls, EClass elemToInit,
			EStructuralFeature feat) {
		var ops = new ArrayList<EOperation>();

		BiFunction<EParameter, String, EOperation> opGenerator = (featValParam, featValParamPlugin) -> {
			var op = FluentAPIGenerationUtil.generateEOperation(
					ModelConstants.Initialiation.With.NAME.getFor(StringUtils.capitalize(feat.getName())), initECls);
			FluentAPIGenerationUtil.addBody(op,
					String.format(withXFeatMethodBodyTemplate, feat.getName(), featValParamPlugin));
			FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.Initialiation.With.DOC.getFor(feat.getName()));
			FluentAPIGenerationUtil.addEParameters(op, featValParam);
			return op;
		};

		var originalOpNewFeatValParam = getNewFeatValParam(feat);
		var originalOp = opGenerator.apply(originalOpNewFeatValParam, originalOpNewFeatValParam.getName());
		ops.add(originalOp);

		return ops;
	}

	private EOperation generateWithoutXFeat(EClass initECls, EClass elemToInit, EStructuralFeature feat) {
		var op = FluentAPIGenerationUtil.generateEOperation(
				ModelConstants.Initialiation.Without.NAME.getFor(StringUtils.capitalize(feat.getName())), initECls);
		FluentAPIGenerationUtil.addBody(op, String.format(withoutXFeatMethodBodyTemplate, feat.getName()));
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.Initialiation.Without.DOC.getFor(feat.getName()));
		return op;
	}

	private EOperation generateWithAddedXFeat(FluentAPIGenerationContext context, EClass initECls, EClass elemToInit,
			EStructuralFeature feat) {
		var op = FluentAPIGenerationUtil.generateEOperation(
				ModelConstants.Initialiation.WithAdded.NAME.getFor(StringUtils.capitalize(feat.getName())), initECls);

		FluentAPIGenerationUtil.addBody(op, String.format(withAddedXFeatMethodBodyTemplate, feat.getName()));
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.Initialiation.WithAdded.DOC.getFor(feat.getName()));
		FluentAPIGenerationUtil.addEParameters(op, getAddedFeatValParam(feat));
		return op;
	}

	private EOperation generateWithRemovedXFeat(FluentAPIGenerationContext context, EClass initECls, EClass elemToInit,
			EStructuralFeature feat) {
		var op = FluentAPIGenerationUtil.generateEOperation(
				ModelConstants.Initialiation.WithRemoved.NAME.getFor(StringUtils.capitalize(feat.getName())), initECls);
		FluentAPIGenerationUtil.addBody(op, String.format(withRemovedXFeatMethodBodyTemplate, feat.getName()));
		FluentAPIGenerationUtil.addDocumentation(op,
				ModelConstants.Initialiation.WithRemoved.DOC.getFor(feat.getName()));
		FluentAPIGenerationUtil.addEParameters(op, getRemovedFeatValParam(feat));
		return op;
	}

	private EOperation generateCleanXFeat(FluentAPIGenerationContext context, EClass initECls, EClass elemToInit,
			EStructuralFeature feat) {
		var op = FluentAPIGenerationUtil.generateEOperation(
				ModelConstants.Initialiation.Clean.NAME.getFor(StringUtils.capitalize(feat.getName())), initECls);
		FluentAPIGenerationUtil.addBody(op, String.format(cleanXFeatMethodBodyTemplate, feat.getName()));
		FluentAPIGenerationUtil.addDocumentation(op,
				ModelConstants.Initialiation.Clean.DOC.getFor(feat.getName(), feat.getName()));
		return op;
	}

	private EParameter getNewFeatValParam(EStructuralFeature feat) {
		var param = FluentAPIGenerationUtil.generateSingleValuedEParameter(
				ModelConstants.Initialiation.With.PARAMETER_NAME.get(), feat.getEType());
		FluentAPIGenerationUtil.addDocumentation(param,
				ModelConstants.Initialiation.With.PARAMETER_DOC.getFor(feat.getName()));
		return param;
	}

	private EParameter getAddedFeatValParam(EStructuralFeature feat) {
		var param = FluentAPIGenerationUtil.generateSingleValuedEParameter(
				ModelConstants.Initialiation.WithAdded.PARAMETER_NAME.get(), feat.getEType());
		FluentAPIGenerationUtil.addDocumentation(param,
				ModelConstants.Initialiation.WithAdded.PARAMETER_DOC.getFor(feat.getName()));
		return param;
	}

	private EParameter getRemovedFeatValParam(EStructuralFeature feat) {
		var param = FluentAPIGenerationUtil.generateSingleValuedEParameter(
				ModelConstants.Initialiation.WithRemoved.PARAMETER_NAME.get(), feat.getEType());
		FluentAPIGenerationUtil.addDocumentation(param,
				ModelConstants.Initialiation.WithRemoved.PARAMETER_DOC.getFor(feat.getName()));
		return param;
	}

	@Override
	public Map<String, String> getMethodNamesToDescriptions() {
		return Map.of(ModelConstants.Initialiation.With.NAME.getEmpty(),
				ModelConstants.Initialiation.With.SUMMARY.get(),

				ModelConstants.Initialiation.Without.NAME.getEmpty(),
				ModelConstants.Initialiation.Without.SUMMARY.get(),

				ModelConstants.Initialiation.WithAdded.NAME.getEmpty(),
				ModelConstants.Initialiation.WithAdded.SUMMARY.get(),

				ModelConstants.Initialiation.WithRemoved.NAME.getEmpty(),
				ModelConstants.Initialiation.WithRemoved.SUMMARY.get(),

				ModelConstants.Initialiation.Clean.NAME.getEmpty(), ModelConstants.Initialiation.Clean.SUMMARY.get());
	}
}
