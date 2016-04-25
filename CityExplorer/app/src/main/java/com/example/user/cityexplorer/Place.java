package com.example.user.cityexplorer;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by USER on 4/24/2016.
 */
public class Place {
    // currently public all !!!!!!!!
    LatLng postion;
    String name;
    String phone;
    String website;
    String description;
    boolean isBookmark;

    public Place(LatLng Position,String Name, String Phone, String Website, String Des,boolean isBMark){
        postion = Position;
        name = Name;
        phone = Phone;
        website = Website;
        description = Des;
        isBookmark = isBMark;
    }
}
