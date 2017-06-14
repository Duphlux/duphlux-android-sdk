package com.panthelope.duphluxlib.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.panthelope.duphluxlib.R;
import com.panthelope.duphluxlib.lib.AuthRequest;
import com.panthelope.duphluxlib.lib.DuphluxAuthenticationCallback;
import com.panthelope.duphluxlib.lib.Configs;
import com.panthelope.duphluxlib.lib.DuphluxSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AuthenticateActivity extends AppCompatActivity {


    DuphluxSdk duphluxSdk;
    DuphluxAuthenticationCallback authenticationCallback;
    AuthRequest authRequest;
    String phone_number;

    TextView duphlux_number;
    TextView number;
    TextView timeLeft;

    CountDownTimer countDownTimer;
    Button confirm;
    Button redirect;
    Button close;
    Boolean verificationStatus = false;
    String responseMessage;

    ViewGroup subMain;
    ViewGroup loadingDiv;
    long timeout = 900;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);
        phone_number = getIntent().getStringExtra("phone_number");

        duphlux_number = (TextView) findViewById(R.id.duphluxNumber);


        number = (TextView) findViewById(R.id.phone_number);
        timeLeft = (TextView) findViewById(R.id.secondsLeft);
        confirm = (Button) findViewById(R.id.confirmCall);
        redirect = (Button) findViewById(R.id.redirectBack);
        close = (Button) findViewById(R.id.close);

        subMain = (ViewGroup) findViewById(R.id.subMainDiv);
        loadingDiv = (ViewGroup) findViewById(R.id.loadingDiv);

        number.setText("Call from " + phone_number);

        duphluxSdk = DuphluxSdk.initializeSDK(this);
        authRequest = new AuthRequest();
        authRequest.setRedirect_url("https://duphlux.com");
        authRequest.setPhone_number(phone_number);
        authRequest.setTimeout("" + timeout);
        duphluxSdk.authenticate(this, authRequest, new DuphluxAuthenticationCallback() {

            @Override
            public void onStart() {
                // Called before request is made
                // Show busy icon or something...
                //progressDialog.show();
                subMain.setVisibility(View.GONE);
                loadingDiv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(JSONObject jsonObject) {
                // Handle response from Duphlux server.
                // Please see documentation for a sample json response
                loadingDiv.setVisibility(View.GONE);
                try {
                    duphlux_number.setText(jsonObject.getString("number"));
                    final String dNumber = jsonObject.getString("number");
                    duphlux_number.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + dNumber));
                            startActivity(intent);
                        }
                    });
                    long duration = timeout * 1000; //(jsonObject.getLong("expires_at") - (System.currentTimeMillis() / 1000)) * 1000;
                    initializeTimer(duration, 1000);
                    subMain.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(JSONArray jsonArray) {
                // Called when the Duphlux status is false and returns an error
                String errors = processErrors(jsonArray);
                __complete(false, errors);
            }

            @Override
            public void onError(String message, Throwable e) {
                // Called when a nasty error is encountered.
                __complete(false, "An error occurred. Please try again.");
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCall();
            }
        });

        redirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
                __complete(verificationStatus, responseMessage);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
                String msg = responseMessage != null ? responseMessage : "Verification cancelled.";
                __complete(verificationStatus, msg);
            }
        });
    }

    public void updateDisplayStatus(boolean status) {
        ViewGroup counter = (ViewGroup) findViewById(R.id.counterDiv);
        ViewGroup success = (ViewGroup) findViewById(R.id.successDiv);
        ViewGroup failed = (ViewGroup) findViewById(R.id.expiredDiv);

        counter.setVisibility(View.GONE);
        if (status) {
            success.setVisibility(View.VISIBLE);
        } else {
            failed.setVisibility(View.VISIBLE);
        }

        confirm.setVisibility(View.GONE);
        redirect.setVisibility(View.VISIBLE);
    }

    public void __complete(boolean status, String message) {
        Intent intent = new Intent();
        intent.putExtra("status", status);
        intent.putExtra("message", message);
        setResult(Configs.ACTIVITY_RESULT_CODE, intent);
        finish();
    }

    public String processErrors(JSONArray jsonArray) {
        String errors = "Errors Encountered: \n";
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                errors += (i + 1) + ". " + jsonArray.getString(i) + "\n";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Toast.makeText(this, errors, Toast.LENGTH_LONG).show();
        return errors;
    }

    public void initializeTimer(long duration, long interval) {
        countDownTimer = new CountDownTimer(duration, interval) {
            public void onTick(long millisUntilFinished) {
                timeLeft.setText(("" + millisUntilFinished / 1000));
                checkCall();
            }

            public void onFinish() {
                Log.i("logs", "Finsissh");
                checkCall();
            }
        };

        countDownTimer.start();
    }

    protected void checkCall() {
        duphluxSdk.getStatus(this, authRequest, new DuphluxAuthenticationCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    String status = jsonObject.getString("verification_status");
                    if (status.equalsIgnoreCase("verified")) {
                        verificationStatus = true;
                        responseMessage = "Authentication is successful.";
                        updateDisplayStatus(verificationStatus);
                        countDownTimer.cancel();
                    } else {
                        if (status.equalsIgnoreCase("failed")) {
                            verificationStatus = false;
                            responseMessage = "Authentication has expired.";
                            updateDisplayStatus(verificationStatus);
                            countDownTimer.cancel();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(JSONArray jsonArray) {
                //__complete(false);
            }

            @Override
            public void onError(String message, Throwable e) {

            }
        });
    }
}