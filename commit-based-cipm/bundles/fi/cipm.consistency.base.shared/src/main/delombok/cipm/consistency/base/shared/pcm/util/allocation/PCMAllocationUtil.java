package cipm.consistency.base.shared.pcm.util.allocation;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.allocation.AllocationFactory;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

/**
 * Utilites to interact with allocation models of the PCM (see {@link Allocation}).
 * 
 * @author David Monschein
 *
 */
public class PCMAllocationUtil {

    /**
     * Creates an allocation context for a given assembly context and a given resource container in
     * the given allocation model.
     * 
     * @param allocationModel
     *            the allocation model where the allocation should be saved in
     * @param assemblyCtx
     *            the assembly context that should be deployed
     * @param resourceContainer
     *            the container where the assembly context should be deployed on
     * @return reference to the created allocation context
     */
    public static AllocationContext deployAssemblyOnContainer(Allocation allocationModel, AssemblyContext assemblyCtx,
            ResourceContainer resourceContainer) {
        AllocationContext ctx = AllocationFactory.eINSTANCE.createAllocationContext();
        ctx.setResourceContainer_AllocationContext(resourceContainer);
        ctx.setAssemblyContext_AllocationContext(assemblyCtx);

        allocationModel.getAllocationContexts_Allocation()
            .add(ctx);
        return ctx;
    }

}
