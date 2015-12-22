package com.jordanagreen.androidSecurity;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jordan on 12/8/2015.
 */
public class AndroidTestSuite {

    private List<AndroidTest> mTests;
    private static final String SUCCESS_KEY = "success";
    private static final String TEST_NAME_KEY = "test-name";
    private static final String RESULTS_KEY = "results";
    private static final String EXCEPTION_KEY = "exception";
    private static final String AD_FILTER_FILE = "ImportFilters-Ads.txt";


    AndroidTestSuite(){
        mTests = new ArrayList<>();
    }

    public static void main(String[] args){

        String apkFolder = args[0];
        String outputPath = FilenameUtils.getBaseName(apkFolder) + "-output.json";
        System.out.println(apkFolder + " " + outputPath);
//        String apkFile = "testAPKs/jaderead-restart.apk";
//        String apkFolder = "testAPKs/washizu-dare-test";
//        String apkFolder = "testAPKs/comicrack-free";
        AndroidTestSuite androidTestSuite = new AndroidTestSuite();
        androidTestSuite.addTest(new SourceSinkTest());
//        androidTestSuite.addTest(new ImportTest());
//        androidTestSuite.addTest(new AdTest(AD_FILTER_FILE));
//        androidTestSuite.addTest(new FieldTest());
        JSONArray results;
        try{
            results = androidTestSuite.runTests(apkFolder);
        } catch (JSONException e){
            e.printStackTrace();
            results = new JSONArray();
        }
//        String outputPath = "comicrack-output.json";
//        String outputPath = "washizu-output.json";
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

    JSONArray runTests(String apkFolder) throws JSONException{
        JSONArray arr = new JSONArray();
        for (AndroidTest test: mTests){
            JSONObject json = new JSONObject();
            json.put(TEST_NAME_KEY, test.getClass().getSimpleName());
            try{
                json.put(RESULTS_KEY, test.runTest(apkFolder));
                json.put(SUCCESS_KEY, true);
            } catch (Exception e){
                //if a test goes wrong, fail it and move on to the next one
                e.printStackTrace();
                json.put(SUCCESS_KEY, false);
                json.put(EXCEPTION_KEY, e.toString());
            }
            arr.put(json);
        }
        return arr;
    }


}
