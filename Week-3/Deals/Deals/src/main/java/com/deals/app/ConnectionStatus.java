/*
 * Project:		Deals
 *
 * Package:		Deals-Deals
 *
 * Author:		aaronburke
 *
 * Date:		 	1 21, 2014
 */

package com.deals.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by aaronburke on 12/5/13.
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


