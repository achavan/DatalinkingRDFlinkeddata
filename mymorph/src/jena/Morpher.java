/*
 *  Morpher.java
 *
 * Copyright (c) 2000-2012, The University of Sheffield.
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free
 * software, licenced under the GNU Library General Public License,
 * Version 3, 29 June 2007.
 *
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://gate.ac.uk/gate/licence.html.
 *
 *  ANISH, 4/3/2014
 *
 * For details on the configuration options, see the user guide:
 * http://gate.ac.uk/cgi-bin/userguide/sec:creole-model:config
 */

package jena;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import gate.*;

import gate.creole.*;
import gate.creole.metadata.*;
import gate.util.*;


@CreoleResource(name = "mymorph",
        comment = "Add a descriptive comment about this resource")
public class Morpher    extends AbstractLanguageAnalyser  implements ProcessingResource 
{
	int count=0;
	 Document doc=null;
	 ArrayList<Integer>list=new ArrayList<Integer>();
	 ArrayList<Integer>list1=new ArrayList<Integer>();
	 ArrayList<Integer>list2=new ArrayList<Integer>();
	 Model model = ModelFactory.createDefaultModel();
	 String prefix   = "http://www.semanticweb.org/anish/ontologies/2014/2/untitled-ontology-4#";
	 String UniversityPrefix="http://univname/#";
	 OntModel onto = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
	 OntClass Person ;
	 OntClass University ;

	 OntClass UniversityPerson;
	 OntClass UniversityPosition ;
	 OntClass OrganizationalUnit ;
	 String DBPEDIA="http://dbpedia.org/resource/";
	// FileWriter fileName =null;
	 FileWriter out = null;
	 File file=null;
	 
	 public void execute() throws ExecutionException
	 {					
		// model.read("newo.owl");
		 onto.read("newo.owl");
		 Person = onto.getOntClass(prefix + "Person" );
		 University = onto.getOntClass(prefix + "University" );

		 UniversityPerson = onto.getOntClass(prefix + "UniversityPerson" );
		 UniversityPosition = onto.getOntClass(prefix + "UniversityPosition" );
		 OrganizationalUnit = onto.getOntClass(prefix + "OrganizationalUnit" );
		 try {
			corelation();
		} catch (InvalidOffsetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 try {
			printrdfuniversity() ;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 printrdfOrganizationUnit();
		 printrdfUniversityPerson();
		 printrdfUniversityPosition();
		 rdfbelongsto();
		 rdfhasposition();
		 writeoutput();
		 
	 }
	 void corelation() throws InvalidOffsetException
		{
			doc=getDocument();
			AnnotationSet aanoset=doc.getAnnotations();
			AnnotationSet blacklist1=aanoset.get("Person");
			AnnotationSet blacklist2=aanoset.get("Organization");
			AnnotationSet blacklist3=aanoset.get("Location");
			ArrayList<Long> annotCategory=new ArrayList<Long>();
			int bb1=0;
			for(Annotation annot: blacklist1)
			{   
			
				FeatureMap map = annot.getFeatures();
				if((map.get("matches"))!=null)
				{
					annotCategory=((ArrayList<Long>) map.get("matches"));
					
					for(Object k :annotCategory )
					{
						
						
						if(!list.contains((Integer)k))
						{	
							list.add((Integer)k);
							
						}
					
					}
//==================================================================================================================================					
					for (Object m:annotCategory)
					{
						Annotation antecedent = blacklist1.get((Integer) m);
						
						String content1 = doc.getContent().getContent
								(antecedent.getStartNode().getOffset(),
										antecedent.getEndNode().getOffset()).toString();
						try {
							String uri1=URLEncoder.encode(content1,"UTF-8");
						} catch (UnsupportedEncodingException e) {
							
							e.printStackTrace();
						}
					
						for(Object l:annotCategory)
						{
							
							Annotation antecedent2 = blacklist1.get((Integer)l);
							String content2 = doc.getContent().getContent
									(antecedent2.getStartNode().getOffset(),
											antecedent2.getEndNode().getOffset()).toString();
							try {
								String uri2=URLEncoder.encode(content2,"UTF-8");
							} catch (UnsupportedEncodingException e) {
								
								e.printStackTrace();
							}
							onto.createResource(prefix+content1).addProperty(OWL.sameAs, prefix +content2);
							
						}
//==================================================================================================================================						
					}
			
				}
				
				
			}
		
			count++;	
			}
	 
	void creatfile()
	{
		try
		{
			Random or = new Random();
				
					file=new File("Data6.rdf");
					if(!file.exists())
					{	
						file.createNewFile();
					}
					else
					{
						
						model =  RDFDataMgr.loadModel("Data6.rdf") ;
						
					}
					

		}
	catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	void printrdfuniversity() throws UnsupportedEncodingException
	 {
		 	ArrayList<Integer>checklist=new ArrayList<Integer>();
			 doc=getDocument();
		 	AnnotationSet aanoset=doc.getAnnotations();
			AnnotationSet Uposition=aanoset.get("university");
			int i=0;
			for(Annotation annot: Uposition)
				{String content="";
					try	{ 	
							Integer id= annot.getId();
							
								
								
								content = doc.getContent().getContent
										(annot.getStartNode().getOffset(),
												annot.getEndNode().getOffset()).toString();
								
//===================================================================================================================================								
								String mName= content.replace(" ", "_");
								OntClass equipe = onto.getOntClass(prefix + "University" );
								 
								 Individual m=onto.createIndividual(prefix +mName ,equipe);
								 String foaforg=FOAF.Organization.getURI();
								 m.addProperty(RDF.type,equipe).addProperty(RDFS.subClassOf,FOAF.Organization ).addProperty(OWL.sameAs,DBPEDIA +mName);
								 m.addProperty(FOAF.name,mName);

								checklist.add(id);
								
						 
								//Property p =model.getProperty("");
						  
							
							/*else
							{
								if (!checklist.contains(id))
									{
//=================================================================================================================================
										String mName2= content.replace(" ", "_");
										OntClass equipe = onto.getOntClass(prefix + "University" );
										 String InstanceURI1 = URLEncoder.encode(content,"UTF-8");
										 Individual m=onto.createIndividual(prefix +InstanceURI1 ,equipe);
										 String foaforg=FOAF.Organization.getURI();
										 m.addProperty(RDF.type,equipe).addProperty(RDFS.subClassOf,FOAF.Organization ).addProperty(OWL.sameAs,DBPEDIA +mName2);
										 

										checklist.add(id);
									}
								
							}*/
						
						}
				
					catch(InvalidOffsetException e)
				
						{
							e.printStackTrace();
					
						}
				
					}	 
		
					//  onto.addLiteral(johnSmith, null, 0);
				
					//model.createResource ("http://www.semanticweb.org/"+"xyz");
					// OntClass oc =onto.getOntClass("Person");
			  count++;	
				
	 	}
	void printrdfOrganizationUnit()
	{
		ArrayList<Integer>checklist=new ArrayList<Integer>();
		 doc=getDocument();
	 	AnnotationSet aanoset=doc.getAnnotations();
		AnnotationSet Uposition=aanoset.get("OrganizationalUnit");
		for(Annotation annot: Uposition)
		{String content="";
			try	{ 	
					Integer id= annot.getId();
					if(!list.contains(id))
					{	
						content = doc.getContent().getContent
								(annot.getStartNode().getOffset(),
										annot.getEndNode().getOffset()).toString();
//====================================================================================================================================
						String mName= content.replace(" ", "_");

						 OntClass equipe = onto.getOntClass(prefix + "OrganizationalUnit" );
						 String InstanceURI1 = URLEncoder.encode(content,"UTF-8");
						 Individual m=onto.createIndividual(prefix +InstanceURI1 ,equipe);
						 
						 m.addProperty(RDF.type,equipe);

						checklist.add(id);
//======================================================================================================================================			  
					}
					else
					{
						if (!checklist.contains(id))
							{
								content = doc.getContent().getContent
										(annot.getStartNode().getOffset(),
												annot.getEndNode().getOffset()).toString();
								String mName2= content.replace(" ", "_");

								 OntClass equipe = onto.getOntClass(prefix + "OrganizationalUnit" );
								 String InstanceURI = URLEncoder.encode(content,"UTF-8");
								 Individual m=onto.createIndividual(prefix +InstanceURI ,equipe);
								 
								 m.addProperty(RDF.type,equipe);
//===================================================================================================================================
								checklist.add(id);
							}
						
					}
				
				}
		
			catch(InvalidOffsetException e)
		
				{
					e.printStackTrace();
			
				}catch (IOException e) {
					e.printStackTrace();
				}
		
			}	 

			
		  count++;	

		
	}
	void printrdfUniversityPerson()
	{
		ArrayList<Integer>checklist=new ArrayList<Integer>();
		 doc=getDocument();
	 	AnnotationSet aanoset=doc.getAnnotations();
		AnnotationSet Uposition=aanoset.get("UniversityPerson");
		for(Annotation annot: Uposition)
		{String content="";
		try	{ 	
			Integer id= annot.getId();
			if(!list.contains(id))
			{	
				content = doc.getContent().getContent
						(annot.getStartNode().getOffset(),
								annot.getEndNode().getOffset()).toString();
//================================================================================================================================								
						String mName2= content.replace(" ", "_");
						OntClass equipe = onto.getOntClass(prefix + "UniversityPerson" );
						String InstanceURI = URLEncoder.encode(content,"UTF-8");
						Individual m=onto.createIndividual(prefix +InstanceURI ,equipe);
						m.addProperty(RDF.type,equipe).addProperty(RDFS.subClassOf, FOAF.Person);

				
		 
		  
			}
			else
			{
				if (!checklist.contains(id))
					{
						content = doc.getContent().getContent
								(annot.getStartNode().getOffset(),
										annot.getEndNode().getOffset()).toString();
						String mName2= content.replace(" ", "_");
						OntClass equipe = onto.getOntClass(prefix + "UniversityPerson" );
						String InstanceURI = URLEncoder.encode(content,"UTF-8");
						Individual m=onto.createIndividual(prefix +InstanceURI ,equipe);
						m.addProperty(RDF.type,equipe);
						checklist.add(id);
					}
				
			}
		
		}

	catch(InvalidOffsetException e)

		{
			e.printStackTrace();
	
		}catch (IOException e) {
			e.printStackTrace();
		}

	
			
		}
		
		  count++;	

	}
	void printrdfUniversityPosition()
	{
		ArrayList<Integer>checklist=new ArrayList<Integer>();
		 doc=getDocument();
	 	AnnotationSet aanoset=doc.getAnnotations();
		AnnotationSet Uposition=aanoset.get("UniversityPosition");
		for(Annotation annot: Uposition)
		{String content="";
		try	{ 	
			Integer id= annot.getId();
			if(!list.contains(id))
			{	
				content = doc.getContent().getContent
						(annot.getStartNode().getOffset(),
								annot.getEndNode().getOffset()).toString();
				String mName2= content.replace(" ", "_");
				OntClass equipe = onto.getOntClass(prefix + "UniversityPosition" );
				String InstanceURI = URLEncoder.encode(content,"UTF-8");
				Individual m=onto.createIndividual(prefix +InstanceURI ,equipe);
				m.addProperty(RDF.type,equipe);
				//Property p =model.getProperty("");
		  
			}
			else
			{
				if (!checklist.contains(id))
					{
						content = doc.getContent().getContent
								(annot.getStartNode().getOffset(),
										annot.getEndNode().getOffset()).toString();
//===================================================================================================================================
						String mName2= content.replace(" ", "_");
						OntClass equipe = onto.getOntClass(prefix + "UniversityPosition" );
						String InstanceURI = URLEncoder.encode(content,"UTF-8");
						Individual m=onto.createIndividual(prefix +InstanceURI ,equipe);
						m.addProperty(RDF.type,equipe);
						checklist.add(id);
					}
				
			}
		
		}

	catch(InvalidOffsetException e)

		{
			e.printStackTrace();
	
		}catch (IOException e) {
			e.printStackTrace();
		}

	
			
		
			
		}
		
		  count++;	

	}


	void rdfbelongsto()

	{
		doc=getDocument();
		AnnotationSet aanoset=doc.getAnnotations();
		AnnotationSet Uperson=aanoset.get("Belongs_to");				
		String domaincatagory="";
		String rangecatagory="";


		
		int i=0;
		for(Annotation annot: Uperson)
		{   String content="";
		
			try	
				{ 
				content = doc.getContent().getContent
				(annot.getStartNode().getOffset(),
				annot.getEndNode().getOffset()).toString();

				FeatureMap map = annot.getFeatures();
					if((map.get("dom"))!=null&&map.get("ran")!=null&&map.get("dom")!="")
					{
						domaincatagory= (String) map.get("dom");
						rangecatagory= (String) map.get("ran");
						String domain= domaincatagory.replace(" ", "_");
						String range=rangecatagory.replace(" ", "_");
						String name=content.replace(" ", "_");
						 ObjectProperty belongs_to=onto.getObjectProperty(prefix+"belongs__to");
						 Random or = new Random();
						 Individual m2=onto.createIndividual(prefix+or.nextInt(99999) +name,belongs_to);
						 
						 m2.addProperty(RDFS.domain,UniversityPerson.getURI()+domain);
						 m2.addProperty(RDFS.range,University.getURI()+range);
						 m2.addProperty(RDF.predicate, "belongsto");
						 //System.out.println(prefix + content);
						 //System.out.println(UniversityPerson+domain);
						 
						
				
					}
				}
				
		catch(InvalidOffsetException e)
		
			{
				e.printStackTrace();
			
			}
			} 
		  count++;	

		  } 
	void rdfhasposition()
	{

		doc=getDocument();
		AnnotationSet aanoset=doc.getAnnotations();
		AnnotationSet Uperson=aanoset.get("Has_Position");				
		String domaincatagory="";
		String rangecatagory="";


		
		int i=0;
		for(Annotation annot: Uperson)
		{   String content="";
		
			 
				try {
					content = doc.getContent().getContent
					(annot.getStartNode().getOffset(),
					annot.getEndNode().getOffset()).toString();
				} catch (InvalidOffsetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				FeatureMap map = annot.getFeatures();
					if((map.get("dom"))!=null&&map.get("ran")!=null&&map.get("dom")!=""&&map.get("dom")!="Principal")
					{
						domaincatagory= (String) map.get("dom");
						System.out.println(domaincatagory);
						rangecatagory= (String) map.get("ran");
						String domain= domaincatagory;
						String range=rangecatagory;
						String name=content.replace(" ", "_");
						 ObjectProperty has_position=onto.getObjectProperty(prefix+"has_position");
						 Random or = new Random();
						 Individual m2=onto.createIndividual(prefix+"has_position"+name+or.nextInt(99999),has_position);
						 
						 m2.addProperty(RDFS.domain,UniversityPerson.getURI()+domain);
						 m2.addProperty(RDFS.range,UniversityPosition.getURI()+range);
						 m2.addProperty(RDF.predicate, "hasposition");
						 //System.out.println(prefix + content);
						// System.out.println(UniversityPerson+domain);
						 
						
				
					}
				
		}
		  count++;	

	}
	        
		
	void writeoutput()
	{
		
		
		/*try {
			fileName=new FileWriter(file);
			model.add(onto);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				try {

			model.write(fileName);
			fileName.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		file=new File(System.getProperty("user.home")+"'\'"+"Data6.rdf");
		if(!file.exists())
		{	
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			
			model =  RDFDataMgr.loadModel(System.getProperty("user.home")+"'\'"+"Data6.rdf") ;
			
		}
		
		
		System.out.println("number of triplets "+"------>"+count);
		
			
			model.add(onto);
			
		
		
			try {
				out = new FileWriter( System.getProperty("user.home")+"'\'"+"Data6.rdf" );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 try {
			     model.write( out );
			 }
			 finally {
			    try {
			        out.close();
			    }
			    catch (IOException closeException) {
			        // ignore
			    }
		
	
		
	}
	

	}
	}

 