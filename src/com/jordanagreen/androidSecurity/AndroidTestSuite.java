package com.jordanagreen.androidSecurity;

import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jordan on 12/8/2015.
 */
public class AndroidTestSuite {

    private List<AndroidTest> mTests;

    AndroidTestSuite(){
        mTests = new ArrayList<>();
    }

    public static void main(String[] args){
        String apkFile = "testAPKs/jaderead-restart.apk";
        AndroidTestSuite androidTestSuite = new AndroidTestSuite();
//        androidTestSuite.addTest(new com.jordanagreen.androidSecurity.SourceSinkTest());
        androidTestSuite.addTest(new ImportTest());
        JSONArray results = androidTestSuite.runTests(apkFile);
        String outputPath = "output.json";
        try{
            BufferedWriter bw = new BufferedWriter( new FileWriter(outputPath));
            bw.write(results.toString());
            bw.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void addTest(AndroidTest test){
        mTests.add(test);
    }

    JSONArray runTests(String apkFile){
        JSONArray arr = new JSONArray();
        for (AndroidTest test: mTests){
            try{
                arr.put(test.runTest(apkFile));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return arr;
    }


}
