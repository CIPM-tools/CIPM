package cipm.consistency.fluentapi.test;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EcoreFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cipm.consistency.fluentapi.extensions.FluentAPIMarkExtension;
import cipm.consistency.fluentapi.extensions.FluentAPIWaitForMarkExtension;

/**
 * A test class meant to test {@link FluentAPIWaitForMarkExtensionTest}.
 * 
 * @author Alp Torac Genc
 */
public class FluentAPIWaitForMarkExtensionTest {
	/**
	 * Resets {@link FluentAPIMarkExtension} and
	 * {@link FluentAPIWaitForMarkExtension}
	 */
	@BeforeEach
	public void setUp() {
		FluentAPIMarkExtension.clearAllMarks();
		FluentAPIWaitForMarkExtension.clearAllTasks();
	}

	/**
	 * @param list1 A list
	 * @param list2 Another list
	 * @return Whether all elements of the lists are reference-equal
	 */
	private boolean areAllElementsSame(List<?> list1, List<?> list2) {
		if (list1 == list2)
			return true;
		if (list1 == null ^ list2 == null)
			return false;
		if (list1.size() != list2.size())
			return false;
		return list2.stream().allMatch((sle) -> list1.stream().anyMatch((fle) -> fle == sle));
	}

	/**
	 * Asserts that all given tasks have been added and are waiting on exactly the
	 * given markKeys to be involved in a mark.
	 */
	private void assertTaskPending(List<Object> markKeys, List<Runnable> tasks) {
		Assertions.assertTrue(FluentAPIWaitForMarkExtension.getAllPendingTasks().entrySet().stream()
				.anyMatch((e) -> areAllElementsSame(e.getKey(), markKeys) && areAllElementsSame(e.getValue(), tasks)));
		Assertions.assertTrue(FluentAPIWaitForMarkExtension.hasPendingTasks(markKeys));

		Assertions.assertTrue(areAllElementsSame(tasks, FluentAPIWaitForMarkExtension.getPendingTasks(markKeys)));
		Assertions.assertTrue(
				areAllElementsSame(tasks, FluentAPIWaitForMarkExtension.getPendingTasks(markKeys.toArray())));

		var allRequiredKeys = FluentAPIWaitForMarkExtension.getAllRequiredMarkKeys();
		for (var r : tasks) {
			var requiredKeys = FluentAPIWaitForMarkExtension.getRequiredMarkKeysFor(r);
			Assertions.assertTrue(allRequiredKeys.containsKey(r));
			Assertions.assertNotEquals(0, requiredKeys.size());
		}
	}

	/**
	 * Asserts that all given tasks have been added and are waiting on exactly the
	 * given markKey to be involved in a mark.
	 */
	private void assertTaskPending(Object markKey, List<Runnable> tasks) {
		assertTaskPending(List.of(markKey), tasks);

		Assertions.assertTrue(areAllElementsSame(tasks, FluentAPIWaitForMarkExtension.getPendingTasks(markKey)));
		Assertions.assertTrue(FluentAPIWaitForMarkExtension.hasPendingTasks(markKey));
	}

	/**
	 * Asserts that the given task has been added and is waiting on exactly the
	 * given markKey to be involved in a mark.
	 */
	private void assertTaskPending(Object markKey, Runnable task) {
		assertTaskPending(markKey, List.of(task));
	}

	/**
	 * Asserts that the given task has been added and is waiting on exactly the
	 * given markKeys to be involved in a mark.
	 */
	private void assertTaskPending(List<Object> markKey, Runnable task) {
		assertTaskPending(markKey, List.of(task));
	}

	/**
	 * Asserts that the given task has been added and is waiting on exactly the
	 * given markKeys to be involved in a mark.
	 */
	private void assertTaskPending(Object[] markKey, Runnable task) {
		assertTaskPending(List.of(markKey), List.of(task));
	}

	/**
	 * Asserts that the given task has been added exactly duplicateCount times and
	 * is waiting for key.
	 * 
	 * @param key            The markKey
	 * @param task           The task
	 * @param duplicateCount Amount of duplicated task occurrences
	 */
	private void assertPendingTaskCountEquals(Object key, Runnable task, int duplicateCount) {
		var pendingTasks = FluentAPIWaitForMarkExtension.getPendingTasks(key);
		var pendingDuplicatedTasks = pendingTasks != null
				? pendingTasks.stream().filter((r) -> r == task).collect(Collectors.toList())
				: List.of();
		Assertions.assertEquals(duplicateCount, pendingDuplicatedTasks.size());
	}

	/**
	 * Asserts that none of the given tasks are added and are not waiting on
	 * markKeys to be involved in a mark.
	 */
	private void assertTaskNotPending(List<Object> markKeys, List<Runnable> tasks) {
		Assertions.assertTrue(FluentAPIWaitForMarkExtension.getAllPendingTasks().entrySet().stream()
				.noneMatch((e) -> areAllElementsSame(e.getKey(), markKeys) && areAllElementsSame(e.getValue(), tasks)));
		Assertions.assertFalse(areAllElementsSame(tasks, FluentAPIWaitForMarkExtension.getPendingTasks(markKeys)));
		Assertions.assertFalse(
				areAllElementsSame(tasks, FluentAPIWaitForMarkExtension.getPendingTasks(markKeys.toArray())));

		var allRequiredKeys = FluentAPIWaitForMarkExtension.getAllRequiredMarkKeys();
		for (var r : tasks) {
			var requiredKeys = FluentAPIWaitForMarkExtension.getRequiredMarkKeysFor(r);
			Assertions.assertTrue(requiredKeys.isEmpty());
			Assertions.assertNull(allRequiredKeys.get(r));
		}
	}

	/**
	 * Asserts that none of the given tasks are added and are not waiting on markKey
	 * to be involved in a mark.
	 */
	private void assertTaskNotPending(Object markKey, List<Runnable> tasks) {
		assertTaskNotPending(List.of(markKey), tasks);

		Assertions.assertFalse(areAllElementsSame(tasks, FluentAPIWaitForMarkExtension.getPendingTasks(markKey)));
	}

	/**
	 * Asserts that the given task is not added and is not waiting on markKey to be
	 * involved in a mark.
	 */
	private void assertTaskNotPending(Object markKey, Runnable task) {
		assertTaskNotPending(markKey, List.of(task));
	}

	/**
	 * Asserts that the given task is not added and is not waiting on markKeys to be
	 * involved in a mark.
	 */
	private void assertTaskNotPending(List<Object> markKeys, Runnable task) {
		assertTaskNotPending(markKeys, List.of(task));
	}

	/**
	 * Asserts that adding a single task with a single key works as intended,
	 * regardless of it being triggered.
	 */
	@Test
	public void testAddTask_SingleKey() {
		var key = new Object();
		Runnable r = () -> {
		};

		FluentAPIWaitForMarkExtension.addTask(key, r);
		assertTaskPending(key, r);
		var neededKeys = FluentAPIWaitForMarkExtension.getRequiredMarkKeysFor(r);
		Assertions.assertEquals(1, neededKeys.size());
		Assertions.assertEquals(1, neededKeys.get(0).size());
		Assertions.assertSame(key, neededKeys.get(0).get(0));
	}

	/**
	 * Asserts that adding a single task with an array of keys works as intended,
	 * regardless of it being triggered. Since
	 * {@link FluentAPIWaitForMarkExtension#addTask(Object, Runnable)} considers
	 * Object as its first parameter and is overloaded, ensuring that the correct
	 * method is used becomes particularly important. Otherwise, a key array keyArr
	 * = [key1, key2] itself can be considered as one key (so instead of key1 and
	 * key2, keyCol itself would mark the object).
	 */
	@Test
	public void testAddTask_KeyArray() {
		var keyOne = new Object();
		var keyTwo = new Object();
		var key = new Object[] { keyOne, keyTwo };
		Runnable r = () -> {
		};

		FluentAPIWaitForMarkExtension.addTask(key, r);
		assertTaskPending(key, r);
		var neededKeys = FluentAPIWaitForMarkExtension.getRequiredMarkKeysFor(r);
		Assertions.assertEquals(1, neededKeys.size());
		Assertions.assertEquals(2, neededKeys.get(0).size());
		for (int i = 0; i < key.length; i++) {
			Assertions.assertSame(key[i], neededKeys.get(0).get(i));
		}
	}

	/**
	 * Asserts that adding a single task with a collection of keys works as
	 * intended, regardless of it being triggered. Since
	 * {@link FluentAPIWaitForMarkExtension#addTask(Object, Runnable)} considers
	 * Object as its first parameter and is overloaded, ensuring that the correct
	 * method is used becomes particularly important. Otherwise, a key collection
	 * keyCol = [key1, key2] itself can be considered as one key (so instead of key1
	 * and key2, keyCol itself would mark the object).
	 */
	@Test
	public void testAddTask_KeyCollection() {
		var keyOne = new Object();
		var keyTwo = new Object();
		var key = List.of(keyOne, keyTwo);
		Runnable r = () -> {
		};

		FluentAPIWaitForMarkExtension.addTask(key, r);
		assertTaskPending(key, r);
		var neededKeys = FluentAPIWaitForMarkExtension.getRequiredMarkKeysFor(r);
		Assertions.assertEquals(1, neededKeys.size());
		Assertions.assertEquals(2, neededKeys.get(0).size());
		for (int i = 0; i < key.size(); i++) {
			Assertions.assertSame(key.get(i), neededKeys.get(0).get(i));
		}
	}

	/**
	 * Ensures that adding a single task waiting on a single markKey, as well as
	 * triggering it, work as intended.
	 */
	@Test
	public void testAddTask_OneKeyOneTask() {
		final var ran = new boolean[] { false };
		var key = new Object();
		Runnable r = () -> ran[0] = true;

		assertTaskNotPending(key, r);

		// Add the task, make sure it does not trigger
		FluentAPIWaitForMarkExtension.addTask(key, r);
		assertTaskPending(key, r);
		Assertions.assertFalse(ran[0]);

		// Add the mark, ensure task triggers
		FluentAPIMarkExtension.mark(key, EcoreFactory.eINSTANCE.createEObject());
		assertTaskNotPending(key, r);
		Assertions.assertTrue(ran[0]);
	}

	/**
	 * Ensures that adding multiple task waiting on a single markKey, as well as
	 * triggering them, work as intended.
	 */
	@Test
	public void testAddTask_OneKeyManyTask() {
		final var ran = new boolean[] { false, false };
		var key = new Object();

		Runnable r1 = () -> ran[0] = true;
		Runnable r2 = () -> ran[1] = true;

		// Add the tasks, make sure they do not trigger
		FluentAPIWaitForMarkExtension.addTask(key, r1);
		FluentAPIWaitForMarkExtension.addTask(key, r2);
		assertTaskPending(key, List.of(r1, r2));
		Assertions.assertFalse(ran[0]);
		Assertions.assertFalse(ran[1]);

		// Add the mark, ensure both tasks trigger
		FluentAPIMarkExtension.mark(key, EcoreFactory.eINSTANCE.createEObject());
		assertTaskNotPending(key, r1);
		assertTaskNotPending(key, r2);
		Assertions.assertTrue(ran[0]);
		Assertions.assertTrue(ran[1]);
	}

	/**
	 * Ensures that unmarking an existing key and then adding a task waiting on that
	 * key results in the task not running.
	 */
	@Test
	public void testAddTask_OneKeyManyTask_UnmarkInBetween() {
		final var ran = new boolean[] { false, false };
		var key = new Object();

		Runnable r1 = () -> ran[0] = true;
		Runnable r2 = () -> ran[1] = true;

		// Add first task and trigger it as usual, make sure second task does not
		// trigger
		FluentAPIWaitForMarkExtension.addTask(key, r1);
		assertTaskPending(key, r1);
		Assertions.assertFalse(ran[0]);

		// Add the mark, ensure that the first task triggers and second task does not
		FluentAPIMarkExtension.mark(key, EcoreFactory.eINSTANCE.createEObject());
		Assertions.assertTrue(ran[0]);
		Assertions.assertFalse(ran[1]);

		// Unmark the mutual key
		FluentAPIMarkExtension.unmark(key);

		// Add second task and ensure it does not trigger
		FluentAPIWaitForMarkExtension.addTask(key, r2);
		assertTaskPending(key, r2);
		Assertions.assertFalse(ran[1]);

		// Re-add the mark and ensure the second task triggers
		FluentAPIMarkExtension.mark(key, EcoreFactory.eINSTANCE.createEObject());
		assertTaskNotPending(key, r2);
		Assertions.assertTrue(ran[1]);
	}

	/**
	 * Ensures that adding a task waiting on multiple keys, as well as triggering
	 * it, work as intended.
	 */
	@Test
	public void testAddTask_ManyKeyOneTask() {
		final var ran = new boolean[] { false };
		var key1 = new Object();
		var key2 = new Object();
		Runnable r = () -> ran[0] = true;

		var keyList = List.of(key1, key2);

		assertTaskNotPending(keyList, r);

		// Add the task, make sure it does not trigger
		FluentAPIWaitForMarkExtension.addTask(keyList, r);
		assertTaskPending(keyList, r);
		Assertions.assertFalse(ran[0]);

		// Add the first mark, make sure task does not trigger
		FluentAPIMarkExtension.mark(key1, EcoreFactory.eINSTANCE.createEObject());
		assertTaskPending(keyList, r);
		Assertions.assertFalse(ran[0]);

		// Add the second mark, make sure task triggers
		FluentAPIMarkExtension.mark(key2, EcoreFactory.eINSTANCE.createEObject());
		assertTaskNotPending(keyList, r);
		Assertions.assertTrue(ran[0]);
	}

	/**
	 * Ensures that adding a task waiting on multiple keys, as well as triggering
	 * it, work as intended; if one of the keys gets unmarked before all marks are
	 * present.
	 */
	@Test
	public void testAddTask_ManyKeyOneTask_UnmarkInBetween() {
		final var ran = new boolean[] { false };
		var key1 = new Object();
		var key2 = new Object();
		var key3 = new Object();
		Runnable r = () -> ran[0] = true;

		var keyList = List.of(key1, key2, key3);

		assertTaskNotPending(keyList, r);

		// Add the task, make sure it does not trigger
		FluentAPIWaitForMarkExtension.addTask(keyList, r);
		assertTaskPending(keyList, r);
		Assertions.assertFalse(ran[0]);

		// Add the first mark, make sure task does not trigger
		FluentAPIMarkExtension.mark(key1, EcoreFactory.eINSTANCE.createEObject());
		assertTaskPending(keyList, r);
		Assertions.assertFalse(ran[0]);

		// Add the second mark, make sure task does not trigger
		FluentAPIMarkExtension.mark(key2, EcoreFactory.eINSTANCE.createEObject());
		assertTaskPending(keyList, r);
		Assertions.assertFalse(ran[0]);

		// Remove the first mark
		FluentAPIMarkExtension.unmark(key1);

		// Add the third mark, make sure task does not trigger (since the second mark is
		// unmarked)
		FluentAPIMarkExtension.mark(key3, EcoreFactory.eINSTANCE.createEObject());
		assertTaskPending(keyList, r);
		Assertions.assertFalse(ran[0]);

		// Re-add the first mark, make sure task triggers (since all marks are present)
		FluentAPIMarkExtension.mark(key1, EcoreFactory.eINSTANCE.createEObject());
		assertTaskNotPending(keyList, r);
		Assertions.assertTrue(ran[0]);
	}

	/**
	 * Checks whether nested tasks for the same key work as intended, i.e. both of
	 * them trigger upon the given key getting used to mark an element.
	 */
	@Test
	public void testAddTask_OneKeyOneNestedTask() {
		var keyOne = new Object();
		final var taskRan = new boolean[] { false, false };

		/*
		 * outerTask runs and adds innerTask during its execution (for the same key,
		 * keyOne)
		 */
		Runnable innerTask = () -> taskRan[1] = true;
		Runnable outerTask = () -> {
			taskRan[0] = true;
			FluentAPIWaitForMarkExtension.addTask(keyOne, innerTask);
		};

		// Add the outerTask, ensure that neither it nor innerTask trigger
		FluentAPIWaitForMarkExtension.addTask(keyOne, outerTask);
		Assertions.assertFalse(taskRan[0]);
		Assertions.assertFalse(taskRan[1]);

		// Add the mark, ensure that outerTask and innerTask trigger
		FluentAPIMarkExtension.mark(keyOne, EcoreFactory.eINSTANCE.createEObject());
		Assertions.assertTrue(taskRan[0]);
		Assertions.assertTrue(taskRan[1]);
	}

	/**
	 * Checks whether nested tasks for the same key work as intended, if the
	 * corresponding mark is unmarked; i.e. the outer task triggers upon the given
	 * key getting used to mark an element, the inner task triggers once that key is
	 * re-used to mark an element.
	 */
	@Test
	public void testAddTask_OneKeyOneNestedTask_UnmarkInBetween() {
		var keyOne = new Object();
		final var taskRan = new boolean[] { false, false };

		/*
		 * outerTask runs, unmarks its key (keyOne) and then adds innerTask during its
		 * execution (for the same key, keyOne)
		 */
		Runnable innerTask = () -> taskRan[1] = true;
		Runnable outerTask = () -> {
			taskRan[0] = true;
			FluentAPIMarkExtension.unmark(keyOne);
			FluentAPIWaitForMarkExtension.addTask(keyOne, innerTask);
		};

		// Add the outerTask, ensure that neither it nor innerTask trigger
		FluentAPIWaitForMarkExtension.addTask(keyOne, outerTask);
		Assertions.assertFalse(taskRan[0]);
		Assertions.assertFalse(taskRan[1]);

		// Add the mark, ensure that outerTask triggers but not innerTask (since
		// outerTask unmarks keyOne)
		FluentAPIMarkExtension.mark(keyOne, EcoreFactory.eINSTANCE.createEObject());
		Assertions.assertTrue(taskRan[0]);
		Assertions.assertFalse(taskRan[1]);

		// Re-add the mark, ensure that innerTask triggers
		FluentAPIMarkExtension.mark(keyOne, EcoreFactory.eINSTANCE.createEObject());
		Assertions.assertTrue(taskRan[1]);
	}

	/**
	 * Checks whether nested tasks for different keys work as intended, if first the
	 * outer task' key and then the inner task' key is used to mark an element. In
	 * this case, first the outer task triggers and issues the inner task, then the
	 * inner task triggers.
	 */
	@Test
	public void testAddTask_ManyKeyOneNestedTask_TriggerInOrder() {
		var keyOne = new Object();
		var keyTwo = new Object();
		final var taskRan = new boolean[] { false, false };

		/*
		 * outerTask runs and adds innerTask during its execution (for a different key,
		 * keyTwo)
		 */
		Runnable innerTask = () -> taskRan[1] = true;
		Runnable outerTask = () -> {
			taskRan[0] = true;
			FluentAPIWaitForMarkExtension.addTask(keyTwo, innerTask);
		};

		// Add the outerTask, ensure that neither it nor innerTask trigger
		FluentAPIWaitForMarkExtension.addTask(keyOne, outerTask);
		Assertions.assertFalse(taskRan[0]);
		Assertions.assertFalse(taskRan[1]);

		// Add the first mark, ensure that outerTask triggers and that innerTask is
		// present but does not trigger (since keyTwo is missing)
		FluentAPIMarkExtension.mark(keyOne, EcoreFactory.eINSTANCE.createEObject());
		Assertions.assertTrue(taskRan[0]);
		Assertions.assertFalse(taskRan[1]);

		// Add the second mark, ensure that innreTask triggers
		FluentAPIMarkExtension.mark(keyTwo, EcoreFactory.eINSTANCE.createEObject());
		Assertions.assertTrue(taskRan[1]);
	}

	/**
	 * Checks whether nested tasks for different keys work as intended, if first the
	 * inner task' key and then the outer task' key is used to mark an element. In
	 * this case, the inner task must wait on the outer task to trigger (even if the
	 * inner task' key is used to mark an element), because the outer task issues
	 * the inner task. Once the outer task triggers, the inner task triggers
	 * immediately afterward.
	 */
	@Test
	public void testAddTask_ManyKeyOneNestedTask_InnerWaitsOnOuter() {
		var keyOne = new Object();
		var keyTwo = new Object();
		final var taskRan = new boolean[] { false, false };

		/*
		 * outerTask runs and adds innerTask during its execution (for a different key,
		 * keyTwo)
		 */
		Runnable innerTask = () -> taskRan[1] = true;
		Runnable outerTask = () -> {
			taskRan[0] = true;
			FluentAPIWaitForMarkExtension.addTask(keyTwo, innerTask);
		};

		// Add the outerTask, ensure that neither it nor innerTask trigger
		FluentAPIWaitForMarkExtension.addTask(keyOne, outerTask);
		Assertions.assertFalse(taskRan[0]);
		Assertions.assertFalse(taskRan[1]);

		// Add the second mark, ensure that neither outerTask nor innerTask trigger
		// (since keyOne is missing for outerTask and innerTask is not yet added by the
		// outerTask)
		FluentAPIMarkExtension.mark(keyTwo, EcoreFactory.eINSTANCE.createEObject());
		Assertions.assertFalse(taskRan[0]);
		Assertions.assertFalse(taskRan[1]);

		// Add the first mark, ensure that both tasks trigger (since both keyOne and
		// keyTwo exist)
		FluentAPIMarkExtension.mark(keyOne, EcoreFactory.eINSTANCE.createEObject());
		Assertions.assertTrue(taskRan[0]);
		Assertions.assertTrue(taskRan[1]);
	}

	/**
	 * Ensures that the same task can be added multiple times for the same key.
	 */
	@Test
	public void testAddTask_DuplicatedTask() {
		final var runCount = new int[] { 0 };
		var key = new Object();
		Runnable r = () -> runCount[0]++;

		assertTaskNotPending(key, r);

		// Add the task once, ensure it does not trigger
		FluentAPIWaitForMarkExtension.addTask(key, r);
		assertTaskPending(key, r);
		Assertions.assertEquals(0, runCount[0]);

		// Add the task another time, ensure it is present twice and does not trigger
		FluentAPIWaitForMarkExtension.addTask(key, r);
		assertPendingTaskCountEquals(key, r, 2);
		Assertions.assertEquals(0, runCount[0]);

		// Add the mark, ensure the task runs twice
		FluentAPIMarkExtension.mark(key, EcoreFactory.eINSTANCE.createEObject());
		assertTaskNotPending(key, r);
		Assertions.assertEquals(2, runCount[0]);
	}

	/**
	 * Ensures that the tasks are triggered immediately, if their required keys are
	 * present.
	 */
	@Test
	public void testAddTask_TriggerUponAddingIfMarkExists() {
		final var ran = new boolean[] { false };
		var key = new Object();
		Runnable r = () -> ran[0] = true;

		// Ensure that the task is not added and add the mark
		assertTaskNotPending(key, r);
		FluentAPIMarkExtension.mark(key, EcoreFactory.eINSTANCE.createEObject());
		assertTaskNotPending(key, r);

		// Add the task, ensure that it triggers immediately
		FluentAPIWaitForMarkExtension.addTask(key, r);
		Assertions.assertTrue(ran[0]);
		assertTaskNotPending(key, r);
	}

	/**
	 * Ensures that removing a task waiting on a single key works as intended.
	 */
	@Test
	public void testRemoveTask_SingleKey() {
		final var ran = new boolean[] { false };
		var key = new Object();
		Runnable r = () -> ran[0] = true;

		assertTaskNotPending(key, r);

		// Add the task for key, ensure it does not trigger
		FluentAPIWaitForMarkExtension.addTask(key, r);
		assertTaskPending(key, r);
		Assertions.assertFalse(ran[0]);

		// Remove the task for key, ensure that it is neither present nor triggered
		FluentAPIWaitForMarkExtension.removeTask(key, r);
		assertTaskNotPending(key, r);
		Assertions.assertFalse(ran[0]);
	}

	/**
	 * Ensures that removing a task waiting on multiple keys (and exactly those
	 * keys) works as intended.
	 */
	@Test
	public void testRemoveTask_MultipleKeys_RemoveTaskForExactKeys() {
		final var ran = new boolean[] { false };
		var key1 = new Object();
		var key2 = new Object();
		var keyList = List.of(key1, key2);
		Runnable r = () -> ran[0] = true;

		assertTaskNotPending(keyList, r);

		// Add the task for keyList, ensure it does not trigger
		FluentAPIWaitForMarkExtension.addTask(keyList, r);
		assertTaskPending(keyList, r);
		Assertions.assertFalse(ran[0]);

		// Remove the task for keyList, ensure that it is neither present nor triggered
		FluentAPIWaitForMarkExtension.removeTask(keyList, r);
		assertTaskNotPending(keyList, r);
		Assertions.assertFalse(ran[0]);
	}

	/**
	 * Ensures that a task waiting on multiple keys is not removed, if it is
	 * attempted to be removed for a strict subset of the keys.
	 */
	@Test
	public void testRemoveTask_MultipleKeys_RemoveTaskForKeySubset() {
		final var ran = new boolean[] { false };
		var key1 = new Object();
		var key2 = new Object();
		var keyList = List.of(key1, key2);
		Runnable r = () -> ran[0] = true;

		assertTaskNotPending(keyList, r);

		// Add the task for keyList [key1, key2], ensure it does not trigger
		FluentAPIWaitForMarkExtension.addTask(keyList, r);
		assertTaskPending(keyList, r);
		Assertions.assertFalse(ran[0]);

		// Attempt to remove the task for key1, ensure it is present but does not
		// trigger
		FluentAPIWaitForMarkExtension.removeTask(key1, r);
		assertTaskPending(keyList, r);
		Assertions.assertFalse(ran[0]);
	}

	/**
	 * Ensures that a task waiting on multiple keys is not removed, if it is
	 * attempted to be removed for a strict superset of the keys.
	 */
	@Test
	public void testRemoveTask_MultipleKeys_RemoveTaskForKeySuperset() {
		final var ran = new boolean[] { false };
		var key1 = new Object();
		var key2 = new Object();
		var key3 = new Object();
		var keyListAdd = List.of(key1, key2);
		var keyListRemove = List.of(key1, key2, key3);
		Runnable r = () -> ran[0] = true;

		assertTaskNotPending(keyListAdd, r);

		// Add the task for keyListAdd [key1, key2], ensure it does not trigger
		FluentAPIWaitForMarkExtension.addTask(keyListAdd, r);
		assertTaskPending(keyListAdd, r);
		Assertions.assertFalse(ran[0]);

		// Attempt to remove the task for keyListRemove [key1, key2, key3], ensure it is
		// present but does not trigger
		FluentAPIWaitForMarkExtension.removeTask(keyListRemove, r);
		assertTaskPending(keyListAdd, r);
		Assertions.assertFalse(ran[0]);
	}

	/**
	 * Ensures that for duplicated tasks, only one occurrence is removed at a time.
	 */
	@Test
	public void testRemoveTask_DuplicatedTask() {
		final var runCount = new int[] { 0 };
		var key = new Object();
		Runnable r = () -> runCount[0]++;

		// Add the task for key twice, ensure that it is duplicated
		FluentAPIWaitForMarkExtension.addTask(key, r);
		FluentAPIWaitForMarkExtension.addTask(key, r);
		assertPendingTaskCountEquals(key, r, 2);
		// Ensure that the task is not triggered
		Assertions.assertEquals(0, runCount[0]);

		// Remove one occurrence of task, ensure one occurrence is still present
		FluentAPIWaitForMarkExtension.removeTask(key, r);
		assertPendingTaskCountEquals(key, r, 1);
		// Ensure that the task is not triggered
		Assertions.assertEquals(0, runCount[0]);

		// Remove the last occurrence of task, ensure it is no longer present
		FluentAPIWaitForMarkExtension.removeTask(key, r);
		assertTaskNotPending(key, r);
		// Ensure that the task is not triggered
		Assertions.assertEquals(0, runCount[0]);
	}
}
