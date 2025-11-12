package cipm.consistency.cpr.pcmjava.userinteraction;

import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.common.base.Preconditions;

public class FeatureEntry implements IPcmUserInteractionManagerEntry {
	/**
	 * Indicates that the value is unset. Null cannot be used for that purpose,
	 * since some features are allowed to have a null value assigned to them.
	 */
	private static final Object unsetKey = new Object();

	private final EObject triggeringPCMElement;
	private final EObject affectedJavaElement;
	private final EStructuralFeature affectedJavaElementFeature;
	private Object affectedJavaElementFeatureValue;

	public FeatureEntry(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature affectedJavaElementFeature) {
		this(triggeringPCMElement, affectedJavaElement, affectedJavaElementFeature, unsetKey);
	}

	@SuppressWarnings("unchecked")
	public FeatureEntry(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature affectedJavaElementFeature, Object affectedJavaElementFeatureValue) {
		this.triggeringPCMElement = triggeringPCMElement;
		this.affectedJavaElement = affectedJavaElement;
		this.affectedJavaElementFeature = affectedJavaElementFeature;
		if (affectedJavaElementFeatureValue instanceof List) {
			var val = this.getDefaultValueForMultiValued();
			val.addAll((List<?>) affectedJavaElementFeatureValue);
			this.affectedJavaElementFeatureValue = val;
		} else {
			this.affectedJavaElementFeatureValue = affectedJavaElementFeatureValue;
		}
	}

	public boolean hasAssignedValue() {
		return this.affectedJavaElementFeatureValue != unsetKey;
	}

	public boolean valueEquals(Object value) {
		if (!this.affectedJavaElementFeature.isMany()) {
			return this.affectedJavaElementFeatureValue == value || (this.affectedJavaElementFeatureValue != null
					&& this.affectedJavaElementFeatureValue.equals(value));
		} else {
			var val = this.getCurrentMultiValue();
			return val.size() == 1 && (val.get(0) == value || (val.get(0) != null && val.get(0).equals(value)));
		}
	}

	@SuppressWarnings("unchecked")
	public boolean hasValues(List<?> values) {
		var val = this.getCurrentMultiValue();
		return val.size() == values.size() && val.containsAll(values);
	}

	public boolean hasValue(Object value) {
		return this.valueEquals(value)
				|| (this.affectedJavaElementFeature.isMany() && this.getCurrentMultiValue().contains(value));
	}

	public void unset() {
		this.affectedJavaElementFeatureValue = unsetKey;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getDefaultValueForMultiValued() {
		var defVal = (List) affectedJavaElementFeature.getDefaultValue();
		return defVal == null ? new BasicEList() : new BasicEList(defVal);
	}

	@SuppressWarnings("rawtypes")
	private List getCurrentMultiValue() {
		if (!this.hasAssignedValue()) {
			this.affectedJavaElementFeatureValue = this.getDefaultValueForMultiValued();
		}
		return (List) this.affectedJavaElementFeatureValue;
	}

	private void setSingleValue(Object newValue) {
		Preconditions.checkArgument(!affectedJavaElementFeature.isMany(),
				"Cannot assign a single value to a many-valued affectedJavaElementFeature");
		this.affectedJavaElementFeatureValue = newValue;
	}

	@SuppressWarnings("unchecked")
	public void setMultipleValues(List<?> values) {
		Preconditions.checkArgument(affectedJavaElementFeature.isMany(),
				"Cannot assign multiple values to a single-valued affectedJavaElementFeature");
		var valueList = this.getCurrentMultiValue();
		valueList.addAll(values);
	}

	public void addValues(List<?> values) {
		Preconditions.checkArgument(affectedJavaElementFeature.isMany(),
				"Cannot add values from a single-valued affectedJavaElementFeature");
		values.forEach((v) -> this.addValue(v));
	}

	public void removeValues(List<?> values) {
		Preconditions.checkArgument(affectedJavaElementFeature.isMany(),
				"Cannot remove values from a single-valued affectedJavaElementFeature");
		values.forEach((v) -> this.removeValue(v));
	}

	@SuppressWarnings("unchecked")
	private void addValue(Object value) {
		Preconditions.checkArgument(affectedJavaElementFeature.isMany(),
				"Cannot give multiple values to a single-valued affectedJavaElementFeature");
		getCurrentMultiValue().add(value);
	}

	private void removeValue(Object value) {
		Preconditions.checkArgument(affectedJavaElementFeature.isMany(),
				"Cannot remove value from a single-valued affectedJavaElementFeature");
		getCurrentMultiValue().remove(value);
	}

	public void setOrAddValue(Object value) {
		if (this.affectedJavaElementFeature.isMany()) {
			this.addValue(value);
		} else {
			this.setSingleValue(value);
		}
	}

	public void unsetOrRemoveValue(Object value) {
		if (this.affectedJavaElementFeature.isMany()) {
			this.removeValue(value);
		} else {
			this.unset();
		}
	}

	public EStructuralFeature getAffectedJavaElementFeature() {
		return this.affectedJavaElementFeature;
	}

	public EObject getTriggeringPCMElement() {
		return this.triggeringPCMElement;
	}

	public boolean featureEquals(EStructuralFeature feat) {
		return this.affectedJavaElementFeature.equals(feat);
	}

	public boolean triggeringPCMElementEquals(EObject triggeringPCMElement) {
		return this.eObjectEquals(this.triggeringPCMElement, triggeringPCMElement);
	}

	private boolean eObjectEquals(EObject obj1, EObject obj2) {
		return EcoreUtil.equals(obj1, obj2);
	}

	public boolean isFeatureEntryFor(EObject triggeringPCMElement, EStructuralFeature feat) {
		return this.featureEquals(feat) && this.triggeringPCMElementEquals(triggeringPCMElement);
	}

	public boolean isFeatureEntryFor(EObject triggeringPCMElement, EObject affectedJavaElement,
			EStructuralFeature feat) {
		return isFeatureEntryFor(triggeringPCMElement, feat) && this.affectedJavaElementCompatible(affectedJavaElement);
	}

	public boolean affectedJavaElementCompatible(EObject affectedJavaElement) {
		return !(this.affectedJavaElement != null && affectedJavaElement != null
				&& !eObjectEquals(this.affectedJavaElement, affectedJavaElement));
	}

	public void setValuesFrom(FeatureEntry entry) {
		this.affectedJavaElementFeatureValue = entry.affectedJavaElementFeatureValue;
	}

	public void copyValuesFrom(FeatureEntry entry) {
		if (this.affectedJavaElementFeature.isMany()) {
			this.addValues(entry.getCurrentMultiValue());
		} else {
			this.setValuesFrom(entry);
		}
	}

	public Object getValue() {
		return this.affectedJavaElementFeatureValue;
	}

	@Override
	public boolean equals(Object triggeringPCMElement) {
		if (!(triggeringPCMElement instanceof FeatureEntry)) {
			return false;
		}

		var castedO = (FeatureEntry) triggeringPCMElement;

		return this.featureEquals(castedO.affectedJavaElementFeature)
				&& this.triggeringPCMElementEquals(castedO.triggeringPCMElement)
				// Ensure that affectedJavaElements are not different, if both of them are not
				// null
				&& this.affectedJavaElementCompatible(castedO.affectedJavaElement);
	}
}
