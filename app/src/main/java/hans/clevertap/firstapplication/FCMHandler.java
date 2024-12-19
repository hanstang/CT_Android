package hans.clevertap.firstapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.pushnotification.NotificationInfo;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.clevertap.android.sdk.pushnotification.fcm.CTFcmMessageHandler;


import java.util.Map;

public class FCMHandler extends FirebaseMessagingService {
    final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onMessageReceived(RemoteMessage message) {
        //Toast.makeText(getApplicationContext(), "Push Notif", Toast.LENGTH_LONG).show();
        try {
            if (message.getData().size() > 0) {
                Bundle extras = new Bundle();
                for (Map.Entry<String, String> entry : message.getData().entrySet()) {
                    extras.putString(entry.getKey(), entry.getValue());
                    //Log.d("Payload FCM Hans", entry.getKey() + " : " + entry.getValue());
                }
                Log.d("Payload FCM Hans", message.getData().toString());
                Log.d("Payload FCM Hans", message.getData().get("wzrk_cid"));
                Log.d("Payload FCM Hans", message.getData().get("wzrk_ttl"));
                NotificationInfo info = CleverTapAPI.getNotificationInfo(extras);

                /*
                mHandler.post(new Runnable(){
                    @Override
                    public void run() {
                        //Toast.makeText(getApplicationContext(), "Push Notif", Toast.LENGTH_LONG).show();
                        //Toast.makeText(getApplicationContext(), message.getData().get("nm"), Toast.LENGTH_LONG).show();
                    }
                });*/




                if (info.fromCleverTap) {
                    //creating the push notif
                    if (message.getData().get("wzrk_cid").toString().equals("Testing")) {

                        //CleverTapAPI.createNotification(getApplicationContext(), extras);
                        //new CTFcmMessageHandler().createNotification(getApplicationContext(), message);

                        new CTFcmMessageHandler().createNotification(getApplicationContext(), message);
                        CleverTapAPI.processPushNotification(getApplicationContext(),extras);

                    }
                    else{
                        String GROUP_KEY_WORK_EMAIL = "Testing2";
                        Notification newMessageNotification = new NotificationCompat.Builder(this, "Testing2")
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentTitle(message.getData().get("nt"))
                                .setContentText(message.getData().get("nm"))
                                .setGroup(GROUP_KEY_WORK_EMAIL)
                                .build();

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                        notificationManager.notify( 1, newMessageNotification);

                    }

                    //send push impression to CT
                    //CleverTapAPI.getDefaultInstance(this).pushNotificationViewedEvent(extras);

                } else {
                    // not from CleverTap handle yourself or pass to another provider
                }
            }
        } catch (Throwable t) {
            Log.d("MYFCMLIST", "Error parsing FCM message", t);
        }
    }
}
