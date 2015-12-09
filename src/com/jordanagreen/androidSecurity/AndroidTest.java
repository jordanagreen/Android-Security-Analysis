package com.jordanagreen.androidSecurity;

import org.json.JSONObject;

/**
 * Created by Jordan on 12/8/2015.
 */
public interface AndroidTest {
    //the folder containing the apk file and the classes generated through dare
    JSONObject runTest(String apkFolder) throws Exception;

}
