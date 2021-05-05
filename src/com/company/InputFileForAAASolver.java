package com.company;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.util.iterator.ExtendedIterator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InputFileForAAASolver {

    List<Individual> individualList;
    String allAbducibles;
    List<OntClass> conceptList;
    OntModel ontModel;
    String formatInputFile = "java -Xmx4096m \\\n-jar AAA.jar \\\n-obs %s \\\n-abd %s \\\n-d %s \\\n-i %s \\\n-out %s \\\n-t 14400";
    String folderWithModifiedOntology;

    public InputFileForAAASolver(OntModel model, String folderWithModifiedOntology){
        ExtendedIterator<Individual> a = model.listIndividuals();
        ExtendedIterator<OntClass> classes = model.listClasses();

        individualList = new ArrayList<>();
        conceptList = new ArrayList<>();
        ontModel = model;
        this.folderWithModifiedOntology = folderWithModifiedOntology;

        StringBuffer stringBuffer = new StringBuffer();
        while (a.hasNext()){
            Individual temp = a.next();
            if(temp != null && temp.getURI() != null){
                individualList.add(temp);
                stringBuffer.append(temp.getURI() + ", ");
        }
        }

        while (classes.hasNext()){
            OntClass temp = classes.next();
            if(temp != null && temp.getURI() != null){
                conceptList.add(temp);
                stringBuffer.append(temp.getURI() + ", ");
            }
        }
        allAbducibles = stringBuffer.toString();
        if(allAbducibles.length() > 1 && allAbducibles.charAt(allAbducibles.length() - 2) == ',' && allAbducibles.charAt(allAbducibles.length() - 1) == ' '){
            allAbducibles = allAbducibles.substring(0, allAbducibles.length() - 2);
        }
    }

    public Individual getRandomIndividual(String randomIndividualIRI){
        int max = individualList.size();
        if(max == 0){
            ontModel.createIndividual(ontModel.createResource(randomIndividualIRI));
            Individual random = ontModel.getIndividual(randomIndividualIRI);
            return  random;
        }
        Random random = new Random();
        int index = random.nextInt(max);
        return individualList.get(index);
    }

    public void createInputFile(String ontologyName, OntClass classFromAxiom, String fileName, String outputFile, Individual individual, boolean negation, String randomIndividualIRI) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

        String ontologyFile = folderWithModifiedOntology + ontologyName;
        String observationFormat = "\"%s: %s\"";
        String abdFormat = "\"%s\"";
        String observation = String.format(observationFormat, individual.getURI(), classFromAxiom.getURI());
        if(randomIndividualIRI.equals(individual.getURI()) && !allAbducibles.contains(randomIndividualIRI)){
            allAbducibles += ", " + randomIndividualIRI;
        }
        String abducibles = String.format(abdFormat, allAbducibles);
        String d = "5";
        String output = folderWithModifiedOntology + outputFile;

        String file = String.format(formatInputFile, observation, abducibles, d, ontologyFile, output);

        if(!negation){
            file += " \\\n-n";
        }

        writer.write(file);
        writer.close();
    }

}
