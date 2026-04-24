package cipm.consistency.base.shared.util;

/**
 * Stack for runnables that are executed in a given order.
 * 
 * @author David Monschein
 *
 */
public class StackedRunnable implements Runnable {
    private Runnable[] runnables;
    private boolean debug;

    /**
     * Creates a new stacked runnable of given runnables.
     * 
     * @param debug
     *            whether the execution of the runnables should be wrapped by a try-catch block
     *            which catches all exceptions
     * @param runnables
     *            the runnables that should be executed (the order of execution conforms to the
     *            input order)
     */
    public StackedRunnable(boolean debug, Runnable... runnables) {
        this.runnables = runnables;
        this.debug = debug;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        for (Runnable r : runnables) {
            if (debug) {
                try {
                    r.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                r.run();
            }
        }
    }

}
