/*
 * Project:		LifeMap
 *
 * Package:		LifeMap-LifeMap
 *
 * Author:		aaronburke
 *
 * Purpose:     Display activity that shows the captured event
 *
 * Date:		 	1 16, 2014
 */

package com.example.lifemap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class DisplayCaptureActivity extends ActionBarActivity implements View.OnClickListener {

    Uri mediaUri;
    ImageView picture;
    EditText commentText;
    Button saveBtn;
    Button deleteBtn;
    Context mContext;
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_capture);

        mContext = this;

        mediaUri = null;
        commentText = (EditText) findViewById(R.id.editText);
        picture = (ImageView) findViewById(R.id.picture);
        saveBtn = (Button) findViewById(R.id.saveBtn);
        deleteBtn = (Button) findViewById(R.id.deleteBtn);
        mTextView = (TextView) findViewById(R.id.textView);

        saveBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);

        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            mediaUri = Uri.parse(incomingData.getString("mediaUri"));
            Log.i("StashViewActivity", "URI: " + mediaUri);

            // Use a display data mode if viewing a picture
            if (incomingData.getBoolean("display")) {
                saveBtn.setEnabled(false);
                deleteBtn.setEnabled(false);
                commentText.setText(incomingData.getString("caption"));
                mTextView.setVisibility(View.GONE);
                commentText.setFocusable(false);
            }
        }

        if (mediaUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mediaUri);
                picture.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onClick(View v) {
        if (v.equals(saveBtn)) {
            Intent MainDisplayActivity = new Intent(mContext, MainDisplayActivity.class);
            if (!commentText.getText().toString().equals("")) {
                MainDisplayActivity.putExtra("comment", commentText.getText().toString());
            } else {
                MainDisplayActivity.putExtra("comment", "");
            }
            MainDisplayActivity.putExtra("uri", mediaUri.toString());
            MainDisplayActivity.putExtra("saved", true);
            startActivityForResult(MainDisplayActivity, 0);
        } else if (v.equals(deleteBtn)) {
            Intent MainDisplayActivity = new Intent(mContext, MainDisplayActivity.class);
            MainDisplayActivity.putExtra("saved", false);
            getContentResolver().delete(mediaUri, null, null);
            startActivityForResult(MainDisplayActivity, 0);
        }
    }
}
