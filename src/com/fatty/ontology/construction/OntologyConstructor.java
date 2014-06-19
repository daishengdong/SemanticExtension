package com.fatty.ontology.construction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class OntologyConstructor {
    // URI declarations 
    public static String bookUri = "http://com/fatty/books.owl#"; 
    private static Model model = ModelFactory.createDefaultModel(); 

    public static void construct() {
        // Can also create statements directly .. . 
        // Statement statement = model.createStatement(root_category, directSuperClassOf, computer_category); 
        // but remember to add the created statement to the model 
        // model.add(statement);
    	constructSynonym();
    	constructAntonym();
    	constructCategory();

        try {
	        // new FileOutputStream("data/books.owl");
	        model.write(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/books.owl"), "UTF-8")));
    		System.out.println("本体构建完成！");
        } catch(FileNotFoundException e) {
        	e.printStackTrace();
        } catch(UnsupportedEncodingException e) {
        	e.printStackTrace();
        }
    }

    public static void constructSynonym() {
        File file = new File("data/同义词库.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
	        Property synonymousOf = model.createProperty(bookUri, "synonymousOf"); 
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
            	String[] words = tempString.split(" ");
		        Resource word_rs_last = model.createResource(bookUri + words[0]); 
            	for (int i = 1; i < words.length; ++i) {
			        Resource word_rs_current = model.createResource(bookUri + words[i]); 
			        word_rs_current.addProperty(synonymousOf, word_rs_last);
			        word_rs_last = word_rs_current;
            	}
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    public static void constructAntonym() {
        File file = new File("data/反义词库.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
	        Property antisenseOf = model.createProperty(bookUri, "antisenseOf"); 
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
            	String[] words = tempString.split(" ");
        		Resource word_rs_last = model.createResource(bookUri + words[0]); 
            	for (int i = 0; i < words.length; ++i) {
            		Resource word_rs_current = model.createResource(bookUri + words[i]); 
            		word_rs_current.addProperty(antisenseOf, word_rs_last);
            		word_rs_last = word_rs_current;
            	}
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    public static void constructCategory() {
        // Create properties for the different types of category represent 
        // category properties...
        Property directSuperClassOf = model.createProperty(bookUri, "directSuperClassOf"); 
        /* we have reasoning rule, so no need to do the flowing
        Property superClassOf = model.createProperty(bookUri, "superClassOf"); 
        Property directSubClassOf = model.createProperty(bookUri, "directSubClassOf"); 
        Property subClassOf = model.createProperty(bookUri, "subClassOf"); 
        */
        // Property subClassOf = model.createProperty(bookUri, "subClassOf"); 

        // Create an empty Model 
        // Create a Resource for each category, identified by their URI
        Resource root_category = model.createResource(bookUri + "root_category"); 
        Resource computer_category = model.createResource(bookUri + "计算机"); 
        Resource psychology_category = model.createResource(bookUri + "心理学"); 
        Resource machine_learning_category = model.createResource(bookUri + "机器学习"); 
        Resource artificial_intelligence_category = model.createResource(bookUri + "人工智能"); 
        Resource data_mining_category = model.createResource(bookUri + "数据挖掘"); 
        Resource computer_repairing_category = model.createResource(bookUri + "电脑维修"); 
        Resource program_design_category = model.createResource(bookUri + "程序设计"); 
        Resource android_developing_category = model.createResource(bookUri + "android开发"); 
        Resource web_developing_category = model.createResource(bookUri + "web开发"); 

        // Add properties to property to category 
        // directSuperClassOf ...
        root_category.addProperty(directSuperClassOf, psychology_category); 
        root_category.addProperty(directSuperClassOf, computer_category); 

        computer_category.addProperty(directSuperClassOf, machine_learning_category);
        computer_category.addProperty(directSuperClassOf, computer_repairing_category);
        computer_category.addProperty(directSuperClassOf, program_design_category);

        machine_learning_category.addProperty(directSuperClassOf, artificial_intelligence_category);
        machine_learning_category.addProperty(directSuperClassOf, data_mining_category);
        
        program_design_category.addProperty(directSuperClassOf, android_developing_category);
        program_design_category.addProperty(directSuperClassOf, web_developing_category);

        // subClassOf ...
        /* we have reasoning rule, so no need to do the flowing
        psychology_category.addProperty(directSubClassOf, root_category);
        computer_category.addProperty(directSubClassOf, root_category);

        machine_learning_category.addProperty(directSubClassOf, computer_category);
        computer_repairing_category.addProperty(directSubClassOf, computer_category);

        program_design_category.addProperty(directSubClassOf, computer_category);

        artificial_intelligence_category.addProperty(directSubClassOf, machine_learning_category);
        data_mining_category.addProperty(directSubClassOf, machine_learning_category);

        android_developing_category.addProperty(directSubClassOf, program_design_category);
        web_developing_category.addProperty(directSubClassOf, program_design_category);
        */
    }
}
