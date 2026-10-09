package cipm.consistency.fitests.similarity.jamopp;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.params.provider.Arguments;

import cipm.consistency.fluentapi.java.api.ApiFactory;
import cipm.consistency.fluentapi.java.api.FluentJavaAPI;

public final class JaMoPPArguments {
	private static final FluentJavaAPI api = ApiFactory.eINSTANCE.createFluentJavaAPI();

	private static final List<Class<? extends EObject>> supportedClasses = api.getAllSupportedClasses();

	public static List<Class<? extends EObject>> getAllConcreteClasses() {
		return supportedClasses.stream().map((cls) -> (Class<? extends EObject>) cls).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	public static <T extends EObject> List<Class<? extends T>> getAllConcreteClassesBySuper(Class<T> superType) {
		return supportedClasses.stream().filter((cls) -> superType.isAssignableFrom(cls))
				.map((cls) -> (Class<? extends T>) cls).collect(Collectors.toList());
	}

	public static Stream<Arguments> getAllConcreteClassesAsArgs() {
		return toArgStream(getAllConcreteClasses());
	}

	public static Stream<Arguments> getAllConcreteClassesBySuperAsArgs(Class<? extends EObject> superType) {
		return toArgStream(getAllConcreteClassesBySuper(superType));
	}

	private static <T, C extends Collection<Class<? extends T>>> Stream<Arguments> toArgStream(C col) {
		return col.stream().map((c) -> Arguments.of(c, generateDisplayNameForInit(c)));
	}

	/**
	 * Used by the static get...InitialiserArgumentsFor methods within this class.
	 * 
	 * @return A display name associated with the given initialiser, which can be
	 *         used by parameterised tests.
	 */
	public static String generateDisplayNameForInit(Class<?> cls) {
		return cls.getSimpleName();
	}
}
