package com.company;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.util.iterator.ExtendedIterator;

import java.io.*;
import java.time.Period;
import java.util.*;

public class NewInputFileForMHS_MXP {

    int INTERSECTION = 0;
    int HERMIT = 2;
    int PELLET = 3;
    int JFACT = 4;

    List<String> individualList;
    List<String[]> classList;
    List<String[]> changedClassList;
    List<String> changedIndividualList;
    Map<String, String> prefixes;
    String formatInputFile = "-f: files/%s.owl\n-o: %s\n-t: 14400";
    String configurationFile = "_configurationFile.txt";
    String configurationFileIndividuals = "_configurationFile_individuals.txt";

    public NewInputFileForMHS_MXP(boolean fromConfig, List<String> individualList, List<String[]> classList) {
        this.individualList = individualList;
        this.classList = classList;
        this.changedClassList = new ArrayList<>();
        for(String[] l : classList){
            this.changedClassList.add(l.clone());
        }
        this.changedIndividualList = new ArrayList<>(individualList);
        prefixes = new HashMap<>();

        if(fromConfig){
            getPrefixes(individualList, classList);
        } else {

        }
    }

    public void getPrefixes(List<String> individualList, List<String[]> classList){
        int i = 0;
        List<String> allSymbols = new ArrayList<>();
        allSymbols.addAll(individualList);
        for(String[] list : classList){
            allSymbols.addAll(Arrays.asList(list));
        }

        for(String symbol : allSymbols){
            String prefixValue;
            if(symbol.contains("#")){
                String[] temp = symbol.split("#");
                prefixValue = temp[0] + "#";
                if(!prefixes.containsKey(prefixValue)){
                    prefixes.put(prefixValue, "prefix" + i);
                    i++;
                }

            } else {
                if(!symbol.equals("www.University0.edu") && !symbol.equals("http://www.Department9.University0.edu")){
                    int index = symbol.lastIndexOf('/');
                    String temp = symbol.substring(0, index);
                    prefixValue = temp + "/";
                    if(!prefixes.containsKey(prefixValue)){
                        prefixes.put(prefixValue, "prefix" + i);
                        i++;
                    }
                }
            }

        }

        createClassesWithPrefixes();
        createIndividualsWithPrefixes();
    }

    public void createClassesWithPrefixes(){
        for(int i = 0; i < classList.size(); i++){
            for(int j = 0; j < classList.get(i).length; j++){
                String onlyClass;
                for(String prefixVal : prefixes.keySet()){
                    if(classList.get(i)[j].contains(prefixVal)){
                        onlyClass = classList.get(i)[j].replace(prefixVal, "");
                        changedClassList.get(i)[j] = prefixes.get(prefixVal) + ":" + onlyClass;
                    }
                }
            }
        }
    }

    public void createIndividualsWithPrefixes(){
        for(int i = 0; i < individualList.size(); i++){
            if(!individualList.get(i).equals("www.University0.edu") && !individualList.get(i).equals("http://www.Department9.University0.edu")){
                String onlyIndividual;
                for(String prefixVal : prefixes.keySet()){
                    if(individualList.get(i).contains(prefixVal)){
                        onlyIndividual = individualList.get(i).replace(prefixVal, "");
                        changedIndividualList.set(i, prefixes.get(prefixVal) + ":" + onlyIndividual);
                    }
                }
            }
        }
    }

    public String createPrefixesInManchesterSyntax(int configIndex){
        StringBuilder result = new StringBuilder();
        String formatPrefix = "Prefix: %s: <%s> ";
        Set<String> usedPrefixes = new HashSet<>();

        for(int i = 0; i < classList.get(configIndex).length; i++){
            for(String prefixVal : prefixes.keySet()){
                if(classList.get(configIndex)[i].contains(prefixVal)){
                    usedPrefixes.add(prefixVal);
                }
            }
        }

        for(String prefixVal : prefixes.keySet()){
            if(individualList.get(configIndex).contains(prefixVal)){
                usedPrefixes.add(prefixVal);
            }
        }

        for(String prefix : usedPrefixes){
            result.append(String.format(formatPrefix, prefixes.get(prefix), prefix));
        }

        return result.toString();
    }

    public String createClassDeclarationInManchesterSyntax(int configIndex){
        StringBuilder result = new StringBuilder();
        String formatClassDeclaration = "Class: %s ";

        for(int i = 0; i < changedClassList.get(configIndex).length; i++){
            result.append(String.format(formatClassDeclaration, changedClassList.get(configIndex)[i]));
        }

        return result.toString();
    }

    public String createTypeForIndividualInManchesterSyntax(int configIndex, int connection){
        StringBuilder result = new StringBuilder();
        if(INTERSECTION == connection){
            for(int i = 0; i < changedClassList.get(configIndex).length; i++){
                result.append(changedClassList.get(configIndex)[i]);
                if(i != changedClassList.get(configIndex).length - 1){
                    result.append(" and ");
                }
            }
        }
        return result.toString();
    }

    public String createConceptAssertionInManchesterSyntax(int configIndex, String individualType){
        String formatObservation = "Individual: %s Types: %s";
        return String.format(formatObservation, changedIndividualList.get(configIndex), individualType);
    }

    public String createObservationOntologyInManchesterSyntax(int configIndex, int connection){
        StringBuilder observation = new StringBuilder();
        String prefixes = createPrefixesInManchesterSyntax(configIndex);
        String classesDeclarations = createClassDeclarationInManchesterSyntax(configIndex);
        String individualType = createTypeForIndividualInManchesterSyntax(configIndex, connection);
        String conceptAssertion = createConceptAssertionInManchesterSyntax(configIndex, individualType);
        observation.append(prefixes);
        observation.append(classesDeclarations);
        observation.append(conceptAssertion);
        return observation.toString();
    }

    public void createInputFileFromConfig(String ontologyName, String fileName, boolean usingNegation, int reasoner, int configIndex, int connection, String folderForInputFiles) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(folderForInputFiles + fileName));

        String observation = createObservationOntologyInManchesterSyntax(configIndex, connection);

        String file = String.format(formatInputFile, ontologyName, observation);
        if(!usingNegation){
            file += "\n-n: false";
        }
        if(reasoner == JFACT){
            file += "\n-r: jFact";
        } else if(reasoner == PELLET){
            file += "\n-r: pellet";
        }
        writer.write(file);
        writer.close();
    }

    public void createAllInputFilesFromConfig(String ontologyName, String folderForInputFiles) throws IOException {
        for(int configIndex = 0; configIndex < classList.size(); configIndex++){
            String fileName1 = ontologyName + "_" + classList.get(configIndex).length + "_" + configIndex + ".in";
            String fileName2 = ontologyName + "_" + classList.get(configIndex).length + "_" + configIndex + "_noNeg.in";
            String fileName3 = ontologyName + "_" + classList.get(configIndex).length + "_" + configIndex + "_P.in";;
            String fileName4 = ontologyName + "_" + classList.get(configIndex).length + "_" + configIndex + "_P_noNeg.in";
            String fileName5 = ontologyName + "_" + classList.get(configIndex).length + "_" + configIndex + "_J.in";;
            String fileName6 = ontologyName + "_" + classList.get(configIndex).length + "_" + configIndex + "_J_noNeg.in";

            createInputFileFromConfig(ontologyName, fileName1, true, HERMIT, configIndex, INTERSECTION, folderForInputFiles);
            createInputFileFromConfig(ontologyName, fileName2, false, HERMIT, configIndex, INTERSECTION, folderForInputFiles);
            /*createInputFileFromConfig(ontologyName, fileName3, true, PELLET, configIndex, INTERSECTION, folderForInputFiles);
            createInputFileFromConfig(ontologyName, fileName4, false, PELLET, configIndex, INTERSECTION, folderForInputFiles);
            createInputFileFromConfig(ontologyName, fileName5, true, JFACT, configIndex, INTERSECTION, folderForInputFiles);
            createInputFileFromConfig(ontologyName, fileName6, false, JFACT, configIndex, INTERSECTION, folderForInputFiles);*/
        }
    }
}
