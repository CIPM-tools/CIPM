package cipm.consistency.cpr.util;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.commons.Commentable;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.types.ClassifierReference;
import org.emftext.language.java.types.TypeReference;
import org.emftext.language.java.types.TypesFactory;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.DataType;
import org.palladiosimulator.pcm.repository.Interface;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.PrimitiveDataType;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingInternalBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

import tools.vitruv.applications.pcmjava.seffstatements.code2seff.extended.CommitIntegrationCodeToSeffFactory;
import tools.vitruv.applications.pcmjava.seffstatements.pojotransformations.code2seff.FunctionClassificationStrategyForPackageMapping;
import tools.vitruv.framework.correspondence.CorrespondenceModel;

/**
 * An internal utility class.
 * 
 * @author Martin Armbruster
 */
public final class InternalUtils {
	private InternalUtils() {
	}
	
	/**
	 * Returns the module in which a classifier is contained.
	 * 
	 * @param classifier the classifier for which the module is looked up.
	 * @return the module in which the classifier is contained
	 *         or null if the classifier is not contained within a module.
	 */
	public static org.emftext.language.java.containers.Module getModule(ConcreteClassifier classifier) {
		if (classifier.getPackage() != null) {
			if (classifier.getPackage().getModule() != null) {
				return classifier.getPackage().getModule();
			}
		}
		return null;
	}
	
	/**
	 * Checks if a call to a method is an external call.
	 * 
	 * @param method the method to check.
	 * @param cm the correspondence model.
	 * @param com basic component in which the call occurs.
	 * @return true if a call to the method is an external call.
	 */
	public static boolean isExternalCall(Method method, CorrespondenceModel cm, BasicComponent com) {
		var factory = new CommitIntegrationCodeToSeffFactory();
		class LocalStrategy extends FunctionClassificationStrategyForPackageMapping {
			public LocalStrategy() {
				super(factory.createBasicComponentFinding(), cm, com);
			}

			@Override
			public boolean isExternalCall(Method call) {
				return super.isExternalCall(call);
			}
		}
		var strategy = new LocalStrategy();
		return strategy.isExternalCall(method);
	}
	
	/**
	 * Returns the name of a DataType.
	 * 
	 * @param dataType the DataType to convert.
	 * @return the name of the DataType.
	 */
	public static String convertToName(DataType dataType) {
		if (dataType instanceof Entity) {
			return ((Entity) dataType).getEntityName();
		} else if (dataType instanceof PrimitiveDataType) {
			return ((PrimitiveDataType) dataType).getType().getLiteral();
		}
		return "";
	}
	
	/**
	 * Returns a TypeReference to the Object class.
	 * 
	 * @param context context from whicht the object class is retrieved.
	 * @return the TypeReference or null if the Object class cannot be found.
	 */
	public static TypeReference getTypeReferenceToObject(Commentable context) {
		var objClass = context.getObjectClass();
		if (!objClass.eIsProxy()) {
			ClassifierReference result = TypesFactory.eINSTANCE.createClassifierReference();
			result.setTarget(context.getObjectClass());
			return result;
		}
		return null;
	}
	
	/**
	 * Finds a RequiredRole for an interface in a component if its exists.
	 * 
	 * @param component the component.
	 * @param interfaze the interface.
	 * @return the RequiredRole or null if it does not exist.
	 */
	public static OperationRequiredRole findRequiredRole(RepositoryComponent component, Interface interfaze) {
		for (var role : component.getRequiredRoles_InterfaceRequiringEntity()) {
			if (role instanceof OperationRequiredRole) {
				var opRole = (OperationRequiredRole) role;
				if (opRole.getRequiredInterface__OperationRequiredRole() == interfaze) {
					return opRole;
				}
			}
		}
		return null;
	}
	
	/**
	 * Finds the parent ResourceDemandingSEFF for a ResourceDemandingBehaviour which is a ResourceDemandingSEFF,
	 * not a ResourceDemandingInternalBehaviour, or not contained within a ResourceDemandingInternalBehaviour.
	 * 
	 * @param behaviour the ResourceDemandingBehaviour.
	 * @return the parent ResourceDemandingSEFF or null if it cannot be found, the behaviour is a
	 *         ResourceDemandingInternalBehaviour, or the behaviour is contained within a
	 *         ResourceDemandingInternalBehaviour.
	 */
	public static ResourceDemandingSEFF getParentSEFFNotForInternalBehaviour(ResourceDemandingBehaviour behaviour) {
		EObject parent = behaviour;
		while (parent != null && !(parent instanceof RepositoryComponent)
				&& !(parent instanceof ResourceDemandingInternalBehaviour)) {
			if (parent instanceof ResourceDemandingSEFF) {
				return (ResourceDemandingSEFF) parent;
			}
			parent = parent.eContainer();
		}
		return null;
	}
}
