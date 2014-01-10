package com.example.implicitexecute;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends ActionBarActivity {

    private Button launcherBtn;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        launcherBtn = (Button) findViewById(R.id.launcherBtn);
        launcherBtn.setEnabled(false);
        launcherBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                String urlStr = "http://" + editText.getText().toString();
                if (URLUtil.isValidUrl(urlStr)) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(urlStr));
                    startActivity(i);
                }



            }
        });

        editText = (EditText) findViewById(R.id.urlEditText);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                launcherBtn.setEnabled(true);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(TextUtils.isEmpty(s)) {
                    // call method
                }
            }
        });
    }

}
