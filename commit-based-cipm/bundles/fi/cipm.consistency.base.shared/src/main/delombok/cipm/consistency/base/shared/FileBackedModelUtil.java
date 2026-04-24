package cipm.consistency.base.shared;

import java.io.File;
import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;

/**
 * Utility to synchronize models in the memory with files. It registers a listener on the models and
 * on every change the model is saved to the underlying file.
 * 
 * @author David Monschein
 *
 */
public class FileBackedModelUtil {

    /**
     * Synchronizes a model with a file.
     * 
     * @param <T>
     *            model type
     * @param model
     *            reference to the model that should be synchronized
     * @param file
     *            the file where the model should be stored
     * @param clz
     *            class of the model type
     * @return null if the synchronization failed, the input model if the synchronization was
     *         successful
     */
    public static <T extends EObject> T synchronize(T model, final File file, Class<T> clz) {
        return synchronize(model, file, clz, null, null);
    }

    /**
     * Synchronizes a model with a file.
     * 
     * @param <T>
     *            model type
     * @param model
     *            reference to the model that should be synchronized
     * @param file
     *            the file where the model should be stored
     * @param clz
     *            class of the model type
     * @param listener
     *            this listener is called whenever the model changes and after it has been
     *            synchronized with the underlying file
     * @param createFunction
     *            function which is called to initialize a model if the file does not contain a
     *            model and the given model is null
     * @return null if the synchronization failed, the input model if the synchronization was
     *         successful
     */
    public static <T extends EObject> T synchronize(T model, final File file, Class<T> clz,
            final IModelChangeListener listener, Function<Void, T> createFunction) {

        if (file == null) {
            return null;
        }

        if (model == null) {
            if (file.exists()) {
                model = ModelUtil.readFromFile(file.getAbsolutePath(), clz);
            }
            if (model == null) {
                if (createFunction != null) {
                    model = createFunction.apply(null);
                } else {
                    return null;
                }
            }
        }

        // SAVE IT AT THE BEGINNING
        // BECAUSE WE DONT KNOW IF IT IS SYNCED
        if (model != null) {
            if (file.getAbsoluteFile()
                .getParentFile() != null) {
                file.getAbsoluteFile()
                    .getParentFile()
                    .mkdirs();
            }
            ModelUtil.saveToFile(model, file.getAbsolutePath());
        }

        final T modelRefCopy = model;
        model.eAdapters()
            .add(new EContentAdapter() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void notifyChanged(org.eclipse.emf.common.notify.Notification notification) {
                    super.notifyChanged(notification);

                    // SYNC WITH FILE
                    if (!file.exists()) {
                        // CREATE PARENT DIRS
                        file.getParentFile()
                            .mkdirs();
                    }
                    ModelUtil.saveToFile(modelRefCopy, file);

                    if (listener != null) {
                        listener.onChange(notification);
                    }
                }
            });

        return model;
    }

    /**
     * Removes the listeners for the synchronization from the given model.
     * 
     * @param obj
     *            the model which should not be synchronized anymore
     */
    public static void clear(EObject obj) {
        if (obj != null) {
            obj.eAdapters()
                .clear();
        }
    }

}
