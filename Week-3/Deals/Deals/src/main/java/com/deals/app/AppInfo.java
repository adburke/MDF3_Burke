/*
 * Project:		Deals
 *
 * Package:		Deals-Deals
 *
 * Author:		aaronburke
 *
 * Purpose:     Application Info Page
 *
 * Date:		 	1 23, 2014
 */

package com.deals.app;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;


public class AppInfo extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_info);

        ActionBar actionBar = getActionBar();
        // Creates back action on icon
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


    }
}
