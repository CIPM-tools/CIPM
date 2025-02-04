package cipm.consistency.fitests.similarity.params;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An abstract class for classes to implement, which uses a {@link Map} to store
 * mappings of (class object, attribute object) pairs to their expected
 * similarity checking results. An attribute object denotes an attribute within
 * a class (i.e. it serves as the identifier of the attribute), rather than the
 * value of an attribute.
 * 
 * @see {@link #addSimilarityEntry(Class, Object, Boolean)} for more information
 *      on adding entries.
 * @see {@link #getExpectedSimilarityResult(Class, Object)} for more information
 *      on expected similarity checking results.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractSimilarityValues implements ISimilarityValues {
	/**
	 * The expected similarity checking result for cases, where there is no matching
	 * {@link SimilarityEntry} in this instance.
	 * 
	 * @see {@link #getExpectedSimilarityResult(Class, Object)}
	 */
	private Boolean defaultSimResult = null;
	/**
	 * Maps {@link SimilarityEntry} to their expected similarity checking result.
	 * 
	 * @see {@link #getExpectedSimilarityResult(Class, Object)}
	 */
	private Map<SimilarityEntry, Boolean> similarityValues;

	/**
	 * Constructs an instance and initialises the internal data structure that
	 * stores expected similarity checking results.
	 * 
	 * @see {@link #addSimilarityEntry(Class, Object, Boolean)} for more information
	 *      on adding entries.
	 * @see {@link #getExpectedSimilarityResult(Class, Object)} for more information
	 *      on expected similarity checking results.
	 */
	public AbstractSimilarityValues() {
		this.similarityValues = this.initSimilarityValues();
	}

	/**
	 * @return Creates a {@link SimilarityEntry} with the given parameters.
	 */
	protected SimilarityEntry createEntry(Class<?> objCls, Object attr) {
		return new SimilarityEntry(objCls, attr);
	}

	/**
	 * First, tries to find a direct match for the given parameters (see
	 * {@link #findDirectEntry(Class, Object)}). If there is no direct match, it
	 * relaxes objCls to super types of objCls and tries to find a match again (see
	 * {@link #findParentEntry(Class, Object)}).
	 * 
	 * @param objCls The interface of the Object instance
	 * @param An     attribute of the Object instance, which is the subject of the
	 *               current similarity check
	 * @return The corresponding {@link SimilarityEntry}
	 */
	protected SimilarityEntry findEntry(Class<?> objCls, Object attr) {
		var directMatch = this.findDirectEntry(objCls, attr);

		return directMatch != null ? directMatch : this.findParentEntry(objCls, attr);
	}

	/**
	 * Tries to find a directly matching entry (i.e. an entry with objCls-attr
	 * pair). Ignores entries belonging to super-types of objCls.
	 * 
	 * @param objCls The interface of the Object instance
	 * @param attr   An attribute of the Object instance, which is the subject of
	 *               the current similarity check
	 * @return The corresponding {@link SimilarityEntry}
	 */
	protected SimilarityEntry findDirectEntry(Class<?> objCls, Object attr) {
		return this.similarityValues.keySet().stream()
				.filter((se) -> se.getObjectClass().equals(objCls) && se.getAttribute().equals(attr)).findFirst()
				.orElse(null);
	}

	/**
	 * Tries to find an entry with a super-type of objCls using a depth-first
	 * recursion in the type hierarchy of objCls towards Object. If there is a
	 * direct entry (see {@link #findDirectEntry(Class, Object)}), returns it. <br>
	 * <br>
	 * 
	 * @param objCls The interface of the Object instance
	 * @param attr   An attribute of the Object instance, which is the subject of
	 *               the current similarity check
	 * @return A corresponding {@link SimilarityEntry}
	 */
	protected SimilarityEntry findParentEntry(Class<?> objCls, Object attr) {
		var parents = objCls.getInterfaces();
		SimilarityEntry result = null;

		for (var p : parents) {
			result = this.findDirectEntry(p, attr);
			if (result != null)
				return result;

			result = this.findParentEntry(p, attr);
			if (result != null)
				return result;
		}

		return result;
	}

	/**
	 * @return The map that will be used by this instance to store mappings of
	 *         (object class, attribute object) pairs to their expected similarity
	 *         value.
	 */
	protected Map<SimilarityEntry, Boolean> initSimilarityValues() {
		return new HashMap<SimilarityEntry, Boolean>();
	}

	/**
	 * Adds the given {@link SimilarityEntry} along with its expected similarity
	 * result to this instance.
	 */
	protected void addEntry(SimilarityEntry se, Boolean expectedSimResult) {
		this.similarityValues.put(se, expectedSimResult);
	}

	protected Boolean getExpectedSimilarityResult(SimilarityEntry se) {
		return this.similarityValues.get(se);
	}

	protected List<SimilarityEntry> getAllEntries() {
		return new ArrayList<>(this.similarityValues.keySet());
	}

	@Override
	public void addSimilarityEntry(Class<?> objCls, Object attr, Boolean expectedSimResult) {
		this.addEntry(this.createEntry(objCls, attr), expectedSimResult);
	}

	@Override
	public void removeSimilarityEntry(Class<?> objCls, Object attr) {
		var se = this.findEntry(objCls, attr);

		if (se != null)
			this.similarityValues.remove(se);
	}

	@Override
	public Boolean getStoredExpectedSimilarityResult(Class<?> objCls, Object attr) {
		var se = this.findEntry(objCls, attr);

		return se != null ? this.similarityValues.get(se) : null;
	}

	@Override
	public Boolean getStoredDirectExpectedSimilarityResult(Class<?> objCls, Object attr) {
		var se = this.findDirectEntry(objCls, attr);

		return se != null ? this.similarityValues.get(se) : null;
	}

	@Override
	public void clear() {
		this.similarityValues.clear();
	}

	@Override
	public void setDefaultSimilarityResult(Boolean defSimRes) {
		this.defaultSimResult = defSimRes;
	}

	@Override
	public Boolean getDefaultSimilarityResult() {
		return this.defaultSimResult;
	}

	/**
	 * Contains a class object and an attribute object of that class. An attribute
	 * object denotes an attribute within a class (i.e. it serves as the identifier
	 * of the attribute), rather than the value of an attribute.
	 * 
	 * @author Alp Torac Genc
	 */
	protected class SimilarityEntry {
		/**
		 * The class object.
		 */
		private final Class<? extends Object> objCls;
		/**
		 * An attribute object of {@link #objCls}.
		 */
		private final Object attr;

		/**
		 * @param objCls A class object
		 * @param attr   An attribute object of {@link #objCls}
		 */
		private SimilarityEntry(Class<? extends Object> objCls, Object attr) {
			this.objCls = objCls;
			this.attr = attr;
		}

		/**
		 * @return The class object stored in this instance.
		 */
		public Class<? extends Object> getObjectClass() {
			return this.objCls;
		}

		/**
		 * @return The attribute stored in this entry.
		 */
		public Object getAttribute() {
			return this.attr;
		}

		/**
		 * Compares two {@link SimilarityEntry} instances based on what they store.
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;

			if (obj == null || !(obj instanceof SimilarityEntry))
				return false;

			var castedO = (SimilarityEntry) obj;

			if (this.attr == null ^ castedO.attr == null)
				return false;

			if (this.objCls == null ^ castedO.objCls == null)
				return false;

			// Covers both members being null
			if (this.attr == castedO.attr && this.objCls == castedO.objCls)
				return true;

			return this.attr.equals(castedO.attr) && this.objCls.equals(castedO.objCls);
		}
	}
}
