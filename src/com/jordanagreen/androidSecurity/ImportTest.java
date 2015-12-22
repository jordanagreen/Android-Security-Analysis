package com.jordanagreen.androidSecurity;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jordan on 12/9/2015.
 */
public class ImportTest implements AndroidTest {

    private ClassPool mClassPool;
    private Set<Pattern> mFilter;

    ImportTest(){
        mClassPool = ClassPool.getDefault();
        mFilter = null;
    }

    ImportTest(String filterFile){
        mClassPool = ClassPool.getDefault();
        mFilter = Helper.getFilterFromFile(filterFile);
    }



    @Override
    public JSONObject runTest(String apkFolder) throws NotFoundException, JSONException, IOException{
        JSONObject json = new JSONObject();

        //read from a fully qualified class name - must be part of this project, so not that useful
//        String className ="com.jordanagreen.androidSecurity.AndroidTestSuite";

        //read from a .class file that isn't in this project
//        InputStream ins = new FileInputStream("AndroidTestSuite.class");
//        String directory = "testAPKs/washizu-dare-test/app-debug";
//        InputStream ins = new FileInputStream(directory + "/Game.class");
//        CtClass newClass = mClassPool.makeClass(ins);

//        String directory = "testAPKs/washizu-dare-test/app-debug/com/example/jordanagreen/washizu";
//        List<String> classes = getClassFiles(directory);
//        Set<Pattern> filter = getFilterFromFile(FILTER_FILE);
        List<String> classes = Helper.getClassFiles(apkFolder);
        JSONArray arr = new JSONArray();
        for (String classFilepath: classes){
            InputStream ins = new FileInputStream(classFilepath);
            CtClass newClass = mClassPool.makeClass(ins);
            String name = newClass.getName();
//            System.out.println(name);
            JSONObject obj = new JSONObject();
            JSONArray imports = getImportedClasses(name, mFilter);
            if (imports.length() > 0){
                obj.put(name, imports);
                arr.put(obj);
            }
        }
        json.put("Classes", arr);
        return json;

    }



    private JSONArray getImportedClasses(String className) throws NotFoundException {
        JSONArray arr = new JSONArray();
        for (String importedClass : (Iterable<String>) mClassPool.get(className).getRefClasses()) {
            arr.put(importedClass);
        }
        return arr;
    }

    private JSONArray getImportedClasses(String className, Set<Pattern> filters) throws NotFoundException{
        if (filters == null){
            return getImportedClasses(className);
        }
        JSONArray arr = new JSONArray();
        for (String importedClass: (Iterable<String>) mClassPool.get(className).getRefClasses()){
            for (Pattern filter: filters){
                Matcher matcher = filter.matcher(importedClass);
                if (matcher.find()){
                    System.out.println(importedClass + " " + filter.pattern());
                    arr.put(importedClass);
                    break;
                }
            }
        }
        return arr;
    }

}
