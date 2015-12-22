package com.jordanagreen.androidSecurity;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jordan on 12/16/2015.
 */

// for helper methods that get reused in different tests
public class Helper {

    static Set<Pattern> getFilterFromFile(String filename) {
        try{
            BufferedReader br = new BufferedReader(new FileReader(filename));
            Set<Pattern> filter = new HashSet<>();
            String line;
            while ((line = br.readLine()) != null){
                if (line.trim().equals("")){
                    continue;
                }
                filter.add(Pattern.compile(line.trim().toLowerCase()));
            }
            return filter;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //return the names all the .class files in the given directory
    static List<String> getClassFiles(String directory){
//        System.out.println(directory);
        File dir = new File(directory);
        String[] filter = new String[]{"class"};
        List<File> files = (List<File>) FileUtils.listFiles(dir, filter, true);
        //for now we need the paths, not the actual files
        //TODO: see if we can just return the file itself and run javassist on that
        List<String> classes = new ArrayList<>();
        for (File file: files){
            classes.add(file.getPath());
        }
        return classes;
    }

    static boolean hasAnyMatch(String text, Set<Pattern> patterns){
        for (Pattern pattern: patterns){
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()){
                return true;
            }
        }
        return false;
    }

}
