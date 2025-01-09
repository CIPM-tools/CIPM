package cipm.consistency.tools.evaluation.data;

import java.util.ArrayList;
import java.util.List;

/**
 * A data structure for the evaluation of the instrumentation.
 * 
 * @author Martin Armbruster
 */
public class InstrumentationEvaluationData {
	private int expectedLowerStatementDifferenceCount = -1;
	private int expectedUpperStatementDifferenceCount = -1;
	private int statementDifferenceCount = -1;
	private int reloadedStatementDifferenceCount = -1;
	private boolean compiles = false;
	private int numberChangedMethods = -1;
	private List<String> unmatchedChangedMethods = new ArrayList<>();
	private List<String> unmatchedIPs = new ArrayList<>();
	
	public int getExpectedLowerStatementDifferenceCount() {
		return expectedLowerStatementDifferenceCount;
	}
	
	public void setExpectedLowerStatementDifferenceCount(int expectedStatementDifferenceCount) {
		this.expectedLowerStatementDifferenceCount = expectedStatementDifferenceCount;
	}
	
	public int getExpectedUpperStatementDifferenceCount() {
		return expectedUpperStatementDifferenceCount;
	}
	
	public void setExpectedUpperStatementDifferenceCount(int expectedStatementDifferenceCount) {
		this.expectedUpperStatementDifferenceCount = expectedStatementDifferenceCount;
	}
	
	public int getStatementDifferenceCount() {
		return statementDifferenceCount;
	}
	
	public void setStatementDifferenceCount(int statementDifferenceCount) {
		this.statementDifferenceCount = statementDifferenceCount;
	}
	
	public int getReloadedStatementDifferenceCount() {
		return reloadedStatementDifferenceCount;
	}
	
	public void setReloadedStatementDifferenceCount(int reloadedStatementDifferenceCount) {
		this.reloadedStatementDifferenceCount = reloadedStatementDifferenceCount;
	}
	
	public boolean isCompiles() {
		return compiles;
	}
	
	public void setCompiles(boolean compiles) {
		this.compiles = compiles;
	}
	
	public int getNumberChangedMethods() {
		return numberChangedMethods;
	}
	
	public void setNumberChangedMethods(int numberChangedMethods) {
		this.numberChangedMethods = numberChangedMethods;
	}
	
	public List<String> getUnmatchedChangedMethods() {
		return unmatchedChangedMethods;
	}
	
	public List<String> getUnmatchedIPs() {
		return unmatchedIPs;
	}
}
