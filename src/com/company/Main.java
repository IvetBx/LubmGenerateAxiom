package com.company;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDF;

public class Main {

    String configurationFile = "configurationFile.txt";
    String configurationFileIndividuals = "configurationFile_individuals.txt";

    public void createFileWithConfiguration(List<String[]> combinations, int prefix){
        try {
            FileWriter writer = new FileWriter(prefix + "_" + configurationFile, true);
            for(int i = 0; i < combinations.size(); i++){
                for(int j = 0; j < combinations.get(i).length; j++){
                    if(j != combinations.get(i).length - 1){
                        writer.append(combinations.get(i)[j] + ", ");
                    } else {
                        writer.append(combinations.get(i)[j] + "\n");
                    }
                }
                writer.append("\n");
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String[]> loadAlreadyUsedClasses(String configurationFileName) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(configurationFileName));
        List<String[]> result = new ArrayList<>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if(!line.equals("")){
                String[] classes = line.split(", ");
                result.add(classes);
            }
        }
        return result;
    }

    public List<String> loadAlreadyUsedIndividuals(String configurationFileName) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(configurationFileName));
        List<String> result = new ArrayList<>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            result.add(line);
        }
        return result;
    }

    public String createFolderWithOntologiesAndInputFiles(String newFolder){
        try{
            Files.createDirectory(Paths.get(newFolder));
            return Paths.get(newFolder).toAbsolutePath().toString();
        } catch (FileAlreadyExistsException e){
            System.out.println("Folder already exist and contains ontologies.");
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public OntClass createAxiom(OntModel model, List<String[]> combinationOfClasses, String uriNewClass, int i){
        OntClass dummyClass = model.createClass(uriNewClass);
        RDFNode[] list = new RDFNode[combinationOfClasses.get(i).length];
        for(int j = 0; j < combinationOfClasses.get(i).length; j++){
            OntClass ontClass3 = model.getOntClass(combinationOfClasses.get(i)[j]);
            list[j] = ontClass3;
        }
        RDFList rdfList = model.createList(list);
        OntClass temp = model.createClass();            //nepomenovana trieda reprezentujuca prienik tried v rdfList
        temp.convertToIntersectionClass(rdfList);
        dummyClass.addEquivalentClass(temp);
        return dummyClass;
    }

    public void writeOntologyToFile(OntModel model, String newFolder, String ontologyName) throws IOException {
        FileWriter myWriter = new FileWriter(newFolder + ontologyName);
        model.write(myWriter);
        myWriter.close();
    }

    public void createModifiedOntology(String fileWithOntology, String numberOfIndividuals, String fileWithClasses, String newFolder, String uriNewClass, int n, int y, String folderNameAlgorithm, int startPointInOntologyName, boolean classFromConfig, boolean individualFromConfig, String randomIndividualIRI) throws IOException {

        List<String[]> combinationOfClasses;
        List<String> usedIndividuals = new ArrayList<>();
        //vytvori sa y kombinacii tried ktore su v subore s menom fileWithClasses dlzky n
        if(classFromConfig){
            combinationOfClasses = loadAlreadyUsedClasses(n + "_" + configurationFile);
        } else{
            ClassWithSubclasses c = new ClassWithSubclasses(fileWithClasses, n + "_" + configurationFile);
            combinationOfClasses = c.generate2(n, y);
            createFileWithConfiguration(combinationOfClasses, n);
        }

        if(individualFromConfig){
            usedIndividuals = loadAlreadyUsedIndividuals(n + "_" + configurationFileIndividuals);
        }

        //vytvori model ontologie podla owl suboru v premennej fileWithOntology
        OntModel model = ModelFactory.createOntologyModel();
        model.read(fileWithOntology);

        InputFileForMHS_MXP inputFile = new InputFileForMHS_MXP(model, folderNameAlgorithm + newFolder);
        InputFileForAAASolver inputFile2 = new InputFileForAAASolver(model, folderNameAlgorithm + newFolder);

        //vrchny cyklus prechadza vsetky n-tice, vnutorny cyklus naplni RDFNode list classami z kombinacii
        for(int i = 0; i < combinationOfClasses.size(); i++){
            //vytvori sa v ontologii trieda DummyClass, ktora bude zadefinovana takto: DummyClass \ekvivalent (class1 \and class2 ... \and classN)
            OntClass dummyClass = createAxiom(model, combinationOfClasses, uriNewClass, i);

            //zapis celej ontologie do suboru s menom lubm-numberOfIndividuals_i.owl
            // v novo-vytvorenom priecinku newFolder  - kde i vyjadruje i-tu nticu z y kombinacii, a numberOfIndividuals je pocet individualov v LUBM
            String ontologyName = "lubm-" + numberOfIndividuals + "_" + n + "_" + (startPointInOntologyName + i) + ".owl";

            writeOntologyToFile(model, newFolder, ontologyName);

            Individual individual;
            if(individualFromConfig){
                String uri = usedIndividuals.get(i);
                individual = model.getIndividual(uri);
            } else {

                individual = inputFile.getRandomIndividual(n, randomIndividualIRI);
            }

            //vytvorenie inputFile pre MHS_MXP algoritmus pre konkretnu ontologiu s nahodne vybranym individualom z nej
            String inputFileNameNotNegation = "lubm-" + numberOfIndividuals + "_" + n + "_" + (startPointInOntologyName + i) + "_MXP_notNegation.in";
            String inputFileName = "lubm-" + numberOfIndividuals + "_" + n + "_" + (startPointInOntologyName + i) + "_MXP.in";
            String inputFileNamePelletNotNegation = "lubm-" + numberOfIndividuals + "_" + n + "_" + (startPointInOntologyName + i) + "_MXP_notNegation_P.in";
            String inputFileNamePellet  = "lubm-" + numberOfIndividuals + "_" + n + "_" + (startPointInOntologyName + i) + "_MXP_P.in";
            String inputFile2Name = "lubm-" + numberOfIndividuals + "_" + n + "_" + (startPointInOntologyName + i) + "_AAA.in";
            String outputFile2Name = "lubm-" + numberOfIndividuals + "_" + n + "_" + (startPointInOntologyName + i) + "_AAA.out";
            String inputFile2NameNotNegation = "lubm-" + numberOfIndividuals + "_" + n + "_" + (startPointInOntologyName + i) + "_AAA_noneg.in";
            String outputFile2NameNotNegation = "lubm-" + numberOfIndividuals + "_" + n + "_" + (startPointInOntologyName + i) + "_AAA_noneg.out";

            inputFile.createInputFile(ontologyName, dummyClass, inputFileNameNotNegation, false, individual, false);
            inputFile.createInputFile(ontologyName, dummyClass, inputFileName, true, individual, false);
            inputFile.createInputFile(ontologyName, dummyClass, inputFileNamePelletNotNegation, false, individual, true);
            inputFile.createInputFile(ontologyName, dummyClass, inputFileNamePellet, true, individual, true);
            inputFile2.createInputFile(ontologyName, dummyClass, inputFile2Name, outputFile2Name, individual, true, randomIndividualIRI);
            inputFile2.createInputFile(ontologyName, dummyClass, inputFile2NameNotNegation, outputFile2NameNotNegation, individual, false, randomIndividualIRI);

            //classu po zapise do suboru mazem, aby ju stacilo v novej iteracii pridat a nie nacitavat znova celu ontologiu
            dummyClass.remove();
        }
    }

    public static void main(String[] args) throws IOException {
        //ontologia, z ktorej vychadzame - ideme do nej pridavat axiom
        String fileWithOntology = "test-cases/lubm-0.owl" ;
        //pocet individualov, iba kvoli nazvy suboru
        String numberOfIndividuals = "0";
        //subor, kde na kazdom riadku je URI triedy, ktora ma nejake podtriedy (zatial iba rucne vytvorene pre LUBM)
        String fileWithClasses = "test-cases/lubm-classes.txt";
        //URI novej triedy, ktora bude v axiome vystupovat
        String uriNewClass = "http://swat.cse.lehigh.edu/onto/univ-bench.owl#DummyClass";
        String uriNewIndividual = "http://www.Department9.University0.edu/DummyIndividual";
        //n-tice
        int n = 3;
        //pocet ontologii (teda n-tic), ktore chceme generovat
        int y = 5;

        //cesta k priecinku, kde budeme ukladat nove ontologie, aby sme mali zaciatok prveho riadku v input file
        String folderNameAlgorithm = "";
        boolean classFromConfig = false;
        boolean individualFromConfig = false;

        for(int i = 1; i < n; i++){
            Main m = new Main();
            String folder = "";
            m.createModifiedOntology(fileWithOntology, numberOfIndividuals, fileWithClasses, folder, uriNewClass, i, y, folderNameAlgorithm, 1, classFromConfig, individualFromConfig, uriNewIndividual);
        }

        /*ChangeInFile change = new ChangeInFile();
        for(int i = 5; i <= 5; i++){
            for(int j = 10; j <= 14; j++){
                String number = "0";
                String subor = "lubm-" + number + "_" + i + "_" + j + "_MXP_notNegation_J.in";
                String zmenaNAzvu = "lubm-" + number + "_" + i + "_" + j + "_MXP_";
                //String directory = "/home/iveta/Plocha/skola/diplomovka/testingFiles" + number + "_doplnene/";
                String directory = "/home/iveta/Plocha/skola/diplomovka/jFact0_doplnene/";
                change.odstranNegaciu(directory, zmenaNAzvu, subor, number);
            }
        }*/


    }
}
