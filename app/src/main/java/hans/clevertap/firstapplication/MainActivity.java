package hans.clevertap.firstapplication;

import android.app.AlertDialog;
import android.app.Application;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.clevertap.android.geofence.CTGeofenceAPI;
import com.clevertap.android.geofence.CTGeofenceSettings;
import com.clevertap.android.geofence.Logger;
import com.clevertap.android.geofence.interfaces.CTGeofenceEventsListener;
import com.clevertap.android.geofence.interfaces.CTLocationUpdatesListener;
import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.CleverTapInstanceConfig;
import com.clevertap.android.sdk.interfaces.OnInitCleverTapIDListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    CleverTapAPI clevertapDefaultInstance;
    private Uri uri;

    @Override
    protected void onResume() {
        super.onResume();
        //CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        //clevertapDefaultInstance.pushEvent("Main Activity Open");

        //CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            try {
                if (clevertapDefaultInstance.isPushPermissionGranted()) {
                    Log.v("User Permission", " that's True");
                } else {
                    Log.v("User Permission", " that's False");
                    clevertapDefaultInstance.promptForPushPermission(true);
                    Log.v("User Permission", " now become true");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.v("User Info Error", e.toString());
            }
        }
        //clevertapDefaultInstance.promptForPushPermission(true);
        */



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // getting the data from our intent in our uri.
        uri = getIntent().getData();

        // checking if the uri is null or not.
        if (uri != null) {
            // if the uri is not null then we are getting
            // the path segments and storing it in list.
            List<String> parameters = uri.getPathSegments();

            // after that we are extracting string
            // from that parameters.
            String param = parameters.get(parameters.size() - 1);

            // on below line we are setting that string
            // to our text view which we got as params.
            Log.d("Hans URI Gather",uri.toString());

            try {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://clevertap.com/"));
                startActivity(myIntent);
                Log.d("Hans URI Open","open browser");
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No application can handle this request."
                        + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
            //startActivity(browserIntent);
        }

        //CleverTapInstanceConfig config =  CleverTapInstanceConfig.createInstance(this,"RK4-45K-W56Z","354-432");
        //clevertapDefaultInstance = CleverTapAPI.instanceWithConfig(this,config);

        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        //clevertapDefaultInstance.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);

        Location location = clevertapDefaultInstance.getLocation();
        clevertapDefaultInstance.setLocation(location);

        //FirebaseAnalytics mFirebaseAnalytics;
        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //mFirebaseAnalytics.setUserProperty("ct_objectId",Objects.requireNonNull(CleverTapAPI.getDefaultInstance(this)).getCleverTapID());


        //Log.v("User Info",clevertapDefaultInstance.getCleverTapID());

        //clevertapDefaultInstance.enablePersonalization();
        //clevertapDefaultInstance.setCTPushNotificationListener(this);


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            //for checking the status of push notification permission android 13 below
            if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                //Toast.makeText(this, "Notification is on for this Application", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(this, "Notification is off for this Application", Toast.LENGTH_SHORT).show();
                //show some alert dialog first to asking
                new AlertDialog.Builder(this)
                        .setTitle("Push Permission")
                        .setMessage("You turn off the push, will you want to turn it on again?")
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with the operation
                                //code for go to the setting
                                Intent intent = new Intent();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                    intent.putExtra("app_package", getPackageName());
                                    intent.putExtra("app_uid", getApplicationInfo().uid);
                                } else {
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                }
                                startActivity(intent);
                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }





        //Check Login User
        SharedPreferences sp1=this.getSharedPreferences("First_APP", MODE_PRIVATE);
        if(sp1.contains("UserID")){
            String UserIDSP=sp1.getString("UserID", null);
            if(!UserIDSP.isEmpty()){
                Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                startActivity(intent);
                finish();
            }else{
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clevertapDefaultInstance.pushEvent("Login Page Open");
                    }
                }, 2000);
            }
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    clevertapDefaultInstance.pushEvent("Login Page Open");
                }
            }, 2000);
        }


        EditText user_identity = (EditText) findViewById(R.id.user_identity);


        //Get CleverTap ID
        TextView txtCurrentUser = (TextView) findViewById(R.id.current_user);
        try {
            //final String currentUser;
            /*
            clevertapDefaultInstance.getCleverTapID(new OnInitCleverTapIDListener(){
                @Override
                public void onInitCleverTapID(String s) {
                    currentUser = s;
                }
            });
            */

            String currentUser = clevertapDefaultInstance.getCleverTapID().toString();
            //txtCurrentUser.setText(clevertapDefaultInstance.getProperty("Email").toString());
            txtCurrentUser.setText(currentUser);
            //Toast.makeText(getApplicationContext(),currentUser,Toast.LENGTH_LONG).show();
            //Log.v("User Info",clevertapDefaultInstance.getCleverTapID());
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("User Info Error",e.toString());
        }

        // Get CleverTap Account ID
        TextView txtCurrentAccountID = (TextView) findViewById(R.id.current_account_id);
        try {

            ApplicationInfo app = this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = app.metaData;
            //txtCurrentAccountID.setText(bundle.getString("CLEVERTAP_ACCOUNT_ID") + " (" + bundle.getString("CLEVERTAP_REGION") + ")")

            txtCurrentAccountID.setText(clevertapDefaultInstance.getAccountId().toString());
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }


        Button btnLogin = (Button) findViewById(R.id.button_login);
        Spinner spinnerCustomerType = (Spinner) findViewById(R.id.spinner_customer_type);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("User ID",user_identity.getText().toString());
                // Do something in response to button click
                if(user_identity.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "User ID Can't be Empty", Toast.LENGTH_LONG).show();
                }else{
                    //Halo doc
                    HashMap<String, Object> profileUpdatePre = new HashMap<String, Object>();
                    //profileUpdate.put("Identity", user_identity.getText().toString());
                    /*
                    profileUpdatePre.put("email", user_identity.getText().toString()+"@email.com");
                    profileUpdatePre.put("Customer Type",spinnerCustomerType.getSelectedItem().toString());
                    profileUpdatePre.put("phone","GzchQBYTB5C06sq1r3vPncE13THYROVw0hWLzscjOmd+zp67fFDPiEpJ9gBW+JiMTT+r5P0TKXF+qYf0EdDFxw==");
                    clevertapDefaultInstance.onUserLogin(profileUpdatePre);*/

                    //CLeverTap OnLogin
                    HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
                    profileUpdate.put("Identity", user_identity.getText().toString());
                    profileUpdate.put("email", user_identity.getText().toString()+"@email.com");
                    profileUpdate.put("Customer Type",spinnerCustomerType.getSelectedItem().toString());
                    //profileUpdate.put("tz","Asia/Kolkata");
                    //profileUpdate.put("Phone","+6280808080");
                    clevertapDefaultInstance.onUserLogin(profileUpdate);
                    //clevertapDefaultInstance.pushProfile(profileUpdate);
                    //push event
                    clevertapDefaultInstance.pushEvent("Login Success");

                    //create login pref
                    SharedPreferences sp=getSharedPreferences("First_APP", MODE_PRIVATE);
                    SharedPreferences.Editor Ed=sp.edit();
                    Ed.putString("UserID",user_identity.getText().toString() );
                    Ed.putString("CustomerType",spinnerCustomerType.getSelectedItem().toString());
                    Ed.commit();

                    //move to NavigationActivity
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                            //intent.putExtra("webpage","paxel4");
                            startActivity(intent);
                            finish();
                        }
                    }, 1000);

                    Toast.makeText(getApplicationContext(), "Login/Register Clicked", Toast.LENGTH_LONG).show();
                }
            }
        });
//
//        Button btnLogout = (Button) findViewById(R.id.button_logout);
//        btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //clevertapDefaultInstance.setOptOut(false);
//                clevertapDefaultInstance.pushEvent("Do Logout");
//
//                //delete shared preferences
//                /*
//                SharedPreferences preferences = getApplicationContext().getSharedPreferences("WizRocket", MODE_PRIVATE);
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.clear();
//                editor.apply();
//
//                //reset sdk
//                CleverTapAPI.setInstances(null);
//
//
//                //ActivityLifecycleCallback.register(getApplication());
//                //Application.ActivityLifecycleCallbacks(getApplicationContext());
//                //FirstApp firstApp = new FirstApp();
//                //firstApp.onCreate();
//
//                clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
//                Log.v("Hans New CTID",new Gson().toJson(clevertapDefaultInstance.getCleverTapID()));
//                */
//                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        Button btnEvent = (Button) findViewById(R.id.button_event);
//        EditText user_region = (EditText) findViewById(R.id.InputRegion);
//        btnEvent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //clevertapDefaultInstance.pushEvent("Complete Delivery");
//                //clevertapDefaultInstance.pushEvent("Survey In App");
//                HashMap<String, Object> EvtProperties = new HashMap<String, Object>();
//                EvtProperties.put("product id", user_region.getText().toString());
//                clevertapDefaultInstance.pushEvent("Product viewed",EvtProperties);
//                //clevertapDefaultInstance.getDefaultInstance(getApplicationContext()).resumeInAppNotifications();
//                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        /*
//        btnPushProfile = (Button) findViewById(R.id.button_push);
//        btnPushProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
//
//                if(user_identity.getText().toString() != null)
//                {
//                    profileUpdate.put("Identity", user_identity.getText().toString());
//                }
//                if(user_email.getText().toString() != null) {
//                    profileUpdate.put("Email", user_email.getText().toString());
//                    //int index = user_email.getText().toString().indexOf('@');
//                    //profileUpdate.put("Name", user_email.getText().toString().substring(0, index));
//                }
//                if(user_phone.getText().toString() != null) {
//                    profileUpdate.put("Phone", user_phone.getText().toString());
//                }
//
//                clevertapDefaultInstance.pushProfile(profileUpdate);
//                txtCurrentUser.setText(clevertapDefaultInstance.getCleverTapID().toString());
//                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_LONG).show();
//            }
//        });
//         */
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CleverTapAPI.createNotificationChannel(getApplicationContext(),
//                    "Testing","Test Channel 1",
//                    "Test Channel Description",
//                    NotificationManager.IMPORTANCE_MAX,true);
//        }
//
//        //EditText user_region = (EditText) findViewById(R.id.InputRegion);
//        /*
//        Button btnInAPP = (Button) findViewById(R.id.button_inApp);
//        btnInAPP.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                // event with properties
//                /*
//                HashMap<String, Object> regionProperties = new HashMap<String, Object>();
//                regionProperties.put("Region", user_region.getText().toString());
//
//                clevertapDefaultInstance.pushEvent("Region Clicked",regionProperties);
//                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_LONG).show();
//                */
//
//                HashMap<String, Object> regionProperties = new HashMap<String, Object>();
//                //regionProperties.put("screen_name", "deals_discovery");
//                regionProperties.put("screen_name", "deals");
//                regionProperties.put("category", "test1");
//
//                clevertapDefaultInstance.pushEvent("Screen Displayed",regionProperties);
//                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        Button btnInbox = (Button) findViewById(R.id.button_openInbox);
//        btnInbox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // event with properties
//                //clevertapDefaultInstance.pushEvent("Request Inbox Event");
//                Intent intent = new Intent(MainActivity.this, InboxActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        Button btnWebvbiew = (Button) findViewById(R.id.button_wb);
//        btnWebvbiew.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // event with properties
//                //clevertapDefaultInstance.pushEvent("Request Inbox Event");
//                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
//                intent.putExtra("webpage","paxel1");
//                startActivity(intent);
//            }
//        });
//
//        Button btnWebvbiew2 = (Button) findViewById(R.id.button_wb2);
//        btnWebvbiew2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // event with properties
//                //clevertapDefaultInstance.pushEvent("Request Inbox Event");
//                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
//                intent.putExtra("webpage","paxel2");
//                startActivity(intent);
//            }
//        });
//
//        Button btnWebvbiew3 = (Button) findViewById(R.id.button_wb3);
//        btnWebvbiew3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // event with properties
//                //clevertapDefaultInstance.pushEvent("Request Inbox Event");
//                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
//                intent.putExtra("webpage","paxel3");
//                startActivity(intent);
//            }
//        });
//
//        Button btnWebvbiew4 = (Button) findViewById(R.id.button_wb4);
//        btnWebvbiew4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // event with properties
//                //clevertapDefaultInstance.pushEvent("Request Inbox Event");
//                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
//                intent.putExtra("webpage","paxel4");
//                startActivity(intent);
//            }
//        });
//
//        Button btnNativeDisplay = (Button) findViewById(R.id.button_nativeDsiplay);
//        btnNativeDisplay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // event with properties
//                //clevertapDefaultInstance.pushEvent("Request Inbox Event");
//                Intent intent = new Intent(MainActivity.this, NativeDisplayActivity.class);
//                startActivity(intent);
//            }
//        });
//

        //btnNativeDisplay.setVisibility(View.GONE);
        //btnInbox.setVisibility(View.GONE);
        //btnInAPP.setVisibility(View.GONE);
        //user_region.setVisibility(View.GONE);
        //btnPushProfile.setVisibility(View.GONE);
        //btnLogout.setVisibility(View.GONE);
        //user_email.setVisibility(View.GONE);
        //user_phone.setVisibility(View.GONE);
        //txtCurrentUser.setVisibility(View.GONE);
    }





}