package cipm.consistency.fluentapi.gen.superinit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EcorePackage;

import cipm.consistency.fluentapi.gen.FluentAPIGenerationUtil;
import cipm.consistency.fluentapi.gen.FluentAPIMethodsUtil;
import cipm.consistency.fluentapi.gen.IFluentAPIMethodGenerator;
import cipm.consistency.fluentapi.gen.ModelConstants;

public class FluentAPISuperInitialisationCreateNowMethodGenerator implements IFluentAPIMethodGenerator {
	private static final String createNowMethodBodyTemplate = FluentAPIMethodsUtil.joinLOC(
			ModelConstants.SuperInitialisation.ToAPI.NAME.thisCall()
					+ ModelConstants.FluentAPI.DropInitialisation.NAME.call("this"),
			// %s: Element class
			"return (%s) " + ModelConstants.SuperInitialisation.CurrentElement.NAME.thisGetterCall());

	public List<EOperation> generateAllCreateNowMethods(EClass elemToInit) {
		var ops = new ArrayList<EOperation>();

		ops.add(generateCreateNowMethod(elemToInit));
		ops.add(generateGenericCreateNowMethod(elemToInit));

		return ops;
	}

	private EOperation generateCreateNowMethod(EClass elemToInit) {
		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.SuperInitialisation.CreateNow.NAME.get(),
				elemToInit);
		FluentAPIGenerationUtil.addBody(op,
				String.format(createNowMethodBodyTemplate, elemToInit.getInstanceClass().getName()));
		FluentAPIGenerationUtil.addDocumentation(op, ModelConstants.SuperInitialisation.CreateNow.DOC.get());
		return op;
	}

	private EOperation generateGenericCreateNowMethod(EClass elemToInit) {
		// Goal: <T> T createNow(Class<T> createNowMethodParamName)

		// "<T>" in "<T> T createNow(...)"
		var methodTypeParam = FluentAPIGenerationUtil
				.generateETypeParameter(ModelConstants.GeneralParameters.TYPE_PARAMETER_NAME.get());

		// Make sure to create 2 generic types, one for the method parameter (Class<T>)
		// and one for the return type of the method (T)

		// "T" in "...createNow(Class<T> createNowMethodParamName)"
		var genericParamTypeForJavaClass = FluentAPIGenerationUtil
				.generateEGenericTypeWithTypeParameter(methodTypeParam);

		// "Class<T>" in "...createNow(Class<T> createNowMethodParamName)"
		var genericClassType = FluentAPIGenerationUtil
				.generateEGenericTypeWithClassifier(EcorePackage.Literals.EJAVA_CLASS);
		FluentAPIGenerationUtil.addTypeArgument(genericClassType, genericParamTypeForJavaClass);

		// "T" in "... T createNow(...)"
		var genericParamTypeForOp = FluentAPIGenerationUtil.generateEGenericTypeWithTypeParameter(methodTypeParam);

		// "Class<T> createNowMethodParamName" in "...createNow(Class<T>
		// createNowMethodParamName)"
		var param = FluentAPIGenerationUtil.generateSingleValuedEParameter(
				ModelConstants.SuperInitialisation.CreateNow.CLASS_PARAMETER_NAME.get(), genericClassType);

		var op = FluentAPIGenerationUtil.generateEOperation(ModelConstants.SuperInitialisation.CreateNow.NAME.get(),
				genericParamTypeForOp);
		FluentAPIGenerationUtil.addBody(op, String.format(createNowMethodBodyTemplate, methodTypeParam.getName()));
		FluentAPIGenerationUtil.addTypeParameters(op, methodTypeParam);
		FluentAPIGenerationUtil.addEParameters(op, param);
		FluentAPIGenerationUtil.addDocumentation(op,
				ModelConstants.SuperInitialisation.CreateNow.CLASS_PARAMETER_DOC.get());

		return op;
	}

	@Override
	public Map<String, String> getMethodNamesToDescriptions() {
		return Map.of(ModelConstants.SuperInitialisation.CreateNow.NAME.get(),
				ModelConstants.SuperInitialisation.CreateNow.SUMMARY.get());
	}
}
