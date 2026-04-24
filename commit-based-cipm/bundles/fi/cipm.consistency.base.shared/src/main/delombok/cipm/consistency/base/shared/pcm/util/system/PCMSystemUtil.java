package cipm.consistency.base.shared.pcm.util.system;

import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ComposedStructure;
import org.palladiosimulator.pcm.core.composition.CompositionFactory;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.core.composition.RequiredDelegationConnector;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.system.System;

/**
 * Utility functions to interact with system models of the Palladio Component Model (PCM).
 * 
 * @author David Monschein
 *
 */
public class PCMSystemUtil {

    /**
     * Creates an assembly connector (see {@link AssemblyConnector}).
     * 
     * @param currentOuterStructure
     *            the structure where the connector should be saved in
     * @param prov
     *            the provided role
     * @param providing
     *            the providing assembly context
     * @param req
     *            the required role
     * @param requiring
     *            the requiring assembly context
     * @return the created connector
     */
    public static AssemblyConnector createAssemblyConnector(ComposedStructure currentOuterStructure,
            OperationProvidedRole prov, AssemblyContext providing, OperationRequiredRole req,
            AssemblyContext requiring) {
        AssemblyConnector nConnector = CompositionFactory.eINSTANCE.createAssemblyConnector();
        nConnector.setProvidedRole_AssemblyConnector(prov);
        nConnector.setRequiredRole_AssemblyConnector(req);
        nConnector.setProvidingAssemblyContext_AssemblyConnector(providing);
        nConnector.setRequiringAssemblyContext_AssemblyConnector(requiring);

        nConnector.setEntityName("Connector " + requiring.getEntityName() + " -> " + providing.getEntityName());

        currentOuterStructure.getConnectors__ComposedStructure()
            .add(nConnector);

        return nConnector;
    }

    /**
     * Creates a provided role delegation within a given system.
     * 
     * @param system
     *            the enclosing structure where the delegation should be saved
     * @param outerProvidedRole
     *            the outer provided role
     * @param ctx
     *            the assembly context that provides the required interface
     * @param innerProvidedRole
     *            the provided role of the providing assembly context
     * @return the created delegation within the enclosing system
     */
    public static ProvidedDelegationConnector createProvidedDelegation(System system,
            OperationProvidedRole outerProvidedRole, AssemblyContext ctx, OperationProvidedRole innerProvidedRole) {
        return createProvidedDelegation(system, outerProvidedRole, ctx, innerProvidedRole, true);
    }

    /**
     * Creates a provided role delegation within a given system.
     * 
     * @param system
     *            the enclosing structure where the delegation should be saved
     * @param outerProvidedRole
     *            the outer provided role
     * @param ctx
     *            the assembly context that provides the required interface
     * @param innerProvidedRole
     *            the provided role of the providing assembly context
     * @param add
     *            whether the connector should be added to the system or not (if not it is only
     *            created and present in the memory)
     * @return the created delegation
     */
    public static ProvidedDelegationConnector createProvidedDelegation(System system,
            OperationProvidedRole outerProvidedRole, AssemblyContext ctx, OperationProvidedRole innerProvidedRole,
            boolean add) {
        ProvidedDelegationConnector conn = CompositionFactory.eINSTANCE.createProvidedDelegationConnector();

        conn.setAssemblyContext_ProvidedDelegationConnector(ctx);
        conn.setInnerProvidedRole_ProvidedDelegationConnector(innerProvidedRole);
        conn.setOuterProvidedRole_ProvidedDelegationConnector(outerProvidedRole);
        conn.setEntityName(
                "ProvDelegation " + outerProvidedRole.getEntityName() + " -> " + innerProvidedRole.getEntityName());

        if (add) {
            conn.setParentStructure__Connector(system);
            system.getConnectors__ComposedStructure()
                .add(conn);
        }

        return conn;
    }

    /**
     * Creates a new required role delegation within a given system.
     * 
     * @param ctx
     *            the inner assembly context that requires the role
     * @param irr
     *            the required role of the inner assembly context
     * @param system
     *            the enclosing structure (system)
     * @param role
     *            the required role of the system
     * @return the created required delegation connector, which has also been added to the system
     */
    public static RequiredDelegationConnector createRequiredDelegation(AssemblyContext ctx, OperationRequiredRole irr,
            System system, OperationRequiredRole role) {
        RequiredDelegationConnector conn = CompositionFactory.eINSTANCE.createRequiredDelegationConnector();

        conn.setAssemblyContext_RequiredDelegationConnector(ctx);
        conn.setInnerRequiredRole_RequiredDelegationConnector(irr);
        conn.setOuterRequiredRole_RequiredDelegationConnector(role);
        conn.setEntityName("ReqDelegation " + irr.getEntityName() + " -> " + role.getEntityName());
        conn.setParentStructure__Connector(system);
        system.getConnectors__ComposedStructure()
            .add(conn);

        return conn;
    }

    /**
     * Creates a new assembly context of a given component type.
     * 
     * @param system
     *            the system where the created assembly context should be saved in
     * @param comp
     *            the component type of the assembly context
     * @return the created assembly context
     */
    public static AssemblyContext createAssemblyContext(System system, RepositoryComponent comp) {
        AssemblyContext ctx = CompositionFactory.eINSTANCE.createAssemblyContext();
        ctx.setEncapsulatedComponent__AssemblyContext(comp);
        system.getAssemblyContexts__ComposedStructure()
            .add(ctx);
        return ctx;
    }

    /**
     * Creates a new assembly context of a given component type with a specified name.
     * 
     * @param system
     *            the system where the created assembly context should be saved in
     * @param comp
     *            the component type of the assembly context
     * @param name
     *            the name of the assembly context (see {@link Entity#getEntityName()}).
     * @return the created assembly context
     */
    public static AssemblyContext createAssemblyContext(System system, RepositoryComponent comp, String name) {
        AssemblyContext ctx = CompositionFactory.eINSTANCE.createAssemblyContext();
        ctx.setEncapsulatedComponent__AssemblyContext(comp);
        system.getAssemblyContexts__ComposedStructure()
            .add(ctx);
        ctx.setEntityName(name);
        return ctx;
    }

    /**
     * Creates a provided role for a system with a given interface.
     * 
     * @param currentSystem
     *            the system
     * @param entryPoint
     *            the interface
     * @return the created provided role of the system, conforming to the given interface
     */
    public static OperationProvidedRole createProvidedRole(System currentSystem, OperationInterface entryPoint) {
        OperationProvidedRole output = RepositoryFactory.eINSTANCE.createOperationProvidedRole();
        output.setProvidedInterface__OperationProvidedRole(entryPoint);
        output.setEntityName("SystemProvided_" + entryPoint.getEntityName());
        currentSystem.getProvidedRoles_InterfaceProvidingEntity()
            .add(output);

        return output;
    }

    /**
     * Creates a required role for a system with a given interface.
     * 
     * @param currentSystem
     *            system
     * @param reqInterface
     *            interface
     * @return the created required role of the system, conforming to the given interface
     */
    public static OperationRequiredRole createRequiredRole(System currentSystem, OperationInterface reqInterface) {
        OperationRequiredRole output = RepositoryFactory.eINSTANCE.createOperationRequiredRole();
        output.setRequiredInterface__OperationRequiredRole(reqInterface);
        currentSystem.getRequiredRoles_InterfaceRequiringEntity()
            .add(output);

        return output;
    }

}
