package com.jordanagreen.androidSecurity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
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

}
