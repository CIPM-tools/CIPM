package cipm.consistency.fluentapi.gen.rootapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EcorePackage;

import cipm.consistency.fluentapi.extensions.FluentAPIMarkExtension;
import cipm.consistency.fluentapi.gen.FluentAPIGeneralParameterGenerator;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationContext;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationUtil;
import cipm.consistency.fluentapi.gen.FluentAPIMethodsUtil;
import cipm.consistency.fluentapi.gen.IFluentAPIMethodGenerator;
import cipm.consistency.fluentapi.gen.ModelConstants;

public class FluentAPIRootAPIMarkMethodGenerator implements IFluentAPIMethodGenerator {
	private static final String unmarkMethodBody = FluentAPIMethodsUtil.joinLOC(FluentAPIMarkExtension.class.getName()
			+ ".unmark(" + ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get() + ")", "return this");

	private static final String unmarkFullMethodBody = FluentAPIMethodsUtil
			.joinLOC(FluentAPIMarkExtension.class.getName() + ".unmark("
					+ ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get() + ", "
					+ ModelConstants.GeneralParameters.MARK_VALUE_PARAMETER_NAME.get() + ")", "return this");

	private static final String markMethodBodyTemplate = FluentAPIMethodsUtil
			.joinLOC(FluentAPIMarkExtension.class.getName() + ".mark("
					+ ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get() + ", "
					+ ModelConstants.GeneralParameters.MARK_VALUE_PARAMETER_NAME.get() + ")", "return this");

	private static final String getMarkedMethodBody = FluentAPIMethodsUtil
			.joinLOC("return " + FluentAPIMarkExtension.class.getName() + ".getMarked("
					+ ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get() + ")");

	private static final String getMarkedXMethodBodyTemplate =
			// %s: Element's class
			// %s: Element's class
			FluentAPIMethodsUtil.joinLOC("return (%s) " + FluentAPIMarkExtension.class.getName() + ".getMarked("
					+ ModelConstants.GeneralParameters.MARK_KEY_PARAMETER_NAME.get() + ", %s.class)");

	public List<EOperation> generateAllMarkMethods(FluentAPIGenerationContext context) {
		var allEClss = context.getAllEligibleTargetMetamodelEClasses();
		var ops = new ArrayList<EOperation>();
		ops.add(generateUnmarkMethod(context));
		ops.add(generateUnmarkFullMethod(context));
		ops.add(generateMarkMethod(context));
		ops.add(generateGetMarkedMethod());
		allEClss.forEach((eCls) -> ops.add(generateGetMarkedXMethod(eCls)));
		return ops;
	}

	private EOperation generateMarkMethod(FluentAPIGenerationContext context) {
		var markKeyParam = FluentAPIGeneralParameterGenerator.getMarkKeyParam();
		var markValParam = FluentAPIGeneralParameterGenerator.getMarkValParam();
		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.FluentAPI.Mark.NAME.get(),
				context.getFluentAPIECls());
		FluentAPIGenerationUtil.addBody(op, markMethodBodyTemplate);
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.Mark.DOC.get());
		FluentAPIGenerationUtil.addEParameters(op, markKeyParam, markValParam);
		return op;
	}

	private EOperation generateUnmarkMethod(FluentAPIGenerationContext context) {
		var param = FluentAPIGeneralParameterGenerator.getMarkKeyParam();
		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.FluentAPI.Unmark.NAME.get(),
				context.getFluentAPIECls());
		FluentAPIGenerationUtil.addBody(op, unmarkMethodBody);
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.Unmark.DOC.get());
		FluentAPIGenerationUtil.addEParameters(op, param);
		return op;
	}

	private EOperation generateUnmarkFullMethod(FluentAPIGenerationContext context) {
		var markKeyParam = FluentAPIGeneralParameterGenerator.getMarkKeyParam();
		var markValParam = FluentAPIGeneralParameterGenerator.getMarkValParam();
		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.FluentAPI.Unmark.NAME.get(),
				context.getFluentAPIECls());
		FluentAPIGenerationUtil.addBody(op, unmarkFullMethodBody);
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.Unmark.DOC.get());
		FluentAPIGenerationUtil.addEParameters(op, markKeyParam, markValParam);
		return op;
	}

	private EOperation generateGetMarkedMethod() {
		var param = FluentAPIGeneralParameterGenerator.getMarkKeyParam();
		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.FluentAPI.GetMarked.TOP_NAME.get(),
				EcorePackage.Literals.EOBJECT);
		FluentAPIGenerationUtil.addBody(op, getMarkedMethodBody);
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.GetMarked.SUMMARY.get());
		FluentAPIGenerationUtil.addEParameters(op, param);
		return op;
	}

	private EOperation generateGetMarkedXMethod(EClass elemToInit) {
		var param = FluentAPIGeneralParameterGenerator.getMarkKeyParam();
		var op = FluentAPIGenerationUtil.generateEOperation(
				ModelConstants.FluentAPI.GetMarked.NAME.getFor(StringUtils.capitalize(elemToInit.getName())),
				elemToInit);
		FluentAPIGenerationUtil.addBody(op, String.format(getMarkedXMethodBodyTemplate,
				elemToInit.getInstanceClass().getName(), elemToInit.getInstanceClass().getName()));
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.FluentAPI.GetMarked.SUMMARY.get());
		FluentAPIGenerationUtil.addEParameters(op, param);
		return op;
	}

	@Override
	public Map<String, String> getMethodNamesToDescriptions() {
		return Map.of(ModelConstants.FluentAPI.Mark.NAME.get(), ModelConstants.FluentAPI.Mark.SUMMARY.get(),
				ModelConstants.FluentAPI.Unmark.NAME.get(), ModelConstants.FluentAPI.Unmark.SUMMARY.get(),
				ModelConstants.FluentAPI.GetMarked.NAME.getEmpty(), ModelConstants.FluentAPI.GetMarked.SUMMARY.get());
	}
}
