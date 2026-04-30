package cipm.consistency.designtime.instrumentation2.instrumenter;

import tools.cipm.models.instrumentation.InstrumentationModel.ActionInstrumentationPoint;
import cipm.consistency.designtime.instrumentation2.ActionStatementMapping;
import org.emftext.language.java.references.IdentifierReference;
import org.emftext.language.java.references.MethodCall;
import org.emftext.language.java.references.ReferencesFactory;
import org.emftext.language.java.statements.ExpressionStatement;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementsFactory;

/**
 * An instrumenter for ExternalCallActions.
 * 
 * @author Martin Armbruster
 */
public class ExternalCallActionInstrumenter extends AbstractInstrumenter {
    protected ExternalCallActionInstrumenter(MinimalMonitoringEnvironmentModelGenerator gen) {
        super(gen);
    }

    @Override
    protected void instrument(ActionInstrumentationPoint aip, ActionStatementMapping statementMap) {
        // Statement for setting the external call id.
        Statement callStatement = statementMap.get(aip.getAction());
        String externalCallId = aip.getAction()
            .getId();
        ExpressionStatement enterSt = StatementsFactory.eINSTANCE.createExpressionStatement();
        IdentifierReference objRef = ReferencesFactory.eINSTANCE.createIdentifierReference();
        objRef.setTarget(this.threadMonitoringVariable);
        MethodCall enterCall = ReferencesFactory.eINSTANCE.createMethodCall();
        enterCall.setTarget(environmentGen.setExternalCallIdMethod);
        createAndAddStringArgument(enterCall, externalCallId);

        objRef.setNext(enterCall);
        enterSt.setExpression(objRef);
        callStatement.addBeforeContainingStatement(enterSt);
    }
}
