package com.adburke.webcontact;

import android.app.Activity;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends Activity {

    WebView contactWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        contactWebView = (WebView) findViewById(R.id.contactWebView);
        contactWebView.loadUrl("file:///android_asset/form.html");
        contactWebView.setBackgroundColor(0x00000000);
        // Enable JavaScript
        WebSettings webSettings = contactWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // Binds the interface class to the javascript running in the WebView
        contactWebView.addJavascriptInterface(new ContactAppInterface(this), "Android");

    }

    public class ContactAppInterface {
        Context mContext;

        ContactAppInterface(Context context) {
            mContext = context;
        }

        @JavascriptInterface
        public void saveContact(String name, String phone, String email, String address) {
            Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

            intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email);
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
            intent.putExtra(ContactsContract.Intents.Insert.POSTAL, address);

            startActivity(intent);
        }

    }

}
