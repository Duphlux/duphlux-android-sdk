package com.panthelope.duphluxlib.lib;

import android.app.Activity;
import android.content.Intent;

import com.panthelope.duphluxlib.activity.AuthenticateActivity;

/**
 * Created by ikenna on 05/06/2017.
 */

public class DuphluxSdk {

    private static DuphluxSdk instance = null;

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    protected DuphluxSdk(String token) {
        setToken(token);
    }

    public static DuphluxSdk initializeSDK(String token) {
        if (instance == null) {
            instance = new DuphluxSdk(token);
        }
        return instance;
    }

    public static DuphluxSdk initializeSDK(Activity activity) {
        String token = Configs.getMetadata(activity, Configs.meta_app_token_key);
        if (instance == null) {
            instance = new DuphluxSdk(token);
        }
        return instance;
    }

    public void authenticate(Activity activity, AuthRequest authRequest, DuphluxAuthenticationCallback authenticationCallback) {
        if (!authRequest.isValid()) {
            return;
        }
        DuphluxRequest duphluxRequest = new DuphluxRequest(getToken());
        duphluxRequest.authenticate(activity, authRequest, authenticationCallback);
    }

    public void getStatus(Activity activity, AuthRequest authRequest, DuphluxAuthenticationCallback authenticationCallback) {
        if (authRequest.getTransaction_reference() == null) {
            return;
        }
        DuphluxRequest duphluxRequest = new DuphluxRequest(getToken());
        duphluxRequest.getStatus(activity, authRequest, authenticationCallback);
    }

    public static void launch(Activity activity, String phone_number){
        Intent intent = new Intent(activity, AuthenticateActivity.class);
        intent.putExtra("phone_number", phone_number);
        activity.startActivityForResult(intent, Configs.ACTIVITY_RESULT_CODE);
    }

}
