package cipm.consistency.fluentapi.gen.superinit;

import java.util.Map;

import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EcorePackage;

import cipm.consistency.fluentapi.gen.FluentAPIGenerationUtil;
import cipm.consistency.fluentapi.gen.FluentAPIMethodsUtil;
import cipm.consistency.fluentapi.gen.IFluentAPIMethodGenerator;
import cipm.consistency.fluentapi.gen.ModelConstants;

public class FluentAPISuperInitialisationGetInitialisedEClassMethodGenerator implements IFluentAPIMethodGenerator {
	private static final String getInitialisedEClassMethodBody = FluentAPIMethodsUtil
			.joinLOC("return " + ModelConstants.SuperInitialisation.NewElement.NAME.thisCall()
					+ ModelConstants.SuperInitialisation.CreateNow.NAME.call() + ".eClass()");

	public EOperation generateGetInitialisedEClassMethod() {
		var op = FluentAPIGenerationUtil.generateEOperation(
				ModelConstants.SuperInitialisation.GetInitialisedEClass.NAME.get(), EcorePackage.Literals.ECLASS);
		FluentAPIGenerationUtil.addBody(op, getInitialisedEClassMethodBody);
		FluentAPIGenerationUtil.addDocumentation(op,
				ModelConstants.SuperInitialisation.GetInitialisedEClass.SUMMARY.get());
		return op;
	}

	@Override
	public Map<String, String> getMethodNamesToDescriptions() {
		return Map.of(ModelConstants.SuperInitialisation.GetInitialisedEClass.NAME.get(),
				ModelConstants.SuperInitialisation.GetInitialisedEClass.SUMMARY.get());
	}
}
