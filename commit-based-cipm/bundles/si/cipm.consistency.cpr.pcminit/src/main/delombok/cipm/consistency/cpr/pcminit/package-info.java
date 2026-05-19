/**
 * This package contains consistency preservation rules that are used to initialize the PCM.
 * 
 * Why is this needed?
 *      Vitruv now differentiates between Correspondences and ReactionCorrespondences.
 *      Within reactions only the latter seem to be accessible. We need correspondences
 *      to initialize the PCM and hence also do the initialization in reactions.
 */
package cipm.consistency.cpr.pcminit;