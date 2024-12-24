package cipm.consistency.tools.evaluation.data;

public abstract class ComparisonEvaluationData {
	private int oldElementsCount;
	private int newElementsCount;
	private int intersectionCardinality;
	private int unionCardinality;
	private double jc;
	
	public int getOldElementsCount() {
		return oldElementsCount;
	}
	
	public void setOldElementsCount(int oldElementsCount) {
		this.oldElementsCount = oldElementsCount;
	}
	
	public int getNewElementsCount() {
		return newElementsCount;
	}
	
	public void setNewElementsCount(int newElementsCount) {
		this.newElementsCount = newElementsCount;
	}
	
	public int getIntersectionCardinality() {
		return intersectionCardinality;
	}
	
	public void setIntersectionCardinality(int intersectionCardinality) {
		this.intersectionCardinality = intersectionCardinality;
	}
	
	public int getUnionCardinality() {
		return unionCardinality;
	}
	
	public void setUnionCardinality(int unionCardinality) {
		this.unionCardinality = unionCardinality;
	}
	
	public double getJc() {
		return jc;
	}
	
	public void setJc(double jc) {
		this.jc = jc;
	}
}
