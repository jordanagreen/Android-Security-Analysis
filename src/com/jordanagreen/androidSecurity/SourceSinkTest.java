package com.jordanagreen.androidSecurity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.results.ResultSinkInfo;
import soot.jimple.infoflow.results.ResultSourceInfo;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;
import soot.util.MultiMap;

import java.io.IOException;

/**
 * Created by Jordan on 12/8/2015.
 */
public class SourceSinkTest implements AndroidTest {

    public JSONObject runTest(String apkFile){
        String sourceSinkFile = "testAPKs/sourcesAndSinks.txt";
        JSONObject json = new JSONObject();
        try {
            InfoflowResults infoflowResults = analyzeAPKFile(apkFile, sourceSinkFile);
            MultiMap<ResultSinkInfo, ResultSourceInfo> results = infoflowResults.getResults();

            JSONArray arr = new JSONArray();
            for (ResultSinkInfo sink : results.keySet()) {
                JSONObject obj = new JSONObject();
                obj.put("Sink", sink);
                JSONArray sources = new JSONArray();
                for (ResultSourceInfo source : results.get(sink)) {
                    sources.put(source);
                }
                obj.put("sources", sources);
                arr.put(obj);
            }
            json.put("Source_Sink_Results", arr);

        } catch (IOException | XmlPullParserException | JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Analyzes the given APK file for data flows with a given xml file
     * @param apkFileName The full path and file name of the APK file to analyze
     * @param xmlFileName The full path and file name of the xml file where sources and sinks are defined
     * @return The data leaks found in the given APK file
     * @throws IOException Thrown if the given APK file or any other required
     * file could not be found
     * @throws XmlPullParserException Thrown if the Android manifest file could
     * not be read.
     */
    public InfoflowResults analyzeAPKFile(String apkFileName, String xmlFileName)
            throws IOException, XmlPullParserException {
        return analyzeAPKFile(apkFileName, xmlFileName, false, false, false);
    }

    /**
     * Analyzes the given APK file for data flows with a given xml file
     * @param apkFileName The full path and file name of the APK file to analyze
     * @param xmlFileName The full path and file name of the xml file where sources and sinks are defined
     * @param enableImplicitFlows True if implicit flows shall be tracked,
     * otherwise false
     * @return The data leaks found in the given APK file
     * @throws IOException Thrown if the given APK file or any other required
     * file could not be found
     * @throws XmlPullParserException Thrown if the Android manifest file could
     * not be read.
     */
    public InfoflowResults analyzeAPKFile(String apkFileName, String xmlFileName, boolean enableImplicitFlows, boolean enableStaticFields, boolean flowSensitiveAliasing)
            throws IOException, XmlPullParserException {
        String androidJars = System.getenv("ANDROID_JARS");
        if (androidJars == null)
            androidJars = System.getProperty("ANDROID_JARS");
        if (androidJars == null)
            androidJars = "C:\\Users\\Jordan\\AppData\\Local\\Android\\sdk\\platforms";
//			throw new RuntimeException("Android JAR dir not set");
        System.out.println("Loading Android.jar files from " + androidJars);

        SetupApplication setupApplication = new SetupApplication(androidJars, apkFileName);
        setupApplication.setTaintWrapper(new EasyTaintWrapper("testAPKs/EasyTaintWrapperSource.txt"));
        setupApplication.calculateSourcesSinksEntrypoints(xmlFileName);
        setupApplication.getConfig().setEnableImplicitFlows(enableImplicitFlows);
        setupApplication.getConfig().setEnableStaticFieldTracking(enableStaticFields);
        setupApplication.getConfig().setFlowSensitiveAliasing(flowSensitiveAliasing);
        return setupApplication.runInfoflow();
    }
}
