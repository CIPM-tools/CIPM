package cipm.consistency.commitintegration.lang.lua.runtimedata;

public class ChangedResources {
    private static boolean propagatedResourcesWereChanged = false;
    
    public static void setResourcesWereChanged() {
        propagatedResourcesWereChanged = true;
    }
    
    public static boolean getAndResetResourcesWereChanged() {
        var val = propagatedResourcesWereChanged;
        propagatedResourcesWereChanged = false;
        return val;
    }
}
