package cipm.consistency.initialisers.jamopp.members;

import org.emftext.language.java.members.MembersFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

import org.emftext.language.java.members.AdditionalField;

public class AdditionalFieldInitialiser extends AbstractInitialiserBase implements IAdditionalFieldInitialiser {
	@Override
	public IAdditionalFieldInitialiser newInitialiser() {
		return new AdditionalFieldInitialiser();
	}

	@Override
	public AdditionalField instantiate() {
		return MembersFactory.eINSTANCE.createAdditionalField();
	}
}
