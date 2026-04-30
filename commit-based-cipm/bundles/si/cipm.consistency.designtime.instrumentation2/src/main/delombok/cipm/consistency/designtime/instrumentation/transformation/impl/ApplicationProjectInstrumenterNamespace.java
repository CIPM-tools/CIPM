package cipm.consistency.designtime.instrumentation.transformation.impl;

/**
 * Constants for the instrumentation.
 * 
 * @author David Monschein
 */
public interface ApplicationProjectInstrumenterNamespace {

    String RESOURCE_ID_CPU = "_oro4gG3fEdy4YaaT-RYrLQ";

    String THREAD_MONITORING_CONTROLLER_VARIABLE = "threadMonitoringController";
    String SERVICE_PARAMETERS_VARIABLE = "monitoringServiceParameters";
    String COUNTER_VARIABLE = "loopIterationCounter";

    String METHOD_GET_INSTANCE = "getInstance";
    String METHOD_ADD_PARAMETER_VALUE = "addValue";

    String METHOD_EXIT_SERVICE = "exitService";
    String METHOD_ENTER_SERVICE = "enterService";

    String METHOD_ENTER_INTERNAL_ACTION = "enterInternalAction";
    String METHOD_EXIT_INTERNAL_ACTION = "exitInternalAction";

    String METHOD_ENTER_BRANCH = "enterBranch";

    String METHOD_EXIT_LOOP = "exitLoop";

    String METHOD_ATOMIC_INTEGER_INCREMENT = "getAndIncrement";
    String METHOD_ATOMIC_INTEGER_GET = "get";

    String METHOD_BEFORE_EXTERNAL_CALL = "setExternalCallId";

}
