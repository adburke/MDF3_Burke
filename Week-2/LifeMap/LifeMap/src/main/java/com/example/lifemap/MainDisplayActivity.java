package com.example.lifemap;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;


import java.util.List;


public class MainDisplayActivity extends ActionBarActivity implements LocationListener {

    GoogleMap gMap;
    Location currentLocation;
    LocationManager locManager;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        gMap.setMyLocationEnabled(true);

        // Initialize a Location Manager
        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Create just a default criteria object
        Criteria criteria = new Criteria();

        if (locManager == null) {
            Log.i("MainDisplayActivity", "Location Manager is not available.");
        } else {
            List<String> providers = locManager.getAllProviders();
            Log.i("MainDisplayActivity", "Providers: " + providers.toString());
            String provider = locManager.getBestProvider(criteria, true);
            // Poll at a rate of 15s and 0m moved in distance
            // Would have to think about this per application for battery and how often
            //    location updates would be required
            locManager.requestLocationUpdates(provider, 15*1000, 0, this);
        }

        
        //Log.i("MainDisplayActivity", "Location: " );


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.i("MainDisplayActivity", "Lat: " + location.getLatitude() + " -- Long: " + location.getLongitude());
        // Update my current loc variable when onLocationChanged is called.
        currentLocation = location;
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
}
