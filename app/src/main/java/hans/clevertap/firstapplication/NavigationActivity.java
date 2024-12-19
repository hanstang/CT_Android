package hans.clevertap.firstapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.clevertap.android.sdk.CTInboxListener;
import com.clevertap.android.sdk.CTInboxStyleConfig;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.CleverTapInstanceConfig;
import com.clevertap.android.sdk.displayunits.DisplayUnitListener;
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnit;
import com.clevertap.android.sdk.inapp.CTLocalInApp;
import com.clevertap.android.sdk.inbox.CTInboxMessage;
import com.clevertap.android.sdk.inbox.CTInboxMessageContent;


import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigationActivity extends AppCompatActivity implements CTInboxListener, DisplayUnitListener {
    CleverTapAPI clevertapDefaultInstance;
    private float dX, dY;
    private boolean isDragging = false;
    private int screenWidth, screenHeight;
    private int containerWidth, containerHeight;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        FrameLayout floatingImageContainer = findViewById(R.id.floating_image_container);
        ImageView closeButton = findViewById(R.id.close_button);
        ImageView floatingImage = findViewById(R.id.floating_image);

        // Load the image from the URL using Glide
        /*
        Glide.with(this)
                .load("https://w7.pngwing.com/pngs/773/739/png-transparent-bullion-coin-gold-bar-gold-as-an-investment-gold-thumbnail.png")
                .into(floatingImage);*/

        // Capture screen dimensions and container dimensions
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);

        rootView.post(() -> {
            screenWidth = rootView.getWidth();
            screenHeight = rootView.getHeight();
            containerWidth = floatingImageContainer.getWidth();
            containerHeight = floatingImageContainer.getHeight();
        });

        floatingImageContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        isDragging = false;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float newX = event.getRawX() + dX;
                        float newY = event.getRawY() + dY;

                        // Ensure the container stays within screen bounds
                        newX = Math.max(0, Math.min(newX, screenWidth - containerWidth));
                        newY = Math.max(0, Math.min(newY, screenHeight - containerHeight));

                        view.animate()
                                .x(newX)
                                .y(newY)
                                .setDuration(0)
                                .start();

                        isDragging = true;
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (!isDragging) {
                            //openNewActivity();
                            Toast.makeText(getApplicationContext(), "Img Clicked", Toast.LENGTH_LONG).show();
                        }
                        return true;
                }
                return false;
            }
        });

        // Close button action to hide the floating container
        closeButton.setOnClickListener(v -> floatingImageContainer.setVisibility(View.GONE));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clevertapDefaultInstance.pushEvent("Navigation Page Open");
            }
        }, 2000);



        //init clevertap
        //CleverTapInstanceConfig config =  CleverTapInstanceConfig.createInstance(this,"RK4-45K-W56Z","354-432");
        //clevertapDefaultInstance = CleverTapAPI.instanceWithConfig(this,config);
        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());

        //Set the Notification Inbox Listener
        //clevertapDefaultInstance.setCTNotificationInboxListener(this);
        //clevertapDefaultInstance.initializeInbox();

        //set the Native Display Listener
        clevertapDefaultInstance.setDisplayUnitListener(this);


        /*
        HashMap<String, Object> profileUpdate = new HashMap<String, Object>();

        profileUpdate.put("Prefered Language", "English");
        profileUpdate.put("tz","Asia/Kolkata");

        clevertapDefaultInstance.pushProfile(profileUpdate);
        */





        //User Info load
        SharedPreferences sp1=this.getSharedPreferences("First_APP", MODE_PRIVATE);
        String userIDSP=sp1.getString("UserID", null);
        String userTypeSP= sp1.getString("CustomerType",null);
        TextView txtUserID = (TextView) findViewById(R.id.current_userID);
        TextView txtUserType = (TextView) findViewById(R.id.current_userType);
        txtUserID.setText(userIDSP + "-" + clevertapDefaultInstance.getCleverTapID());
        txtUserType.setText(userTypeSP + "-" + clevertapDefaultInstance.getProperty("Identity"));

        switch (userTypeSP){
            case "Gold":
                floatingImage.setImageResource(R.drawable.gold);
                break;
            case "Silver":
                floatingImage.setImageResource(R.drawable.silver);
                break;
            case "Platinum":
                floatingImage.setImageResource(R.drawable.platinum);
                break;
            default:
                floatingImage.setImageResource(R.drawable.ic_launcher_foreground);
        }
        int var_show;
        var_show = (int)clevertapDefaultInstance.getVariableValue("var_float_img");
        switch (var_show){
            case 0:
                floatingImageContainer.setVisibility(View.GONE);
                break;
            case 1:
                floatingImageContainer.setVisibility(View.VISIBLE);
                break;
        }
        String var_img_url;
        var_img_url = (String) clevertapDefaultInstance.getVariableValue("var_image_url");
        if(!var_img_url.equals("str")){
            if(var_img_url!="var"){
                Glide.with(this)
                        .load(var_img_url)
                        .into(floatingImage);
            }
        }






        //floatingImageContainer.setVisibility(View.GONE)






        SharedPreferences sp2=this.getSharedPreferences("WizRocket",MODE_PRIVATE);
        Map<String, ?> keys = sp2.getAll();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());
        }


        Button btnLogout = (Button) findViewById(R.id.button_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //push event
                clevertapDefaultInstance.pushEvent("Logout Success");

                //remove USER pf
                SharedPreferences sp=getSharedPreferences("First_APP", MODE_PRIVATE);
                SharedPreferences.Editor Ed=sp.edit();
                Ed.putString("UserID", null);
                Ed.putString("CustomerType", null);
                Ed.commit();


                //remove CT pf
                /*
                SharedPreferences sp2=getSharedPreferences("WizRocket",MODE_PRIVATE);
                SharedPreferences.Editor Ed1=sp2.edit();
                Ed1.clear();
                Ed1.apply();
                */

                //move to NavigationActivity
                Intent intent = new Intent(NavigationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(getApplicationContext(), "Logout Clicked", Toast.LENGTH_LONG).show();
            }
        });

        Spinner spinnerPushType = (Spinner) findViewById(R.id.spinner_pushType);
        Button btnTriggerPush = (Button) findViewById(R.id.button_triggerPush);
        btnTriggerPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> eventProperties = new HashMap<String, Object>();
                eventProperties.put("Push Type", spinnerPushType.getSelectedItem().toString());
                clevertapDefaultInstance.pushEvent("Push Request",eventProperties);
                Toast.makeText(getApplicationContext(), "Push Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        Spinner spinnerInAPPType = (Spinner) findViewById(R.id.spinner_inAPPType);
        Button btnTriggerInAPP = (Button) findViewById(R.id.button_triggerInAPP);
        btnTriggerInAPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> eventProperties = new HashMap<String, Object>();
                eventProperties.put("InApp Type", spinnerInAPPType.getSelectedItem().toString());
                clevertapDefaultInstance.pushEvent("InAPP Request",eventProperties);
                Toast.makeText(getApplicationContext(), "In APP Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnNPS = (Button) findViewById(R.id.button_nps);
        btnNPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numb = 360065760;
                HashMap<String, Object> eventProperties = new HashMap<String, Object>();
                eventProperties.put("amount",numb);
                clevertapDefaultInstance.pushEvent("test",eventProperties);
            }
        });

        Button btnAppInbox = (Button) findViewById(R.id.button_appInbox);
        btnAppInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clevertapDefaultInstance.pushEvent("Request Inbox Event");
                if (clevertapDefaultInstance != null) {
                    Log.d("Inbox Hans", "Message init");

                    //Set the Notification Inbox Listener
                    clevertapDefaultInstance.setCTNotificationInboxListener(NavigationActivity.this);
                    //Initialize the inbox and wait for callbacks on overridden methods
                    clevertapDefaultInstance.initializeInbox();
                }
            }
        });

        Button btnWebView = (Button) findViewById(R.id.button_webView);
        btnWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Push Permission Hans", String.valueOf(clevertapDefaultInstance.isPushPermissionGranted()));


                JSONObject jsonObject = CTLocalInApp.builder()
                        .setInAppType(CTLocalInApp.InAppType.ALERT)
                        .setTitleText("Get Notified")
                        .setMessageText("Enable Notification permission")
                        .followDeviceOrientation(true)
                        .setPositiveBtnText("Allow")
                        .setNegativeBtnText("Cancel")
                        .setFallbackToSettings(true)
                        .build();
                clevertapDefaultInstance.promptPushPrimer(jsonObject);
                //clevertapDefaultInstance.promptForPushPermission(true);

                //Intent intent = new Intent(NavigationActivity.this, WebViewActivity.class);
                //startActivity(intent);
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void inboxDidInitialize() {
        ArrayList<CTInboxMessage> inboxMessages = CleverTapAPI.getDefaultInstance(this).getAllInboxMessages();
        for (int i = 0; i <inboxMessages.size() ; i++) {
            Log.d("Hans Inbox Show", inboxMessages.get(i).getMessageId().toString());
            if (inboxMessages.get(i).getMessageId().toString().contains("1702532616_")) {
                //Log.d("Hans Inbox Show", "remove the app inbox with message id:" + inboxMessages.get(i).getMessageId().toString());
                //CleverTapAPI.getDefaultInstance(this).deleteInboxMessage(inboxMessages.get(i).getMessageId());
            }
        }
        showAppInbox();
    }

    @Override
    public void inboxMessagesDidUpdate() {

    }

    private void showAppInbox() {
        /*
        ArrayList<String> tabs = new ArrayList<>();
        tabs.add("Promotions");//We support upto 2 tabs only. Additional tabs will be ignored
//        tabs.add("Promotions");
        tabs.add("TestFilterTag");

        CTInboxStyleConfig styleConfig = new CTInboxStyleConfig();
        styleConfig.setFirstTabTitle("TestFilterTag");
        styleConfig.setTabs(tabs);//Do not use this if you don't want to use tabs
        styleConfig.setTabBackgroundColor("#FFFFFF");
        styleConfig.setSelectedTabIndicatorColor("#3498DB");
        styleConfig.setSelectedTabColor("#3498DB");
        styleConfig.setUnselectedTabColor("#808B96");
        styleConfig.setBackButtonColor("#3498DB");
        styleConfig.setNavBarTitleColor("#3498DB");
        styleConfig.setNavBarTitle("MY INBOX");
        styleConfig.setNavBarColor("#FFFFFF");
        styleConfig.setInboxBackgroundColor("#85C1E9");*/
        if (clevertapDefaultInstance != null) {
            //clevertapDefaultInstance.showAppInbox(styleConfig); //With Tabs
            clevertapDefaultInstance.showAppInbox();
        }
    }

    //Native display
    @Override
    public void onDisplayUnitsLoaded(ArrayList<CleverTapDisplayUnit> units) {
        for (int i = 0; i <units.size() ; i++) {
            CleverTapDisplayUnit unit = units.get(i);
            Log.d("ND Hans", unit.getUnitID().toString() +" - "+ unit.getType().toString());
            //raise viewed event
            CleverTapAPI.getDefaultInstance(this).pushDisplayUnitViewedEventForID(unit.getUnitID());
            if(unit.getType().toString().equals("custom-key-value")){
                Log.d("ND Hans", unit.getCustomExtras().toString());
                if(unit.getCustomExtras().containsKey("Action")){
                    if(unit.getCustomExtras().get("Action").toString().equals("Remove App Inbox")){
                        //Store the information of campaing id or do the remove process
                        //Log.d("ND Hans", "Please Remove App Inbox with Campaign " + unit.getCustomExtras().get("Campaign ID").toString());


                    }
                }
            }
        }
    }
}