package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.modifiers.Abstract;
import org.emftext.language.java.modifiers.Default;
import org.emftext.language.java.modifiers.Final;
import org.emftext.language.java.modifiers.Native;
import org.emftext.language.java.modifiers.Private;
import org.emftext.language.java.modifiers.Protected;
import org.emftext.language.java.modifiers.Public;
import org.emftext.language.java.modifiers.Static;
import org.emftext.language.java.modifiers.Strictfp;
import org.emftext.language.java.modifiers.Synchronized;
import org.emftext.language.java.modifiers.Transient;
import org.emftext.language.java.modifiers.Volatile;

import cipm.consistency.initialisers.jamopp.modifiers.AbstractInitialiser;
import cipm.consistency.initialisers.jamopp.modifiers.DefaultInitialiser;
import cipm.consistency.initialisers.jamopp.modifiers.FinalInitialiser;
import cipm.consistency.initialisers.jamopp.modifiers.NativeInitialiser;
import cipm.consistency.initialisers.jamopp.modifiers.PrivateInitialiser;
import cipm.consistency.initialisers.jamopp.modifiers.ProtectedInitialiser;
import cipm.consistency.initialisers.jamopp.modifiers.PublicInitialiser;
import cipm.consistency.initialisers.jamopp.modifiers.StaticInitialiser;
import cipm.consistency.initialisers.jamopp.modifiers.StrictfpInitialiser;
import cipm.consistency.initialisers.jamopp.modifiers.SynchronizedInitialiser;
import cipm.consistency.initialisers.jamopp.modifiers.TransientInitialiser;
import cipm.consistency.initialisers.jamopp.modifiers.VolatileInitialiser;

/**
 * An interface that can be implemented by tests, which work with
 * {@link Modifier} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link Modifier} instances.
 */
public interface UsesModifiers {
	public default Default createDefault() {
		return new DefaultInitialiser().instantiate();
	}

	public default Final createFinal() {
		return new FinalInitialiser().instantiate();
	}

	public default Native createNative() {
		return new NativeInitialiser().instantiate();
	}

	public default Private createPrivate() {
		return new PrivateInitialiser().instantiate();
	}

	public default Public createPublic() {
		return new PublicInitialiser().instantiate();
	}

	public default Static createStatic() {
		return new StaticInitialiser().instantiate();
	}

	public default Strictfp createStrictfp() {
		return new StrictfpInitialiser().instantiate();
	}

	public default Transient createTransient() {
		return new TransientInitialiser().instantiate();
	}

	public default Abstract createAbstract() {
		return new AbstractInitialiser().instantiate();
	}

	public default Synchronized createSynchronized() {
		return new SynchronizedInitialiser().instantiate();
	}

	public default Volatile createVolatile() {
		return new VolatileInitialiser().instantiate();
	}

	public default Protected createProtected() {
		return new ProtectedInitialiser().instantiate();
	}
}
