package cipm.consistency.commitintegration.lang.lua.changeresolution.lua;

import org.eclipse.emf.compare.utils.IEqualityHelper;
import org.eclipse.emf.ecore.EObject;
import org.splevo.diffing.match.HierarchicalMatchEngine.EqualityStrategy;

public class LuaEqualityStrategy implements EqualityStrategy {
    private IEqualityHelper equalityHelper;

    public LuaEqualityStrategy(IEqualityHelper equalityHelper) {
        this.equalityHelper = equalityHelper;
    }

    @Override
    public boolean areEqual(EObject left, EObject right) {
        return equalityHelper.matchingValues(left, right);
    }

}
