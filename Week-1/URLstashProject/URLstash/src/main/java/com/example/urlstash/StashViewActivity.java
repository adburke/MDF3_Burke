/*
 * Project:		URLstash
 *
 * Package:		URLstash
 *
 * Author:		aaronburke
 *
 * Date:		1, 9, 2014
 *
 * Purpose:     Display stash data in a listview
 */


package com.example.urlstash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StashViewActivity extends Activity {

    FileManager mFile;
    ListView stashList;
    static String mStashFile = "stash_data.txt";
    Context mContext;
    JSONArray stashData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stash_activity);

        mContext = this;
        String readData = null;

        stashList = (ListView) findViewById(R.id.stashList);
        stashList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url;

                    try {
                        url = stashData.getJSONObject(position).getString("url");
                        Intent URLstashActivity = new Intent(mContext, URLstash.class);
                        URLstashActivity.putExtra("url", url);
                        startActivityForResult(URLstashActivity, 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                Log.i("StashViewActivity", "Selected at position: " + position);
            }
        });

        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            readData = incomingData.getString("readData");
            Log.i("StashViewActivity", "READ: " + readData);
        }

        if (readData != null) {
            try {
                JSONObject holder = new JSONObject(readData);
                stashData = holder.getJSONArray("stashData");
                Log.i("StashViewActivity", "stashData: " + stashData.toString());
                onListUpdate(stashData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    public void onListUpdate(JSONArray newData) {

        ArrayList<String> titleList = new ArrayList<String>();
        
        for (int i = 0, j = newData.length(); i < j; i++) {
            try {
                titleList.add(newData.getJSONObject(i).getString("title"));
                StashListAdapter listAdapter = new StashListAdapter(mContext, android.R.layout.simple_list_item_1, android.R.id.text1, titleList);
                stashList.setAdapter(listAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.i("StashViewActivity", "titleList = " + titleList.toString());

    }

    // Extend SimpleAdapter to create zebra coloring on rows
    public class StashListAdapter extends ArrayAdapter {

        public StashListAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Get reference to the row
            View view = super.getView(position, convertView, parent);
            // Check for odd or even to set alternate colors to the row background
            if(position % 2 == 0){
                if (view != null) {
                    view.setBackgroundColor(Color.rgb(238, 233, 233));
                }
            }
            else {
                if (view != null) {
                    view.setBackgroundColor(Color.rgb(255, 255, 255));
                }
            }
            return view;
        }
    }


}
