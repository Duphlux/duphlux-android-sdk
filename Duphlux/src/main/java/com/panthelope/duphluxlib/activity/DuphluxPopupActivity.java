package com.panthelope.duphluxlib.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.panthelope.duphluxlib.R;
import com.panthelope.duphluxlib.lib.DuphluxAuthRequest;
import com.panthelope.duphluxlib.lib.DuphluxAuthenticationCallback;
import com.panthelope.duphluxlib.lib.DuphluxConfigs;
import com.panthelope.duphluxlib.lib.DuphluxSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DuphluxPopupActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CALL_ACTION = 1101;
    DuphluxSdk duphluxSdk;
    DuphluxAuthenticationCallback authenticationCallback;
    DuphluxAuthRequest duphluxAuthRequest;
    String phone_number;

    TextView duphlux_number;
    TextView number;
    TextView timeLeft;

    CountDownTimer countDownTimer;
    Button dail_now;
    Button redirect;
    Button close;
    Boolean verificationStatus = false;
    String responseMessage;

    ViewGroup subMain;
    ViewGroup loadingDiv;
    long timeout = 900;

    boolean inProgress = false;
    String dNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);
        this.setFinishOnTouchOutside(false);


        phone_number = getIntent().getStringExtra("phone_number");

        duphlux_number = (TextView) findViewById(R.id.duphluxNumber);
        number = (TextView) findViewById(R.id.phone_number);
        timeLeft = (TextView) findViewById(R.id.secondsLeft);
        dail_now = (Button) findViewById(R.id.confirmCall);
        redirect = (Button) findViewById(R.id.redirectBack);
        close = (Button) findViewById(R.id.close);

        subMain = (ViewGroup) findViewById(R.id.subMainDiv);
        loadingDiv = (ViewGroup) findViewById(R.id.loadingDiv);

        number.setText("Call from " + phone_number);

        dail_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dNumber != null) {
                    dial_number(dNumber);
                }
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

        checkInprogress();
    }

    public void checkInprogress() {
        if (!inProgress) {
            begin();
        } else {
            subMain.setVisibility(View.VISIBLE);
            loadingDiv.setVisibility(View.GONE);
        }
    }

    public void begin() {
        duphluxSdk = DuphluxSdk.initializeSDK(this);
        duphluxAuthRequest = new DuphluxAuthRequest();
        duphluxAuthRequest.setRedirect_url("https://duphlux.com");
        duphluxAuthRequest.setPhone_number(phone_number);
        duphluxAuthRequest.setTimeout("" + timeout);
        duphluxSdk.authenticate(this, duphluxAuthRequest, new DuphluxAuthenticationCallback() {

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
                    dNumber = jsonObject.getString("number");
                    duphlux_number.setText(dNumber);
                    duphlux_number.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dial_number(dNumber);
                        }
                    });
                    long duration = timeout * 1000;
                    initializeTimer(duration, 1000);
                    subMain.setVisibility(View.VISIBLE);
                    inProgress = true;
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

        dail_now.setVisibility(View.GONE);
        redirect.setVisibility(View.VISIBLE);
    }

    public void __complete(boolean status, String message) {
        Intent intent = new Intent();
        intent.putExtra("status", status);
        intent.putExtra("message", message);
        setResult(DuphluxConfigs.ACTIVITY_RESULT_CODE, intent);
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
        duphluxSdk.getStatus(this, duphluxAuthRequest, new DuphluxAuthenticationCallback() {
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

    public void dial_number(String dNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + dNumber));
        if (Build.VERSION.SDK_INT > 22) {
            if (ActivityCompat.checkSelfPermission(DuphluxPopupActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DuphluxPopupActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(DuphluxPopupActivity.this, Manifest.permission.CALL_PHONE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DuphluxPopupActivity.this);
                    builder.setTitle("Permission Required")
                            .setMessage(getString(R.string.app_name) + " needs permissions to make the call")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(DuphluxPopupActivity.this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_ACTION);
                                }
                            })
                            .setNegativeButton("Deny", null)
                            .show();
                } else {
                    ActivityCompat.requestPermissions(DuphluxPopupActivity.this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_ACTION);
                }
                return;
            }
        }
        startActivity(intent);
    }
}
