package org.splevo.vpm.analyzer.semantic.lucene.finder;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.splevo.vpm.analyzer.semantic.ConfigDefaults;

/**
 * This {@link AbstractRelationshipFinder} uses rare terms in a
 * document and searches for different documents having the same term.
 * 
 * @author Daniel Kojic
 *
 */
public class RareTermFinder extends AbstractLuceneQueryFinder {

	/** The maximum share of a term in a document to be part of the search. */
	private double maxPercentage;
	
	/** Stores a String representation of the rare terms. */
	private StringBuilder termsFound;

	
	/**
	 * Initializations. Queries the content of the
	 * given {@link DirectoryReader}. 
	 * 
	 * @param reader The {@link DirectoryReader}.
	 * @param matchComments Indicates whether to include comments for analysis or not.
	 * @param maxPercentage The maximum term percentage.
	 */
	public RareTermFinder(DirectoryReader reader, boolean matchComments, double maxPercentage) {
		super(reader, matchComments);
		this.maxPercentage = maxPercentage;
		this.termsFound = new StringBuilder();
	}

	/**
	 * Initializations. Queries the content of the
	 * given {@link DirectoryReader}.
	 * 
	 * @param reader The {@link DirectoryReader}.
	 * @param matchComments Indicates whether to include comments for analysis or not.
	 */
	public RareTermFinder(DirectoryReader reader, boolean matchComments) {
		super(reader, matchComments);
		this.maxPercentage = ConfigDefaults.DEFAULT_OVERALL_MINIMUM_SIMILARITY;
		this.termsFound = new StringBuilder();
	}

	@Override
	protected Query buildQuery(String fieldName, Map<String, Integer> termFrequencies) {
		BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
		BooleanQuery finalQuery = new BooleanQuery();
		Integer min = Collections.min(termFrequencies.values());
		int sum = getSum(termFrequencies.values());
		termsFound = new StringBuilder();
		for (String key : termFrequencies.keySet()) {
			float percentageShare = (float) termFrequencies.get(key) / (float) sum;
			if (termFrequencies.get(key) == min || percentageShare < this.maxPercentage) {
				termsFound.append(key + " ");
				Term t = new Term(fieldName, key);
				TermQuery termQuery = new TermQuery(t);
				finalQuery.add(termQuery, Occur.SHOULD);
			}
		}
		
		return finalQuery;
	}

	/**
	 * Calculates the sum of all integers in the given {@link Collection}.
	 * 
	 * @param values The {@link Collection} containing the integers.
	 * @return The sum.
	 */
	private int getSum(Collection<Integer> values) {
		int sum = 0;
		
		for (Integer integer : values) {
			sum += integer;
		}
		
		return sum;
	}

	@Override
	protected String getExplanation() {
		return "Rare Terms: " + termsFound.toString();
	}

}
