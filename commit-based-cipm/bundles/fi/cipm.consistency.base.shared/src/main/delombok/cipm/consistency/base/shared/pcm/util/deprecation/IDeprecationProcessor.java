package cipm.consistency.base.shared.pcm.util.deprecation;

import de.uka.ipd.sdq.identifier.Identifier;

/**
 * A deprecation processor determines whether a given element should be deleted or not. The
 * processing is iteration based, so the processor is also notified when an iteration ends.
 * 
 * @author David Monschein
 *
 */
public interface IDeprecationProcessor {

    /**
     * Whether a given element should be deleted.
     * 
     * @param el
     *            the element
     * @return true if it should be deleted, false otherwise
     */
    public boolean shouldDelete(Identifier el);

    /**
     * Determines whether an element is deprecated now and should be deleted in the next iteration.
     * 
     * @param el
     *            the element
     * @return true if the element is deprecated, false otherwise
     */
    public boolean isCurrentlyDeprecated(Identifier el);

    /**
     * Called when an iteration is finished. This enables the detection of consecutive absences.
     */
    public void iterationFinished();

}
