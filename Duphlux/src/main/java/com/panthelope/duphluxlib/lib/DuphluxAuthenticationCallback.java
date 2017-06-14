package com.panthelope.duphluxlib.lib;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by ikenna on 05/06/2017.
 */

public interface DuphluxAuthenticationCallback {

    void onStart();

    void onSuccess(JSONObject jsonObject);

    void onFailed(JSONArray jsonArray);

    void onError(String message, Throwable e);
}
