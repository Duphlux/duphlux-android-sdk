package com.panthelope.duphluxlib.lib;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by ikenna on 05/06/2017.
 */

public class DuphluxApiClient {

    private static final String BASE_URL = "https://duphlux.com/webservice/";
    //private static final String BASE_URL = "http://10.0.3.2/flashmo/api/webservice/";

    private static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    private static String logfile = "duphlux_api_client";

    public static void post(String url, String token, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if (token != null) {
            if (!token.equalsIgnoreCase("false")) {
                client.addHeader("token", token);
            }
        }
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post_raw(Context context, String url, String token, String raw_data, AsyncHttpResponseHandler responseHandler) {
        if (token != null) {
            if (!token.equalsIgnoreCase("false")) {
                client.addHeader("token", token);
            }
        }
        StringEntity entity = null;
        try {
            entity = new StringEntity(raw_data);
            client.post(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }


    public static JSONObject getResponsePayLoad(String response) {
        try {
            JSONObject responseObj = new JSONObject(response);
            return responseObj.getJSONObject("PayLoad");
        } catch (JSONException e) {
            //Log.i(logfile, "PayLoad: " + e.getMessage().toString());
        }
        return null;
    }

    public static Boolean getResponseStatus(String response) {
        try {
            JSONObject payload = getResponsePayLoad(response);
            if (payload != null) {
                if (payload.getBoolean("status")) {
                    return true;
                }
            }
        } catch (JSONException e) {
            //Log.i(logfile, "Status: " + e.getMessage().toString());
        }
        return false;
    }

    public static JSONObject getResponseData(String response) {
        try {
            JSONObject payload = getResponsePayLoad(response);
            if (payload != null) {
                return payload.getJSONObject("data");
            }
        } catch (JSONException e) {
            //Log.i(logfile, "Data: " + e.getMessage().toString());
        }
        return null;
    }

    public static JSONArray getResponseError(String response) {
        try {
            JSONObject payload = getResponsePayLoad(response);
            if (payload != null) {
                return payload.getJSONArray("errors");
            } else {
                JSONArray jsonArray = new JSONArray();
                jsonArray.put("System exception occurred.");
                return jsonArray;
            }
        } catch (JSONException e) {
            //Log.i(logfile, e.getMessage().toString());
        }
        return null;
    }

    public static String getResponseErrorsAsString(String response) {
        String errors = "";
        try {
            JSONArray repErrors = getResponseError(response);
            for (int i = 0; i <= repErrors.length(); i++) {
                errors += (i + 1) + ". " + repErrors.getString(i) + "\n";
            }
            return errors;
        } catch (JSONException e) {
            //Log.i(logfile, "ErrorE: " + e.getMessage().toString());
        }
        return errors;
    }
}
