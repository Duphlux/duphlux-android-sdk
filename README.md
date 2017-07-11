# DuphluxLib
The Official Duphlux Android SDK

## Getting Started

Duphlux SDK adds mobile number authentication to your android app. Verify your users’ mobile numbers by having them give a missed call. It’s simple, seamless and instant.

### Prerequisites

DuphluxSDK requires a minimum gradle compileSdkVersion of 25.
Preferred targetSdkVersion should be atleast 25


### Installing

Install Duphlux by following the simple steps:


Add the snippet below to your root build.gradle file.

```
allprojects {
    repositories {
       	. . .
        maven { url 'https://jitpack.io' }
    }
}
```

Next, add the lines below to your app build.gradle file

```
dependencies {
    compile 'com.github.Duphlux:duphlux-android-sdk:v1.0.2'
}
```

Now, add the following to your manifest file.

```
<!-- Duphlux App Token -->
<meta-data android:name="com.panthelope.duphlux.app.token" android:value="YOUR-APP-ACCESS-TOKEN" />
```

That’s all!


## Using the SDK

To start using duphlux, you’d need to initialize the SDK.

```
DuphluxSdk duphluxSdk = DuphluxSdk.initializeSDK(); // This will load your app-token from the manifest file.
```

Duphlux can be used in 2 ways to authenticate your users’ mobile numbers

### Standard Implementation

This method gives you full control. Here you initiate an authentication request to retrieve and display a duphlux phone number to your users to give a missed call.
Note that you will be required to handle the event upon a successful call by the user.


```
DuphluxAuthRequest  duphluxAuthRequest = new DuphluxAuthRequest();
duphluxAuthRequest.setTimeout(timeout);
duphluxAuthRequest.setPhone_number("YOUR-USERS-MOBILE-NUMBER");
duphluxAuthRequest.setTransaction_reference("UNIQUE-REFERENCE"); // Optional. Duphlux will generate a unique reference for you if not set.

```

Now make an authentication request to the duphlux endpoint

```
                duphluxSdk.authenticate(MainActivity.this, duphluxAuthRequest, new DuphluxAuthenticationCallback() {
   		    @Override
                    public void onStart() {
                        // Called before request is made
                        // Show busy icon or something...
                    }

                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        // Handle response from Duphlux server.
			// Display duphlux number returned to your user to give a missed call
                        // Please see documentation for a sample json response. https://duphlux.com/documentation
                        Log.i("Response", jsonObject.toString());
                        try {
			    // jsonObject.getString("number”);
			    // jsonObject.getLong("expires_at”);
			    // jsonObject.getString(“verification_url”);
			    // jsonObject.getString(“verification_reference”);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(JSONArray jsonArray) {
                        // Called when the Duphlux status is false and returns an error
                        // Please see documentation for a sample json error response. https://duphlux.com/documentation
                    }

                    @Override
                    public void onError(String message, Throwable e) {
                        // Called when a nasty error is encountered.
                    }
                });
```



Once your user has given our number a missed call, you’d need to query to confirm the status of your previous request.

```
		duphluxSdk.getStatus(MainActivity.this, duphluxAuthRequest, new DuphluxAuthenticationCallback() {
                    @Override
                    public void onStart() {
                        // Called before request is made
                        // Show busy icon or something...
                    }

                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        // Handle response from Duphlux server.
                        // Please see documentation for a sample json response. https://duphlux.com/documentation
                        try {
                            String status = jsonObject.getString("verification_status"); // can be either “verified”, “pending” or “failed”
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

```


### Duphlux Inline Implementation

This method takes your users through the journey and returns a boolean on completion. Recommended.

```
DuphluxSdk.launch(MainActivity.this, number.getText().toString()); // Launches the duphlux interface.

```

To capture the result from the above code snippet, you’d need to overwrite your onActivityResult method.

```
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

```

**Note**

You’d need to import the necessary classes shown below if not already done by your IDE.

```
import com.panthelope.duphluxlib.lib.DuphluxAuthRequest;
import com.panthelope.duphluxlib.lib.DuphluxAuthenticationCallback;
import com.panthelope.duphluxlib.lib.DuphluxConfigs;
import com.panthelope.duphluxlib.lib.DuphluxSdk;

```
## Contact

* Send us an email at **devops@duphlux.com** - [Duphlux](https://duphlux.com) 

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone who's code was used

