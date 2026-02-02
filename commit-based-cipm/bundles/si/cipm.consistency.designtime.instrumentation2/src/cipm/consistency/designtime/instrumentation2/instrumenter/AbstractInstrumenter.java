package cipm.consistency.designtime.instrumentation2.instrumenter;

import tools.cipm.models.instrumentation.InstrumentationModel.ActionInstrumentationPoint;
import cipm.consistency.designtime.instrumentation2.ActionStatementMapping;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.references.MethodCall;
import org.emftext.language.java.references.ReferencesFactory;
import org.emftext.language.java.references.StringReference;
import org.emftext.language.java.types.ClassifierReference;
import org.emftext.language.java.types.NamespaceClassifierReference;
import org.emftext.language.java.types.TypesFactory;
import org.emftext.language.java.variables.LocalVariable;

/**
 * An abstract instrumenter.
 * 
 * @author Martin Armbruster
 */
public abstract class AbstractInstrumenter {
    protected MinimalMonitoringEnvironmentModelGenerator environmentGen;
    protected LocalVariable threadMonitoringVariable;

    protected AbstractInstrumenter(MinimalMonitoringEnvironmentModelGenerator gen) {
        this.environmentGen = gen;
    }

    protected void setLocalThreadMonitoringVariable(LocalVariable monitorVar) {
        this.threadMonitoringVariable = monitorVar;
    }

    protected void createAndAddStringArgument(MethodCall call, String s) {
        StringReference ref = ReferencesFactory.eINSTANCE.createStringReference();
        ref.setValue(s);
        call.getArguments()
            .add(ref);
    }

    protected NamespaceClassifierReference createTypeReference(ConcreteClassifier classifier) {
        NamespaceClassifierReference result = TypesFactory.eINSTANCE.createNamespaceClassifierReference();
        result.getNamespaces()
            .addAll(classifier.getContainingCompilationUnit()
                .getNamespaces());
        ClassifierReference actualRef = TypesFactory.eINSTANCE.createClassifierReference();
        actualRef.setTarget(classifier);
        result.getClassifierReferences()
            .add(actualRef);
        return result;
    }

    /**
     * Instruments an AbstractAction.
     * 
     * @param aip
     *            provides the AbstractAction for the instrumentation.
     * @param statementMapping
     *            a mapping to retrieve the corresponding statements of the AbstractAction.
     */
    protected abstract void instrument(ActionInstrumentationPoint aip, ActionStatementMapping statementMapping);
}
