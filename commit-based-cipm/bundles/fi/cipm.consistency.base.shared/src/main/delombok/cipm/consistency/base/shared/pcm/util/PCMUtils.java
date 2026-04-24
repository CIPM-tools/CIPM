package cipm.consistency.base.shared.pcm.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.pcm.PcmPackage;
import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryPackage;
import org.palladiosimulator.pcm.resourcetype.ResourceRepository;
import org.palladiosimulator.pcm.resourcetype.ResourcetypePackage;

import de.uka.ipd.sdq.identifier.Identifier;
//import tools.vitruv.dsls.reactions.meta.correspondence.reactions.ReactionsPackage;
import tools.vitruv.change.correspondence.CorrespondencePackage;

/**
 * Utility functions that simplify the interaction with Palladio Component Model (PCM) models.
 * 
 * @author David Monschein
 *
 */
public class PCMUtils {

    /**
     * Visits all common PCM package classes to load them.
     */
    public static void loadPCMModels() {
        RepositoryPackage.eINSTANCE.eClass();
        PcmPackage.eINSTANCE.eClass();
        ResourcetypePackage.eINSTANCE.eClass();

        initPathmaps();
    }

    public static void initVitruvius() {
        CorrespondencePackage.eINSTANCE.eClass();
//		ReactionsPackage.eINSTANCE.eClass();
    }

    /**
     * Gets the default resource repository of the PCM.
     * 
     * @return the default resource repository of the PCM
     */
    public static ResourceRepository getDefaultResourceRepository() {
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry()
            .getExtensionToFactoryMap()
            .put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

        URI filePathUri = org.eclipse.emf.common.util.URI.createURI("pathmap://PCM_MODELS/Palladio.resourcetype");

        Resource resource = resourceSet.getResource(filePathUri, true);

        return (ResourceRepository) resource.getContents()
            .get(0);
    }

    public static Repository getDefaultPrimitiveTypes() {
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry()
            .getExtensionToFactoryMap()
            .put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

        URI filePathUri = org.eclipse.emf.common.util.URI.createURI("pathmap://PCM_MODELS/PrimitiveTypes.repository");

        Resource resource = resourceSet.getResource(filePathUri, true);

        return (Repository) resource.getContents()
            .get(0);
    }

    /**
     * Searches a given element and looks for an element with a given type and ID.
     * 
     * @param <T>
     *            the type of the element to search for
     * @param parent
     *            the parent element that should be searched
     * @param type
     *            the type of the element to find
     * @param id
     *            the ID of the element to find
     * @return the element with the given type and ID or null if it does not exist
     */
    @SuppressWarnings("unchecked") // it IS a type safe cast
    public static <T> T getElementById(EObject parent, Class<T> type, String id) {
        Optional<Identifier> result = getElementById(parent, id);
        if (!result.isPresent()) {
            return null;
        }
        if (!type.isInstance(result.get())) {
            return null;
        } else {
            return (T) result.get();
        }
    }

    /**
     * Gets all elements of a given type within a specified root element.
     * 
     * @param <T>
     *            the type of the elements to search for
     * @param parent
     *            the root element which should be searched
     * @param class1
     *            the class corresponding to the type of the elements to search for
     * @return a list of elements that are contained by the root element and conform to the given
     *         type
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getElementsByType(EObject parent, Class<T> class1) {
        List<T> ret = new ArrayList<>();
        TreeIterator<EObject> it = parent.eAllContents();
        while (it.hasNext()) {
            EObject eo = it.next();
            if (class1.isInstance(eo)) {
                ret.add((T) eo);
            }
        }
        return ret;
    }

    /**
     * Gets an element by ID within a parent element.
     * 
     * @param parent
     *            the element to search
     * @param id
     *            the ID to search for
     * @return an optional which either contains the element to search for or otherwise is empty
     */
    public static Optional<Identifier> getElementById(EObject parent, String id) {
        TreeIterator<EObject> it = parent.eAllContents();
        while (it.hasNext()) {
            EObject eo = it.next();
            if (Identifier.class.isInstance(eo)) {
                Identifier ident = (Identifier) eo;
                if (ident.getId()
                    .equals(id)) {
                    return Optional.of(ident);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Initializes all models that are commonly references by PCM models. If this is not done before
     * interacting with the models, IDs are generated randomly and metric references do not work.
     */
    private static void initPathmaps() {
        final String metricSpecModel = "models/Palladio.resourcetype";
        final URL url = PCMUtils.class.getClassLoader()
            .getResource(metricSpecModel);
        if (url == null) {
            throw new RuntimeException("Error getting common metric definitions");
        }
        String urlString = url.toString();
        if (!urlString.endsWith(metricSpecModel)) {
            throw new RuntimeException("Error getting common metric definitions. Got: " + urlString);
        }
        urlString = urlString.substring(0, urlString.length() - metricSpecModel.length() - 1);
        if (!urlString.endsWith("/"))
            urlString += "/";
        final URI uri = URI.createURI(urlString);
        final URI target = uri.appendSegment("models")
            .appendSegment("");
        URIConverter.URI_MAP.put(URI.createURI("pathmap://PCM_MODELS/"), target);

        final Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        final Map<String, Object> m = reg.getExtensionToFactoryMap();
        m.put("resourcetype", new XMIResourceFactoryImpl());
        m.put("repository", new XMIResourceFactoryImpl());
    }

    /**
     * Creates an instance of {@link PCMRandomVariable} by a given string specification.
     * 
     * @param string
     *            the specification for the stochastic expression
     * @return the generated instance of {@link PCMRandomVariable} with the given specification
     */
    public static PCMRandomVariable createRandomVariableFromString(String string) {
        PCMRandomVariable var = CoreFactory.eINSTANCE.createPCMRandomVariable();
        var.setSpecification(string);
        return var;
    }

}
