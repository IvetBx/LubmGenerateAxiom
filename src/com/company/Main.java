package com.company;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.*;

public class Main {

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

    public void createModifiedOntology(String fileWithOntology, String numberOfIndividuals, String fileWithClasses, String newFolder, String uriNewClass, int n, int y, String folderNameAlgorithm) throws IOException {

        //vytvori sa y kombinacii tried ktore su v subore s menom fileWithClasses dlzky n
        ClassWithSubclasses c = new ClassWithSubclasses(fileWithClasses);
        List<String[]> combinationOfClasses = c.generate2(n, y);

        //vytvorenie priecinka, do ktoreho ulozime vsetky nove ontologie na zaklade behu jedneho programu
        String folderAbsolutePath = createFolderWithOntologiesAndInputFiles(newFolder);
        if(folderAbsolutePath == ""){
            return;
        }

        //vytvori model ontologie podla owl suboru v premennej fileWithOntology
        OntModel model = ModelFactory.createOntologyModel();
        model.read(fileWithOntology);

        //vytvori sa instancia triedy InputFileForMHS_MXP, ktora si ulozi model aj prieconik, kde sa budu inputFile ukladat (aby to nebolo potrebne volat vzdy v cykle)
        InputFileForMHS_MXP inputFile = new InputFileForMHS_MXP(model, folderNameAlgorithm + newFolder);
        //ak by sme chceli cestu priamo v tomto subore, kde sa to ulozilo
        //InputFileForMHS_MXP inputFile = new InputFileForMHS_MXP(model, folderNameAlgorithm + newFolder);

        //vrchny cyklus prechadza vsetky n-tice, vnutorny cyklus naplni RDFNode list classami z kombinacii
        for(int i = 0; i < combinationOfClasses.size(); i++){
            System.out.println(i);
            //vytvori sa v ontologii trieda DummyClass, ktora bude zadefinovana takto: DummyClass \ekvivalent (class1 \and class2 ... \and classN)
            OntClass dummyClass = createAxiom(model, combinationOfClasses, uriNewClass, i);

            //zapis celej ontologie do suboru s menom lubm-numberOfIndividuals_i.owl
            // v novo-vytvorenom priecinku newFolder  - kde i vyjadruje i-tu nticu z y kombinacii, a numberOfIndividuals je pocet individualov v LUBM
            String ontologyName = "lubm-" + numberOfIndividuals + "_" + i + ".owl";
            writeOntologyToFile(model, newFolder, ontologyName);

            //vytvorenie inputFile pre MHS_MXP algoritmus pre konkretnu ontologiu s nahodne vybranym individualom z nej
            String inputFileName = "lubm-" + numberOfIndividuals + "_" + i + "_input.txt";
            inputFile.createInputFile(ontologyName, dummyClass, newFolder + inputFileName);

            //classu po zapise do suboru mazem, aby ju stacilo v novej iteracii pridat a nie nacitavat znova celu ontologiu
            dummyClass.remove();
        }
    }

    public static void main(String[] args) throws IOException {
        //ontologia, z ktorej vychadzame - ideme do nej pridavat axiom
        String fileWithOntology = "lubm-125.owl" ;
        //pocet individualov, iba kvoli nazvy suboru
        String numberOfIndividuals = "125";
        //subor, kde na kazdom riadku je URI triedy, ktora ma nejake podtriedy (zatial iba rucne vytvorene pre LUBM)
        String fileWithClasses = "lubm-classes-with-subclasses.txt";
        //nazor suboru do ktoreho chceme ulozit modifikovane ontologie z jedneho behu programu - je potrebne mat lomitko na konci
        String newFolder = "file/";
        //URI novej triedy, ktora bude v axiome vystupovat
        String uriNewClass = "http://swat.cse.lehigh.edu/onto/univ-bench.owl#DummyClass";
        //n-tice
        int n = 6;
        //pocet ontologii (teda n-tic), ktore chceme generovat
        int y = 19;

        //cesta k priecinku, kde budeme ukladat nove ontologie, aby sme mali zaciatok prveho riadku v input file
        String folderNameAlgorithm = "/home/gablikova4/mhs-mxp_v2/files/";

        Main m = new Main();
        m.createModifiedOntology(fileWithOntology, numberOfIndividuals, fileWithClasses, newFolder, uriNewClass, n, y, folderNameAlgorithm);
    }
}
