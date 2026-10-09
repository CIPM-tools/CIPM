package cipm.consistency.fluentapi.java.builder;

import java.util.ArrayList;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelFactory;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;

import cipm.consistency.fluentapi.builder.FluentAPIAbstractBuilder;
import cipm.consistency.fluentapi.gen.FluentAPIGenerationContext;
import cipm.consistency.fluentapi.gen.FluentAPIGenerator;
import cipm.consistency.fluentapi.java.metamodel.FluentAPIJavaMetamodelFilter;
import cipm.consistency.fluentapi.java.metamodel.FluentAPIJavaMetamodelPackageProvider;
import cipm.consistency.fluentapi.java.postprocessor.FluentAPIGenerationJavaMetamodelPostProcessor;
import cipm.consistency.fluentapi.metamodel.FluentAPITargetMetamodelFilter;
import cipm.consistency.fluentapi.metamodel.FluentAPITargetMetamodelPackageProvider;
import cipm.consistency.fluentapi.postprocessor.FluentAPIGenerationBigNumberParameterPostProcessor;
import cipm.consistency.fluentapi.postprocessor.FluentAPIGenerationForEachOverloadPostProcessor;
import cipm.consistency.fluentapi.postprocessor.FluentAPIGenerationMultipleValueParameterSameMethodBodyOverloadPostProcessor;

/**
 * An implementation of {@link FluentAPIAbstractBuilder} for the JaMoPP
 * metamodel.
 * 
 * @author Alp Torac Genc
 */
public class FluentJavaAPIBuilder extends FluentAPIAbstractBuilder {
	private static final FluentAPITargetMetamodelPackageProvider provider = new FluentAPIJavaMetamodelPackageProvider();
	private static final FluentAPITargetMetamodelFilter filter = new FluentAPIJavaMetamodelFilter();

	@Override
	protected GenModel generateGenModel(Resource genModelRes, Resource ecoreRes, FluentAPIGenerationContext context) {
		var genModel = GenModelFactory.eINSTANCE.createGenModel();
		genModelRes.getContents().add(genModel);

		genModel.setModelDirectory("/" + getGeneratedFluentAPIModelDirectoryPath().toString());
		genModel.setOperationReflection(true);
		genModel.setImportOrganizing(true);
		genModel.setComplianceLevel(getJDKVersion());
		genModel.setModelName(getModelName());
		genModel.setModelPluginID(getModelPluginID());
		genModel.getForeignModel().add(ecoreRes.getURI().lastSegment());

		var targetMetamodelGenModel = provider.getTargetMetamodelGenModels().get(0);
		genModel.getUsedGenPackages().addAll(targetMetamodelGenModel.getGenPackages());

		var initEPacs = new ArrayList<EPackage>();
		var toGen = (EPackage) ecoreRes.getContents().get(0);

		initEPacs.add(toGen);
		genModel.initialize(initEPacs);

		genModel.reconcile();

		genModel.setCanGenerate(true);

		var apiGenPac = genModel.findGenPackage(toGen);
		apiGenPac.setBasePackage(context.getBasePackageName());

		return genModel;
	}

	@Override
	protected void generateEcoreModel(Resource ecoreRes, FluentAPIGenerationContext context) {
		new FluentAPIGenerator().generateRootAPIPackages(context);
		new FluentAPIGenerationJavaMetamodelPostProcessor(context.getAllInitEClss(), provider).apply();
		new FluentAPIGenerationBigNumberParameterPostProcessor(context.getAllInitEClss()).apply();

		var allEClss = new ArrayList<EClass>();
		allEClss.add(context.getFluentAPIECls());
		allEClss.add(context.getInitSuperECls());
		allEClss.addAll(context.getAllInitEClss());

		new FluentAPIGenerationForEachOverloadPostProcessor(context, allEClss).apply();
		new FluentAPIGenerationMultipleValueParameterSameMethodBodyOverloadPostProcessor(context, allEClss).apply();

		ecoreRes.getContents().add(context.getRootPackage());
	}

	@Override
	protected FluentAPITargetMetamodelPackageProvider getTargetMetamodelPackageProvider() {
		return provider;
	}

	@Override
	protected FluentAPITargetMetamodelFilter getTargetMetamodelFilter() {
		return filter;
	}
}
