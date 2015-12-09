package com.jordanagreen.androidSecurity;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Jordan on 12/9/2015.
 */
public class ImportTest implements AndroidTest {

    private ClassPool mClassPool;

    ImportTest(){
        mClassPool = ClassPool.getDefault();
    }

    @Override
    public JSONObject runTest(String apkFile) throws NotFoundException, JSONException, IOException{
        JSONObject json = new JSONObject();

        //read from a fully qualified class name - must be part of this project, so not that useful
//        String className ="com.jordanagreen.androidSecurity.AndroidTestSuite";

        //read from a .class file that isn't in this project
        InputStream ins = new FileInputStream("AndroidTestSuite.class");
        CtClass newClass = mClassPool.makeClass(ins);

        JSONArray arr = new JSONArray();

        //TODO: figure out how to get a list of classes from an .apk and run this on all of them
        String className = newClass.getName();
        JSONObject obj = new JSONObject();
        obj.put(className, getImportedClasses(className));
        arr.put(obj);

        json.put("Classes", arr);
        return json;
    }

    private JSONArray getImportedClasses(String className) throws NotFoundException{
        JSONArray arr = new JSONArray();
        for (String importedClass : (Iterable<String>)mClassPool.get(className).getRefClasses()) {
            System.out.println(importedClass);
            arr.put(importedClass);
        }
        return arr;
    }

}
