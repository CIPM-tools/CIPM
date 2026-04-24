package cipm.consistency.base.shared.pcm.util.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

/**
 * Utility functions to work with repository models of the Palladio Component Model (PCM).
 * 
 * @author David Monschein
 *
 */
public class PCMRepositoryUtil {

    /**
     * Gets all components that provide a given operation interface.
     * 
     * @param baseRepository
     *            the repository that is used to find the components
     * @param iface
     *            the operation interface that should be provided
     * @return all components within the given repository that provide the specified operation
     *         interface
     */
    public static List<RepositoryComponent> getComponentsProvidingInterface(Repository baseRepository,
            OperationInterface iface) {
        return baseRepository.getComponents__Repository()
            .stream()
            .filter(comp -> {
                return comp.getProvidedRoles_InterfaceProvidingEntity()
                    .stream()
                    .anyMatch(pr -> {
                        if (pr instanceof OperationProvidedRole) {
                            return ((OperationProvidedRole) pr).getProvidedInterface__OperationProvidedRole()
                                .getId()
                                .equals(iface.getId());
                        }
                        return false;
                    });
            })
            .collect(Collectors.toList());
    }

    /**
     * Gets the enclosing service for an abstract action within a SEFF.
     * 
     * @param ext
     *            the abstract action contained by a SEFF
     * @return the enclosing service or null if it does not exist
     */
    public static ResourceDemandingSEFF getParentService(AbstractAction ext) {
        EObject current = ext;
        while (current != null && !(current instanceof ResourceDemandingSEFF)) {
            current = current.eContainer();
        }

        if (current != null && current instanceof ResourceDemandingSEFF) {
            return (ResourceDemandingSEFF) current;
        } else {
            return null;
        }
    }

}
