package hans.clevertap.firstapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.clevertap.android.sdk.CTWebInterface;
import com.clevertap.android.sdk.CleverTapAPI;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Intent intent = getIntent();
        String value = intent.getStringExtra("webpage");
        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        WebView webview1= (WebView) findViewById(R.id.IDWebView);
        webview1.getSettings().setJavaScriptEnabled(true);

        /*
        webview1.setWebChromeClient(new WebChromeClient());
        webview1.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.v("WebView log", consoleMessage.message());
                return true;
            }
        });*/

        webview1.addJavascriptInterface(new CTWebInterface(clevertapDefaultInstance.getDefaultInstance(this)),"CleverTapWebView");
        //webview1.loadUrl("https://clevertap.com/");
        webview1.loadUrl("http://192.168.1.103:8080/eatigoInApp.html");
        /*
        if (value.equals("paxel1")){
            webview1.loadUrl("https://hanstang.github.io/ct_paxel_demo1/");
        }
        else if (value.equals("paxel2")) {
            webview1.loadUrl("https://hansclevertap.github.io/ct_paxel_demo2/");
        }
        else if (value.equals("paxel3")){
                webview1.loadUrl("https://amitclevertap.github.io/ct_paxel_demo3/");
        }
        else if (value.equals("paxel4")) {
            webview1.loadUrl("https://hanshotmail.github.io/ct_paxel_demo4/");
        }
        else{
            webview1.loadUrl("https://clevertap.com/");
        }*/

    }
}