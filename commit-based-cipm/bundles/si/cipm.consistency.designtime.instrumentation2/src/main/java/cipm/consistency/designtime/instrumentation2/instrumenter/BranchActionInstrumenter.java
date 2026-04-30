package cipm.consistency.designtime.instrumentation2.instrumenter;

import tools.cipm.models.instrumentation.InstrumentationModel.ActionInstrumentationPoint;
import cipm.consistency.designtime.instrumentation2.ActionStatementMapping;
import org.emftext.language.java.references.IdentifierReference;
import org.emftext.language.java.references.MethodCall;
import org.emftext.language.java.references.ReferencesFactory;
import org.emftext.language.java.statements.Block;
import org.emftext.language.java.statements.ExpressionStatement;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementContainer;
import org.emftext.language.java.statements.StatementsFactory;

/**
 * An instrumenter for BranchActions.
 * 
 * @author Martin Armbruster
 */
public class BranchActionInstrumenter extends AbstractInstrumenter {
    protected BranchActionInstrumenter(MinimalMonitoringEnvironmentModelGenerator gen) {
        super(gen);
    }

    @Override
    protected void instrument(ActionInstrumentationPoint aip, ActionStatementMapping statementMapping) {
        Statement branchSt = statementMapping.get(aip.getAction());
        String transitionId = aip.getAction()
            .getId();
        if (branchSt instanceof StatementContainer && ((StatementContainer) branchSt).getStatement() instanceof Block) {
            // Enter statement at the beginning of the inner statements.
            ExpressionStatement enterSt = StatementsFactory.eINSTANCE.createExpressionStatement();
            IdentifierReference idRef = ReferencesFactory.eINSTANCE.createIdentifierReference();
            idRef.setTarget(this.threadMonitoringVariable);
            MethodCall enterCall = ReferencesFactory.eINSTANCE.createMethodCall();
            enterCall.setTarget(environmentGen.enterBranchMethod);
            this.createAndAddStringArgument(enterCall, transitionId);

            idRef.setNext(enterCall);
            enterSt.setExpression(idRef);
            ((Block) ((StatementContainer) branchSt).getStatement()).getStatements()
                .add(0, enterSt);
        }
    }
}
