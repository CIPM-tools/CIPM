package cipm.consistency.initialisers.jamopp.annotations;

import java.util.Collection;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserPackage;

public class AnnotationsInitialiserPackage implements IInitialiserPackage {
	@Override
	public Collection<IInitialiser> getInitialiserInstances() {
		return this.initCol(
				new IInitialiser[] { new AnnotationAttributeSettingInitialiser(), new AnnotationInstanceInitialiser(),
						new AnnotationParameterListInitialiser(), new SingleAnnotationParameterInitialiser() });
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Class<? extends IInitialiser>> getInitialiserInterfaceTypes() {
		return this.initCol(new Class[] { IAnnotableInitialiser.class, IAnnotationAttributeSettingInitialiser.class,
				IAnnotationInstanceInitialiser.class, IAnnotationParameterInitialiser.class,
				IAnnotationParameterListInitialiser.class, IAnnotationValueInitialiser.class,
				ISingleAnnotationParameterInitialiser.class });
	}
}
