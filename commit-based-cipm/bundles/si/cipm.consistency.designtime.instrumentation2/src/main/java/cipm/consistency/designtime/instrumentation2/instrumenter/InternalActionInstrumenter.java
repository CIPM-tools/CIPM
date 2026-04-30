package cipm.consistency.designtime.instrumentation2.instrumenter;

import tools.cipm.models.instrumentation.InstrumentationModel.ActionInstrumentationPoint;
import cipm.consistency.designtime.instrumentation.transformation.impl.ApplicationProjectInstrumenterNamespace;
import cipm.consistency.designtime.instrumentation2.ActionStatementMapping;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.expressions.LambdaExpression;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.references.IdentifierReference;
import org.emftext.language.java.references.MethodCall;
import org.emftext.language.java.references.ReferencesFactory;
import org.emftext.language.java.statements.Block;
import org.emftext.language.java.statements.Condition;
import org.emftext.language.java.statements.ExpressionStatement;
import org.emftext.language.java.statements.LocalVariableStatement;
import org.emftext.language.java.statements.Return;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementsFactory;
import org.emftext.language.java.statements.Throw;
import org.emftext.language.java.variables.LocalVariable;
import org.emftext.language.java.variables.VariablesFactory;

/**
 * An instrumenter for InternalActions.
 * 
 * @author Martin Armbruster
 */
public class InternalActionInstrumenter extends AbstractInstrumenter {
    protected InternalActionInstrumenter(MinimalMonitoringEnvironmentModelGenerator gen) {
        super(gen);
    }

    @Override
    protected void instrument(ActionInstrumentationPoint aip, ActionStatementMapping statementMap) {
        Statement start = statementMap.get(aip.getAction());
        Statement end = statementMap.getAbstractActionToLastStatementMapping()
            .get(aip.getAction());
        String correspondingInternalActionId = aip.getAction()
            .getId();

        // Entry.
        IdentifierReference objRef = ReferencesFactory.eINSTANCE.createIdentifierReference();
        objRef.setTarget(this.threadMonitoringVariable);
        MethodCall entryCall = ReferencesFactory.eINSTANCE.createMethodCall();
        objRef.setNext(entryCall);
        createArguments(entryCall, correspondingInternalActionId);
        entryCall.setTarget(environmentGen.enterInternalActionMethod);

        ExpressionStatement entryStatement = StatementsFactory.eINSTANCE.createExpressionStatement();
        entryStatement.setExpression(objRef);
        start.addBeforeContainingStatement(entryStatement);

        // Exit.
        objRef = ReferencesFactory.eINSTANCE.createIdentifierReference();
        objRef.setTarget(this.threadMonitoringVariable);
        MethodCall exitCall = ReferencesFactory.eINSTANCE.createMethodCall();
        objRef.setNext(exitCall);
        createArguments(exitCall, correspondingInternalActionId);
        exitCall.setTarget(environmentGen.exitInternalActionMethod);

        ExpressionStatement exitStatement = StatementsFactory.eINSTANCE.createExpressionStatement();
        exitStatement.setExpression(objRef);

        // Check if last statement is or contains return statement.
        if (end instanceof Return) {
            Return ret = (Return) end;
            addExitStatementOnReturn(ret, exitStatement);
        } else if (end instanceof Throw) {
            end.addBeforeContainingStatement(exitStatement);
        } else {
            var retList = end.getChildrenByType(Return.class);
            if (retList.size() != 0) {
                for (Return ret : retList) {
                    // Case: if (condition) return -1;
                    // Needs refactoring to: if (condition) { return -1; }
                    if (ret.eContainer() instanceof Condition) {
                        Condition parent = (Condition) ret.eContainer();
                        Block newParent = StatementsFactory.eINSTANCE.createBlock();
                        newParent.setName("");
                        newParent.getStatements()
                            .add(ret);
                        if (parent.getStatement() == null) {
                            parent.setStatement(newParent);
                        } else {
                            parent.setElseStatement(newParent);
                        }
                    }
                    addExitStatementOnReturn(ret, EcoreUtil.copy(exitStatement));
                }
            } else {
                end.addAfterContainingStatement(exitStatement);
            }
        }
    }

    private void createArguments(MethodCall call, String correspondingInternalActionId) {
        createAndAddStringArgument(call, correspondingInternalActionId);
        createAndAddStringArgument(call, ApplicationProjectInstrumenterNamespace.RESOURCE_ID_CPU);
    }

    private Method findMethod(EObject start) {
        EObject parent = start.eContainer();
        while (!(parent instanceof Method)) {
            parent = parent.eContainer();
        }
        return (Method) parent;
    }

    private void addExitStatementOnReturn(Return ret, Statement exitStatement) {
        if (ret.getReturnValue() != null) {
            // Check that return statement is not contained within a LambdaExpression.
            // If this is the case, the exit statement generation is surpressed.
            EObject container = ret.eContainer();
            while (container != null && !(container instanceof Method)) {
                if (container instanceof LambdaExpression) {
                    return;
                }
                container = container.eContainer();
            }

            LocalVariable returnVariable = VariablesFactory.eINSTANCE.createLocalVariable();
            returnVariable.setTypeReference(EcoreUtil.copy(findMethod(ret).getTypeReference()));
            returnVariable
                .setName("longAndUniqueNameToAvoidDuplicationsAndCompilationErrors" + System.currentTimeMillis()
                        + Double.toString(Math.random())
                            .replace('.', '0')
                            .replace('-', '0'));
            returnVariable.setInitialValue(ret.getReturnValue());
            LocalVariableStatement retVarStat = StatementsFactory.eINSTANCE.createLocalVariableStatement();
            retVarStat.setVariable(returnVariable);

            IdentifierReference idRef = ReferencesFactory.eINSTANCE.createIdentifierReference();
            idRef.setTarget(returnVariable);
            ret.setReturnValue(idRef);

            ret.addBeforeContainingStatement(retVarStat);
            retVarStat.addAfterContainingStatement(exitStatement);
        } else {
            ret.addBeforeContainingStatement(exitStatement);
        }
    }
}
