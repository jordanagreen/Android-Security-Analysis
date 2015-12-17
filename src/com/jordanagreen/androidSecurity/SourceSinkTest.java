package com.jordanagreen.androidSecurity;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.handlers.ResultsAvailableHandler;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.results.ResultSinkInfo;
import soot.jimple.infoflow.results.ResultSourceInfo;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;
import soot.util.MultiMap;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Jordan on 12/8/2015.
 */
public class SourceSinkTest implements AndroidTest {

    @Override
    public JSONObject runTest(String apkFolder){
        String apkFile = getApkFile(apkFolder);
        String sourceSinkFile = "testAPKs/sourcesAndSinks.txt";
        JSONObject json = new JSONObject();
        try {
            System.out.println("Starting analysis");
            analyzeAPKFile(apkFile, sourceSinkFile, (iInfoflowCFG, infoflowResults) -> {
                    MultiMap<ResultSinkInfo, ResultSourceInfo> results = infoflowResults.getResults();
                    System.out.println("Analysis done");
                    JSONArray arr = new JSONArray();
                    try {
                        for (ResultSinkInfo sink : results.keySet()) {
                            JSONObject obj = new JSONObject();
                            obj.put("sink", sink);
                            JSONArray sources = new JSONArray();
                            results.get(sink).forEach(sources::put);
                            obj.put("sources", sources);
                            arr.put(obj);
                        }
                        json.put("results", arr);
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
            });

        } catch (IOException | XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json;
    }

    private String getApkFile(String apkFolder){
        //TODO: find the actual apk file in this folder - for now just return the test file
//        return apkFolder + "/app-debug.apk";
//        return apkFolder + "/comicrack.apk";
        File dir = new File(apkFolder);
        String[] filter = new String[]{"apk"};
        //TODO: can probably just ignore the android APK files, they just take up space in the results
        List<File> files = (List<File>) FileUtils.listFiles(dir, filter, true);
        return files.get(0).getPath();
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
    public InfoflowResults analyzeAPKFile(String apkFileName, String xmlFileName, ResultsAvailableHandler handler)
            throws IOException, XmlPullParserException {
        return analyzeAPKFile(apkFileName, xmlFileName, handler, false, false, false);
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
    public InfoflowResults analyzeAPKFile(String apkFileName, String xmlFileName, ResultsAvailableHandler handler,
                                          boolean enableImplicitFlows, boolean enableStaticFields,
                                          boolean flowSensitiveAliasing)
            throws IOException, XmlPullParserException {
        String androidJars = System.getenv("ANDROID_JARS");
        if (androidJars == null)
            androidJars = System.getProperty("ANDROID_JARS");
        if (androidJars == null)
            //TODO: this should be fixed once testing is done
            androidJars = "C:\\Users\\Jordan\\AppData\\Local\\Android\\sdk\\platforms";
//			throw new RuntimeException("Android JAR dir not set");
        System.out.println("Loading Android.jar files from " + androidJars);

        SetupApplication setupApplication = new SetupApplication(androidJars, apkFileName);
        setupApplication.setTaintWrapper(new EasyTaintWrapper("testAPKs/EasyTaintWrapperSource.txt"));
        setupApplication.calculateSourcesSinksEntrypoints(xmlFileName);
        setupApplication.getConfig().setEnableImplicitFlows(enableImplicitFlows);
        setupApplication.getConfig().setEnableStaticFieldTracking(enableStaticFields);
        setupApplication.getConfig().setFlowSensitiveAliasing(flowSensitiveAliasing);
        return setupApplication.runInfoflow(handler);
    }
}
