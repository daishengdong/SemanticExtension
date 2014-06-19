package com.fatty.ontology.reasoning;

import java.util.ArrayList;
import java.util.List;

import com.fatty.ontology.construction.OntologyConstructor;
import com.fatty.searcher.Searcher;

public class OntologyReasoning {
	public static List<String> reasoning(String text, boolean usingCategory, boolean usingSynonym, boolean usingAntonym, MyReasoner reasoner) {
		// Ê×ÏÈ·Ö´Ê
		List<String> wordList = Searcher.splitWord(text);

		String prefix = "PREFIX BOOKS:<" + OntologyConstructor.bookUri + "> "; 
		List<String> reasonedList = new ArrayList<String>();
		for (String word : wordList) {
			if (usingCategory) {
				String queryString = prefix +
						"SELECT ?resource " +
						"WHERE {?resource BOOKS:subClassOf BOOKS:" + word + "}";
				List<String> tmpList = reasoner.searchOnto(queryString);
				reasonedList.addAll(tmpList);
			}
			if (usingSynonym) {
				String queryString = prefix +
						"SELECT ?resource " +
						"WHERE {?resource BOOKS:synonymousOf BOOKS:" + word + "}";
				List<String> tmpList = reasoner.searchOnto(queryString);
				reasonedList.addAll(tmpList);
			}
			if (usingAntonym) {
				String queryString = prefix +
						"SELECT ?resource " +
						"WHERE {?resource BOOKS:antisenseOf BOOKS:" + word + "}";
				List<String> tmpList = reasoner.searchOnto(queryString);
				reasonedList.addAll(tmpList);
			}
		}
		return reasonedList;
	}
}
