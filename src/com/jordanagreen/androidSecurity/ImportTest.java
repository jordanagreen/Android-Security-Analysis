package com.jordanagreen.androidSecurity;

import javassist.ClassPool;
import javassist.NotFoundException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jordan on 12/9/2015.
 */
public class ImportTest implements AndroidTest {

    @Override
    public JSONObject runTest(String apkFile) throws NotFoundException, JSONException{
        JSONObject json = new JSONObject();
        String className ="com.jordanagreen.androidSecurity.AndroidTestSuite";
        JSONArray arr = new JSONArray();
        //TODO: figure out how to get a lsit of classes from an .apk and run this on all of them
        arr.put(getImportedClasses(className));
        json.put("Classes", arr);
        return json;
    }

    private JSONArray getImportedClasses(String className) throws NotFoundException{
        JSONArray arr = new JSONArray();
        for (String importedClass : (Iterable<String>)ClassPool.getDefault().get(className).getRefClasses()) {
            System.out.println(importedClass);
            arr.put(importedClass);
        }
        return arr;
    }
}
