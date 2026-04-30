package cipm.consistency.designtime.instrumentation2;

import java.util.HashMap;
import java.util.Map;
import org.emftext.language.java.statements.Statement;
import org.palladiosimulator.pcm.seff.AbstractAction;

/**
 * A mapping between AbstractActions and their corresponding statement. In case of InternalActions,
 * the statement is the first statement.
 * 
 * @author Martin Armbruster
 */
@SuppressWarnings("serial")
public class ActionStatementMapping extends HashMap<AbstractAction, Statement> {
    private HashMap<AbstractAction, Statement> actionToLastStatement = new HashMap<>();

    /**
     * Provides an additional mapping between AbstractActions and their corresponding last
     * statement. Mostly intended for InternalActions.
     * 
     * @return a mapping between AbstractActions and their corresponding last statement.
     */
    public Map<AbstractAction, Statement> getAbstractActionToLastStatementMapping() {
        return this.actionToLastStatement;
    }
}
