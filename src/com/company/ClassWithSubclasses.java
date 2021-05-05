package com.company;

import org.apache.commons.compress.archivers.StreamingNotSupportedException;

import java.io.*;
import java.security.InvalidParameterException;
import java.util.*;

public class ClassWithSubclasses {

    ArrayList<String> classWithSubclasses = new ArrayList<>();
    String configurationFileName;
    Set<Set<String>> alreadyUsedClasses = new HashSet<>();

    public ClassWithSubclasses(String filename, String configurationFileName1) {
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String className = myReader.nextLine();
                classWithSubclasses.add(className);
            }
            myReader.close();
            configurationFileName = configurationFileName1;
            loadAlreadyUsedClasses();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void loadAlreadyUsedClasses() throws FileNotFoundException {
        Scanner sc = new Scanner(new File(configurationFileName));
        StringBuffer buffer = new StringBuffer();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if(!line.equals("")){
                String[] classes = line.split(", ");
                Set<String> targetSet = new HashSet<>(Arrays.asList(classes));
                alreadyUsedClasses.add(targetSet);
            }
        }
    }

    public List<String[]> generateNewCombinationWithoutDuplication(List<String[]> combinations) {
        List<String[]> newCombinations = new ArrayList<>();
        for(int i = 0; i < combinations.size(); i++){
            Set<String> targetSet = new HashSet<>(Arrays.asList(combinations.get(i)));
            boolean equal = false;
            for(Set<String> s : alreadyUsedClasses){
                if(s.equals(targetSet)){
                    equal = true;
                    break;
                }
            }
            if(!equal){
                newCombinations.add(combinations.get(i));
            }
        }
        return newCombinations;
    }

    public List<String[]> generate2(int n, int y) {
        if(classWithSubclasses.size() < n){
            throw new InvalidParameterException("n is larger than number of classes");
        } else if(n <= 0 || y <= 0){
            throw new InvalidParameterException("n and y should be positive number");
        }
        int numberOfClasses = classWithSubclasses.size();
        List<String[]> combinations = new ArrayList<>();
        int[] combination = new int[n];

        for (int i = 0; i < n; i++) {
            combination[i] = i;
        }

        while (combination[n - 1] < numberOfClasses) {
            String[] combinationString = new String[combination.length];
            for(int i = 0; i < combination.length; i++){
                combinationString[i] = classWithSubclasses.get(combination[i]);
            }
            combinations.add(combinationString);

            int t = n - 1;
            while (t != 0 && combination[t] == numberOfClasses - n + t) {
                t--;
            }
            combination[t]++;
            for (int i = t + 1; i < n; i++) {
                combination[i] = combination[i - 1] + 1;
            }
        }

        List<String[]> newCombinations = generateNewCombinationWithoutDuplication(combinations);

        if(newCombinations.size() < y){
            return newCombinations;
        }
        Collections.shuffle(newCombinations);
        return newCombinations.subList(0, y);
    }




}
