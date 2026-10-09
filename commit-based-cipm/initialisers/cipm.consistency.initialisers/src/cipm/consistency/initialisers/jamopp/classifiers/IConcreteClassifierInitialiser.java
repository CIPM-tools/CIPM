package cipm.consistency.initialisers.jamopp.classifiers;

import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.containers.Package;

import cipm.consistency.initialisers.jamopp.generics.ITypeParametrizableInitialiser;
import cipm.consistency.initialisers.jamopp.members.IMemberContainerInitialiser;
import cipm.consistency.initialisers.jamopp.members.IMemberInitialiser;
import cipm.consistency.initialisers.jamopp.modifiers.IAnnotableAndModifiableInitialiser;
import cipm.consistency.initialisers.jamopp.statements.IStatementInitialiser;

/**
 * An interface meant for {@link IInitialiser} implementors that are supposed to
 * create {@link ConcreteClassifier} instances. <br>
 * <br>
 * <b>Note: {@link #setPackage(ConcreteClassifier, Package)} does not add the
 * given {@link ConcreteClassifier} to the given {@link Package}.</b>
 * 
 * @author Alp Torac Genc
 *
 */
public interface IConcreteClassifierInitialiser extends IAnnotableAndModifiableInitialiser, IMemberContainerInitialiser,
		IMemberInitialiser, IStatementInitialiser, IClassifierInitialiser, ITypeParametrizableInitialiser {

	@Override
	public ConcreteClassifier instantiate();

	/**
	 * Sets the package of cls as pac. <br>
	 * <br>
	 * <b>Note: DOES NOT modify the classifiers contained by pac.</b>
	 * 
	 * @see {@link IConcreteClassifierInitialiser}
	 */
	public default boolean setPackage(ConcreteClassifier cls, Package pac) {
		cls.setPackage(pac);
		return (pac == null && cls.getPackage() == null) || cls.getPackage().equals(pac);
	}
}
