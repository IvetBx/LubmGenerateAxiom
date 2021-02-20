package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class ClassWithSubclasses {

    ArrayList<String> classWithSubclasses = new ArrayList<>();

    public ClassWithSubclasses(String filename) {
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String className = myReader.nextLine();
                classWithSubclasses.add(className);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
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

        if(combinations.size() < y){
            return combinations;
        }
        Collections.shuffle(combinations);
        return combinations.subList(0, y);
    }




}
