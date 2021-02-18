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

    public void createModifiedOntology(String fileWithOntology, String fileWithClasses, String newFolder, String uriNewClass, int n, int y) throws IOException {

        //vytvori sa y kombinacii tried ktore su v subore s menom fileWithClasses dlzky n
        ClassWithSubclasses c = new ClassWithSubclasses(fileWithClasses);
        List<String[]> x = c.generate2(n, y);

        //vytvorenie priecinka, do ktoreho ulozime vsetky nove ontologie na zaklade behu jedneho programu
        try{
            Files.createDirectory(Paths.get(newFolder));
        } catch (FileAlreadyExistsException e){
            System.out.println("Folder already exist and contains ontologies.");
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //vytvori model ontologie podla owl suboru v premennej fileWithOntology
        OntModel model = ModelFactory.createOntologyModel();
        model.read(fileWithOntology);

        //vrchny cyklus prechadza vsetky n-tice
        //vnutorny cyklus naplni RDFNode list classami z kombinacii
        for(int i = 0; i < x.size(); i++){
            OntClass dummyClass = model.createClass(uriNewClass);
            RDFNode[] list = new RDFNode[x.get(i).length];
            for(int j = 0; j < x.get(i).length; j++){
                OntClass ontClass3 = model.getOntClass(x.get(i)[j]);
                list[j] = ontClass3;
            }
            RDFList rdfList = model.createList(list);
            OntClass temp = model.createClass();            //nepomenovana trieda reprezentujuca prienik tried v rdfList
            temp.convertToIntersectionClass(rdfList);
            dummyClass.addEquivalentClass(temp);

            //zapis celej ontologie do suboru v novo-vytvorenom priecinku s menom lubm-125_i.owl - kde i vyjadruje i-tu nticu z y kombinacii
            FileWriter myWriter = new FileWriter(   newFolder + "lubm-125_" + i + ".owl");
            model.write(myWriter);
            myWriter.close();

            //classu aj s ekvivalentnym prienikom tried po zapise do suboru mazem, aby ju stacilo v novej iteracii pridat a nie nacitavat znova celu ontologiu
            dummyClass.remove();
        }
    }

    public static void main(String[] args) throws IOException {
        String fileWithOntology = "lubm-125.owl" ;
        String fileWithClasses = "lubm-classes-with-subclasses.txt";
        String newFolder = "file2/";
        String uriNewClass = "http://swat.cse.lehigh.edu/onto/univ-bench.owl#DummyClass";
        int n = 3;
        int y = 3;

        Main m = new Main();
        m.createModifiedOntology(fileWithOntology, fileWithClasses, newFolder, uriNewClass, n, y);
    }
}
