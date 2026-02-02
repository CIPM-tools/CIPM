package cipm.consistency.designtime.instrumentation2.instrumenter;

import tools.cipm.models.instrumentation.InstrumentationModel.ActionInstrumentationPoint;
import cipm.consistency.designtime.instrumentation.transformation.impl.ApplicationProjectInstrumenterNamespace;
import cipm.consistency.designtime.instrumentation2.ActionStatementMapping;
import org.emftext.language.java.instantiations.InstantiationsFactory;
import org.emftext.language.java.instantiations.NewConstructorCall;
import org.emftext.language.java.literals.LiteralsFactory;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.parameters.Parameter;
import org.emftext.language.java.references.IdentifierReference;
import org.emftext.language.java.references.MethodCall;
import org.emftext.language.java.references.Reference;
import org.emftext.language.java.references.ReferencesFactory;
import org.emftext.language.java.references.SelfReference;
import org.emftext.language.java.statements.Block;
import org.emftext.language.java.statements.ExpressionStatement;
import org.emftext.language.java.statements.LocalVariableStatement;
import org.emftext.language.java.statements.StatementsFactory;
import org.emftext.language.java.statements.TryBlock;
import org.emftext.language.java.variables.LocalVariable;
import org.emftext.language.java.variables.VariablesFactory;

/**
 * An instrumenter for services.
 * 
 * @author Martin Armbruster
 */
public class ServiceInstrumenter extends AbstractInstrumenter {
    private Method service;
    private String correspondingSeffId;

    protected ServiceInstrumenter(MinimalMonitoringEnvironmentModelGenerator gen) {
        super(gen);
    }

    protected void setService(Method m, String seffId) {
        this.service = m;
        this.correspondingSeffId = seffId;
    }

    @Override
    protected void instrument(ActionInstrumentationPoint aip, ActionStatementMapping statementMap) {
        // Create new body for the method.
        Block newBody = StatementsFactory.eINSTANCE.createBlock();

        // Create variable of ServiceParameters class.
        LocalVariableStatement serviceParameterVar = createServiceParameterVariableCreationStatement();
        newBody.getStatements()
            .add(serviceParameterVar);

        // Add arguments to ServiceParameters instance.
        for (Parameter param : service.getParameters()) {
            IdentifierReference serviceVarRef = ReferencesFactory.eINSTANCE.createIdentifierReference();
            serviceVarRef.setTarget(serviceParameterVar.getVariable());
            MethodCall addParamCall = ReferencesFactory.eINSTANCE.createMethodCall();
            addParamCall.setTarget(environmentGen.addParameterValueMethod);
            this.createAndAddStringArgument(addParamCall, param.getName());
            IdentifierReference paramRef = ReferencesFactory.eINSTANCE.createIdentifierReference();
            paramRef.setTarget(param);
            addParamCall.getArguments()
                .add(paramRef);
            serviceVarRef.setNext(addParamCall);

            ExpressionStatement varRefSt = StatementsFactory.eINSTANCE.createExpressionStatement();
            varRefSt.setExpression(serviceVarRef);
            newBody.getStatements()
                .add(varRefSt);
        }

        // Enter service statement.
        ExpressionStatement enterSt = this.createServiceEnterStatement(correspondingSeffId,
                serviceParameterVar.getVariable());
        newBody.getStatements()
            .add(enterSt);

        // Try block for original method body.
        TryBlock tryBlock = StatementsFactory.eINSTANCE.createTryBlock();
        tryBlock.setBlock(service.getBlock());

        // Exit service statement in finally.
        Block finallyBlock = StatementsFactory.eINSTANCE.createBlock();
        finallyBlock.getStatements()
            .add(createServiceExitStatement(correspondingSeffId));
        tryBlock.setFinallyBlock(finallyBlock);

        // Exchange the method body.
        newBody.getStatements()
            .add(tryBlock);
        service.setStatement(newBody);
    }

    private LocalVariableStatement createServiceParameterVariableCreationStatement() {
        LocalVariable locVar = VariablesFactory.eINSTANCE.createLocalVariable();
        locVar.setName(ApplicationProjectInstrumenterNamespace.SERVICE_PARAMETERS_VARIABLE);
        locVar.setTypeReference(this.createTypeReference(environmentGen.serviceParametersClassifier));
        NewConstructorCall init = InstantiationsFactory.eINSTANCE.createNewConstructorCall();
        init.setTypeReference(this.createTypeReference(environmentGen.serviceParametersClassifier));
        locVar.setInitialValue(init);

        LocalVariableStatement result = StatementsFactory.eINSTANCE.createLocalVariableStatement();
        result.setVariable(locVar);
        return result;
    }

    private ExpressionStatement createServiceEnterStatement(String seffId, LocalVariable serviceParam) {
        IdentifierReference rootRef = ReferencesFactory.eINSTANCE.createIdentifierReference();
        rootRef.setTarget(this.threadMonitoringVariable);
        MethodCall enterCall = ReferencesFactory.eINSTANCE.createMethodCall();
        enterCall.setTarget(environmentGen.enterServiceMethod);
        this.createAndAddStringArgument(enterCall, seffId);
        enterCall.getArguments()
            .add(this.createEnteredObjectReference());
        IdentifierReference serviceParametersRef = ReferencesFactory.eINSTANCE.createIdentifierReference();
        serviceParametersRef.setTarget(serviceParam);
        enterCall.getArguments()
            .add(serviceParametersRef);

        rootRef.setNext(enterCall);
        ExpressionStatement result = StatementsFactory.eINSTANCE.createExpressionStatement();
        result.setExpression(rootRef);
        return result;
    }

    private Reference createEnteredObjectReference() {
        if (this.service.isStatic()) {
            IdentifierReference idRef = ReferencesFactory.eINSTANCE.createIdentifierReference();
            idRef.setTarget(this.service.getContainingConcreteClassifier());
            idRef.setNext(ReferencesFactory.eINSTANCE.createReflectiveClassReference());
            return idRef;
        } else {
            SelfReference selfRef = ReferencesFactory.eINSTANCE.createSelfReference();
            selfRef.setSelf(LiteralsFactory.eINSTANCE.createThis());
            return selfRef;
        }
    }

    private ExpressionStatement createServiceExitStatement(String seffId) {
        IdentifierReference threadRef = ReferencesFactory.eINSTANCE.createIdentifierReference();
        threadRef.setTarget(this.threadMonitoringVariable);
        MethodCall exitCall = ReferencesFactory.eINSTANCE.createMethodCall();
        exitCall.setTarget(environmentGen.exitServiceMethod);
        this.createAndAddStringArgument(exitCall, seffId);
        threadRef.setNext(exitCall);

        ExpressionStatement result = StatementsFactory.eINSTANCE.createExpressionStatement();
        result.setExpression(threadRef);
        return result;
    }
}
