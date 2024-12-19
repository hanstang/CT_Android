package hans.clevertap.firstapplication;

import android.app.Application;
import android.app.NotificationManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.clevertap.android.geofence.CTGeofenceAPI;
import com.clevertap.android.geofence.CTGeofenceSettings;
import com.clevertap.android.geofence.Logger;
import com.clevertap.android.geofence.interfaces.CTGeofenceEventsListener;
import com.clevertap.android.geofence.interfaces.CTLocationUpdatesListener;
import com.clevertap.android.pushtemplates.PushTemplateNotificationHandler;
import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.interfaces.NotificationHandler;
import com.clevertap.android.sdk.interfaces.OnInitCleverTapIDListener;
import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener;
import com.clevertap.android.sdk.variables.Var;
import com.clevertap.android.sdk.variables.callbacks.FetchVariablesCallback;
import com.clevertap.android.sdk.variables.callbacks.VariableCallback;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class FirstApp extends Application {
    private Uri uri;

    @Override
    public void onCreate() {
        ActivityLifecycleCallback.register(this);
        super.onCreate();
        //registerActivityLifecycleCallbacks((ActivityLifecycleCallbacks) this);
        // Required initialization logic here!
        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        clevertapDefaultInstance.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);
        //clevertapDefaultInstance.enablePersonalization();
        //clevertapDefaultInstance.setCTPushNotificationListener(this);
        CleverTapAPI.setNotificationHandler((NotificationHandler)new PushTemplateNotificationHandler());


        //CleverTap Variable Product Experience
        Var<Integer> var_float_img = clevertapDefaultInstance.defineVariable("var_float_img", 1);
        Var<String> var_image_url = clevertapDefaultInstance.defineVariable("var_image_url", "str");
        clevertapDefaultInstance.syncVariables();

        var_float_img.addValueChangedCallback(new VariableCallback() {
            @Override
            public void onValueChanged(Var varInstance) {
                // invoked on app start and whenever value is changed
                Log.d("Product EXP values", "var_float_img changed");
            }
        });

        var_image_url.addValueChangedCallback(new VariableCallback() {
            @Override
            public void onValueChanged(Var varInstance) {
                // invoked on app start and whenever value is changed
                Log.d("Product EXP values","var_image_url changed");
            }
        });

        clevertapDefaultInstance.fetchVariables(new FetchVariablesCallback() {
            @Override
            public void onVariablesFetched(boolean isSuccess) {
                // isSuccess is true when server request is successful, false otherwise
            }
        });






        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CleverTapAPI.createNotificationChannel(getApplicationContext(),
                    "Testing","Test Channel 1",
                    "Test Channel Description",
                    NotificationManager.IMPORTANCE_MAX,true);
            CleverTapAPI.createNotificationChannel(getApplicationContext(),
                    "Testing2","Test Channel 2",
                    "Test Channel Description 2",
                    NotificationManager.IMPORTANCE_MAX,true);
        }

        clevertapDefaultInstance.getCleverTapID(new OnInitCleverTapIDListener() {
            @Override
            public void onInitCleverTapID(final String CTID) {
                // Callback on main thread
                FirebaseAnalytics mFirebaseAnalytics;
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
                mFirebaseAnalytics.setUserProperty("ct_objectId", Objects.requireNonNull(CTID));
                mFirebaseAnalytics.setUserProperty("ct_region", Objects.requireNonNull("ID/TW"));
                Log.v("User Info APP",CTID);
            }
        });

        /*
        if (clevertapDefaultInstance.isPushPermissionGranted()) {
            Log.v("User Permission"," that's True");
        }else {
            Log.v("User Permission"," that's False");
            clevertapDefaultInstance.promptForPushPermission(false);
            Log.v("User Permission"," now become true");
        }*/



        /*
        CTGeofenceSettings ctGeofenceSettings = new CTGeofenceSettings.Builder()
                .enableBackgroundLocationUpdates(true)//boolean to enable background location updates
                .setLogLevel(Logger.VERBOSE)//Log Level
                .setLocationAccuracy(CTGeofenceSettings.ACCURACY_HIGH)//byte value for Location Accuracy
                .setLocationFetchMode(CTGeofenceSettings.FETCH_CURRENT_LOCATION_PERIODIC)//byte value for Fetch Mode
                .setGeofenceMonitoringCount(50)//int value for number of Geofences CleverTap can monitor
                .setInterval(1800000)//long value for interval in milliseconds
                .setFastestInterval(1800000)//long value for fastest interval in milliseconds
                .setSmallestDisplacement(200)//float value for smallest Displacement in meters
                .setGeofenceNotificationResponsiveness(0)// int value for geofence notification responsiveness in milliseconds
                .build();
         */



        CTGeofenceSettings ctGeofenceSettings = new CTGeofenceSettings.Builder()
                .setLogLevel(Logger.VERBOSE)//Log Level
                .setLocationAccuracy(CTGeofenceSettings.ACCURACY_HIGH)//byte value for Location Accuracy
                .setLocationFetchMode(CTGeofenceSettings.FETCH_CURRENT_LOCATION_PERIODIC)//byte value for Fetch Mode
                .build();

        CTGeofenceAPI.getInstance(getApplicationContext()).init(ctGeofenceSettings,clevertapDefaultInstance);

        try {
            CTGeofenceAPI.getInstance(getApplicationContext()).triggerLocation();
            Log.v("Geofence Hans Success","triggerLocation");
        } catch (IllegalStateException e){
            // thrown when this method is called before geofence SDK initialization
            Log.v("Geofence Hans Error",e.toString());
        }

        CTGeofenceAPI.getInstance(getApplicationContext())
                .setOnGeofenceApiInitializedListener(new CTGeofenceAPI.OnGeofenceApiInitializedListener() {
                    @Override
                    public void OnGeofenceApiInitialized() {
                        //App is notified on the main thread that CTGeofenceAPI is initialized
                        Log.d("Hans Geofence", "OnGeofenceApiInitialized() called");
                    }
                });

        CTGeofenceAPI.getInstance(getApplicationContext())
                .setCtGeofenceEventsListener(new CTGeofenceEventsListener() {
                    @Override
                    public void onGeofenceEnteredEvent(JSONObject jsonObject) {
                        //Callback on the main thread when the user enters Geofence with info in jsonObject
                        Log.d("Hans Geofence", "onGeofenceEnteredEvent() called with: jsonObject = [" + new Gson().toJson(jsonObject) + "]");
                    }

                    @Override
                    public void onGeofenceExitedEvent(JSONObject jsonObject) {
                        //Callback on the main thread when user exits Geofence with info in jsonObject
                        Log.d("Hans Geofence", "onGeofenceExitedEvent() called with: jsonObject = [" + new Gson().toJson(jsonObject) + "]");

                    }
                });

        CTGeofenceAPI.getInstance(getApplicationContext())
                .setCtLocationUpdatesListener(new CTLocationUpdatesListener() {
                    @Override
                    public void onLocationUpdates(Location location) {
                        //New location on the main thread as provided by the Android OS
                        Log.d("Hans Geofence", "onLocationUpdates() called with: location = [" + location.toString() + "]");
                    }
                });


    }



    /**
     * Receives a callback whenever push notification payload is received.
     *
     * @param payload
     */

    /*
    @Override
    public void onNotificationClickedPayloadReceived(HashMap<String, Object> payload) {
        Toast.makeText(getApplicationContext(),payload.toString(), Toast.LENGTH_LONG).show();
        Log.d("Payload Data", "Payload App Level:" + payload.toString());
    }*/
}
