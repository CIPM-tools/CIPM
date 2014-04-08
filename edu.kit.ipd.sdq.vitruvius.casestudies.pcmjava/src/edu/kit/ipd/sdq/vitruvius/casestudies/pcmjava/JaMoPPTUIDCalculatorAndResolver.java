package edu.kit.ipd.sdq.vitruvius.casestudies.pcmjava;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.Package;
import org.emftext.language.java.members.Field;
import org.emftext.language.java.members.Member;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.parameters.Parameter;
import org.emftext.language.java.types.ClassifierReference;
import org.emftext.language.java.types.NamespaceClassifierReference;
import org.emftext.language.java.types.PrimitiveType;
import org.emftext.language.java.types.TypeReference;

import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.VURI;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.interfaces.user.TUIDCalculatorAndResolver;

public class JaMoPPTUIDCalculatorAndResolver implements TUIDCalculatorAndResolver {

    private static final Logger logger = Logger.getLogger(JaMoPPTUIDCalculatorAndResolver.class.getSimpleName());
    private static final String TUIDIdentifier = JaMoPPTUIDCalculatorAndResolver.class.getSimpleName();

    @Override
    public String getTUID(final EObject eObject) {
        String tuid = TUIDIdentifier + EOBJECT_SEPERATOR;
        if (eObject instanceof Package) {
            return getTUIDFromPackage((Package) eObject);
        } else if (eObject instanceof CompilationUnit) {
            return getTUIDFromCompilationUnit((CompilationUnit) eObject);
        } else if (eObject instanceof Classifier) {
            return getTUIDFromClassifier((Classifier) eObject);
        } else if (eObject instanceof Member) {
            return getTUIDFromMember((Member) eObject);
        }
        logger.warn("no TUID building mechanism found for eObject: " + eObject);
        return tuid;
    }

    @Override
    public VURI getModelVURIContainingIdentifiedEObject(final String extTuid) {
        String tuid = checkTUID(extTuid);
        String[] ids = tuid.split(EOBJECT_SEPERATOR);
        String vuriKey = ids[0];
        vuriKey = vuriKey.substring(0, vuriKey.lastIndexOf("$"));
        vuriKey = vuriKey.replace(".", "/");
        if (1 < ids.length) {
            vuriKey += "/" + ids[1];
        }
        return VURI.getInstance(vuriKey);

    }

    @Override
    public EObject getIdentifiedEObjectWithinRootEObject(final EObject root, final String extTuid) {
        String tuid = checkTUID(extTuid);
        String tuidRootObj = getTUID(root);
        if (!tuid.startsWith(tuidRootObj)) {
            logger.warn("TUID " + tuid + " is not in EObject " + root);
            return null;
        }
        String identifier = tuid.substring(tuidRootObj.length(), tuid.length());
        if (identifier.startsWith(EOBJECT_SEPERATOR)) {
            identifier = identifier.substring(EOBJECT_SEPERATOR.length(), identifier.length());
        }
        String[] ids = identifier.split(EOBJECT_SEPERATOR);
        if (root instanceof CompilationUnit) {
            return findEObjectInCompilationUnit((CompilationUnit) root, ids);
        } else if (root instanceof Package) {
            return findEObjectsInPacakge((Package) root, ids);
        }
        logger.warn("No EObject found for TUID: " + tuid + " in root object: " + root);
        return null;
    }

    private String checkTUID(final String tuid) {
        if (!tuid.startsWith(TUIDIdentifier)) {
            throw new RuntimeException("TUID: " + tuid + " not generated by class " + TUIDIdentifier);
        }
        return tuid.substring(TUIDIdentifier.length() + EOBJECT_SEPERATOR.length());
    }

    private EObject findEObjectsInPacakge(final Package pack, final String[] ids) {
        if (0 == ids.length) {
            return pack;
        }
        for (CompilationUnit cu : pack.getCompilationUnits()) {
            if (cu.getName().equals(ids[0])) {
                System.arraycopy(ids, 1, ids, 0, ids.length - 1);
                return findEObjectInCompilationUnit(cu, ids);
            }
        }
        logger.warn("Compilation unit : " + ids[0] + " not found in package: " + pack);
        return null;
    }

    private EObject findEObjectInCompilationUnit(final CompilationUnit cu, final String[] ids) {
        if (0 == ids.length) {
            return cu;
        }
        Classifier classifier = cu.getContainedClassifier(ids[0]);
        if (null == classifier) {
            logger.warn("Could not found classifier " + ids[0] + " in compilation unit " + cu);
            return null;
        }
        if (1 == ids.length) {
            return classifier;
        }
        for (Member member : classifier.getAllMembers(classifier)) {
            if (member instanceof Method) {
                Method method = (Method) member;
                if (getNameFromTypeReference(method.getTypeReference()).equals(ids[1])
                        && member.getName().equals(ids[2]) && checkParameters(method.getParameters(), ids)) {
                    return member;
                }
            } else if (member instanceof Field) {
                logger.trace("Fields are currently not supported. TUID: " + ids + " CompilationUnit: " + cu);
            }
        }
        logger.warn("Could not found member " + ids[1] + " in compilation unit " + cu);
        return null;
    }

    private boolean checkParameters(final EList<Parameter> parameters, final String[] ids) {
        int idsOfSet = 3;
        if (parameters.size() != ids.length - idsOfSet) {
            return false;
        }
        for (Parameter param : parameters) {
            if (false == (null != ids[idsOfSet] && getNameFromTypeReference(param.getTypeReference()).equals(
                    ids[idsOfSet]))) {
                return false;
            }
            idsOfSet++;
        }
        return true;
    }

    private String getTUIDFromPackage(final Package jaMoPPPackage) {
        return jaMoPPPackage.getNamespacesAsString();
    }

    private String getTUIDFromCompilationUnit(final CompilationUnit compilationUnit) {
        String className = null;
        /**
         * if compilation.getName == null (which can happen) we use the name of the first classifier
         * in the compilation unit as name. If there are no classifiers in the compilation unit we
         * use <null> as name
         */
        if (null != compilationUnit.getName()) {
            className = compilationUnit.getName();
        } else if (0 != compilationUnit.getClassifiers().size()) {
            className = compilationUnit.getClassifiers().get(0).getName() + ".java";
        } else {
            logger.warn("Could not determine a name for compilation unit: " + compilationUnit);
        }

        return compilationUnit.getNamespacesAsString() + EOBJECT_SEPERATOR + className;
    }

    private String getTUIDFromClassifier(final Classifier classifier) {
        String tuid = getTUIDFromCompilationUnit(classifier.getContainingCompilationUnit());
        tuid += EOBJECT_SEPERATOR + classifier.getName();
        return tuid;
    }

    private String getTUIDFromMember(final Member member) {
        String tuid = getTUIDFromClassifier(member.getContainingConcreteClassifier());
        if (member instanceof Method) {
            Method method = (Method) member;
            tuid += EOBJECT_SEPERATOR + getNameFromTypeReference(method.getTypeReference());
            tuid += EOBJECT_SEPERATOR + member.getName();
            for (Parameter param : method.getParameters()) {
                tuid += TUIDCalculatorAndResolver.EOBJECT_SEPERATOR
                        + getNameFromTypeReference(param.getTypeReference());
            }
        } else {
            tuid += EOBJECT_SEPERATOR + member.getName();
        }
        return tuid;
    }

    private String getNameFromTypeReference(final TypeReference typeReference) {
        if (typeReference instanceof ClassifierReference) {
            return getNameFromClassifierReference((ClassifierReference) typeReference);
        } else if (typeReference instanceof NamespaceClassifierReference) {
            NamespaceClassifierReference namespaceClassifierReference = (NamespaceClassifierReference) typeReference;
            String name = "";
            int i = 0;
            for (ClassifierReference cr : namespaceClassifierReference.getClassifierReferences()) {
                name += (i > 0 ? i : "") + getNameFromClassifierReference(cr);
                i++;
            }
            return name;
        } else if (typeReference instanceof PrimitiveType) {
            PrimitiveType primitiveType = (PrimitiveType) typeReference;
            return primitiveType.eClass().getName().replaceAll("Impl", "");
        }
        logger.error("getNameFromTypeReference failed. Could not found a name for type reference: " + typeReference);
        return "";
    }

    private String getNameFromClassifierReference(final ClassifierReference classifierReference) {
        return classifierReference.getTarget().getName();
    }
}
