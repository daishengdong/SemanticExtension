package com.fatty.ontology.reasoning;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
// import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.shared.RulesetNotFoundException;

/**
 * @purpose 根据本体文件以及规则，实现本体推理功能
 * 
 */
public class MyReasoner {
	private InfModel inf = null;

	/**
	 * 获取一个推理接口
	 * @param path
	 * @return
	 * @throws RulesetNotFoundException
	 */
	private GenericRuleReasoner getReasoner(String path) throws RulesetNotFoundException {
		List<Rule> rules = Rule.rulesFromURL(path);  //"file:data/books.rules"
		GenericRuleReasoner reasoner = new GenericRuleReasoner(rules);
		reasoner.setOWLTranslation(true);
		reasoner.setDerivationLogging(true);
		reasoner.setTransitiveClosureCaching(true);
		return reasoner;
	}
	
	/**
	 * 获取推理的本体
	 * @param path
	 * @return
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	private OntModel getOntModel(String path) {
		Model model = ModelFactory.createDefaultModel();

		try {
			//"file:data/books.owl"
	        InputStreamReader isr = new InputStreamReader(new FileInputStream(new File(path)), "UTF-8");  
	        model.read(isr, null);
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		
		OntModel ont = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF, model);
		/*
		Resource configuration = ont.createResource(); //a new anonymous resource linked to this model
		configuration.addProperty(ReasonerVocabulary.PROPruleMode , "hybrid");
		*/
		return ont;
	}
	
	/**
	 * InfModel是对常规Model的扩展，支持任何相关的推理能力
	 * @param ontPath
	 * @param rulePath
	 * @return
	 */
	public InfModel getInfModel(String rulePath, String ontPath) {
		this.inf = ModelFactory.createInfModel(getReasoner(rulePath), getOntModel(ontPath));
		return this.inf;
	}
	
	/**
	 * InfModel是对常规Model的扩展，支持任何相关的推理能力
	 * @param model
	 * @param rulePath
	 * @return
	 */
	public InfModel getInfModel(String rulePath, OntModel model) {
		this.inf = ModelFactory.createInfModel(getReasoner(rulePath), model);
		return this.inf;
	}
	
	/**
	 * 打印推理结果
	 * @param a
	 * @param b
	 */
	public void printInferResult(Resource a, Resource b) {
		StmtIterator stmtIter = this.inf.listStatements(a, null, b);
		if (!stmtIter.hasNext()) {
			System.out.println("there is no relation between "
				      + a.getLocalName() + " and " + b.getLocalName());
			System.out.println("\n-------------------\n");
		}
		while (stmtIter.hasNext()) {
			Statement s = stmtIter.nextStatement();
			System.out.println("Relation between " + a.getLocalName() + " and "
				      + b.getLocalName() + " is :");
			System.out.println(a.getLocalName() + " "
				      + s.getPredicate().getLocalName() + " " + b.getLocalName());
			System.out.println("\n-------------------\n");
		}
	}
	
	public List<String> searchOnto(String queryString) {
		Query query = QueryFactory.create(queryString);	  
	    QueryExecution qexec = QueryExecutionFactory.create(query, this.inf);
	    try {
	    	ResultSet rs = qexec.execSelect();

	    	List<String> list = new ArrayList<String>();
	    	while (rs.hasNext()) {
		    	Resource resource = (Resource) rs.next().get("resource");
		    	list.add(resource.getLocalName());
	    	}

	    	// ResultSetFormatter.out(System.out, results, query);
	    	return list;
	    } finally {
		    qexec.close();
	    }
	}
	/*
	// Create the data.
    // This wil be the background (unnamed) graph in the dataset.
    Model model = createModel() ;

    // First part or the query string 
    String prolog = "PREFIX dc: <"+DC.getURI()+">" ;

    // Query string.
    String queryString = prolog + NL +
        "SELECT ?title WHERE {?x dc:title ?title}" ; 

    Query query = QueryFactory.create(queryString) ;
    // Print with line numbers
    query.serialize(new IndentedWriter(System.out,true)) ;
    System.out.println() ;

    // Create a single execution of this query, apply to a model
    // which is wrapped up as a Dataset

    QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
    // Or QueryExecutionFactory.create(queryString, model) ;

    System.out.println("Titles: ") ;

    try {
        // Assumption: it's a SELECT query.
        ResultSet rs = qexec.execSelect() ;

        // The order of results is undefined. 
        for ( ; rs.hasNext() ; )
        {
            QuerySolution rb = rs.nextSolution() ;

            // Get title - variable names do not include the '?' (or '$')
            RDFNode x = rb.get("title") ;

            // Check the type of the result value
            if ( x.isLiteral() )
            {
                Literal titleStr = (Literal)x  ;
                System.out.println("    "+titleStr) ;
            }
            else
                System.out.println("Strange - not a literal: "+x) ;

        }
    }
    finally
    {
        // QueryExecution objects should be closed to free any system resources 
        qexec.close() ;
    }
	 */
}
