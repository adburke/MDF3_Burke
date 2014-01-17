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

import android.net.Uri;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by aaronburke on 1/16/14.
 */
public class CapturedEventItem implements ClusterItem {
    public final String caption;
    public final Uri fileLocation;
    protected final LatLng ePosition;

    public CapturedEventItem(String caption, Uri fileLocation, LatLng ePosition) {
        this.caption = caption;
        this.fileLocation = fileLocation;
        this.ePosition = ePosition;
    }

    @Override
    public LatLng getPosition() {
        return ePosition;
    }

}
