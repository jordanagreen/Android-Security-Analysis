package com.jordanagreen.androidSecurity;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jordan on 12/16/2015.
 */
public class FieldTest implements AndroidTest {

    private ClassPool mClassPool;
    private Set<Pattern> mFilter;

    private static final String NAME_KEY = "name";
    private static final String TYPE_KEY = "type";
    private static final String VALUE_KEY = "value";

    FieldTest(){
        mClassPool = ClassPool.getDefault();
    }

    FieldTest(String filterFile){
        mClassPool = ClassPool.getDefault();
        mFilter = Helper.getFilterFromFile(filterFile);
    }

    @Override
    public JSONObject runTest(String apkFolder) throws Exception {
        List<String> classes = getClassFiles(apkFolder);
        JSONObject json = new JSONObject();
        JSONArray arr = new JSONArray();
        for (String classFilepath: classes){
            InputStream ins = new FileInputStream(classFilepath);
            CtClass newClass = mClassPool.makeClass(ins);
            String name = newClass.getName();
            JSONObject obj = new JSONObject();
            JSONArray fields = getFields(name, mFilter);
            if (fields.length() > 0){
                obj.put(name, fields);
                arr.put(obj);
            }
        }
        json.put("Classes", arr);
        return json;
    }

    private JSONArray getFields(String className, Set<Pattern> filters) throws NotFoundException, JSONException {
        if (filters == null){
            return getFields(className);
        }
        JSONArray arr = new JSONArray();
        List<CtField> fields = Arrays.asList(mClassPool.get(className).getFields());
        for (CtField field: fields){
            try{
                String fieldName = field.getName();
                String fieldType = field.getType().toString();
                String fieldValue = field.getConstantValue().toString();
//                    System.out.println(fieldType + " " + fieldName + " " + fieldValue);
                for (Pattern filter: filters){
                    Matcher matcher = filter.matcher(fieldName);
                    if (matcher.find()){
                        JSONObject obj = new JSONObject();
                        obj.put(NAME_KEY, fieldName);
                        obj.put(TYPE_KEY, fieldType);
                        obj.put(VALUE_KEY, fieldValue);
                        arr.put(obj);
                        break;
                    }
                }
            } catch (NotFoundException e){
                //if something isn't found here, just continue to the next one
            }
        }
        return arr;
    }

    private JSONArray getFields(String className) throws NotFoundException, JSONException {
        System.out.println(className);
        JSONArray arr = new JSONArray();
        List<CtField> fields = Arrays.asList(mClassPool.get(className).getFields());
        for (CtField field: fields){
            try{
                String fieldName = field.getName();
                String fieldType = field.getType().toString();
                String fieldValue = field.getConstantValue().toString();
//                    System.out.println(fieldType + " " + fieldName + " " + fieldValue);
                JSONObject obj = new JSONObject();
                obj.put(NAME_KEY, fieldName);
                obj.put(TYPE_KEY, fieldType);
                obj.put(VALUE_KEY, fieldValue);
                arr.put(obj);
            } catch (NotFoundException e){
                //if something isn't found here, just continue to the next one
            }
        }
        return arr;
    }

    private List<String> getClassFiles(String directory){
        System.out.println(directory);
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
}
