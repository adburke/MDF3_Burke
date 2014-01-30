package com.adburke.webcontact;

import android.app.Activity;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Contacts;
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

        WebSettings webSettings = contactWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

    }

    public class ContactAppInterface {
        Context mContext;

        ContactAppInterface(Context context) {
            mContext = context;
        }

        @JavascriptInterface
        public void saveContact(String name, String phone, String email, String address) {
            Intent intent = new Intent(Contacts.Intents.Insert.ACTION);
            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        }

    }

}
