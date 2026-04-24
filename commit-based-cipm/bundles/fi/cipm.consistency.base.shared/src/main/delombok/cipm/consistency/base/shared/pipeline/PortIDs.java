package cipm.consistency.base.shared.pipeline;

/**
 * Used as container for constants that are used to connect pipeline transformations.
 * 
 * @author David Monschein
 *
 */
public interface PortIDs {
    /**
     * The transformation that creates service call traces from the raw monitoring data.
     */
    String T_BUILD_SERVICECALL_TREE = "sc";

    /**
     * The pre-validation transformation that simulates and evaluates the current architecture
     * model.
     */
    String T_VAL_PRE = "validation-pre";

    /**
     * The connection port between the resource environment update transformation and the service
     * call trace generation transformation.
     */
    String T_SC_PCM_RESENV = "sc->resenv";

    /**
     * The connection port between the resource environment update transformation and the system
     * update transformation.
     */
    String T_RESENV_PCM_SYSTEM = "resenv->system";

    /**
     * The connection port between the service call trace generation transformation and the system
     * update transformation.
     */
    String T_SC_PCM_SYSTEM = "sc->system";

    /**
     * The connection port from the system update transformation to the router transformation
     * (special composite transformation).
     */
    String T_SYSTEM_ROUTER = "system->router";

    /**
     * The connection port from the service call trace generation transformation and the router
     * transformation.
     */
    String T_SC_ROUTER = "sc->router";

    /**
     * The connection port from the raw monitoring data to the router transformation (this is used
     * for the validation of the models).
     */
    String T_RAW_ROUTER = "raw->router";

    /**
     * The connection port for the final validation of the architecture model.
     */
    String T_FINAL_VALIDATION = "model->final";

    /**
     * The connection port from the raw monitoring data to the final validation.
     */
    String T_RAW_FINAL_VALIDATION = "raw->final";

    /**
     * The connection port from the final validation to instrumentation model transformation.
     */
    String T_FINAL_INM = "final->inm";
}
