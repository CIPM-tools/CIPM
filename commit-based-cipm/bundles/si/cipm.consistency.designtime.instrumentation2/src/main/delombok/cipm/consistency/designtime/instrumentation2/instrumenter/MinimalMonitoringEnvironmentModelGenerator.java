package cipm.consistency.designtime.instrumentation2.instrumenter;

import cipm.consistency.designtime.instrumentation.transformation.impl.ApplicationProjectInstrumenterNamespace;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.emftext.language.java.LogicalJavaURIGenerator;
import org.emftext.language.java.classifiers.ClassifiersFactory;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.ContainersFactory;
import org.emftext.language.java.containers.Origin;
import org.emftext.language.java.literals.LiteralsFactory;
import org.emftext.language.java.members.MembersFactory;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.modifiers.ModifiersFactory;
import org.emftext.language.java.parameters.OrdinaryParameter;
import org.emftext.language.java.parameters.ParametersFactory;
import org.emftext.language.java.statements.Block;
import org.emftext.language.java.statements.Return;
import org.emftext.language.java.statements.StatementsFactory;
import org.emftext.language.java.types.ClassifierReference;
import org.emftext.language.java.types.TypeReference;
import org.emftext.language.java.types.TypesFactory;

/**
 * A generator for a model of a minimal monitoring environment.
 * 
 * @author Martin Armbruster
 */
public final class MinimalMonitoringEnvironmentModelGenerator {
    final String[] namespaces = { "cipm", "consistency", "bridge", "monitoring", "controller" };
    final String threadMonitoringControllerName = "ThreadMonitoringController";
    final String serviceParametersName = "ServiceParameters";
    final ConcreteClassifier stringClassifier;
    final ConcreteClassifier atomicIntegerClassifier;
    final ConcreteClassifier objectClassifier;
    final Method getAndIncrementMethod;
    final Method getMethod;
    public final CompilationUnit threadMonitoringControllerCU;
    final ConcreteClassifier threadMonitoringControllerClassifier;
    final Method getInstanceMethod;
    final Method enterInternalActionMethod;
    final Method exitInternalActionMethod;
    final Method setExternalCallIdMethod;
    final Method enterBranchMethod;
    final Method exitLoopMethod;
    final Method enterServiceMethod;
    final Method exitServiceMethod;
    public final CompilationUnit serviceParametersCU;
    final ConcreteClassifier serviceParametersClassifier;
    final Method addParameterValueMethod;

    public MinimalMonitoringEnvironmentModelGenerator(Resource context) {
        String[] atomicIntegerPackages = AtomicInteger.class.getPackageName()
            .split("\\" + LogicalJavaURIGenerator.PACKAGE_SEPARATOR);
        ConcreteClassifier potAtomicIntegerClassifier = findClassifierInJavaModel(context, atomicIntegerPackages,
                AtomicInteger.class.getSimpleName());
        if (potAtomicIntegerClassifier == null) {
            CompilationUnit atomicIntegerCU = ContainersFactory.eINSTANCE.createCompilationUnit();
            for (var ns : atomicIntegerPackages) {
                atomicIntegerCU.getNamespaces()
                    .add(ns);
            }
            potAtomicIntegerClassifier = ClassifiersFactory.eINSTANCE.createClass();
            atomicIntegerCU.getClassifiers()
                .add(potAtomicIntegerClassifier);
            potAtomicIntegerClassifier.setName(AtomicInteger.class.getSimpleName());
            getAndIncrementMethod = MembersFactory.eINSTANCE.createClassMethod();
            getAndIncrementMethod.setName(ApplicationProjectInstrumenterNamespace.METHOD_ATOMIC_INTEGER_INCREMENT);
            getMethod = MembersFactory.eINSTANCE.createClassMethod();
            getMethod.setName(ApplicationProjectInstrumenterNamespace.METHOD_ATOMIC_INTEGER_GET);
            potAtomicIntegerClassifier.getMembers()
                .add(getAndIncrementMethod);
            potAtomicIntegerClassifier.getMembers()
                .add(getMethod);
        } else {
            getAndIncrementMethod = findMethod(potAtomicIntegerClassifier,
                    ApplicationProjectInstrumenterNamespace.METHOD_ATOMIC_INTEGER_INCREMENT, 0);
            getMethod = findMethod(potAtomicIntegerClassifier,
                    ApplicationProjectInstrumenterNamespace.METHOD_ATOMIC_INTEGER_GET, 0);
        }
        atomicIntegerClassifier = potAtomicIntegerClassifier;

        String[] javaLang = { "java", "lang" };
        stringClassifier = findClassifierInJavaModel(context, javaLang, String.class.getSimpleName());
        objectClassifier = findClassifierInJavaModel(context, javaLang, Object.class.getSimpleName());

        threadMonitoringControllerCU = ContainersFactory.eINSTANCE.createCompilationUnit();
        threadMonitoringControllerCU.getNamespaces()
            .addAll(Arrays.asList(namespaces));
        threadMonitoringControllerCU.setName(threadMonitoringControllerName);
        threadMonitoringControllerCU.setOrigin(Origin.BINDING);
        threadMonitoringControllerClassifier = ClassifiersFactory.eINSTANCE.createClass();
        threadMonitoringControllerClassifier.setName(threadMonitoringControllerName);
        threadMonitoringControllerClassifier.makePublic();
        threadMonitoringControllerCU.getClassifiers()
            .add(threadMonitoringControllerClassifier);

        serviceParametersCU = ContainersFactory.eINSTANCE.createCompilationUnit();
        serviceParametersCU.getNamespaces()
            .addAll(Arrays.asList(namespaces));
        serviceParametersCU.setName(serviceParametersName);
        serviceParametersCU.setOrigin(Origin.BINDING);
        serviceParametersClassifier = ClassifiersFactory.eINSTANCE.createClass();
        serviceParametersClassifier.setName(serviceParametersName);
        serviceParametersClassifier.makePublic();
        serviceParametersCU.getClassifiers()
            .add(serviceParametersClassifier);

        addParameterValueMethod = MembersFactory.eINSTANCE.createClassMethod();
        addParameterValueMethod.setName(ApplicationProjectInstrumenterNamespace.METHOD_ADD_PARAMETER_VALUE);
        addParameterValueMethod.makePublic();
        addParameterValueMethod.setTypeReference(TypesFactory.eINSTANCE.createVoid());
        addParameterValueMethod.setStatement(StatementsFactory.eINSTANCE.createBlock());
        addParameterValueMethod.getParameters()
            .add(createStringParameter("param1"));
        addParameterValueMethod.getParameters()
            .add(createOrdinaryParameter("param2", createClassifierReference(objectClassifier)));
        serviceParametersClassifier.getMembers()
            .add(addParameterValueMethod);

        getInstanceMethod = MembersFactory.eINSTANCE.createClassMethod();
        getInstanceMethod.setName(ApplicationProjectInstrumenterNamespace.METHOD_GET_INSTANCE);
        getInstanceMethod.makePublic();
        getInstanceMethod.getAnnotationsAndModifiers()
            .add(ModifiersFactory.eINSTANCE.createStatic());
        ClassifierReference ref = TypesFactory.eINSTANCE.createClassifierReference();
        ref.setTarget(threadMonitoringControllerClassifier);
        getInstanceMethod.setTypeReference(ref);
        getInstanceMethod.setStatement(createNullReturningBlock());
        threadMonitoringControllerClassifier.getMembers()
            .add(getInstanceMethod);

        enterInternalActionMethod = MembersFactory.eINSTANCE.createClassMethod();
        enterInternalActionMethod.setName(ApplicationProjectInstrumenterNamespace.METHOD_ENTER_INTERNAL_ACTION);
        enterInternalActionMethod.makePublic();
        enterInternalActionMethod.setTypeReference(TypesFactory.eINSTANCE.createVoid());
        enterInternalActionMethod.setStatement(StatementsFactory.eINSTANCE.createBlock());
        enterInternalActionMethod.getParameters()
            .add(createStringParameter("param1"));
        enterInternalActionMethod.getParameters()
            .add(createStringParameter("param2"));
        threadMonitoringControllerClassifier.getMembers()
            .add(enterInternalActionMethod);

        exitInternalActionMethod = MembersFactory.eINSTANCE.createClassMethod();
        exitInternalActionMethod.setName(ApplicationProjectInstrumenterNamespace.METHOD_EXIT_INTERNAL_ACTION);
        exitInternalActionMethod.makePublic();
        exitInternalActionMethod.setTypeReference(TypesFactory.eINSTANCE.createVoid());
        exitInternalActionMethod.setStatement(StatementsFactory.eINSTANCE.createBlock());
        exitInternalActionMethod.getParameters()
            .add(createStringParameter("param1"));
        exitInternalActionMethod.getParameters()
            .add(createStringParameter("param2"));
        threadMonitoringControllerClassifier.getMembers()
            .add(exitInternalActionMethod);

        setExternalCallIdMethod = MembersFactory.eINSTANCE.createClassMethod();
        setExternalCallIdMethod.setName(ApplicationProjectInstrumenterNamespace.METHOD_BEFORE_EXTERNAL_CALL);
        setExternalCallIdMethod.makePublic();
        setExternalCallIdMethod.setTypeReference(TypesFactory.eINSTANCE.createVoid());
        setExternalCallIdMethod.setStatement(StatementsFactory.eINSTANCE.createBlock());
        setExternalCallIdMethod.getParameters()
            .add(createStringParameter("param1"));
        threadMonitoringControllerClassifier.getMembers()
            .add(setExternalCallIdMethod);

        enterBranchMethod = MembersFactory.eINSTANCE.createClassMethod();
        enterBranchMethod.setName(ApplicationProjectInstrumenterNamespace.METHOD_ENTER_BRANCH);
        enterBranchMethod.makePublic();
        enterBranchMethod.setTypeReference(TypesFactory.eINSTANCE.createVoid());
        enterBranchMethod.setStatement(StatementsFactory.eINSTANCE.createBlock());
        enterBranchMethod.getParameters()
            .add(createStringParameter("param1"));
        threadMonitoringControllerClassifier.getMembers()
            .add(enterBranchMethod);

        exitLoopMethod = MembersFactory.eINSTANCE.createClassMethod();
        exitLoopMethod.setName(ApplicationProjectInstrumenterNamespace.METHOD_EXIT_LOOP);
        exitLoopMethod.makePublic();
        exitLoopMethod.setTypeReference(TypesFactory.eINSTANCE.createVoid());
        exitLoopMethod.setStatement(StatementsFactory.eINSTANCE.createBlock());
        exitLoopMethod.getParameters()
            .add(createStringParameter("param1"));
        exitLoopMethod.getParameters()
            .add(createOrdinaryParameter("param2", TypesFactory.eINSTANCE.createLong()));
        threadMonitoringControllerClassifier.getMembers()
            .add(exitLoopMethod);

        enterServiceMethod = MembersFactory.eINSTANCE.createClassMethod();
        enterServiceMethod.setName(ApplicationProjectInstrumenterNamespace.METHOD_ENTER_SERVICE);
        enterServiceMethod.makePublic();
        enterServiceMethod.getAnnotationsAndModifiers()
            .add(ModifiersFactory.eINSTANCE.createSynchronized());
        enterServiceMethod.setTypeReference(TypesFactory.eINSTANCE.createVoid());
        enterServiceMethod.setStatement(StatementsFactory.eINSTANCE.createBlock());
        enterServiceMethod.getParameters()
            .add(createStringParameter("param1"));
        enterServiceMethod.getParameters()
            .add(createOrdinaryParameter("param2", createClassifierReference(objectClassifier)));
        enterServiceMethod.getParameters()
            .add(createOrdinaryParameter("param3", createClassifierReference(serviceParametersClassifier)));
        threadMonitoringControllerClassifier.getMembers()
            .add(enterServiceMethod);

        exitServiceMethod = MembersFactory.eINSTANCE.createClassMethod();
        exitServiceMethod.setName(ApplicationProjectInstrumenterNamespace.METHOD_EXIT_SERVICE);
        exitServiceMethod.makePublic();
        exitServiceMethod.getAnnotationsAndModifiers()
            .add(ModifiersFactory.eINSTANCE.createSynchronized());
        exitServiceMethod.setTypeReference(TypesFactory.eINSTANCE.createVoid());
        exitServiceMethod.setStatement(StatementsFactory.eINSTANCE.createBlock());
        exitServiceMethod.getParameters()
            .add(createStringParameter("param1"));
        threadMonitoringControllerClassifier.getMembers()
            .add(exitServiceMethod);
    }

    private ConcreteClassifier findClassifierInJavaModel(Resource javaModel, String[] packageParts, String className) {
        for (EObject c : javaModel.getContents()) {
            if (c instanceof CompilationUnit) {
                var cu = (CompilationUnit) c;
                outerIf: if (cu.getName() != null && cu.getName()
                    .equals(className)
                        && cu.getNamespaces()
                            .size() == packageParts.length) {
                    for (int idx = 0; idx < packageParts.length; idx++) {
                        if (!packageParts[idx].equals(cu.getNamespaces()
                            .get(idx))) {
                            break outerIf;
                        }
                    }
                    return cu.getClassifiers()
                        .get(0);
                }
            }
        }
        return null;
    }

    private Method findMethod(ConcreteClassifier classifier, String methName, int paramNumber) {
        return classifier.getMembers()
            .stream()
            .filter(m -> m instanceof Method && m.getName()
                .equals(methName))
            .map(m -> (Method) m)
            .filter(m -> m.getParameters()
                .size() == paramNumber)
            .findFirst()
            .get();
    }

    private Block createNullReturningBlock() {
        Block body = StatementsFactory.eINSTANCE.createBlock();
        Return state = StatementsFactory.eINSTANCE.createReturn();
        state.setReturnValue(LiteralsFactory.eINSTANCE.createNullLiteral());
        body.getStatements()
            .add(state);
        return body;
    }

    private OrdinaryParameter createStringParameter(String paramName) {
        return createOrdinaryParameter(paramName, createClassifierReference(stringClassifier));
    }

    private OrdinaryParameter createOrdinaryParameter(String paramName, TypeReference type) {
        OrdinaryParameter param = ParametersFactory.eINSTANCE.createOrdinaryParameter();
        param.setName(paramName);
        param.setTypeReference(type);
        return param;
    }

    private ClassifierReference createClassifierReference(ConcreteClassifier classifier) {
        ClassifierReference ref = TypesFactory.eINSTANCE.createClassifierReference();
        ref.setTarget(classifier);
        return ref;
    }
}
