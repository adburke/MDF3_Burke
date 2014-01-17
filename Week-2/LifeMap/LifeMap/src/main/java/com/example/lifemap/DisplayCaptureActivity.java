/*
 * Project:		LifeMap
 *
 * Package:		LifeMap-LifeMap
 *
 * Author:		aaronburke
 *
 * Date:		 	1 16, 2014
 */

package com.example.lifemap;

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
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class DisplayCaptureActivity extends ActionBarActivity {

    Uri mediaUri;
    ImageView picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_capture);

        mediaUri = null;
        picture = (ImageView) findViewById(R.id.picture);

        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            mediaUri = Uri.parse(incomingData.getString("mediaUri"));
            Log.i("StashViewActivity", "URI: " + mediaUri);
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

}
