package hans.clevertap.firstapplication;

import androidx.appcompat.app.AppCompatActivity;
import com.clevertap.android.sdk.inbox.CTInboxActivity;
import com.clevertap.android.sdk.CTInboxListener;
import com.clevertap.android.sdk.CTInboxStyleConfig;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.CleverTapInstanceConfig;
import com.clevertap.android.sdk.inbox.CTInboxMessage;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class InboxActivity extends AppCompatActivity implements CTInboxListener{
    CleverTapAPI cleverTapDefaultInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(this);
        if (cleverTapDefaultInstance != null) {
            Log.d("Inbox Hans", "Message init");
            //Set the Notification Inbox Listener
            cleverTapDefaultInstance.setCTNotificationInboxListener(this);
            //Initialize the inbox and wait for callbacks on overridden methods
            cleverTapDefaultInstance.initializeInbox();
        }

        //Toast.makeText(getApplicationContext(),cleverTapDefaultInstance.getAllInboxMessages().toString(),Toast.LENGTH_LONG).show();
        //cleverTapDefaultInstance.getInboxMessageForId(messageId);
        //String currentUser = cleverTapDefaultInstance.getCleverTapID().toString();
        //Toast.makeText(getApplicationContext(),currentUser,Toast.LENGTH_LONG).show();
    }

    /**
     * Receives a callback when inbox controller is initialized
     */
    @Override
    public void inboxDidInitialize() {
        showAppInbox();
        //cleverTapDefaultInstance.showAppInbox();
        Log.d("Inbox Hans", "All Message: " + cleverTapDefaultInstance.getInboxMessageCount());
        Log.d("Inbox Hans", "Unread count: " + cleverTapDefaultInstance.getInboxMessageUnreadCount());
        /*
        ArrayList<CTInboxMessage> ctInboxMessageArrayList = cleverTapDefaultInstance.getAllInboxMessages();
        if (ctInboxMessageArrayList != null && !ctInboxMessageArrayList.isEmpty()) {
            Log.d("Inbox Hans", "Message exist");
            for (CTInboxMessage ctInboxMessage: ctInboxMessageArrayList ) {
                Log.d("Inbox Hans", "Message ID : " + ctInboxMessage.getMessageId());
                //cleverTapDefaultInstance.deleteInboxMessage(ctInboxMessage.getMessageId());
            }
        }*/
    }

    /**
     * Receives a callback when inbox controller updates/deletes/marks as read any {@link CTInboxMessage} object
     */
    @Override
    public void inboxMessagesDidUpdate() {
        Log.d("Inbox Hans","inboxMessagesDidUpdate class");
    }

    private void showAppInbox() {
        ArrayList<String> tabs = new ArrayList<>();
        tabs.add("Promotions");//We support upto 2 tabs only. Additional tabs will be ignored
//        tabs.add("Promotions");

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
        styleConfig.setInboxBackgroundColor("#85C1E9");
        if (cleverTapDefaultInstance != null) {
            cleverTapDefaultInstance.showAppInbox(styleConfig); //With Tabs
        }
    }
}