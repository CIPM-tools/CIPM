package cipm.consistency.base.shared;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLParserPoolImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;

/**
 * General utility which provides operations for EMF models.
 * 
 * @author David Monschein
 *
 */
public class ModelUtil {

    /**
     * Reads a model from a file.
     * 
     * @param <T>
     *            type of the model
     * @param path
     *            file path
     * @param clazz
     *            model type class
     * @return the model parsed from the file or null if it is not valid
     */
    public static <T> T readFromFile(File path, Class<T> clazz) {
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry()
            .getExtensionToFactoryMap()
            .put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

        URI filePathUri = org.eclipse.emf.common.util.URI.createFileURI(path.getAbsolutePath());

        Resource resource = resourceSet.getResource(filePathUri, true);
        return clazz.cast(resource.getContents()
            .get(0));
    }

    /**
     * Reads a Model from file with a given class.
     * 
     * @param <T>
     *            type of the model
     * @param path
     *            file path
     * @param clazz
     *            model type class
     * @return the model parsed from the file or null if it is not valid
     */
    public static <T> T readFromFile(String path, Class<T> clazz) {
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry()
            .getExtensionToFactoryMap()
            .put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

        URI filePathUri = org.eclipse.emf.common.util.URI.createFileURI(new File(path).getAbsolutePath());

        Resource resource = resourceSet.createResource(filePathUri);

        Map<Object, Object> loadOptions = ((XMLResourceImpl) resource).getDefaultLoadOptions();
        loadOptions.put(XMLResource.OPTION_DEFER_ATTACHMENT, Boolean.TRUE);
        loadOptions.put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
        loadOptions.put(XMLResource.OPTION_USE_DEPRECATED_METHODS, Boolean.TRUE);
        loadOptions.put(XMLResource.OPTION_USE_PARSER_POOL, new XMLParserPoolImpl());
        loadOptions.put(XMLResource.OPTION_USE_XML_NAME_TO_FEATURE_MAP, new HashMap<>());

        ((ResourceImpl) resource).setIntrinsicIDToEObjectMap(new HashMap<>());
        try {
            resource.load(loadOptions);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return clazz.cast(resource.getContents()
            .get(0));
    }

    /**
     * Reads a model from a resource.
     * 
     * @param <T>
     *            type of the model
     * @param resource
     *            resource
     * @param clazz
     *            class of the model type
     * @return the parsed model or null if it is not valid
     */
    public static <T> T readFromResource(URL resourceURL, Class<T> clazz) {
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry()
            .getExtensionToFactoryMap()
            .put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

        URI resourceUri = org.eclipse.emf.common.util.URI.createURI(resourceURL.toString());

        Resource resource = resourceSet.createResource(resourceUri);

        Map<Object, Object> loadOptions = ((XMLResourceImpl) resource).getDefaultLoadOptions();
        loadOptions.put(XMLResource.OPTION_DEFER_ATTACHMENT, Boolean.TRUE);
        loadOptions.put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
        loadOptions.put(XMLResource.OPTION_USE_DEPRECATED_METHODS, Boolean.TRUE);
        loadOptions.put(XMLResource.OPTION_USE_PARSER_POOL, new XMLParserPoolImpl());
        loadOptions.put(XMLResource.OPTION_USE_XML_NAME_TO_FEATURE_MAP, new HashMap<>());

        ((ResourceImpl) resource).setIntrinsicIDToEObjectMap(new HashMap<>());
        try {
            resource.load(loadOptions);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return clazz.cast(resource.getContents()
            .get(0));
    }

    /**
     * Saves a model to a file.
     * 
     * @param <T>
     *            model type
     * @param model
     *            model to save
     * @param path
     *            file where the model shoud be saved
     */
    public static <T extends EObject> void saveToFile(T model, File path) {
        if (path != null) {
            if (path.getAbsoluteFile()
                .getParentFile() != null) {
                path.getAbsoluteFile()
                    .getParentFile()
                    .mkdirs();
            }
            saveToFile(model, path.getAbsolutePath());
        }
    }

    /**
     * Saves a model to a file.
     * 
     * @param <T>
     *            model type
     * @param model
     *            model to save
     * @param path
     *            file where the model shoud be saved
     */
    public static <T extends EObject> void saveToFile(T model, String path) {
        URI writeModelURI = URI.createFileURI(path);

        final Resource.Factory.Registry resourceRegistry = Resource.Factory.Registry.INSTANCE;
        final Map<String, Object> map = resourceRegistry.getExtensionToFactoryMap();
        map.put("*", new XMIResourceFactoryImpl());

        final ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.setResourceFactoryRegistry(resourceRegistry);

        final Resource resource = resourceSet.createResource(writeModelURI);
        resource.getContents()
            .add(model);
        try {
            resource.save(null);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Validates a file path and checks if it is a valid model.
     * 
     * @param path
     *            the file path
     * @param type
     *            the model type
     * @return true if the model saved in the given path is a valid model of the given type
     */
    public static boolean validateModelPath(String path, Class<? extends EObject> type) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        File file = new File(path);
        if (file.exists()) {
            try {
                EObject res = ModelUtil.readFromFile(path, type);
                if (res != null && type.isInstance(res)) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Gets all objects of a given type within a model.
     * 
     * @param <T>
     *            type of the objects
     * @param pcmModel
     *            the model
     * @param type
     *            the class corresponding to the type of the objects
     * @return a list of all objects within the given model which conform to the given type
     */
    @SuppressWarnings("unchecked")
    public static <T extends EObject> List<T> getObjects(final EObject pcmModel, final Class<T> type) {
        List<T> results = new ArrayList<>();
        TreeIterator<EObject> it = pcmModel.eAllContents();
        while (it.hasNext()) {
            EObject eo = it.next();
            if (type.isInstance(eo)) {
                results.add((T) eo);
            }
        }
        return results;
    }

}
