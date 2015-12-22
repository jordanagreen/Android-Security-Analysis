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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jordan on 12/16/2015.
 */
public class FieldTest implements AndroidTest {

    private ClassPool mClassPool;
    private Set<Pattern> mFilter;
    private Set<Pattern> mIgnoreFilter;

    private static final String NAME_KEY = "name";
    private static final String TYPE_KEY = "type";
    private static final String VALUE_KEY = "value";

    FieldTest(){
        mClassPool = ClassPool.getDefault();
        mIgnoreFilter = getIgnoreFilter();
    }

    FieldTest(String filterFile){
        mClassPool = ClassPool.getDefault();
        mFilter = Helper.getFilterFromFile(filterFile);
        mIgnoreFilter = getIgnoreFilter();
    }

    @Override
    public JSONObject runTest(String apkFolder) throws Exception {
        List<String> classes = Helper.getClassFiles(apkFolder);
        JSONObject json = new JSONObject();
        JSONArray arr = new JSONArray();
        for (String classFilepath: classes){
            InputStream ins = new FileInputStream(classFilepath);
            CtClass newClass = mClassPool.makeClass(ins);
            String name = newClass.getName();
            //skip any android classes since they're the same in everything
            if (Helper.hasAnyMatch(name, mIgnoreFilter)){
                continue;
            }
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



    private Set<Pattern> getIgnoreFilter(){
        Set<Pattern> ignoreFilter = new HashSet<>();
        ignoreFilter.add(Pattern.compile("^android\\.support\\..*"));
        return ignoreFilter;
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
                String fieldType = field.getType().getPackageName();
                System.out.println(fieldType);
                Object value = field.getConstantValue();
                String fieldValue = (value == null) ? null : value.toString();
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
//        System.out.println(className);
        JSONArray arr = new JSONArray();
        List<CtField> fields = Arrays.asList(mClassPool.get(className).getFields());
        for (CtField field: fields){
            try{
                String fieldName = field.getName();
                String fieldType = field.getType().getSimpleName();
//                System.out.println(fieldType);
                Object value = field.getConstantValue();
                String fieldValue = (value == null) ? null : value.toString();
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

}
