/*
 * Project:		LifeMap
 *
 * Package:		LifeMap-LifeMap
 *
 * Author:		aaronburke
 *
 * Purpose:     Main activity that display a google map with markers of captured events
 *
 * Date:		 	1 16, 2014
 */

package com.example.lifemap;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.Marker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainDisplayActivity extends ActionBarActivity implements LocationListener, View.OnClickListener, OnMarkerClickListener  {

    // Google map
    GoogleMap gMap;
    Marker newMarker;

    CapturedEventItem newEvent;
    String caption;
    Boolean readyToSave;

    // Location variables
    Location currentLocation;
    LocationManager locManager;
    LatLng currentLatLng;

    Context mContext;

    // Keep up with initial zoom of the map
    // Prevents it from firing after the first run
    Boolean appHasZoomed;

    // UI variables
    Button captureBtn;

    // Image capture variables
    private static final int CAPTURE_IMAGE_REQ_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    // Example use for video for future ref
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int FILE_SAVE_REQ_CODE = 200;
    Uri mediaUri;
    Uri savedUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        Boolean status = ConnectionStatus.getNetworkStatus(mContext);
        if (status) {
            Log.i("MainDisplayActivity", "Connection Found!");
            Toast toast = Toast.makeText(mContext, "Internet Connection established!", 15);
            toast.show();

            appHasZoomed = false;

            readyToSave = false;

            captureBtn = (Button) findViewById(R.id.captureBtn);
            captureBtn.setOnClickListener(this);

            // Configure the Google Map
            gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            gMap.setMyLocationEnabled(true);
            gMap.setOnMarkerClickListener(this);

            Bundle incomingData = getIntent().getExtras();
            if (incomingData != null) {
                if (incomingData.getBoolean("saved")) {
                    Log.i("MainDisplayActivity", "File saved add marker.");
                    caption = (incomingData.getString("comment"));
                    savedUri = Uri.parse(incomingData.getString("uri"));
                    readyToSave = true;
                } else if (!incomingData.getBoolean("saved")) {
                    Log.i("MainDisplayActivity", "File deleted.");
                }
            }

            // Initialize a Location Manager
            locManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Create just a default criteria object
            Criteria criteria = new Criteria();

            // Check for LocationManager validity and start listening for updates
            if (locManager == null) {
                Log.i("MainDisplayActivity", "Location Manager is not available.");
            } else {
                List<String> providers = locManager.getAllProviders();
                Log.i("MainDisplayActivity", "Providers: " + providers.toString());
                String provider = locManager.getBestProvider(criteria, true);
                // Poll at a rate of 15s and 0m moved in distance
                // Would have to think about this per application for battery and how often
                // location updates would be required
                locManager.requestLocationUpdates(provider, 15*1000, 0, this);
            }

        } else {
            Log.i("MainDisplayActivity", "NO Connection Found!");
            Toast toast = Toast.makeText(mContext, "NO Internet Connection found. The app requires it!", 15);
            toast.show();
            captureBtn.setEnabled(false);
        }




    }


    @Override
    public void onLocationChanged(Location location) {
        Log.i("MainDisplayActivity", "Lat: " + location.getLatitude() + " -- Long: " + location.getLongitude());
        // Update my current loc variable when onLocationChanged is called.
        // Dirty check to see if location is valid would need a more thorough check
        // because location could become stale

        if (location != null) {
            currentLocation = location;
            if (readyToSave) {
                currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                createNewMarker();
            }
            if (!appHasZoomed) {
                currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
            }
        }
        appHasZoomed = true;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onClick(View v) {
        if (v.equals(captureBtn)) {
            Log.i("MainDisplayActivity", "Capture btn clicked.");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mediaUri = getOutputFileUri(MEDIA_TYPE_IMAGE);
            if (mediaUri != null) {
                Log.i("MainDisplayActivity", "Picture Location: " + mediaUri.toString() );
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
                startActivityForResult(intent, CAPTURE_IMAGE_REQ_CODE);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                Log.i("MainDisplayActivity", "Captured Picture going to display activity");
                //createNewMarker();
                Intent stashViewActivity = new Intent(mContext, DisplayCaptureActivity.class);
                stashViewActivity.putExtra("mediaUri", mediaUri.toString());
                startActivityForResult(stashViewActivity, FILE_SAVE_REQ_CODE);
            }
        }
    }

    // Display the marker picture
    @Override
    public boolean onMarkerClick(final Marker marker) {
        // TODO Auto-generated method stub
        Log.i("MainDisplayActivity", "Marker Selected");
        if (marker.equals(newMarker))
        {
            Intent stashViewActivity = new Intent(mContext, DisplayCaptureActivity.class);
            stashViewActivity.putExtra("mediaUri", newEvent.fileLocation.toString());
            stashViewActivity.putExtra("display", true);
            stashViewActivity.putExtra("caption", newEvent.caption);
            startActivityForResult(stashViewActivity, 0);
        }

        return true;
    }

    /* MY METHODS */
    /*------------*/

    // Create a new marker and create a new object
    public void createNewMarker() {
        newEvent = new CapturedEventItem(caption, savedUri, currentLatLng);
        newMarker =  gMap.addMarker(new MarkerOptions().position(newEvent.ePosition).title(newEvent.caption));
    }

    // Return a file URI from the created output file name and location
    private static Uri getOutputFileUri(int type) {
        File mediaFile = createOutputFile(type);
        if (mediaFile != null) {
            return Uri.fromFile(mediaFile);
        } else {
            return null;
        }
    }

    // Create a File for saving based on file type passed in
    private static File createOutputFile(int type) {

        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "lifemap");

        // Create the storage directory if it does not exist
        if (! storageDir.exists()) {
            if (! storageDir.mkdirs()) {
                Log.i("lifemap", "Failed in creating directory");
                // End method and return null
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(storageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(storageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}

