package cipm.consistency.vsum.test.pcm.userinteraction;

import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.common.base.Preconditions;

public class FeatureEntry {
	/**
	 * Indicates that the value is unset. Null cannot be used for that purpose,
	 * since some features are allowed to have a null value assigned to them.
	 */
	private static final Object unsetKey = new Object();

	private final EObject obj;
	private final EStructuralFeature feature;
	private Object value;

	public FeatureEntry(EObject obj, EStructuralFeature feature) {
		this(obj, feature, unsetKey);
	}

	public FeatureEntry(EObject obj, EStructuralFeature feature, Object value) {
		this.obj = obj;
		this.feature = feature;
		this.value = value;
	}

	@SuppressWarnings("unchecked")
	public FeatureEntry(EObject obj, EStructuralFeature feature, List<?> values) {
		this.obj = obj;
		this.feature = feature;
		var val = this.getDefaultValueForMultiValued();
		val.addAll(values);
		this.value = val;
	}

	public boolean hasAssignedValue() {
		return this.value != unsetKey;
	}

	public boolean valueEquals(Object value) {
		if (!this.feature.isMany()) {
			return this.value == value || (this.value != null && this.value.equals(value));
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
		return this.valueEquals(value) || (this.feature.isMany() && this.getCurrentMultiValue().contains(value));
	}

	public void unset() {
		this.value = unsetKey;
	}

	@SuppressWarnings("rawtypes")
	private List getDefaultValueForMultiValued() {
		return (List) feature.getDefaultValue();
	}

	@SuppressWarnings("rawtypes")
	private List getCurrentMultiValue() {
		if (!this.hasAssignedValue()) {
			var defaultVal = this.getDefaultValueForMultiValued();
			if (defaultVal != null) {
				this.value = defaultVal;
			} else {
				this.value = new BasicEList();
			}
		}
		return (List) this.value;
	}

	private void setSingleValue(Object newValue) {
		Preconditions.checkArgument(!feature.isMany(), "Cannot assign a single value to a many-valued feature");
		this.value = newValue;
	}

	@SuppressWarnings("unchecked")
	public void setMultipleValues(List<?> values) {
		Preconditions.checkArgument(feature.isMany(), "Cannot assign multiple values to a single-valued feature");
		var valueList = this.getCurrentMultiValue();
		valueList.addAll(values);
	}

	public void addValues(List<?> values) {
		Preconditions.checkArgument(feature.isMany(), "Cannot add values from a single-valued feature");
		values.forEach((v) -> this.addValue(v));
	}

	public void removeValues(List<?> values) {
		Preconditions.checkArgument(feature.isMany(), "Cannot remove values from a single-valued feature");
		values.forEach((v) -> this.removeValue(v));
	}

	@SuppressWarnings("unchecked")
	private void addValue(Object value) {
		Preconditions.checkArgument(feature.isMany(), "Cannot give multiple values to a single-valued feature");
		getCurrentMultiValue().add(value);
	}

	private void removeValue(Object value) {
		Preconditions.checkArgument(feature.isMany(), "Cannot remove value from a single-valued feature");
		getCurrentMultiValue().remove(value);
	}

	public void setOrAddValue(Object value) {
		if (this.feature.isMany()) {
			this.addValue(value);
		} else {
			this.setSingleValue(value);
		}
	}

	public void unsetOrRemoveValue(Object value) {
		if (this.feature.isMany()) {
			this.removeValue(value);
		} else {
			this.unset();
		}
	}

	public EStructuralFeature getFeature() {
		return this.feature;
	}

	public EObject getEObject() {
		return this.obj;
	}

	public boolean featureEquals(EStructuralFeature feat) {
		return this.feature.equals(feat);
	}

	public boolean eObjectEquals(EObject obj) {
		return this.eObjectEquals(this.obj, obj);
	}

	private boolean eObjectEquals(EObject obj1, EObject obj2) {
		return EcoreUtil.equals(obj1, obj2);
	}

	public boolean isFeatureEntryFor(EObject obj, EStructuralFeature feat) {
		return this.featureEquals(feat) && this.eObjectEquals(obj);
	}

	public void setValuesFrom(FeatureEntry entry) {
		this.value = entry.value;
	}

	public void copyValuesFrom(FeatureEntry entry) {
		if (this.feature.isMany()) {
			this.addValues(entry.getCurrentMultiValue());
		} else {
			this.setValuesFrom(entry);
		}
	}

	public Object getValue() {
		return this.value;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FeatureEntry)) {
			return false;
		}

		var castedO = (FeatureEntry) obj;

		return this.featureEquals(castedO.feature) && this.eObjectEquals(castedO.obj);
	}
}
