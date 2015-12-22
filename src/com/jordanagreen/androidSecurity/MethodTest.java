package com.jordanagreen.androidSecurity;

import javassist.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by Jordan on 12/21/2015.
 */
public class MethodTest implements AndroidTest {

    private ClassPool mClassPool;
    private Set<Pattern> mFilter;
    private Set<Pattern> mIgnoreFilter;

    private static final String NAME_KEY = "name";
    private static final String SIGNATURE_KEY = "signature";
    private static final String RETURN_KEY = "return_type";

    public MethodTest(){
        mClassPool = ClassPool.getDefault();
        mIgnoreFilter = getIgnoreFilter();
    }

    public MethodTest(String filterFile){
        mClassPool = ClassPool.getDefault();
        mFilter = Helper.getFilterFromFile(filterFile);
        mIgnoreFilter = getIgnoreFilter();
    }

    private Set<Pattern> getIgnoreFilter(){
        Set<Pattern> ignoreFilter = new HashSet<>();
        ignoreFilter.add(Pattern.compile("^android\\.support\\..*"));
        return ignoreFilter;
    }

    @Override
    public JSONObject runTest(String apkFolder) throws Exception {
        JSONObject json = new JSONObject();
        JSONArray arr = new JSONArray();
        List<String> classes = Helper.getClassFiles(apkFolder);
        for (String classFilepath: classes){
            InputStream ins = new FileInputStream(classFilepath);
            CtClass newClass = mClassPool.makeClass(ins);
            String name = newClass.getName();
            if (Helper.hasAnyMatch(name, mIgnoreFilter)){
                continue;
            }
            JSONObject obj = new JSONObject();
            JSONArray methods = getMethods(name);
            if (methods.length() > 0){
                obj.put(name, methods);
                arr.put(obj);
            }
        }
        return json;
    }

    private JSONArray getMethods(String className) throws NotFoundException, JSONException{
        JSONArray arr = new JSONArray();
        List<CtMethod> methods = Arrays.asList(mClassPool.get(className).getMethods());
        for (CtMethod method: methods){
            JSONObject obj = new JSONObject();
            String name = method.getName();
            String signature = method.getSignature();
            String returnType = method.getReturnType().getName();
            obj.put(NAME_KEY, name);
            obj.put(SIGNATURE_KEY, signature);
            obj.put(RETURN_KEY, returnType);
            arr.put(obj);
        }
        return arr;
    }
}
