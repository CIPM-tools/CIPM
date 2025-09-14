package cipm.consistency.fitests.similarity.jamopp.parser.timemeasurement;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that contains information about time measurements in a map instance.
 * There is no mandatory information that should be given to this class. <br>
 * <br>
 * For convenience and clarity, instances should be constructed via builder
 * classes such as {@link ParserTestTimeMeasurementKeyBuilder}. Its constructor
 * is left public to allow parsing instances of this class from data files.
 * 
 * @author Alp Torac Genc
 */
public class ParserTestTimeMeasurementKey {
	private final Map<ParserTestTimeMeasurerKeyType, String> keyMap;

	/**
	 * Constructs an instance with the given (key, value) pairs in keyMap. For
	 * performance reasons, keyMap will not be copied. It will be directly assigned
	 * to this instance, meaning that modifications to keyMap from outside will be
	 * reflected to this instance.
	 * 
	 * @param keyMap (key, value) pairs that this class should store. Although it is
	 *               allowed to be null, passing null here will most likely render
	 *               this instance useless
	 */
	public ParserTestTimeMeasurementKey(Map<ParserTestTimeMeasurerKeyType, String> keyMap) {
		this.keyMap = keyMap;
	}

	/**
	 * Can be used to copy the contents of this instance (while creating a clone for
	 * instance).
	 * 
	 * @return A copy of all added keys and their values. Modifying the return value
	 *         will not affect this instance.
	 */
	public Map<ParserTestTimeMeasurerKeyType, String> getKeys() {
		if (keyMap == null) {
			return new HashMap<ParserTestTimeMeasurerKeyType, String>();
		}
		return new HashMap<ParserTestTimeMeasurerKeyType, String>(keyMap);
	}

	/**
	 * Two instances of this class are similar, if their contents
	 * ({@link #getKeys()} in this case) are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ParserTestTimeMeasurementKey)) {
			return false;
		}
		var castedO = (ParserTestTimeMeasurementKey) obj;

		// Avoid NullPointerExceptions
		if (this.keyMap == null && castedO.keyMap == null) {
			return true;
		} else if (this.keyMap == null ^ castedO.keyMap == null) {
			return false;
		}

		return this.keyMap.size() == castedO.keyMap.size()
				&& this.keyMap.entrySet().containsAll(castedO.keyMap.entrySet());
	}
}
