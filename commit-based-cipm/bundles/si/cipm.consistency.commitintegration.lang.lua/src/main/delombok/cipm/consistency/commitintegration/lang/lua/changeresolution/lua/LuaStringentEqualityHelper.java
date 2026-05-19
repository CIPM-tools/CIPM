package cipm.consistency.commitintegration.lang.lua.changeresolution.lua;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.xtext.lua.lua.Refble;
import org.xtext.lua.lua.Statement_Assignment;

import com.google.common.cache.LoadingCache;

/**
 * A more stringent equality helper for the evaluation
 * The normal helper misses some differences intentionally, to facilitate the change resolution
 * using EMF compare.
 * 
 * @author Lukas Burgey
 *
 */
public class LuaStringentEqualityHelper extends LuaEqualityHelper {

    public LuaStringentEqualityHelper(LoadingCache<EObject, URI> uriCache) {
        super(uriCache);
    }

    @Override
    protected boolean match(Statement_Assignment left, Statement_Assignment right) {
        if (!matchEList(left.getDests(), right.getDests())) {
            return false;
        }
        if (!matchEList(left.getValues(), right.getValues())) {
            return false;
        }
        
        return true;
    }
}
