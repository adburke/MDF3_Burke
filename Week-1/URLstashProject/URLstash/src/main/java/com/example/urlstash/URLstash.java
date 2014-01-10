/*
 * Project:		URLstash
 *
 * Package:		URLstash
 *
 * Author:		aaronburke
 *
 * Date:		1, 8, 2014
 *
 * Purpose: Displaying WebView of current URL and allowing to save(bookmark) it in your Stash
 */

package com.example.urlstash;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


public class URLstash extends Activity {

    Context mContext;

    // UI variables
    private CustomWebView mainWebView;
    private URL incomingUrl;
    private EditText urlEditText;

    // File manager variables
    FileManager mFile;
    Boolean writeStatus;
    static String mStashFile = "stash_data.txt";

    private String pageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        // Set incoming url to null for checking valid data
        incomingUrl = null;
        String stashUrl = null; // incoming from StashViewActivity

        // Get ref to editText
        urlEditText = (EditText) findViewById(R.id.urlEditText);

        // Create and configure Custom WebView to provide swipe capabilities
        LinearLayout layout = (LinearLayout) findViewById(R.id.container);
        mainWebView = new CustomWebView(this);
        mainWebView.getSettings().setBuiltInZoomControls(true); // Forced zoom control to super
        layout.addView(mainWebView);
        WebSettings webSettings = mainWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mainWebView.setWebViewClient(new WebViewClient() {
            // Listen for webview page changes and update the edit text to show the correct url
            public void onPageFinished(WebView view, String url) {
                String formatedUrl = url.replaceAll("(http://|https://)","");
                urlEditText.setText(formatedUrl);
                pageTitle = view.getTitle();
            }
        });

        // Get the intent that started this activity
        Intent intent = getIntent();

        // Pull out the URL data from the intent
        Uri data = intent.getData();
        try {
            if (data != null) {
                incomingUrl = new URL(data.getScheme(), data.getHost(), data.getPath());
                // Load the url in the WebView and set the EditText to display the url
                 mainWebView.loadUrl(incomingUrl.toString());

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            stashUrl = incomingData.getString("url");
            Log.i("URLstash Activity", "url: " + stashUrl);
            mainWebView.loadUrl(stashUrl);
        }

        // Wire up functionality to all of the buttons
        Button viewStashBtn = (Button) findViewById(R.id.viewStashBtn);
        viewStashBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String readData = null;
                // Perform action on click
                Intent stashViewActivity = new Intent(mContext, StashViewActivity.class);
                File file = mContext.getFileStreamPath(mStashFile);
                if (file.exists()) {

                    Log.i("FILE", "DOES EXIST");
                    readData = mFile.readFile(mContext, mStashFile);
                    if (readData != null) {
                        stashViewActivity.putExtra("readData", readData);

                    }
                } else {
                    Log.i("FILE", "DOES NOT EXIST");
                }
                startActivityForResult(stashViewActivity, 0);
            }
        });
        Button addStashBtn = (Button) findViewById(R.id.addStashBtn);
        addStashBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                mFile = FileManager.getMinstance();
                if (urlEditText.getText().toString() != "") {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Save this page to your stash?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    File file = mContext.getFileStreamPath(mStashFile);
                                    if (file.exists()) {
                                        Log.i("FILE", "DOES EXIST");
                                        String readData = mFile.readFile(mContext, mStashFile);
                                        JSONObject appendJson = appendToJson(pageTitle, "http://" + urlEditText.getText().toString(),readData);
                                        writeStatus = mFile.writeFile(mContext, mStashFile, appendJson.toString());
                                    } else {
                                        Log.i("FILE", "DOES NOT EXIST");
                                        JSONObject newJson = createNewJson(pageTitle, "http://" + urlEditText.getText().toString());
                                        writeStatus = mFile.writeFile(mContext, mStashFile, newJson.toString());
                                    }
                                }
                            })
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                    // Create the AlertDialog object and return it
                    builder.show();

                }
            }
        });
        Button goBtn = (Button) findViewById(R.id.goBtn);
        goBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                mainWebView.loadUrl("http://" + urlEditText.getText().toString());
            }
        });

    }

    // Create starting json if file does not exist
    public JSONObject createNewJson(String title, String url) {
        JSONObject holder = new JSONObject();
        JSONArray stashData = new JSONArray();
        JSONObject urlData = new JSONObject();


        try {
            urlData.put("title", title);
            urlData.put("url", url);
            stashData.put(urlData);
            holder.put("stashData",stashData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("NEW JSON OBJECT", holder.toString());
        return holder;
    }
    // Append to existing json
    public JSONObject appendToJson(String title, String url, String object){
        JSONObject holder = new JSONObject();
        JSONObject urlData = new JSONObject();

        try {
            JSONObject existingObject = new JSONObject(object);
            JSONArray stashData = existingObject.getJSONArray("stashData");
            urlData.put("title", title);
            urlData.put("url", url);
            stashData.put(urlData);
            holder.put("stashData",stashData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("APPENDED JSON OBJECT", holder.toString());
        return holder;
    }

    class CustomWebView extends WebView {
        Context context;
        GestureDetector gestureDetect;

        // Keep up with fling state to check if we need to override or use the webview touch event
        private boolean swipped;

        // Some static variables to calculate gestures in onFling
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        public CustomWebView(Context context) {
            super(context);

            this.context = context;
            gestureDetect = new GestureDetector(context, gestureListener);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            gestureDetect.onTouchEvent(event);
            if (swipped) {
                swipped = false;
                return true;
            } else {
                return super.onTouchEvent(event);
            }
        }

        GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
            public boolean onDown(MotionEvent event) {
                return true;
            }

            public boolean onFling(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
                // Catch other gesture motions and let the webview handle them
                if (Math.abs(event1.getY() - event1.getY()) > SWIPE_MAX_OFF_PATH) {
                    return false;
                }
                // Catch the left and right swipes so we can override the onTouchEvent with our custom GestureDetector
                if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(distanceX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (mainWebView.canGoForward()) {
                        mainWebView.goForward();
                    }
                    Log.i("Swiped","swipe left");
                    swipped = true;
                } else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE && Math.abs(distanceX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (mainWebView.canGoBack()) {
                        mainWebView.goBack();
                    }
                    Log.i("Swiped","swipe right");
                    swipped = true;
                }
                return true;
            }

        };

    }

}
