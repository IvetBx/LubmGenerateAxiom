package com.company;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ChangeInFile {

    public void zmenCestuKontologiiMXP(String fileName, String directory) throws IOException {
        String filePath = fileName;
        Scanner sc = new Scanner(new File(filePath));
        StringBuffer buffer = new StringBuffer();
        while (sc.hasNextLine()) {
            buffer.append(sc.nextLine() + System.lineSeparator());
        }
        String fileContents = buffer.toString();
        sc.close();
        //directory = files/
        fileContents = fileContents.replaceAll("-f: ", "-f: " + directory);
        FileWriter writer = new FileWriter(filePath);
        writer.write(fileContents);
        writer.close();
    }

    public void zmenReasoner(String directory, String directory2, String menoBezKoncovky, String fileName, String oldReasoner, String newReasoner) throws IOException {
        String filePath = directory + fileName;
        Scanner sc = new Scanner(new File(filePath));
        StringBuffer buffer = new StringBuffer();
        while (sc.hasNextLine()) {
            buffer.append(sc.nextLine() + System.lineSeparator());
        }
        String fileContents = buffer.toString();
        sc.close();
        fileContents = fileContents.replaceAll("-r: " + oldReasoner + "\n", "-r: " + newReasoner);
        FileWriter writer = new FileWriter(directory2 + menoBezKoncovky + ".in");
        writer.write(fileContents);
        writer.close();
    }

    public void pridajReasoner(String menoBezKoncovky, String fileName, String reasonerName, String endOfFile) throws IOException {
        String filePath = fileName;
        Scanner sc = new Scanner(new File(filePath));
        StringBuffer buffer = new StringBuffer();
        while (sc.hasNextLine()) {
            buffer.append(sc.nextLine() + System.lineSeparator());
        }
        //reasonerName = pellet, hermit, jFact
        buffer.append("-r: " + reasonerName);
        String fileContents = buffer.toString();
        sc.close();
        //endOfFile = _P.in, _jFact.in,
        FileWriter writer = new FileWriter(menoBezKoncovky + endOfFile);
        writer.write(fileContents);
        writer.close();
    }

    public void pridajTime(String fileName) throws IOException {
        String filePath = fileName;
        Scanner sc = new Scanner(new File(filePath));
        StringBuffer buffer = new StringBuffer();
        while (sc.hasNextLine()) {
            buffer.append(sc.nextLine() + System.lineSeparator());
        }
        buffer.append("-t: 14400");
        String fileContents = buffer.toString();
        sc.close();
        FileWriter writer = new FileWriter(fileName);
        writer.write(fileContents);
        writer.close();
    }

    public void zmenKonceRiadkov(String menoBezKoncovky, String fileName)throws IOException{
        String filePath = fileName;
        Scanner sc = new Scanner(new File(filePath));
        StringBuffer buffer = new StringBuffer();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (!line.contains("\\")){
                buffer.append(line + " \\" + System.lineSeparator());
            } else {
                buffer.append(line + System.lineSeparator());
            }
        }
        String fileContents = buffer.toString();
        sc.close();
        FileWriter writer = new FileWriter(menoBezKoncovky + ".in");
        writer.write(fileContents);
        writer.close();
    }

    public void pridajNegation(String menoBezKoncovky, String fileName) throws IOException {
        String filePath = fileName;
        Scanner sc = new Scanner(new File(filePath));
        StringBuffer buffer = new StringBuffer();
        while (sc.hasNextLine()) {
            buffer.append(sc.nextLine() + System.lineSeparator());
        }
        buffer.append("-n");
        String fileContents = buffer.toString();
        fileContents = fileContents.replaceAll(menoBezKoncovky + ".out", menoBezKoncovky + "_noneg.out");
        sc.close();
        FileWriter writer = new FileWriter(menoBezKoncovky + "_noneg.in");
        writer.write(fileContents);
        writer.close();
    }

    public void odstranNegaciu(String directory, String directory2, String menoBezKoncovky, String fileName) throws IOException {
        String filePath = directory + fileName;
        Scanner sc = new Scanner(new File(filePath));
        StringBuffer buffer = new StringBuffer();
        while (sc.hasNextLine()) {
            buffer.append(sc.nextLine() + System.lineSeparator());
        }
        String fileContents = buffer.toString();
        sc.close();
        fileContents = fileContents.replaceAll("-n: false\n", "");
        FileWriter writer = new FileWriter(directory2 + menoBezKoncovky + ".in");
        writer.write(fileContents);
        writer.close();
    }




}
