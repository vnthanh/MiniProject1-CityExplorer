package com.example.user.cityexplorer;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by USER on 4/24/2016.
 */
public class Place {
    LatLng postion;
    String name;
    String phone;
    String website;
    String description;

    public Place(LatLng Position,String Name, String Phone, String Website, String Des){
        postion = Position;
        name = Name;
        phone = Phone;
        website = Website;
        description = Des;
    }
}
