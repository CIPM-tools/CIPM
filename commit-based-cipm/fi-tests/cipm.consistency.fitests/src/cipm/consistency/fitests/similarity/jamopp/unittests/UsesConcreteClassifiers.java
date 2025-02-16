package cipm.consistency.fitests.similarity.jamopp.unittests;

import org.emftext.language.java.classifiers.ConcreteClassifier;

import cipm.consistency.initialisers.jamopp.classifiers.ClassInitialiser;
import cipm.consistency.initialisers.jamopp.classifiers.IConcreteClassifierInitialiser;
import cipm.consistency.initialisers.jamopp.containers.CompilationUnitInitialiser;

import org.emftext.language.java.classifiers.Class;

/**
 * An interface that can be implemented by tests, which work with
 * {@link ConcreteClassifier} instances. <br>
 * <br>
 * Contains methods that can be used to create {@link ConcreteClassifier}
 * instances.
 */
public interface UsesConcreteClassifiers extends UsesPackages, UsesNames {
	/**
	 * @return A {@link Class} instance without setting any of its attributes
	 */
	public default Class createMinimalClass() {
		return new ClassInitialiser().instantiate();
	}

	/**
	 * @return A {@link Class} instance contained in a {@link CompilationUnit}
	 *         instance. Attributes of neither the {@link Class} instance nor the
	 *         {@link CompilationUnit} instance are set.
	 */
	public default Class createMinimalClassWithCU() {
		var cls = this.createMinimalClass();

		var cuInit = new CompilationUnitInitialiser();
		var cu = cuInit.instantiate();
		cuInit.addClassifier(cu, cls);

		return cls;
	}
	
	/**
	 * @param init    The initialiser that will be used to create the instance
	 * @param clsName The name of the instance to be constructed
	 * @return A {@link ConcreteClassifier} instance with the given name. The
	 *         concrete type of the instance depends on init.
	 */
	public default ConcreteClassifier createMinimalClassifier(IConcreteClassifierInitialiser init, String clsName) {
		ConcreteClassifier result = init.instantiate();
		init.setName(result, clsName);
		return result;
	}

	/**
	 * @param init    The initialiser that will be used to create the instance
	 * @param clsName The name of the instance to be constructed
	 * @param cuName  The name of the {@link CompilationUnit} that will contain the
	 *                created instance
	 * @return A {@link ConcreteClassifier} instance that is contained by a
	 *         {@link CompilationUnit}. The given parameters are used during both of
	 *         their construction.
	 */
	public default ConcreteClassifier createMinimalClassifierWithCU(IConcreteClassifierInitialiser init, String clsName,
			String cuName) {
		ConcreteClassifier result = init.instantiate();
		init.setName(result, clsName);

		var cuInit = new CompilationUnitInitialiser();
		var cu = cuInit.instantiate();
		cuInit.setName(cu, cuName);
		cuInit.addClassifier(cu, result);

		return result;
	}

	/**
	 * @param init    The initialiser that will be used to create the instance
	 * @param clsName The name of the instance to be constructed
	 * @param pacNss  See {@link #createMinimalPackage(String[])}
	 * @return A {@link ConcreteClassifier} instance that will be contained by a
	 *         {@link Package} constructed by
	 *         {@link #createMinimalPackage(String[])}.
	 */
	public default ConcreteClassifier createMinimalClassifierWithPac(IConcreteClassifierInitialiser init,
			String clsName, String[] pacNss) {
		ConcreteClassifier result = init.instantiate();
		init.setName(result, clsName);
		init.setPackage(result, this.createMinimalPackage(pacNss));
		return result;
	}

	/**
	 * A variant of
	 * {@link #createMinimalClassifierWithPac(IConcreteClassifierInitialiser, String, String[])},
	 * where the constructed instance is of type {@link Class}.
	 */
	public default Class createMinimalClassWithPac(String clsName, String[] pacNss) {
		return (Class) this.createMinimalClassifierWithPac(new ClassInitialiser(), clsName, pacNss);
	}

	/**
	 * A variant of
	 * {@link #createMinimalClassifierWithCU(IConcreteClassifierInitialiser, String, String)},
	 * where the name of the constructed {@link CompilationUnit} is
	 * {@link #getDefaultName()}.
	 */
	public default ConcreteClassifier createMinimalClassifierWithCU(IConcreteClassifierInitialiser init,
			String clsName) {
		return this.createMinimalClassifierWithCU(init, clsName, this.getDefaultName());
	}

	/**
	 * A variant of
	 * {@link #createMinimalClassifier(IConcreteClassifierInitialiser, String)},
	 * where a {@link Class} instance with the given parameter is created.
	 */
	public default Class createMinimalClass(String clsName) {
		return (Class) this.createMinimalClassifier(new ClassInitialiser(), clsName);
	}

	/**
	 * A variant of
	 * {@link #createMinimalClassifierWithCU(IConcreteClassifierInitialiser, String)},
	 * where the constructed instance is of type {@link Class} with the given
	 * parameter.
	 */
	public default Class createMinimalClassWithCU(String clsName) {
		return (Class) this.createMinimalClassifierWithCU(new ClassInitialiser(), clsName);
	}
}