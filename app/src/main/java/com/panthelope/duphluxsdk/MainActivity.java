package com.panthelope.duphluxsdk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.panthelope.duphluxlib.lib.DuphluxAuthRequest;
import com.panthelope.duphluxlib.lib.DuphluxAuthenticationCallback;
import com.panthelope.duphluxlib.lib.DuphluxConfigs;
import com.panthelope.duphluxlib.lib.DuphluxSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    EditText number;

    TextView duphlux_number;
    TextView expires;

    Button developer;
    Button wizard;
    Button status;

    DuphluxSdk duphluxSdk;
    DuphluxAuthRequest duphluxAuthRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        number = (EditText) findViewById(R.id.number);

        duphlux_number = (TextView) findViewById(R.id.duphlux_number);
        expires = (TextView) findViewById(R.id.expires);

        developer = (Button) findViewById(R.id.developerButton);
        wizard = (Button) findViewById(R.id.wizardButton);
        status = (Button) findViewById(R.id.getStatusButton);

        duphluxSdk = DuphluxSdk.initializeSDK(this);
        duphluxAuthRequest = new DuphluxAuthRequest();
        duphluxAuthRequest.setRedirect_url("https://duphlux.com");

        developer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                duphluxAuthRequest.setPhone_number(number.getText().toString());
                duphluxSdk.authenticate(MainActivity.this, duphluxAuthRequest, new DuphluxAuthenticationCallback() {
                    @Override
                    public void onStart() {
                        // Called before request is made
                        // Show busy icon or something...
                    }

                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        // Handle response from Duphlux server.
                        // Please see documentation for a sample json response
                        Log.i("Response", jsonObject.toString());
                        try {
                            duphlux_number.setText("Duphlux Number: " + jsonObject.getString("number"));
                            Timestamp timestamp = new Timestamp(jsonObject.getLong("expires_at"));
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm:ss:S");
                            expires.setText("Expires at: " + simpleDateFormat.format(timestamp));
                            status.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(JSONArray jsonArray) {
                        // Called when the Duphlux status is false and returns an error
                        processErrors(jsonArray);
                    }

                    @Override
                    public void onError(String message, Throwable e) {
                        // Called when a nasty error is encountered.
                    }
                });
            }
        });

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                duphluxSdk.getStatus(MainActivity.this, duphluxAuthRequest, new DuphluxAuthenticationCallback() {
                    @Override
                    public void onStart() {
                        // Called before request is made
                        // Show busy icon or something...
                    }

                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        // Handle response from Duphlux server.
                        // Please see documentation for a sample json response
                        try {
                            String status = jsonObject.getString("verification_status");
                            Toast.makeText(MainActivity.this, status, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(JSONArray jsonArray) {
                        // Called when the Duphlux status is false and returns an error
                        processErrors(jsonArray);
                    }

                    @Override
                    public void onError(String message, Throwable e) {
                        // Called when a nasty error is encountered.
                    }
                });
            }
        });

        wizard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DuphluxSdk.launch(MainActivity.this, number.getText().toString());
            }
        });
    }

    public void processErrors(JSONArray jsonArray) {
        String errors = "Errors Encountered: \n";
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                errors += (i + 1) + ". " + jsonArray.getString(i) + "\n";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(MainActivity.this, errors, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DuphluxConfigs.ACTIVITY_RESULT_CODE) {
            // Returns data with a boolean value for status and a corresponding message.
            if(data.getBooleanExtra("status", false)){

            }
            Toast.makeText(MainActivity.this, data.getStringExtra("message"), Toast.LENGTH_LONG).show();
        }
    }
}
