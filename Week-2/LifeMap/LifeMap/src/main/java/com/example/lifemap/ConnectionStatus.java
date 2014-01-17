/*
 * Project:		LifeMap
 *
 * Package:		LifeMap-LifeMap
 *
 * Author:		aaronburke
 *
 * Purpose:     Provide connection status to app
 *
 * Date:		 	1 16, 2014
 */

package com.example.lifemap;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by aaronburke on 1/16/14.
 */
public class ConnectionStatus {
    public static Boolean getNetworkStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get network info object
        NetworkInfo currentNet = cm.getActiveNetworkInfo();
        Boolean status = false;
        // Check the network info object for connection
        if(currentNet != null) {
            if(currentNet.isConnectedOrConnecting()) {
                status = true;
            }
        }
        return status;
    }

    public static String getNetworkStatusType(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get network info object
        NetworkInfo currentNet = cm.getActiveNetworkInfo();
        String connType = null;
        if(currentNet != null) {
            if (currentNet.getType() == ConnectivityManager.TYPE_WIFI) {
                connType = "Wifi enabled";
            } else if (currentNet.getType() == ConnectivityManager.TYPE_MOBILE) {
                connType = "Mobile data enabled";
            }
        }
        return connType;
    }
}
