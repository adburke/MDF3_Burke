/*
 * Project:		LifeMap
 *
 * Package:		LifeMap-LifeMap
 *
 * Author:		aaronburke
 *
 * Purpose:     Had other plans for this object but Android Studio didn't like me this week
 *
 * Date:		 	1 16, 2014
 */

package com.example.lifemap;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by aaronburke on 1/16/14.
 */
public class CapturedEventItem implements ClusterItem, Parcelable {
    public String caption;
    public final Uri fileLocation;
    public final LatLng ePosition;

    public CapturedEventItem(String caption, Uri fileLocation, LatLng ePosition) {
        this.caption = caption;
        this.fileLocation = fileLocation;
        this.ePosition = ePosition;
    }

    public String setCaption(String caption){
        return this.caption = caption;
    }


    @Override

    public LatLng getPosition() {
        return ePosition;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
