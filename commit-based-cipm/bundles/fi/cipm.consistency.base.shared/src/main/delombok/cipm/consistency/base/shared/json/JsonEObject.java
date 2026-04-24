package cipm.consistency.base.shared.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;

/**
 * Class that can be used to wrap a {@link EObject} as a valid JSON object.
 * 
 * @author David Monschein
 *
 */
public final class JsonEObject {

    /**
     * Type of the corresponding {@link EObject}.
     */
    private String type;

    /**
     * Childs of the corresponding {@link EObject}.
     */
    private List<JsonEObject> childs;

    /**
     * Attributes of the {@link EObject}, the values are converted to strings.
     */
    private Map<String, String> attributes;

    /**
     * Creates a {@link JsonEObject} from a {@link EObject}.
     * 
     * @param clone
     *            the EObject which should be converted into a valid JSON object
     * @return JSON representation of the given {@link EObject}
     */
    public static JsonEObject create(EObject clone) {
        JsonEObject ret = new JsonEObject();

        if (clone != null) {
            ret.setType(clone.eClass()
                .getName());

            // get fields
            for (EAttribute field : clone.eClass()
                .getEAllAttributes()) {
                ret.attributes.put(field.getName(), String.valueOf(clone.eGet(field)));
            }

            for (EObject child : clone.eContents()) {
                ret.childs.add(JsonEObject.create(child));
            }
        }

        return ret;
    }

    /**
     * Initializes a new {@link JsonEObject} with no childs and attributes.
     */
    private JsonEObject() {
        this.childs = new ArrayList<>();
        this.attributes = new HashMap<>();
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<JsonEObject> getChilds() {
		return new ArrayList<>(childs);
	}

	public void setChilds(List<JsonEObject> childs) {
		this.childs.clear();
		this.childs.addAll(childs);
	}

	public Map<String, String> getAttributes() {
		return new HashMap<>(attributes);
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes.clear();
		this.attributes.putAll(attributes);
	}
}
