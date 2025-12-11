package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * A class that can be used to serialise {@link IPcmUserInteractionManagerEntry}
 * implementors.
 * 
 * @author Alp Torac Genc
 */
public class PcmUserInteractionManagerEntrySerialiser {
	private static final String unsetKeySerialisation = "Unset";
	private static final String noResourceSerialisation = "without resource";
	private static final String nullSerialisation = "null";

	private static final String triggeringElementKey = "Trigger element";
	private static final String affectedElementKey = "Affected element";
	private static final String featureKey = "Affected feature";
	private static final String featureValueKey = "Affected feature value";

	private static final String knownElementKey = "Known element";
	private static final String correspondentsKey = "Correspondents";
	private static final String correspondenceTagKey = "Tag";

	private String serialiseEPackage(EPackage pac) {
		if (pac != null) {
			return pac.getClass().getSimpleName();
		} else {
			return nullSerialisation;
		}
	}

	private String serialiseEObject(EObject obj) {
		if (obj != null && obj.eResource() != null) {
			return obj.eResource().getURIFragment(obj) + " (" + serialiseEPackage(obj.eClass().getEPackage()) + ": "
					+ obj.eClass().getInstanceClass().getSimpleName() + ")";
		} else if (obj != null) {
			return obj.toString() + " " + noResourceSerialisation;
		} else {
			return nullSerialisation;
		}
	}

	private String serialiseEStructuralFeature(EStructuralFeature feat) {
		if (feat != null) {
			var containingECls = feat.getEContainingClass();
			var containingCls = containingECls.getInstanceClass();
			return serialiseEPackage(containingECls.getEPackage()) + "." + containingCls.getSimpleName() + "."
					+ feat.getName();
		} else {
			return nullSerialisation;
		}
	}

	public String serialiseCorrespondenceEntry(CorrespondenceEntry corEntry) {
		var map = new LinkedHashMap<String, Object>();
		map.put(knownElementKey, serialiseEObject(corEntry.getKnownElement()));
		map.put(correspondentsKey, corEntry.getCorrespondentsForKnownElement().stream().map(this::serialiseEObject)
				.collect(Collectors.toList()));
		map.put(correspondenceTagKey,
				corEntry.getCorrespondenceTag() != null ? corEntry.getCorrespondenceTag() : nullSerialisation);
		return map.toString();
	}

	public String serialiseFeatureEntry(FeatureEntry featEntry) {
		var map = new LinkedHashMap<String, Object>();
		map.put(triggeringElementKey, serialiseEObject(featEntry.getTriggeringPCMElement()));
		map.put(affectedElementKey, serialiseEObject(featEntry.getAffectedJavaElement()));
		map.put(featureKey, serialiseEStructuralFeature(featEntry.getAffectedJavaElementFeature()));
		map.put(featureValueKey,
				featEntry.getValue() != null ? (!featEntry.isUnset() ? featEntry.getValue() : unsetKeySerialisation)
						: nullSerialisation);
		return map.toString();
	}
}
