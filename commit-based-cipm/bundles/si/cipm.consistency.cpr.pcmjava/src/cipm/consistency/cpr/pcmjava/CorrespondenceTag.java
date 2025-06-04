package cipm.consistency.cpr.pcmjava;

public enum CorrespondenceTag {
	/**
	 * Indicates that the PCM counterpart's name is fully qualified, meaning that it
	 * consists of the namespaces and the name of the Java correspondent, and that
	 * the fully qualified name of the Java correspondent should be synchronised.
	 * <br>
	 * <br>
	 * If a correspondence {@code PCMElement <-> JavaElement} has this tag:
	 * <ul>
	 * <li>{@code PCMElement.entityName = namespace1.namespace2.....namespaceN.name}
	 * <li>{@code JavaElement.namespaces = [namespace1, namespace2, ..., namespaceN]}
	 * <li>{@code JavaElement.name = name}
	 * </ul>
	 */
	FULLY_QUALIFIED_NAME_EQUAL("fullyQualifiedNameEqual"),
	/**
	 * Indicates that the Java counterpart's name should be synchronised with the
	 * name of its PCM counterpart <br>
	 * <br>
	 * If a correspondence {@code PCMElement <-> JavaElement} has this tag:
	 * <ul>
	 * <li>{@code PCMElement.entityName = someNameWithoutNamespaces}
	 * <li>{@code JavaElement.name = PCMElement.entityName}
	 * <li>No action regarding {@code JavaElement.namespaces}
	 * </ul>
	 */
	ONLY_NAME_EQUAL("onlyNameEqual"),
	/**
	 * Indicates that the PCM counterpart's name consists only of namespaces, to
	 * which the Java correspondent's namespaces should be synchronised:
	 * {@code JavaElement.namespaces} <br>
	 * <br>
	 * If a correspondence {@code PCMElement <-> JavaElement} has this tag:
	 * <ul>
	 * <li>{@code PCMElement.entityName = namespace1.namespace2.....namespaceN}
	 * <li>{@code JavaElement.namespaces = [namespace1, namespace2, ..., namespaceN]}
	 * <li>No action regarding {@code JavaElement.name}
	 * </ul>
	 */
	ONLY_NAMESPACE_EQUAL("onlyNamespaceEqual");

	private final String tag;

	private CorrespondenceTag(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return this.tag;
	}

	@Override
	public String toString() {
		return this.tag;
	}
}
