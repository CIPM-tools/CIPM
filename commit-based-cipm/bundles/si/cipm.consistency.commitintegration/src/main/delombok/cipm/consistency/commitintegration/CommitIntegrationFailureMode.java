package cipm.consistency.commitintegration;

public enum CommitIntegrationFailureMode {
    /**
     * Abort when propagations fail
     */
    ABORT,

    /**
     * Locate the last successful snapshot of the commit integration state and try load it.
     */
    BACKUP,
    
    /**
     * Dispose the loaded commit integration state and reload it from disk.
     */
    RELOAD,
    
    /**
     * Dispose the loaded commit integration state and load a new empty VSUM.
     */
    CLEAN;
}
