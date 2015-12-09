package com.jordanagreen.androidSecurity;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import jdk.internal.util.xml.impl.Input;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jordan on 12/9/2015.
 */
public class ImportTest implements AndroidTest {

    private ClassPool mClassPool;

    ImportTest(){
        mClassPool = ClassPool.getDefault();
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
        List<String> classes = getClassFiles(apkFolder);
        JSONArray arr = new JSONArray();
        for (String classFilepath: classes){
            InputStream ins = new FileInputStream(classFilepath);
            CtClass newClass = mClassPool.makeClass(ins);
            String name = newClass.getName();
            System.out.println("\n" + name);
            JSONObject obj = new JSONObject();
            //TODO: check imports against a list of ones we want to look for (ad libraries, etc.)
            obj.put(name, getImportedClasses(name));
            arr.put(obj);
        }
        json.put("Classes", arr);
        return json;

    }

    //return the names all the .class files in the given directory
    private List<String> getClassFiles(String directory){
        System.out.println(directory);
        File dir = new File(directory);

        String[] filter = new String[]{"class"};

        List<File> files = (List<File>) FileUtils.listFiles(dir, filter, true);
        System.out.println(files.size());

        //for now we need the paths, not the actual files
        //TODO: see if we can just return the file itself and run javassist on that
        List<String> classes = new ArrayList<>();
        for (File file: files){
//            System.out.println(file.getPath());
            classes.add(file.getPath());
        }
        return classes;
    }

    private JSONArray getImportedClasses(String className) throws NotFoundException{
        JSONArray arr = new JSONArray();
        for (String importedClass : (Iterable<String>)mClassPool.get(className).getRefClasses()) {
//            System.out.println(importedClass);
            arr.put(importedClass);
        }
        return arr;
    }

}
