package cipm.consistency.designtime.instrumentation2.instrumenter;

import tools.cipm.models.instrumentation.InstrumentationModel.ActionInstrumentationPoint;
import cipm.consistency.designtime.instrumentation.transformation.impl.ApplicationProjectInstrumenterNamespace;
import cipm.consistency.designtime.instrumentation2.ActionStatementMapping;
import java.math.BigInteger;
import java.util.HashMap;
import org.emftext.language.java.instantiations.InstantiationsFactory;
import org.emftext.language.java.instantiations.NewConstructorCall;
import org.emftext.language.java.literals.DecimalIntegerLiteral;
import org.emftext.language.java.literals.LiteralsFactory;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.references.IdentifierReference;
import org.emftext.language.java.references.MethodCall;
import org.emftext.language.java.references.ReferencesFactory;
import org.emftext.language.java.statements.Block;
import org.emftext.language.java.statements.ExpressionStatement;
import org.emftext.language.java.statements.LocalVariableStatement;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementContainer;
import org.emftext.language.java.statements.StatementsFactory;
import org.emftext.language.java.variables.LocalVariable;
import org.emftext.language.java.variables.VariablesFactory;

/**
 * An instrumenter for LoopActions.
 * 
 * @author Martin Armbruster
 */
public class LoopActionInstrumenter extends AbstractInstrumenter {
    protected LoopActionInstrumenter(MinimalMonitoringEnvironmentModelGenerator gen) {
        super(gen);
    }

    private HashMap<Method, Integer> introducedCounters = new HashMap<>();

    @Override
    protected void instrument(ActionInstrumentationPoint aip, ActionStatementMapping statementMap) {
        Statement loopSt = statementMap.get(aip.getAction());
        String loopId = aip.getAction()
            .getId();

        // Create counter name.
        Method parent = loopSt.getParentByType(Method.class);
        int currentCounterId = 1;
        if (introducedCounters.containsKey(parent)) {
            currentCounterId = introducedCounters.get(parent);
        }
        introducedCounters.put(parent, currentCounterId + 1);
        String counterName = ApplicationProjectInstrumenterNamespace.COUNTER_VARIABLE + currentCounterId;

        // Counter declaration.
        LocalVariableStatement declStatement = createCounterDeclaration(counterName);
        loopSt.addBeforeContainingStatement(declStatement);

        Block loopBlock = findLoopBlock(loopSt);
        if (loopBlock != null) {
            // Increment counter.
            IdentifierReference counterRef = ReferencesFactory.eINSTANCE.createIdentifierReference();
            counterRef.setTarget(declStatement.getVariable());
            MethodCall incrementCall = ReferencesFactory.eINSTANCE.createMethodCall();
            incrementCall.setTarget(environmentGen.getAndIncrementMethod);
            counterRef.setNext(incrementCall);

            ExpressionStatement incSt = StatementsFactory.eINSTANCE.createExpressionStatement();
            incSt.setExpression(counterRef);
            loopBlock.getStatements()
                .add(0, incSt);
        }

        // Log statement for loop exit.
        Statement exit = createLogExitStatement(loopId, declStatement.getVariable());
        loopSt.addAfterContainingStatement(exit);
    }

    private LocalVariableStatement createCounterDeclaration(String counterName) {
        LocalVariableStatement declStatement = StatementsFactory.eINSTANCE.createLocalVariableStatement();
        LocalVariable locVar = VariablesFactory.eINSTANCE.createLocalVariable();
        locVar.setName(counterName);
        locVar.setTypeReference(this.createTypeReference(environmentGen.atomicIntegerClassifier));

        NewConstructorCall locVarInit = InstantiationsFactory.eINSTANCE.createNewConstructorCall();
        locVarInit.setTypeReference(this.createTypeReference(environmentGen.atomicIntegerClassifier));
        DecimalIntegerLiteral literal = LiteralsFactory.eINSTANCE.createDecimalIntegerLiteral();
        literal.setDecimalValue(BigInteger.ZERO);
        locVarInit.getArguments()
            .add(literal);
        locVar.setInitialValue(locVarInit);

        declStatement.setVariable(locVar);
        return declStatement;
    }

    private Block findLoopBlock(Statement loopSt) {
        if (loopSt instanceof StatementContainer && ((StatementContainer) loopSt).getStatement() instanceof Block) {
            return (Block) ((StatementContainer) loopSt).getStatement();
        }
        return null;
    }

    private Statement createLogExitStatement(String loopId, LocalVariable counterVar) {
        IdentifierReference threadRef = ReferencesFactory.eINSTANCE.createIdentifierReference();
        threadRef.setTarget(this.threadMonitoringVariable);
        MethodCall threadCall = ReferencesFactory.eINSTANCE.createMethodCall();
        threadCall.setTarget(environmentGen.exitLoopMethod);
        this.createAndAddStringArgument(threadCall, loopId);

        IdentifierReference secArg = ReferencesFactory.eINSTANCE.createIdentifierReference();
        secArg.setTarget(counterVar);
        MethodCall callToGet = ReferencesFactory.eINSTANCE.createMethodCall();
        callToGet.setTarget(environmentGen.getMethod);
        secArg.setNext(callToGet);

        threadCall.getArguments()
            .add(secArg);
        threadRef.setNext(threadCall);
        ExpressionStatement result = StatementsFactory.eINSTANCE.createExpressionStatement();
        result.setExpression(threadRef);
        return result;
    }
}
