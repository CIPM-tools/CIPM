/**
 * This package contains change propagation rules from Measurements model to PCM.
 *
 * Measurements -> PCM (measurementsPcmUpdate.reactions):
 * Updates PCM model based on measurement data:
 * - InternalActionRecord  -> InternalAction resource demand update
 * - LoopActionRecord  -> LoopAction iteration count update
 */
package cipm.consistency.cpr.measurementspcm;
