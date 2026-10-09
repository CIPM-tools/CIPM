package cipm.consistency.initialisers.jamopp.modifiers;

import org.emftext.language.java.annotations.AnnotationInstance;
import org.emftext.language.java.modifiers.AnnotableAndModifiable;
import org.emftext.language.java.modifiers.Modifier;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface IAnnotableAndModifiableInitialiser extends ICommentableInitialiser {
	@Override
	public AnnotableAndModifiable instantiate();

	public default boolean addModifier(AnnotableAndModifiable aam, Modifier modif) {
		if (modif != null) {
			aam.addModifier(modif);
			return aam.getAnnotationsAndModifiers().contains(modif) && aam.hasModifier(modif.getClass())
					&& aam.getModifiers().contains(modif);
		}
		return true;
	}

	public default boolean addModifiers(AnnotableAndModifiable aam, Modifier[] modifs) {
		return this.doMultipleModifications(aam, modifs, this::addModifier);
	}

	public default boolean addAnnotationInstance(AnnotableAndModifiable aam, AnnotationInstance annoAndModif) {
		if (aam != null) {
			aam.getAnnotationsAndModifiers().add(annoAndModif);
			return aam.getAnnotationsAndModifiers().contains(annoAndModif)
					&& aam.getAnnotationInstances().contains(annoAndModif);
		}
		return true;
	}

	public default boolean addAnnotationInstances(AnnotableAndModifiable aam, AnnotationInstance[] annoAndModifArr) {
		return this.doMultipleModifications(aam, annoAndModifArr, this::addAnnotationInstance);
	}

	public default boolean makePrivate(AnnotableAndModifiable aam) {
		aam.makePrivate();
		return aam.isPrivate();
	}

	public default boolean makeProtected(AnnotableAndModifiable aam) {
		aam.makeProtected();
		return aam.isProtected();
	}

	public default boolean makePublic(AnnotableAndModifiable aam) {
		aam.makePublic();
		return aam.isPublic();
	}
}
