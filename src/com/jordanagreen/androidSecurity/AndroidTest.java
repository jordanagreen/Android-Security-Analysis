package com.jordanagreen.androidSecurity;

import org.json.JSONObject;

/**
 * Created by Jordan on 12/8/2015.
 */
public interface AndroidTest {
    JSONObject runTest(String apkFile) throws Exception;
}
