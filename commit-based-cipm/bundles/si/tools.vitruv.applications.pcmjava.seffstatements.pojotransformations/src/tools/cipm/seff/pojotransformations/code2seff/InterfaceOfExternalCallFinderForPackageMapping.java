package tools.cipm.seff.pojotransformations.code2seff;

import java.util.Set;

import org.apache.log4j.Logger;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.statements.Statement;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFinding;

import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.correspondence.CorrespondenceModelUtil;

/**
 * Class realizes a InterfaceOfExternalCallFinding for the simple package mapping
 *
 * @author langhamm
 *
 */
public class InterfaceOfExternalCallFinderForPackageMapping implements InterfaceOfExternalCallFinding {

    private static final Logger LOGGER = Logger
            .getLogger(InterfaceOfExternalCallFinderForPackageMapping.class.getSimpleName());

    private final CorrespondenceModel correspondenceModel;
    private final BasicComponent myBasicComponent;

    public InterfaceOfExternalCallFinderForPackageMapping(final CorrespondenceModel correspondenceModel,
            final BasicComponent myBasicComponent) {
        this.correspondenceModel = correspondenceModel;
        this.myBasicComponent = myBasicComponent;

    }

    /**
     * A InterfacePortOperationTuple is found by finding the corresponding operation signature to
     * the called method. The required role is found by looking at all required roles of the
     * component and return the first one that requires the same interface the operation signature
     * is in. Current limitations: 1) the called method has to be an interface method/a method that
     * directly corresponds to a operation signature, and 2) each interface can only be required
     * once by a given component. Both limitations are, however, OK for the beginning.
     */
    @Override
    public InterfacePortOperationTuple getCalledInterfacePort(final Method calledMethod, Statement statement) {
        final InterfacePortOperationTuple interfacePortOperationTuple = new InterfacePortOperationTuple();
        final OperationSignature opSig = this.queryInterfaceOperation(calledMethod);
        if (null != opSig) {
            interfacePortOperationTuple.signature = opSig;
            final OperationInterface accessedOpIf = opSig.getInterface__OperationSignature();
            for (final RequiredRole requiredRole : this.myBasicComponent.getRequiredRoles_InterfaceRequiringEntity()) {
                if (requiredRole instanceof OperationRequiredRole) {
                    final OperationRequiredRole orr = (OperationRequiredRole) requiredRole;
                    if (orr.getRequiredInterface__OperationRequiredRole().getId().equals(accessedOpIf.getId())) {
                        interfacePortOperationTuple.role = orr;
                        break;
                    }
                }
            }
        }

        return interfacePortOperationTuple;
    }

    /**
     * Returns the OperationSignature for the invoked method. If the invoked method directly
     * corresponds to an OperationSignature we are finished already. If the invoked method
     * corresponds to a SEFF (aka. it is a class method that implements an interface method, that
     * corresponds to an operation signature) we can use the operation signature from the SEFF.
     *
     * @param invokedMethod the invoked method.
     * @return the corresponding OperationSignature.
     */
    private OperationSignature queryInterfaceOperation(final Method invokedMethod) {
        final Set<OperationSignature> correspondingOpSigs = CorrespondenceModelUtil
                .getCorrespondingEObjects(this.correspondenceModel, invokedMethod, OperationSignature.class);
        if (null != correspondingOpSigs && 0 < correspondingOpSigs.size()) {
            return correspondingOpSigs.iterator().next();
        }
        final Set<ResourceDemandingSEFF> correspondingRDSEFFs = CorrespondenceModelUtil
                .getCorrespondingEObjects(this.correspondenceModel, invokedMethod,
                        ResourceDemandingSEFF.class);
        if (null != correspondingRDSEFFs && 0 < correspondingRDSEFFs.size()) {
            for (final ResourceDemandingSEFF seff : correspondingRDSEFFs) {
                if (seff.getDescribedService__SEFF() instanceof OperationSignature) {
                    return (OperationSignature) seff.getDescribedService__SEFF();
                }
            }
        }
        LOGGER.warn("Could not find operation signature for method " + invokedMethod);
        return null;
    }
}
