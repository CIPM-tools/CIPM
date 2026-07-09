package cipm.consistency.fluentapi.test;

import org.junit.jupiter.api.BeforeEach;

import cipm.consistency.fluentapi.extensions.FluentAPIInitialisationStorage;
import cipm.consistency.fluentapi.extensions.FluentAPIMarkExtension;
import cipm.consistency.fluentapi.extensions.FluentAPIWaitForMarkExtension;

/**
 * An abstract test case class that concrete test case classes for testing
 * fluent api should consider implementing. The purpose of this class is to
 * aggregate common fluent api testing operations.
 * <p>
 * <p>
 * It is recommended to override any methods with {@code @BeforeEach} and
 * {@code @AfterEach} annotations, as well as calling their super versions as
 * the first statement (for BeforeEach methods) or as the last statement (for
 * AfterEach methods).
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractFluentAPITest {
	/**
	 * Prepares for the upcoming test case.
	 * <p>
	 * <p>
	 * AbstractFluentAPITest: Resets all extension classes that can be reset.
	 * 
	 * @see {@link cipm.consistency.fluentapi.extensions}
	 */
	@BeforeEach
	public void setUp() {
		FluentAPIMarkExtension.clearAllMarks();
		FluentAPIWaitForMarkExtension.clearAllTasks();
		FluentAPIInitialisationStorage.clearAllOngoingInitialisations();
	}
}
