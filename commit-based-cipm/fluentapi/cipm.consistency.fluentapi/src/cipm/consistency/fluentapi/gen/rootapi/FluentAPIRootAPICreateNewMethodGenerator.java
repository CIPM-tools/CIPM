package cipm.consistency.fluentapi.gen.rootapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EcorePackage;

import cipm.consistency.fluentapi.gen.FluentAPIGenerationContext;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationUtil;
import cipm.consistency.fluentapi.gen.FluentAPIMethodsUtil;
import cipm.consistency.fluentapi.gen.IFluentAPIMethodGenerator;
import cipm.consistency.fluentapi.gen.ModelConstants;

/*
 * createNew() generation is separated from FluentAPIRootAPINewMethodGenerator, since the "createNewX"
 * methods cannot share their name with "newX" methods, due to Java limitations.
 */

public class FluentAPIRootAPICreateNewMethodGenerator implements IFluentAPIMethodGenerator {
	private static final String createNewXMethodBodyTemplate = FluentAPIMethodsUtil
			// %s: Fully qualified class name
			// %s: Fully qualified class name
			.joinLOC("return (%s) " + ModelConstants.FluentAPI.GetInitialisationFor.NAME.thisCall("%s.class")
					+ ModelConstants.SuperInitialisation.CreateNow.NAME.call());

	private static final String createNewXWithClassParamMethodBody = FluentAPIMethodsUtil
			.joinLOC("return (" + ModelConstants.GeneralParameters.TYPE_PARAMETER_NAME.get() + ") "
					+ ModelConstants.FluentAPI.GetInitialisationFor.NAME
							.thisCall(ModelConstants.GeneralParameters.ARBITRARY_ECLASS_PARAMETER_NAME.get())
					+ ModelConstants.SuperInitialisation.CreateNow.NAME.call());

	public List<EOperation> generateAllCreateNewMethods(FluentAPIGenerationContext context) {
		var eObjEClss = context.getAllEligibleTargetMetamodelConcreteEClasses();
		var ops = new ArrayList<EOperation>();

		ops.add(generateGenericCreateNewMethod());

		for (var eObjEClass : eObjEClss) {
			ops.add(generateCreateNewMethod(eObjEClass));
		}

		return ops;
	}

	private EOperation generateCreateNewMethod(EClass eObjEClass) {
		var op = FluentAPIGenerationUtil.generateEOperation(
				ModelConstants.FluentAPI.CreateNew.NAME.getFor(eObjEClass.getInstanceClass().getSimpleName()),
				eObjEClass);
		FluentAPIGenerationUtil.addBody(op, String.format(createNewXMethodBodyTemplate,
				eObjEClass.getInstanceClass().getName(), eObjEClass.getInstanceClass().getName()));
		FluentAPIGenerationUtil.addDocumentation(op,
				ModelConstants.FluentAPI.CreateNew.SUMMARY.getFor(eObjEClass.getInstanceClass().getName(),
						StringUtils.capitalize(eObjEClass.getInstanceClass().getSimpleName())));
		return op;
	}

	private EOperation generateGenericCreateNewMethod() {
		// Goal: <T> T createNewX(Class<T> createNewXWithClassParamParamName)

		var typeParam = FluentAPIGenerationUtil
				.generateETypeParameter(ModelConstants.GeneralParameters.TYPE_PARAMETER_NAME.get());

		// Make sure to create 2 generic types, one for the method parameter (Class<T>)
		// and one for the return type of the method (T)

		// "<T>" in "<T> T createNewX..."
		var methodTypeParam = FluentAPIGenerationUtil.generateEGenericTypeWithTypeParameter(typeParam);

		// "T" in "Class<T> ..."
		var methodParamTypeArgument = FluentAPIGenerationUtil.generateEGenericTypeWithTypeParameter(typeParam);

		// "Class<T>" in "Class<T> createNewXWithClassParamParamName"
		var methodParamType = FluentAPIGenerationUtil
				.generateEGenericTypeWithClassifier(EcorePackage.Literals.EJAVA_CLASS);
		FluentAPIGenerationUtil.addTypeArgument(methodParamType, methodParamTypeArgument);

		// "createNewXWithClassParamParamName" in "Class<T>
		// createNewXWithClassParamParamName"
		var methodParam = FluentAPIGenerationUtil
				.generateSingleValuedEParameter(ModelConstants.GeneralParameters.ARBITRARY_ECLASS_PARAMETER_NAME.get());
		FluentAPIGenerationUtil.addDocumentation(methodParam,
				ModelConstants.GeneralParameters.ARBITRARY_ECLASS_PARAMETER_DOC.get());
		methodParam.setEGenericType(methodParamType);

		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.FluentAPI.CreateNew.TOP_NAME.get());
		FluentAPIGenerationUtil.addBody(op, createNewXWithClassParamMethodBody);
		FluentAPIGenerationUtil.addDocumentation(op,
				ModelConstants.FluentAPI.CreateNew.SUMMARY.getFor(methodParam.getName(), methodParam.getName()));
		op.setEGenericType(methodTypeParam);
		op.getETypeParameters().add(typeParam);
		op.getEParameters().add(methodParam);
		return op;
	}

	@Override
	public Map<String, String> getMethodNamesToDescriptions() {
		return Map.of(ModelConstants.SuperInitialisation.CreateNow.NAME.get(),
				ModelConstants.SuperInitialisation.CreateNow.SUMMARY.get());
	}
}
