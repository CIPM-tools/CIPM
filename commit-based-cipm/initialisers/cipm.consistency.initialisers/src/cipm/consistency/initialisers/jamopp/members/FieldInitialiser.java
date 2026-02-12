package cipm.consistency.initialisers.jamopp.members;

import org.emftext.language.java.members.Field;
import org.emftext.language.java.members.MembersFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class FieldInitialiser extends AbstractInitialiserBase implements IFieldInitialiser {
	@Override
	public Field instantiate() {
		return MembersFactory.eINSTANCE.createField();
	}

	@Override
	public FieldInitialiser newInitialiser() {
		return new FieldInitialiser();
	}
}
