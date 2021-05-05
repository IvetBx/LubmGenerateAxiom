package com.company;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class InputFileForMHS_MXP {

    List<Individual> individualList;
    OntModel ontModel;
    String formatInputFile = "-f: files/%s\n-o: %s\n-p: {\n%s} \n-t: 14400";
    String folderWithModifiedOntology;
    String configurationFile = "configurationFile_individuals.txt";

    public InputFileForMHS_MXP(OntModel model, String folderWithModifiedOntology){
        ExtendedIterator<Individual> a = model.listIndividuals();
        individualList = new ArrayList<>();
        ontModel = model;
        this.folderWithModifiedOntology = folderWithModifiedOntology;

        while (a.hasNext()){
            individualList.add(a.next());
        }
    }

    public void createFileWithConfigurationIndividual(String individualUri, int prefix){
        try {
            FileWriter writer = new FileWriter(prefix + "_" + configurationFile, true);
            writer.append(individualUri + "\n");
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Individual getRandomIndividual(int prefix, String randomIndividualIRI){
        int max = individualList.size();
        if(max == 0){
            Individual individual = ontModel.getIndividual("http://www.Department9.University0.edu/DummyIndividual");
            return individual;
        }
        Random random = new Random();
        int index = random.nextInt(max);
        Individual result = individualList.get(index);
        createFileWithConfigurationIndividual(result.getURI(), prefix);
        return result;
    }

    public void createInputFile(String ontologyName, OntClass classFromAxiom, String fileName, boolean negation, Individual individual, boolean pellet) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

        String ontologyFile = folderWithModifiedOntology + ontologyName;
        String observationFormat = "%s:%s(%s:%s)";
        String prefixClass = "prefix1";
        String prefixIndividual = "prefix2";
        String observation = "";
        String prefixes = "";

        if(classFromAxiom.getNameSpace().equals(individual.getNameSpace())){
            observation = String.format(observationFormat, prefixClass, classFromAxiom.getLocalName(), prefixClass, individual.getLocalName());
            prefixes = prefixClass + ": " + classFromAxiom.getNameSpace() + "\n";
        } else {
            observation = String.format(observationFormat, prefixClass, classFromAxiom.getLocalName(), prefixIndividual, individual.getLocalName());
            prefixes = prefixClass + ": " + classFromAxiom.getNameSpace() + "\n" +
                    prefixIndividual + ": " + individual.getNameSpace() + "\n";
        }

        String file = String.format(formatInputFile, ontologyFile, observation, prefixes);
        if(!negation){
            file += "\n-n: false";

        }
        if(pellet){
            file += "\n-r: pellet";
        }
        writer.write(file);
        writer.close();
    }



}
