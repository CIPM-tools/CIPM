package cipm.consistency.base.shared.pcm.util;

import java.util.concurrent.TimeUnit;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.eclipse.emf.ecore.EObject;

import de.uka.ipd.sdq.identifier.Identifier;

/**
 * Caches PCM elements by their ID. Therefore the elements that should be stored need to extend
 * {@link Identifier}.
 * 
 * @author David Monschein
 *
 * @param <T>
 *            the type of the elements
 */
public class PCMElementIDCache<T extends Identifier> {

    private Cache<String, T> cache;

    private Class<T> clazz;

    /**
     * Creates a new cache for PCM elements that conform to a given class.
     * 
     * @param clazz
     *            the type of elements that should be cached
     */
    public PCMElementIDCache(Class<T> clazz) {
        this.clazz = clazz;
        this.cache = Cache2kBuilder.of(String.class, clazz)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .resilienceDuration(30, TimeUnit.SECONDS)
            .refreshAhead(false)
            .build();
    }

    /**
     * Removes all entries from the cache.
     */
    public void clear() {
        cache.clear();
    }

    /**
     * Resolves the element from the cache or if it is not present in the cache it is resolved from
     * a given root element. If the element is not present their either, null is returned.
     * 
     * @param parent
     *            the root element that should be searched
     * @param id
     *            the ID to search for
     * @return the element, if it is present in the cache or in the given element, null otherwise
     */
    public T resolve(EObject parent, String id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        } else {
            T el = PCMUtils.getElementById(parent, clazz, id);
            if (el != null) {
                cache.put(id, el);
            }
            return el;
        }
    }

}
