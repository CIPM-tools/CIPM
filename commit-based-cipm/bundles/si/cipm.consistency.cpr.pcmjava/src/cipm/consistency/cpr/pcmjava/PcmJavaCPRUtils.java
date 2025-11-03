package cipm.consistency.cpr.pcmjava;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.classifiers.Implementor;
import org.emftext.language.java.classifiers.Interface;
import org.emftext.language.java.commons.Commentable;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.ContainersFactory;
import org.emftext.language.java.containers.JavaRoot;
import org.emftext.language.java.containers.Origin;
import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.literals.Literal;
import org.emftext.language.java.literals.LiteralsFactory;
import org.emftext.language.java.members.ExceptionThrower;
import org.emftext.language.java.members.InterfaceMethod;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.modifiers.Abstract;
import org.emftext.language.java.modifiers.AnnotableAndModifiable;
import org.emftext.language.java.modifiers.Default;
import org.emftext.language.java.parameters.Parametrizable;
import org.emftext.language.java.statements.LocalVariableStatement;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementContainer;
import org.emftext.language.java.statements.StatementListContainer;
import org.emftext.language.java.statements.StatementsFactory;
import org.emftext.language.java.types.PrimitiveType;
import org.emftext.language.java.types.TypeReference;
import org.emftext.language.java.types.TypesFactory;
import org.palladiosimulator.pcm.repository.OperationSignature;

import com.google.common.base.Preconditions;

public final class PcmJavaCPRUtils {
	private static final String abstractModifierName = Abstract.class.getSimpleName();
	private static final String defaultModifierName = Default.class.getSimpleName();

	public static List<LocalVariableStatement> getLocalVariableStatements(Statement st) {
		if (st instanceof LocalVariableStatement) {
			return List.of((LocalVariableStatement) st);
		}
		return List.of();
	}

	public static List<LocalVariableStatement> getLocalVariableStatements(StatementContainer sc) {
		return getLocalVariableStatements(sc.getStatement());
	}

	public static List<LocalVariableStatement> getLocalVariableStatements(StatementListContainer slc) {
		return getLocalVariableStatements(slc.getStatements());
	}

	public static List<LocalVariableStatement> getLocalVariableStatements(List<Statement> statementList) {
		var localVars = new ArrayList<LocalVariableStatement>();
		statementList.forEach((s) -> localVars.addAll(getLocalVariableStatements(s)));
		return localVars;
	}

	public static List<LocalVariableStatement> getAllLocalVariableStatements(Statement st) {
		var localVars = new ArrayList<LocalVariableStatement>();
		if (st instanceof LocalVariableStatement) {
			localVars.add((LocalVariableStatement) st);
		}
		if (st instanceof StatementContainer) {
			localVars.addAll(getAllLocalVariableStatements(((StatementContainer) st).getStatement()));
		}
		if (st instanceof StatementListContainer) {
			localVars.addAll(getAllLocalVariableStatements(((StatementListContainer) st).getStatements()));
		}
		return localVars;
	}

	public static List<LocalVariableStatement> getAllLocalVariableStatements(List<Statement> statementList) {
		var localVars = new ArrayList<LocalVariableStatement>();
		statementList.forEach((s) -> localVars.addAll(getAllLocalVariableStatements(s)));
		return localVars;
	}

	public static List<org.emftext.language.java.members.Method> getAllNonStaticMethodsOf(ConcreteClassifier javaCls) {
		return javaCls.getMethods().stream().filter((m) -> !m.isStatic())
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public static List<org.emftext.language.java.members.Method> getAllNonStaticNonAbstractMethodsOf(
			ConcreteClassifier javaCls) {
		return javaCls.getMethods().stream().filter((m) -> !m.isStatic() && !isJavaElementAbstract(m))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public static List<org.emftext.language.java.members.Method> getAllJavaMethodsRequiringImplementation(
			ConcreteClassifier javaCls) {
		return javaCls.getMethods().stream().filter((m) -> doesJavaMethodRequireImplementation(m))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public static boolean doesJavaMethodRequireImplementation(org.emftext.language.java.members.Method javaMet) {
		return !javaMet.isStatic() && (isJavaElementAbstract(javaMet)
				|| (javaMet instanceof InterfaceMethod && !isJavaElementDefault(javaMet)));
	}

	public static TypeReference implementJavaInterfaceInJavaClassifier(Implementor javaCls, Interface javaIfc) {
		if (javaCls.getImplements().stream().anyMatch((tr) -> tr.getPureClassifierReference().getTarget() == javaIfc))
			return null;

		var implementsRef = TypesFactory.eINSTANCE.createClassifierReference();
		implementsRef.setTarget(javaIfc);
		javaCls.getImplements().add(implementsRef);

		return implementsRef;
	}

	public static List<org.emftext.language.java.members.Method> generateJavaInterfaceMethodStubsInJavaClassifier(
			Implementor javaCls, Interface javaIfc) {
		var methodStubs = new ArrayList<org.emftext.language.java.members.Method>();

		/*
		 * Note: Even if javaCls is abstract, javaIfc method stubs will be added to it
		 * instead of each sub class of javaCls
		 */

		// All Implementor instances are actually also ConcreteClassifier instances
		// since concrete Implementors are either Class or Enumeration
		var castedJavaCls = (ConcreteClassifier) javaCls;
		var javaIfcMets = getAllJavaMethodsRequiringImplementation(javaIfc);
		for (var javaIfcMet : javaIfcMets) {
			if (!doesJavaClassifierImplementMethod(castedJavaCls, javaIfcMet)) {
				var metStub = getJavaMethodStubFor(javaIfcMet);
				methodStubs.add(metStub);
				castedJavaCls.getMembers().add(metStub);
			}
		}

		return methodStubs;
	}

	public static Literal getJavaParameterStubFor(TypeReference tref) {
		Literal literalStub = null;
		var returnType = tref.getTarget();
		var returnTypeCls = returnType.getClass();
		if (PrimitiveType.class.isAssignableFrom(returnTypeCls)) {
			if (org.emftext.language.java.types.Boolean.class.equals(returnTypeCls)) {
				var boolLit = LiteralsFactory.eINSTANCE.createBooleanLiteral();
				boolLit.setValue(false);
				literalStub = boolLit;
			} else if (org.emftext.language.java.types.Void.class.equals(returnTypeCls)) {
				literalStub = null;
			} else if (org.emftext.language.java.types.Char.class.equals(returnTypeCls)) {
				var charLit = LiteralsFactory.eINSTANCE.createCharacterLiteral();
				charLit.setValue("");
				literalStub = charLit;
			} else {
				var numLit = LiteralsFactory.eINSTANCE.createDecimalIntegerLiteral();
				numLit.setDecimalValue(BigInteger.ZERO);
				literalStub = numLit;
			}
		} else {
			var nullLit = LiteralsFactory.eINSTANCE.createNullLiteral();
			literalStub = nullLit;
		}
		return literalStub;
	}

	public static org.emftext.language.java.members.Method getJavaMethodStubFor(
			org.emftext.language.java.members.Method javaMetToImplement) {
		var stub = EcoreUtil.copy(javaMetToImplement);

		// stub.getStatement(): The block of the method
		// stub.getStatements(): Individual statements in method body
		//
		// Ensure that the method stub has a block, otherwise inserting
		// statements will not work (EMFText limitation)
		if (stub.getBlock() == null)
			stub.setStatement(StatementsFactory.eINSTANCE.createBlock());

		stub.getStatements().clear();

		var returnSt = StatementsFactory.eINSTANCE.createReturn();
		Expression returnVal = null;
		var returnType = javaMetToImplement.getTypeReference();

		returnVal = getJavaParameterStubFor(returnType);
		if (returnVal != null) {
			returnSt.setReturnValue(returnVal);
			stub.getStatements().add(returnSt);
		}

		return stub;
	}

	public static boolean doesJavaClassifierImplementMethod(ConcreteClassifier javaCls,
			org.emftext.language.java.members.Method met) {
		return getAllNonStaticMethodsOf(javaCls).stream().anyMatch((jcm) -> jcm.isSignatureMatching(met));
	}

	public static boolean areJavaReturnTypesMatching(org.emftext.language.java.members.Method implementingMet,
			org.emftext.language.java.members.Method metToImplement) {
		var rt1 = implementingMet.getTypeReference().getPureClassifierReference().getTarget();
		var rt2 = metToImplement.getTypeReference().getPureClassifierReference().getTarget();

		return EcoreUtil.equals(rt1, rt2)
				|| rt2.getAllSuperClassifiers().stream().anyMatch((sc) -> EcoreUtil.equals(rt1, sc));
	}

	public static boolean areJavaExceptionsEqual(ExceptionThrower et1, ExceptionThrower et2) {
		var excs1 = et1.getExceptions();
		var excs2 = et2.getExceptions();

		if (excs1.size() != excs2.size())
			return false;

		for (int i = 0; i < excs1.size(); i++) {
			if (!EcoreUtil.equals(excs1.get(i), excs2.get(i)))
				return false;
		}

		return true;
	}

	public static boolean areJavaModifiersEqual(AnnotableAndModifiable aam1, AnnotableAndModifiable aam2) {
		var mods1 = aam1.getModifiers();
		var mods2 = aam2.getModifiers();

		if (mods1.size() != mods2.size())
			return false;

		for (int i = 0; i < mods1.size(); i++) {
			if (!EcoreUtil.equals(mods1.get(i), mods2.get(i)))
				return false;
		}

		return true;
	}

	public static boolean areJavaParametersEqual(Parametrizable p1, Parametrizable p2) {
		var params1 = p1.getParameters();
		var params2 = p2.getParameters();

		if (params1.size() != params2.size())
			return false;

		for (int i = 0; i < params1.size(); i++) {
			if (!EcoreUtil.equals(params1.get(i), params2.get(i)))
				return false;
		}

		return true;
	}

	public static boolean isJavaElementAbstract(AnnotableAndModifiable javaElem) {
		return javaElem.getModifiers().stream()
				.anyMatch((m) -> m.getClass().getSimpleName().equals(abstractModifierName));
	}

	public static boolean isJavaElementDefault(AnnotableAndModifiable javaElem) {
		return javaElem.getModifiers().stream()
				.anyMatch((m) -> m.getClass().getSimpleName().equals(defaultModifierName));
	}

	/**
	 * Adds correspondences between PCM OperationSignature and Java Method. Assumes
	 * them to match and does not check whether they match.
	 */
	public static void addCorrespondencesForPcmOperationSignatureAndJavaInterfaceMethod(
			EditableCorrespondenceModelView<?> corView, OperationSignature pcmSig, Method javaMet) {
		Preconditions.checkArgument(javaMet.eContainer() instanceof Interface,
				"Given Java method is not an interface method");

		// Add correspondences between method signatures (as a whole)
		addCorrespondenceToJavaCorrespondent(corView, pcmSig, javaMet);

		// Add correspondences between parameters (if existent)
		// Note: PCM parameters are NOT first class entities
		var pcmParams = pcmSig.getParameters__OperationSignature();
		var javaParams = javaMet.getParameters();
		for (int i = 0; i < pcmParams.size(); i++) {
			addCorrespondenceToJavaCorrespondent(corView, pcmParams.get(i), javaParams.get(i));
		}

		// Add correspondences between PCM exception types and Java References to
		// exception Classifiers (if existent)
		//
		// Note: PCM exception types are NOT first class entities
		//
		// Since exception types could be excluded while looking for matching PCM and
		// Java Method signatures, it is important to re-check whether they match
		pcmSig.getExceptions__Signature().forEach((pcmExc) -> {
			var javaExc = javaMet.getExceptions().stream().filter((javaExcRef) -> javaExcRef
					.getPureClassifierReference().getTarget().getName().equals(pcmExc.getExceptionName())).findFirst();
			if (javaExc.isPresent()) {
				addCorrespondenceToJavaCorrespondent(corView, pcmExc, javaExc.get());
			}
		});
	}

	/**
	 * Adds the correspondence (pcmElement, javaCorrespondent, tag) to given corView
	 * 
	 * @return javaCorrespondent
	 */
	public static <O extends Commentable> O addCorrespondenceToJavaCorrespondent(
			EditableCorrespondenceModelView<?> corView, EObject pcmElement, O javaCorrespondent, String tag) {
		if (javaCorrespondent != null) {
			corView.addCorrespondenceBetween(pcmElement, javaCorrespondent, tag);
		}

		return javaCorrespondent;
	}

	/**
	 * Adds the correspondence (pcmElement, javaCorrespondent, null) to given
	 * corView
	 * 
	 * @return javaCorrespondent
	 */
	public static <O extends Commentable> O addCorrespondenceToJavaCorrespondent(
			EditableCorrespondenceModelView<?> corView, EObject pcmElement, O javaCorrespondent) {
		return addCorrespondenceToJavaCorrespondent(corView, pcmElement, javaCorrespondent, null);
	}

	/**
	 * Adds javaCorrespondent to javaModelResource by using
	 * placeInJavaModelResourceFunc, if javaCorrespondent is not already in a
	 * Resource. placeInJavaModelResourceFunc denotes how and to which location in
	 * javaModelResource javaCorrespondent is to be inserted.
	 * 
	 * @return javaCorrespondent
	 */
	public static <O extends Commentable> O addToJavaModelIfNotThere(Resource javaModelResource, O javaCorrespondent,
			BiConsumer<Resource, O> placeInJavaModelResourceFunc) {
		if (javaCorrespondent != null && javaCorrespondent.eResource() == null) {
			placeInJavaModelResourceFunc.accept(javaModelResource, javaCorrespondent);
		}

		return javaCorrespondent;
	}

	/**
	 * Adds javaCorrespondent to a Resource by using placeInJavaModelResourceFunc,
	 * if javaCorrespondent is not already in a Resource.
	 * placeInJavaModelResourceFunc denotes how and to which location in a Resource
	 * javaCorrespondent is to be inserted.
	 * <p>
	 * Use this method, if javaCorrespondent should be nested in another Java model
	 * element, which is already in a Resource.
	 * 
	 * @return javaCorrespondent
	 */
	public static <O extends Commentable> O addToJavaModelIfNotThere(O javaCorrespondent,
			Consumer<O> placeInJavaModelResourceFunc) {
		return addToJavaModelIfNotThere(null, javaCorrespondent, (r, o) -> placeInJavaModelResourceFunc.accept(o));
	}

	/**
	 * Adds all missing imports to given CompilationUnit
	 */
	public static void prepareJavaCompilationUnit(CompilationUnit cu) {
		var allTypeReferences = new ArrayList<TypeReference>();
		cu.eAllContents().forEachRemaining((o) -> {
			if (o instanceof TypeReference)
				allTypeReferences.add((TypeReference) o);
		});

		for (var ref : allTypeReferences) {
			if (cu.getImports().stream().noneMatch((i) -> i.getImportedClassifiers().stream()
					.anyMatch((cls) -> ref.getPureClassifierReference().getTarget() == cls))) {
				cu.addImport(ref.getPureClassifierReference().getTarget().getName());
			}
		}
	}

	/**
	 * Adds all missing imports to given Java Classifier
	 */
	public static void prepareJavaClassifier(ConcreteClassifier javaCls) {
		if (javaCls.getContainingCompilationUnit() != null) {
			prepareJavaCompilationUnit(javaCls.getContainingCompilationUnit());
		}
	}

	/**
	 * Adds a correspondence between pcmElem and Java Classifier.
	 * <p>
	 * If Java correspondent is located, it will be a ConcreteClassifier. If Java
	 * correspondent is freshly created, it will be a ConcreteClassifier contained
	 * in a CompilationUnit. The reason is that ConcreteClassifiers do not directly
	 * support the namespace feature, it is instead derived from its containers.
	 */
	public static void integrateJavaClassifierCorrespondent(EditableCorrespondenceModelView<?> corView, EObject pcmElem,
			Resource javaModelResource, ConcreteClassifier javaCls, List<String> namespaces) {
		PcmJavaCPRUtils.addJavaClassifierIntoResource(javaModelResource, javaCls, namespaces);
	}

	/**
	 * @return Whether the given obj is in the given resource r
	 */
	public static boolean isInResource(Resource r, EObject obj) {
		EObject objInR = null;
		var it = r.getAllContents();
		while (it.hasNext() && objInR == null) {
			var currentObj = it.next();
			if (currentObj == obj) {
				objInR = currentObj;
				break;
			}
		}
		return objInR != null;
	}

	/**
	 * Adds the given Java Classifier into the given Resource, if it is not already
	 * there.
	 * 
	 * @return All JavaRoot instances that were created to add javaCls with given
	 *         namespaces to r
	 */
	public static List<JavaRoot> addJavaClassifierIntoResource(Resource r, ConcreteClassifier javaCls,
			List<String> javaClsNss) {
		if (isInResource(r, javaCls)) {
			return null;
		}

		var topContents = r.getContents();

		var possibleContainers = topContents.stream().filter((c) -> c instanceof JavaRoot).map((c) -> (JavaRoot) c)
				.collect(Collectors.toCollection(ArrayList::new));

		JavaRoot bottomMostExistingParentContainer = null;
		int longestCommonNsPrefixLen = 0;
		var javaClsNssSize = javaClsNss.size();

		for (var pc : possibleContainers) {
			var currentLongestCommonNsPrefixLen = getLongestCommonNamespacePrefix(pc.getNamespaces(), javaClsNss)
					.size();

			if (currentLongestCommonNsPrefixLen == javaClsNssSize) {
				// Direct parent container (wrt. namespaces) of javaCls found
				bottomMostExistingParentContainer = pc;
				break;
			}

			if (currentLongestCommonNsPrefixLen > 0 && longestCommonNsPrefixLen < currentLongestCommonNsPrefixLen) {
				longestCommonNsPrefixLen = currentLongestCommonNsPrefixLen;
				bottomMostExistingParentContainer = pc;
			}
		}

		var containers = addJavaClassifierIntoJavaRoot(bottomMostExistingParentContainer, javaCls, javaClsNss);
		r.getContents().addAll(containers);
		return containers;
	}

	/**
	 * Inserts javaCls (directly or indirectly depending on concrete JavaRoot type)
	 * into jrOfJavaCls, which in return inserts javaCls into the Java model
	 * resource. If jrOfJavaCls is null, all packages and the compilation unit
	 * leading to javaCls will be constructed.
	 * <p>
	 * Assumption: If a Java Module for javaCls were to exist, it would have existed
	 * already. Therefore, no Java Modules will be constructed.
	 * 
	 * @return All parent Java containers that were freshly created, which were
	 *         required to insert javaCls into the Java model resource
	 */
	public static List<JavaRoot> addJavaClassifierIntoJavaRoot(JavaRoot jrOfJavaCls, ConcreteClassifier javaCls,
			List<String> javaClsNss) {
		if (jrOfJavaCls == null) {
			// javaCls has no pre-existing container, build all packages and the compilation
			// unit leading to it
			return addJavaClassifierIntoJavaPackage(null, null, javaCls, javaClsNss);
		} else if (jrOfJavaCls instanceof org.emftext.language.java.containers.Module) {
			// javaCls' most specific parent container is a module, build all packages and
			// the compilation unit leading to it
			return addJavaClassifierIntoJavaModule(((org.emftext.language.java.containers.Module) jrOfJavaCls), javaCls,
					javaClsNss);
		} else if (jrOfJavaCls instanceof org.emftext.language.java.containers.Package) {
			// javaCls' most specific parent container is a package, build all sub-packages
			// and the compilation unit leading to it
			return addJavaClassifierIntoJavaPackage(null, ((org.emftext.language.java.containers.Package) jrOfJavaCls),
					javaCls, javaClsNss);
		} else if (jrOfJavaCls instanceof CompilationUnit) {
			// javaCls' most specific parent container is a CompilationUnit, just add
			// javaCls to it
			((CompilationUnit) jrOfJavaCls).getClassifiers().add(javaCls);
			return List.of();
		} else {
			throw new IllegalStateException(
					"Unknown container for javaCls exists of type: " + jrOfJavaCls.getClass().getName());
		}
	}

	/**
	 * Inserts javaCls (indirectly) into moduleOfJavaCls, which in return inserts
	 * javaCls into the Java model resource. All packages and the compilation unit
	 * leading to javaCls will be constructed.
	 * <p>
	 * 
	 * @return All parent Java containers that were freshly created, which were
	 *         required to insert javaCls into the Java model resource
	 */
	public static List<JavaRoot> addJavaClassifierIntoJavaModule(
			org.emftext.language.java.containers.Module moduleOfJavaCls, ConcreteClassifier javaCls,
			List<String> javaClsNss) {
		if (javaCls.getPackage() != null) {
			return List.of();
		} else {
			return addJavaClassifierIntoJavaPackage(moduleOfJavaCls, null, javaCls, javaClsNss);
		}

//		var createdContainers = new ArrayList<JavaRoot>();
//
//		var longestNsPrefix = getLongestCommonNamespacePrefix(moduleOfJavaCls.getNamespaces(), javaClsNss);
//		if (longestNsPrefix.size() == javaClsNss.size()) {
//			// Module's namespace matches directly with that of Java Classifier
//			// create a package for the Java Classifier as well as a CompilationUnit
//			var pacs = createJavaPackages(moduleOfJavaCls, javaClsNss);
//			createdContainers.addAll(pacs);
//			pacs.get(pacs.size() - 1).getClassifiers().add(javaCls);
//
//			if (javaCls.getContainingCompilationUnit() == null) {
//				createdContainers.add(createCompilationUnitForJavaClassifier(javaCls, javaClsNss));
//			}
//		} else {
//			// Create the necessary Packages
//			var cons = addJavaClassifierIntoJavaPackage(moduleOfJavaCls, null, javaCls, javaClsNss);
//			createdContainers
//					.addAll(cons.stream().filter((c) -> c instanceof org.emftext.language.java.containers.Package)
//							.collect(Collectors.toList()));
//		}
//
//		return createdContainers;
	}

	/**
	 * Inserts javaCls (directly or indirectly depending on whether parentPackage's
	 * namespace matches) into parentPackage, which in return inserts javaCls into
	 * the Java model resource. All packages and the compilation unit leading to
	 * javaCls will be constructed.
	 * <p>
	 * 
	 * @return All parent Java containers that were freshly created, which were
	 *         required to insert javaCls into the Java model resource
	 */
	public static List<JavaRoot> addJavaClassifierIntoJavaPackage(
			org.emftext.language.java.containers.Module moduleOfJavaCls,
			org.emftext.language.java.containers.Package parentPackage, ConcreteClassifier javaCls,
			List<String> javaClsNss) {
		if (javaCls.getPackage() != null) {
			return List.of();
		}

		List<String> longestNsPrefix = null;

		if (parentPackage != null) {
			longestNsPrefix = getLongestCommonNamespacePrefix(parentPackage.getNamespaces(), javaClsNss);
			if (longestNsPrefix.size() == javaClsNss.size()) {
				// Parent package is the direct container of javaCls, insert it there, create
				// a compilation unit if necessary and return
				parentPackage.getClassifiers().add(javaCls);

				if (javaCls.getContainingCompilationUnit() == null) {
					return List.of(createCompilationUnitForJavaClassifier(javaCls, javaClsNss));
				} else {
					return List.of();
				}
			}
		} else {
			longestNsPrefix = List.of();
		}

		var createdContainers = new ArrayList<JavaRoot>();
		var remainingNss = javaClsNss.stream().skip(longestNsPrefix.size())
				.collect(Collectors.toCollection(ArrayList::new));

		var createdPacs = new ArrayList<org.emftext.language.java.containers.Package>();
		// Create the necessary packages
		for (int i = 0; i < remainingNss.size(); i++) {
			createdPacs.add(createJavaPackage(moduleOfJavaCls, javaClsNss.subList(0, i + longestNsPrefix.size() + 1)));
		}

		if (!createdPacs.isEmpty()) {
			createdContainers.addAll(createdPacs);
			createdPacs.get(createdPacs.size() - 1).getClassifiers().add(javaCls);
		}

		// Create the necessary CompilationUnit
		if (javaCls.getContainingCompilationUnit() == null) {
			createdContainers.add(createCompilationUnitForJavaClassifier(javaCls, javaClsNss));
		}

		return createdContainers;
	}

	/**
	 * Inserts given Java Classifier into an appropriately created CompilationUnit
	 * 
	 * @return A CompilationUnit for the given Java Classifier with the given
	 *         namespaces
	 */
	public static CompilationUnit createCompilationUnitForJavaClassifier(ConcreteClassifier javaCls,
			List<String> javaClsNss) {
		var cu = ContainersFactory.eINSTANCE.createCompilationUnit();

		cu.setName(javaCls.getName());
		// Since all added Java model elements are to be saved into source files,
		// the origin should be FILE
		cu.setOrigin(Origin.FILE);

		cu.getNamespaces().addAll(javaClsNss);
		cu.getClassifiers().add(javaCls);
		return cu;
	}

	/**
	 * @return A Java Package with the given module and namespaces. The given module
	 *         may be null.
	 */
	public static org.emftext.language.java.containers.Package createJavaPackage(
			org.emftext.language.java.containers.Module moduleOfJavaCls, List<String> javaPacNss) {
		var pac = ContainersFactory.eINSTANCE.createPackage();

		if (moduleOfJavaCls != null) {
			pac.setModule(moduleOfJavaCls);
			moduleOfJavaCls.getPackages().add(pac);
		}

		// Since all added Java model elements are to be saved into source files,
		// the origin should be FILE
		pac.setOrigin(Origin.FILE);

		// Package names are irrelevant for CPRs and JaMoPP context, only namespaces
		// matter. Here, use empty String as name, in order to avoid modelling issues
		pac.setName("");
		pac.getNamespaces().addAll(javaPacNss);

		return pac;
	}

	public static List<org.emftext.language.java.containers.Package> createJavaPackages(
			org.emftext.language.java.containers.Module moduleOfJavaCls, List<String> javaPacNss) {
		var createdPacs = new ArrayList<org.emftext.language.java.containers.Package>();
		// Create the necessary packages
		for (int i = 0; i < javaPacNss.size(); i++) {
			createdPacs.add(createJavaPackage(moduleOfJavaCls, javaPacNss.subList(0, i + javaPacNss.size() + 1)));
		}

		return createdPacs;
	}

	/**
	 * @return The longest common namespace prefix for the given namespaces (as
	 *         list)
	 */
	public static List<String> getLongestCommonNamespacePrefix(List<String> nss1, List<String> nss2) {
		if (nss1.isEmpty() || nss2.isEmpty())
			return List.of();

		var minSize = nss1.size() < nss2.size() ? nss1.size() : nss2.size();

		var prefix = new ArrayList<String>();
		for (int i = 0; i < minSize; i++) {
			if (nss1.get(i).equals(nss2.get(i))) {
				prefix.add(nss1.get(i));
			} else {
				break;
			}
		}

		return prefix;
	}
}
