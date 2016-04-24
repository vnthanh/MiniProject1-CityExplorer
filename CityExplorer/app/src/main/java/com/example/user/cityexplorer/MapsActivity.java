package com.example.user.cityexplorer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.datatype.Duration;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    //ArrayList<LatLng> Places = new ArrayList<>();
    ArrayList<com.example.user.cityexplorer.Place> PlacesFromFile = new ArrayList<>();

    EditText et_origin,et_dest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LoadPlacesFromFile(); // Load from file and store in ArrayList<Place>

        et_origin = (EditText) findViewById(R.id.et_originPos);
        et_dest = (EditText) findViewById(R.id.et_destPos);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add marker for all places loaded from file
        for(int i=0;i<PlacesFromFile.size();i++)
        {
            mMap.addMarker(new MarkerOptions().position(PlacesFromFile.get(i).postion).title("Place:"+ i));
        }

        CameraPosition camPos = new CameraPosition(PlacesFromFile.get(0).postion,15,90,30);
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                String text;
                /*String markerPos = marker.getPosition().toString();*/

                text = marker.getTitle().toString();

                Intent intent = new Intent(MapsActivity.this,ScrollingActivity.class);
                intent.putExtra("text",text);
                startActivity(intent);

                return true;
            }
        });
    }

    public void bt_findDirection_Clicked(View view){
       // TODO:
    }


    public void LoadPlacesFromFile()
    {
        com.example.user.cityexplorer.Place tempPlace;
        LatLng tempPosition;
        double tempLat, tempLng; // Lat and Lng
        String tempName;
        String tempPhone;
        String tempWebsite;
        String tempDes;

        Scanner scan  = new Scanner(getResources().openRawResource(R.raw.places));

        // Be carefull , scanner type: double, int, -> mismatch bug
        int nPlaces = scan.nextInt();

        for(int i=0;i<nPlaces;i++)
        {
            tempLat = scan.nextDouble();
            tempLng = scan.nextDouble();
            tempPosition = new LatLng(tempLat,tempLng);
            scan.nextLine();
            tempName = scan.nextLine();
            tempPhone = scan.nextLine();
            tempWebsite = scan.nextLine();
            tempDes = scan.nextLine();

            tempPlace = new com.example.user.cityexplorer.Place(tempPosition,tempName,tempPhone,tempWebsite,tempDes);
            PlacesFromFile.add(tempPlace);
        }

        scan.close();
    }
}
