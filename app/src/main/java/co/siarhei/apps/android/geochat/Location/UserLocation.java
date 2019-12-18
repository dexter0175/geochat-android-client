/*
 * LocationWrapper
 * D:/course/geochat-client-master/app/src/main/java/co/siarhei/apps/android/geochat/Location/UserLocation.java
 *
 * Project: geochat-client-master
 * Module: app
 * Last Modified: 18.12.19 13:43 <dexte>
 * Last Compilation: 18.12.19 13:43
 *
 * Copyright (c) 2019. Martin David Shaw. All rights reserved.
 */

package co.siarhei.apps.android.geochat.Location;

import android.location.Location;

public class UserLocation {


    double lat;
    double lng;



    public UserLocation(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public UserLocation() {

    }

    public UserLocation(Location location) {
        this.lat = location.getLatitude();
        this.lng = location.getLongitude();
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
