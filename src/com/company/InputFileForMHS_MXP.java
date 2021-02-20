package com.company;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.util.iterator.ExtendedIterator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class InputFileForMHS_MXP {

    List<Individual> individualList;
    OntModel ontModel;
    String formatInputFile = "-f: %s\n-o: %s\n-p: {\n%s}";
    String folderWithModifiedOntology;

    public InputFileForMHS_MXP(OntModel model, String folderWithModifiedOntology){
        ExtendedIterator<Individual> a = model.listIndividuals();
        individualList = new ArrayList<>();
        ontModel = model;
        this.folderWithModifiedOntology = folderWithModifiedOntology;

        while (a.hasNext()){
            individualList.add(a.next());
        }
    }

    public Individual getRandomIndividual(){
        int max = individualList.size();
        Random random = new Random();
        int index = random.nextInt(max);
        return individualList.get(index);
    }

    public void createInputFile(String ontologyName, OntClass classFromAxiom, String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        Individual individual = getRandomIndividual();

        String ontologyFile = folderWithModifiedOntology + ontologyName;
        String observationFormat = "%s:%s(%s:%s)";
        String prefixClass = "ode";
        String prefixIndividual = "lubm";
        String observation = String.format(observationFormat, prefixClass, classFromAxiom.getLocalName(), prefixIndividual, individual.getLocalName());
        String prefixes = prefixClass + ": " + classFromAxiom.getNameSpace() + "\n" +
                            prefixIndividual + ": " + individual.getNameSpace() + "\n";
        String file = String.format(formatInputFile, ontologyFile, observation, prefixes);

        writer.write(file);
        writer.close();
    }



}
