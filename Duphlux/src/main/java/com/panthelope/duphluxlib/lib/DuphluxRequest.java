package com.panthelope.duphluxlib.lib;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

/**
 * Created by ikenna on 05/06/2017.
 */

public class DuphluxRequest {

    private String token;

    protected DuphluxAuthenticationCallback authenticationCallback;

    public void setToken(String token) {
        this.token = token;
    }

    public DuphluxRequest(String token) {
        setToken(token);
    }

    public void authenticate(Context context, DuphluxAuthRequest duphluxAuthRequest, DuphluxAuthenticationCallback callback) {

        this.authenticationCallback = callback;

        DuphluxApiClient.post_raw(context, "authe/verify.json", this.token, duphluxAuthRequest.toJsonString(), new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
                authenticationCallback.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                String responseString = new String(response);
                if (DuphluxApiClient.getResponseStatus(responseString)) {
                    authenticationCallback.onSuccess(DuphluxApiClient.getResponseData(responseString));
                } else {
                    authenticationCallback.onFailed(DuphluxApiClient.getResponseError(responseString));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.e("dup", e.getMessage());
                String error = e.getMessage(); //new String(errorResponse);
                authenticationCallback.onError(error, e);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    public void getStatus(Context context, DuphluxAuthRequest duphluxAuthRequest, DuphluxAuthenticationCallback callback) {

        this.authenticationCallback = callback;

        DuphluxApiClient.post_raw(context, "authe/status.json", this.token, duphluxAuthRequest.toJsonString(), new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
                authenticationCallback.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                String responseString = new String(response);
                if (DuphluxApiClient.getResponseStatus(responseString)) {
                    authenticationCallback.onSuccess(DuphluxApiClient.getResponseData(responseString));
                } else {
                    authenticationCallback.onFailed(DuphluxApiClient.getResponseError(responseString));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.e("dup", e.getMessage());
                String error = e.getMessage(); //new String(errorResponse);
                authenticationCallback.onError(error, e);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }
}
