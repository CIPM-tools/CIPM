package cipm.consistency.commitintegration.diff.util.pcm.switches;

import org.splevo.jamopp.diffing.similarity.base.ecore.IInnerSwitch;

/**
 * An interface that contains default methods, which create and send
 * {@link ISimilarityRequest} instances to {@link ISimilarityRequestHandler}
 * instances that are supposed to handle them. <br>
 * <br>
 * These methods can be used to spare code duplication in inner switches, which
 * need them.
 * 
 * @author Alp Torac Genc
 */
public interface IPCMInnerSwitch extends IInnerSwitch {

}
