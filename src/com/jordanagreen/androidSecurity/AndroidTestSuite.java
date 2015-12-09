package com.jordanagreen.androidSecurity;

import org.json.JSONArray;
import org.json.JSONObject;

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
//        String apkFile = "testAPKs/jaderead-restart.apk";
        String apkFolder = "testAPKs/washizu-dare-test";
        AndroidTestSuite androidTestSuite = new AndroidTestSuite();
        androidTestSuite.addTest(new SourceSinkTest());
        androidTestSuite.addTest(new ImportTest());
        JSONArray results = androidTestSuite.runTests(apkFolder);
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

    JSONArray runTests(String apkFolder){
        JSONArray arr = new JSONArray();
        for (AndroidTest test: mTests){
            try{
                JSONObject json = new JSONObject();
                json.put(test.getClass().getSimpleName(), test.runTest(apkFolder));
                arr.put(json);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return arr;
    }


}
