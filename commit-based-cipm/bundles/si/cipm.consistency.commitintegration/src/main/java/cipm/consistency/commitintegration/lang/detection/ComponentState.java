package cipm.consistency.commitintegration.lang.detection;

/**
 * Represents the possible states of a module.
 * 
 * @author Martin Armbruster
 */
public enum ComponentState {
    /**
     * Presents a Microservice component.
     */
    MICROSERVICE_COMPONENT,
    /**
     * Presents a regular component.
     */
    REGULAR_COMPONENT,
    /**
     * Presents a component candidate.
     */
    COMPONENT_CANDIDATE,
    /**
     * Presents a part of another component.
     */
    PART_OF_COMPONENT,
    /**
     * Presents no component.
     */
    NO_COMPONENT
}