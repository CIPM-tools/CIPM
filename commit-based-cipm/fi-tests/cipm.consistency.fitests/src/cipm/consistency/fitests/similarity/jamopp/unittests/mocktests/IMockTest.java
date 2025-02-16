package cipm.consistency.fitests.similarity.jamopp.unittests.mocktests;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.function.Predicate;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import cipm.consistency.initialisers.jamopp.JaMoPPHelper;

/**
 * An interface that provides methods for tests that use mock elements to
 * increase test coverage. This interface also helps reduce dependencies to
 * Mockito in its implementing test classes and centralise the mocking methods
 * they use.
 * 
 * @author Alp Torac Genc
 */
public interface IMockTest {
	/**
	 * @see {@link JaMoPPHelper#getAllClasses()}
	 */
	public static Collection<Class<? extends EObject>> getAllClasses() {
		return new JaMoPPHelper().getAllClasses(null);
	}

	/**
	 * @see {@link JaMoPPHelper#getAllClasses(Predicate)}
	 */
	public static Collection<Class<? extends EObject>> getAllClasses(Predicate<EClass> pred) {
		return new JaMoPPHelper().getAllClasses(pred);
	}

	/**
	 * @see {@link JaMoPPHelper#getAllEClasses()}
	 */
	public static Collection<EClass> getAllEClasses() {
		return new JaMoPPHelper().getAllEClasses();
	}

	/**
	 * @see {@link JaMoPPHelper#getEClassForJavaElement(Class)}
	 */
	public default EClass getEClassForJavaElement(Class<? extends EObject> cls) {
		return new JaMoPPHelper().getEClassForJavaElement(cls);
	}

	/**
	 * @see {@link JaMoPPHelper#getEClassForJavaElementImpl(Class)}
	 */
	public default EClass getEClassForJavaElementImpl(Class<? extends EObject> cls) {
		return new JaMoPPHelper().getEClassForJavaElementImpl(cls);
	}

	/**
	 * Mocks an {@link EObject} sub-type and overrides the {@code mock.eClass()}
	 * method, so that the corresponding similarity checking mechanism can be found
	 * without throwing null pointer exceptions.
	 * 
	 * @param <T>       The type of the mock that will be returned
	 * @param clsToMock The class that will be mocked
	 * @return A mock that is an instance of the given class
	 */
	public default <T extends EObject> T mockEObject(Class<T> clsToMock) {
		var mockedClass = mock(clsToMock);
		when(mockedClass.eClass()).thenReturn(this.getEClassForJavaElement(clsToMock));
		return mockedClass;
	}

	/**
	 * Mocks a concrete implementation type of an {@link EObject} sub-type and
	 * overrides the {@code mock.eClass()} method, so that the corresponding
	 * similarity checking mechanism can be found without throwing null pointer
	 * exceptions. <br>
	 * <br>
	 * <b>Note that clsToMock has to represent a concrete implementation type
	 * xImpl.</b>
	 * 
	 * @param <T>       The type of the mock that will be returned
	 * @param clsToMock The class that will be mocked
	 * @return A mock that is an instance of the given class
	 * 
	 * @see {@link #mockEObject(Class)} for mocking {@link EObject} sub-types
	 */
	public default <T extends EObject> T mockEObjectImpl(Class<T> clsToMock) {
		var mockedClass = mock(clsToMock);
		when(mockedClass.eClass()).thenReturn(this.getEClassForJavaElementImpl(clsToMock));
		return mockedClass;
	}

	/**
	 * Mocks the given class and overrides its eContainer with the given container,
	 * so that {@code mock.eContainer() == container}.
	 * 
	 * @return A mock of the given class, which is an instance of that class.
	 * 
	 * @see {@link #mockEObject(Class)}
	 */
	public default <T extends EObject> T mockEObjectWithContainer(Class<T> clsToMock, EObject container) {
		var mockedClass = this.mockEObject(clsToMock);
		when(mockedClass.eContainer()).thenReturn(container);
		return mockedClass;
	}

	/**
	 * Wraps the given object with a mock object. Can be used to make the type of
	 * objToWrap be equal to the type of a mock object of the same type. For the
	 * current similarity checking to work as intended, types of both objects have
	 * to be equal.
	 * 
	 * @param <T>       The type of the object to be wrapped
	 * @param objToWrap The object to be wrapped
	 * @return A mock object wrapping the given object, which will delegate all
	 *         incoming method calls to the object it wraps.
	 */
	public default <T extends EObject> T spyEObject(T objToWrap) {
		return spy(objToWrap);
	}
}
