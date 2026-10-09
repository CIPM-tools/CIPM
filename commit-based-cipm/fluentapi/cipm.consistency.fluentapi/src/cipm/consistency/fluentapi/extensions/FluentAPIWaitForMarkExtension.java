package cipm.consistency.fluentapi.extensions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;

/**
 * The extension class of fluent api that manages model operations that are to
 * be triggered upon certain marks (markKey, markVal) existing under
 * {@link FluentAPIMarkExtension}. Since this class uses a map to store
 * (requiredMarkKeys, modelBuildingTasks) pairs, where both requiredMarkKeys and
 * modelBuildingTasks are lists, it is possible to duplicate tasks and consider
 * subsets of requiredMarkKeys too.
 * <p>
 * <p>
 * Note: Changing any public member within this file (i.e. either this class or
 * its methods) requires adapting the generation of fluent api. This is due to
 * Java limitations, which do not allow dynamically adjusting static elements,
 * such as method or class names.
 * 
 * @author Alp Torac Genc
 * @see {@link FluentAPIMarkExtension}
 */
public class FluentAPIWaitForMarkExtension {
	/**
	 * The map that contains model building tasks in form of (requiredMarkKeys,
	 * modelBuildingTasks) pairs.
	 */
	private static final Map<List<Object>, List<Runnable>> taskContainer = new LinkedHashMap<>();

	/**
	 * @param markKey A list of required markKey
	 * @return The model building tasks that will trigger exactly when all markKeys
	 *         exist. Tasks that require a subset of markKey are NOT included here.
	 */
	private static Map.Entry<List<Object>, List<Runnable>> getEntryFor(Collection<? extends Object> markKey) {
		return taskContainer.entrySet().stream()
				.filter((e) -> e.getKey().size() == markKey.size() && e.getKey().containsAll(markKey)).findFirst()
				.orElse(null);
	}

	/**
	 * Adds the given task to this class and sets it to trigger, upon markKey being
	 * involved in a mark.
	 * 
	 * @param markKey The markKey, upon which task should trigger
	 * @param task    A model building task
	 * @return {@link #addTask(List, Runnable)}
	 */
	public static boolean addTask(Object markKey, Runnable task) {
		return addTask(List.of(markKey), task);
	}

	/**
	 * Adds the given task to this class and sets it to trigger, upon all markKeys
	 * being involved in a mark.
	 * 
	 * @param markKey A list of markKeys, upon which task should trigger
	 * @param task    A model building task
	 * @return Whether the task has been run or added (currently always returns
	 *         true)
	 */
	public static boolean addTask(Collection<? extends Object> markKey, Runnable task) {
		// Check if the issued task can trigger before adding it
		if (checkMarkPresence(markKey)) {
			task.run();
			return true;
		}

		var entry = getEntryFor(markKey);
		if (entry == null) {
			var runnableList = new ArrayList<Runnable>();
			runnableList.add(task);
			taskContainer.put(List.copyOf(markKey), runnableList);
		} else {
			entry.getValue().add(task);
		}
		return true;
	}

	/**
	 * Removes the given task from this class, which was supposed to trigger, upon
	 * markKey being involved in a mark. Currently, it will only remove a single
	 * occurrence of the task, if it is duplicated.
	 * 
	 * @param markKey The markKey, upon which task should trigger
	 * @param task    A model building task
	 * @return Whether task was removed for markKey
	 */
	public static boolean removeTask(Object markKey, Runnable task) {
		return removeTask(List.of(markKey), task);
	}

	/**
	 * Removes the given task from this class, which was supposed to trigger, upon
	 * all markKeys being involved in a mark. Currently, it will only remove a
	 * single occurrence of the task, if it is duplicated.
	 * 
	 * @param markKey A list of markKeys, upon which task should trigger
	 * @param task    A model building task
	 * @return Whether task was removed for markKey
	 */
	public static boolean removeTask(List<Object> markKey, Runnable task) {
		var entry = getEntryFor(markKey);
		if (entry == null)
			return false;

		var runnableList = entry.getValue();

		var isTaskRemoved = runnableList.remove(task);

		if (isTaskRemoved && runnableList.isEmpty()) {
			taskContainer.remove(entry.getKey());
		}

		return isTaskRemoved;
	}

	/**
	 * @return A map of all (requiredMarkKeys, modelBuildingTasks) pairs, i.e. all
	 *         tasks that have been added to this class, which are still waiting on
	 *         certain markKeys to be involved in a mark to be triggered.
	 */
	public static Map<List<Object>, List<Runnable>> getAllPendingTasks() {
		var markKeysToTask = new LinkedHashMap<List<Object>, List<Runnable>>();
		for (var e : taskContainer.entrySet()) {
			markKeysToTask.put(List.copyOf(e.getKey()), List.copyOf(e.getValue()));
		}
		return markKeysToTask;
	}

	/**
	 * @param markKey The markKey, whose waiting tasks are sought
	 * @return All tasks that have been added to this class, which are still waiting
	 *         on markKey to be involved in a mark to be triggered.
	 */
	public static List<Runnable> getPendingTasks(Object markKey) {
		return getPendingTasks(List.of(markKey));
	}

	/**
	 * @param markKey A list of markKeys, whose waiting tasks are sought
	 * @return All tasks that have been added to this class, which are still waiting
	 *         on markKeys to be involved in a mark to be triggered.
	 */
	public static List<Runnable> getPendingTasks(List<Object> markKey) {
		var entry = getEntryFor(markKey);
		return entry != null ? List.copyOf(entry.getValue()) : null;
	}

	/**
	 * Notifies this class that the mark (markKey, markVal) exists.
	 * 
	 * @param markKey An object that is associated with the model element markVal
	 * @param markVal A model element that is marked with markKey
	 */
	public static void elementMarked(Object markKey, EObject markVal) {
		performIfMarkExists();
	}

	/**
	 * @param markKey A list of markKeys to look for in
	 *                {@link FluentAPIMarkExtension}
	 * @return Whether all given markKeys are involved in marks.
	 */
	private static boolean checkMarkPresence(Collection<? extends Object> markKey) {
		return markKey.stream().allMatch((mk) -> FluentAPIMarkExtension.hasMark(mk));
	}

	/**
	 * Use to get the tasks that can be run.
	 * 
	 * @return A map of all (requiredMarkKeys, modelBuildingTasks) pairs, i.e. all
	 *         tasks that have been added to this class, whose required markKeys are
	 *         involved in marks.
	 */
	@SuppressWarnings("unchecked")
	private static Map<List<Object>, List<Runnable>> getExecutableTasks() {
		var entries = taskContainer.entrySet().stream()
				// Check whether each markKey has a markVal present. Must check for all entries
				// of taskCon, in order to account for potentially nested tasks
				.filter((e) -> checkMarkPresence(e.getKey()))
				// Get all entries with executable tasks
				.toArray(Map.Entry[]::new);
		return Map.ofEntries(entries);
	}

	/**
	 * Returns markKey combinations, such that executing the given task becomes
	 * possible.
	 * 
	 * @param task A model building task
	 * @return A list MKLS of markKey lists MKL_i, such that upon all of one MKL_i's
	 *         markKeys being involved in marks the task will trigger.
	 */
	public static List<List<Object>> getRequiredMarkKeysFor(Runnable task) {
		return taskContainer.entrySet().stream().filter((e) -> e.getValue().contains(task))
				.map((e) -> e.getKey().stream().filter((mk) -> !FluentAPIMarkExtension.hasMark(mk))
						.collect(Collectors.toUnmodifiableList()))
				.filter((l) -> !l.isEmpty()).collect(Collectors.toUnmodifiableList());
	}

	/**
	 * Upon one of the markKey lists being fulfilled, the task can be run.
	 * 
	 * @return A map of tasks and all possible combinations of markKeys to trigger
	 *         them (task, requiredMarkKeys).
	 */
	public static Map<Runnable, List<List<Object>>> getAllRequiredMarkKeys() {
		var result = new LinkedHashMap<Runnable, List<List<Object>>>();

		// Use a set as collector, since tasks cannot be duplicated in a map (as keys of
		// the map)
		var runnableList = taskContainer.values().stream().flatMap((rl) -> rl.stream())
				.collect(Collectors.toUnmodifiableSet());
		for (var r : runnableList) {
			result.put(r, getRequiredMarkKeysFor(r));
		}
		return result;
	}

	/**
	 * Executes all tasks, whose required markKeys are all involved in marks.
	 * <p>
	 * <p>
	 * No need to pass markVal here (object marked with markKey), since it will have
	 * to be found later in code and is therefore irrelevant here.
	 */
	private static int performIfMarkExists() {
		final var count = new int[1];

		/*
		 * For each task with any mutual mark keys with markKey, check if all its mark
		 * keys are present. If so, perform the task(s).
		 */
		var cList = getExecutableTasks();
		if (!cList.isEmpty()) {
			var entry = cList.entrySet().iterator().next();
			var runnables = entry.getValue();
			if (!runnables.isEmpty()) {
				var c = runnables.get(0);

				/*
				 * Make sure the remove c before executing it, since nested task(s) with the
				 * same key may cause an endless loop otherwise
				 */
				removeTask(entry.getKey(), c);
				c.run();
				count[0]++;

				// Re-call this method, since c could have made further task(s)
				count[0] += performIfMarkExists();
			}
		}

		return count[0];
	}

	/**
	 * Removes all model building tasks from this class.
	 */
	public static void clearAllTasks() {
		taskContainer.clear();
	}

	/**
	 * Adds the given task to this class and sets it to trigger, upon all markKeys
	 * being involved in a mark.
	 * 
	 * @param markKey A array of markKeys, upon which task should trigger
	 * @param task    A model building task
	 * @return Whether the task has been run or added (currently always returns
	 *         true)
	 */
	public static boolean addTask(Object[] markKey, Runnable task) {
		return addTask(List.of(markKey), task);
	}

	/**
	 * @param markKey An array of markKeys, whose waiting tasks are sought
	 * @return All tasks that have been added to this class, which are still waiting
	 *         on markKeys to be involved in a mark to be triggered.
	 */
	public static List<Runnable> getPendingTasks(Object[] markKey) {
		return getPendingTasks(List.of(markKey));
	}

	/**
	 * @param markKeys A list of markKeys
	 * @return Whether there are any tasks, which are waiting exactly on the given
	 *         list of markKeys to all be involved in marks. Tasks that consider a
	 *         subset of markKeys are NOT considered here.
	 */
	public static boolean hasPendingTasks(List<Object> markKeys) {
		return markKeys != null && taskContainer.keySet().stream().anyMatch((kl) -> areAllElementsSame(kl, markKeys))
				&& !getEntryFor(markKeys).getValue().isEmpty();
	}

	/**
	 * @return Whether there are any tasks that are waiting exactly on the given
	 *         markKey to be involved in a mark. Tasks that require further markKeys
	 *         are NOT considered here.
	 */
	public static boolean hasPendingTasks(Object markKey) {
		return hasPendingTasks(List.of(markKey));
	}

	/**
	 * @param list1 A list
	 * @param list2 Another list
	 * @return Whether all elements of the given lists are reference-equal.
	 */
	private static boolean areAllElementsSame(List<?> list1, List<?> list2) {
		if (list1 == list2)
			return true;
		if (list1 == null ^ list2 == null)
			return false;
		if (list1.size() != list2.size())
			return false;
		return list2.stream().allMatch((sle) -> list1.stream().anyMatch((fle) -> fle == sle));
	}
}
