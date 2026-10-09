package cipm.consistency.initialisers.jamopp.annotations;

import org.emftext.language.java.annotations.AnnotationAttributeSetting;
import org.emftext.language.java.annotations.AnnotationParameterList;

public interface IAnnotationParameterListInitialiser extends IAnnotationParameterInitialiser {
	@Override
	public AnnotationParameterList instantiate();

	public default boolean addSetting(AnnotationParameterList apl, AnnotationAttributeSetting setting) {
		if (setting != null) {
			apl.getSettings().add(setting);
			return apl.getSettings().contains(setting);
		}
		return true;
	}

	public default boolean addSettings(AnnotationParameterList apl, AnnotationAttributeSetting[] settings) {
		return this.doMultipleModifications(apl, settings, this::addSetting);
	}
}
