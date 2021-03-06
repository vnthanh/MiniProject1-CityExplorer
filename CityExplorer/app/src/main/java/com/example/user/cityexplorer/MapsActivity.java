package com.example.user.cityexplorer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
    ArrayList<com.example.user.cityexplorer.Place> PlacesFromFile;
    EditText et_SearchKeyWord;

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    Button bt_PlaceInfo; // Only display when marker is clicked
    String markerClickedTitle; // To know which marker is click, then press button for info

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        AssetManager am = this.getAssets();
        // first time launch: check if there is a file in getFileDir, if not, open in assets
        // Dummy code for BookMark Act and ScrollingAct only
        try {
            PlacesFromFile = FileManager.LoadPlacesFromFile(openFileInput("places.txt")); // Load from file and store in ArrayList<Place>
        } catch (IOException e) {
            e.printStackTrace();
            try {
                PlacesFromFile = FileManager.LoadPlacesFromFile(am.open("places.txt")); // if not found (not create) in fileDir, go on this file
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        et_SearchKeyWord = (EditText) findViewById(R.id.et_SearchKeyWord);
        //et_dest = (EditText) findViewById(R.id.et_destPos);


        mDrawerList = (ListView) findViewById(R.id.navList);
        addDrawerItems();

        bt_PlaceInfo = (Button) findViewById(R.id.bt_PlaceInfo);
    }

    // Grab result sen back from PlacesActivity
    private static final  int REQ_CODE = 123; //============================== CODE

    private void addDrawerItems() {
        String[] NavArray = {"Places", "Bookmark", "Your places"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, NavArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Go to (start) PlaceActivity
                if (position == 0) {
                    Intent intent = new Intent(MapsActivity.this, PlacesActivity.class);
                    startActivityForResult(intent,REQ_CODE);
                }
                // Go to (start) BookmarkActivity
                if(position == 1){
                    Intent intent = new Intent(MapsActivity.this, BookmarkActivity.class);
                    startActivityForResult(intent,REQ_CODE);
                }
                // Go to (start) AddPlacesActivity
                if(position == 2){
                    Intent intent = new Intent(MapsActivity.this, AddPlacesActivity.class);
                    startActivityForResult(intent,REQ_CODE);
                }

            }
        });
    }
    // Grab back result from PlacesAcvtivity and BookmarkActivity
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Fix add place bug, not draw marker
        // Close drawer (left-side drawer -> close visually)
        DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
        dl.closeDrawers();

        if(intent == null) return; // do nothing, in case: in second act, press "hard back"

        if (requestCode == REQ_CODE) {
            // came back from SecondActivity
            int placeId = intent.getIntExtra("buttonClickedId",-1);
            // got back data (place id, button id -> reset map to display that place)
            CameraPosition camPos = new CameraPosition(PlacesFromFile.get(placeId).postion, 15, 50, 30);
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add marker for all places loaded from file
        for (int i = 0; i < PlacesFromFile.size(); i++) {
            mMap.addMarker(new MarkerOptions().position(PlacesFromFile.get(i).postion).title(PlacesFromFile.get(i).name));
        }

        CameraPosition camPos = new CameraPosition(PlacesFromFile.get(0).postion, 15, 90, 30);
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.getTitle().equals("You are here")) return false; // handle myLocation marker, do nothing

                /*String name = marker.getTitle().toString();
                Intent intent = new Intent(MapsActivity.this, ScrollingActivity.class);
                for (int i = 0; i < PlacesFromFile.size(); i++) {
                    if (name.equals(PlacesFromFile.get(i).name)) {
                        intent.putExtra("name", PlacesFromFile.get(i).name);
                        intent.putExtra("phone", PlacesFromFile.get(i).phone);
                        intent.putExtra("website", PlacesFromFile.get(i).website);
                        intent.putExtra("description", PlacesFromFile.get(i).description);
                    }
                }
                startActivity(intent);
                return true;*/
                marker.showInfoWindow(); // Show marker title
                bt_PlaceInfo.setVisibility(View.VISIBLE);
                bt_PlaceInfo.setText("More info : " + marker.getTitle());

                markerClickedTitle = marker.getTitle().toString();
                return true;
            }
        });



        // Handle to show Place Info Button, hide when clicking map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                bt_PlaceInfo.setVisibility(View.GONE);
            }
        });

        // Devices's GPS
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick(){
                // gps not activated exception
                try{
                    LatLng ll = new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude());

                    mMap.addMarker(new MarkerOptions()
                            .position(ll)
                            .title("You are here")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.customicon)));

                    CameraPosition camPos = new CameraPosition(ll, 15, 90, 30);
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));

                }catch (Exception e)
                {
                    Toast.makeText(MapsActivity.this, "GPS error(CityExplorer)", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }



    public void bt_Search_Clicked(View view) {
        // If place name found -> animate camera to that place
        String keyword = et_SearchKeyWord.getText().toString();
        for(int i=0;i<PlacesFromFile.size();i++)
        {
            if(keyword.equals(PlacesFromFile.get(i).name)){
                CameraPosition camPos = new CameraPosition(PlacesFromFile.get(i).postion, 15, 90, 30);
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));

                // Play sound if found
                MediaPlayer mp = MediaPlayer.create(this, R.raw.sound);
                mp.start();
                return;
            }
        }
        // not found
        Toast.makeText(MapsActivity.this, "Places not found!", Toast.LENGTH_SHORT).show();
    }

    /*public void LoadPlacesFromFile(InputStream inputStream) {

        com.example.user.cityexplorer.Place tempPlace;
        LatLng tempPosition;
        double tempLat, tempLng; // Lat and Lng
        String tempName;
        String tempPhone;
        String tempWebsite;
        String tempDes;
        boolean tempBookmark;
        // input Stream from assets
        Scanner scan = new Scanner(inputStream);

        // Be carefull , scanner type: double, int, -> mismatch bug

        *//*String n = scan.nextLine();
        int nPlaces = Integer.parseInt(n);*//*
        int nPlaces = scan.nextInt();

        for (int i = 0; i < nPlaces; i++) {
            tempLat = scan.nextDouble();
            tempLng = scan.nextDouble();
            tempPosition = new LatLng(tempLat, tempLng);
            scan.nextLine();
            tempName = scan.nextLine();
            tempPhone = scan.nextLine();
            tempWebsite = scan.nextLine();
            tempDes = scan.nextLine();
            tempBookmark = scan.nextBoolean();

            tempPlace = new com.example.user.cityexplorer.Place(tempPosition, tempName, tempPhone, tempWebsite, tempDes,tempBookmark);
            PlacesFromFile.add(tempPlace);
        }

        scan.close();
    }*/

    public void bt_PlaceInfo_Clicked(View view) {
       //if(markerClicke dTitle.equals("You are here")) return false; // handle myLocation marker, do nothing

        String name = markerClickedTitle;
        Intent intent = new Intent(MapsActivity.this, ScrollingActivity.class);


        for (int i = 0; i < PlacesFromFile.size(); i++) {
            if (name.equals(PlacesFromFile.get(i).name)) {
                intent.putExtra("name", PlacesFromFile.get(i).name);
                intent.putExtra("phone", PlacesFromFile.get(i).phone);
                intent.putExtra("website", PlacesFromFile.get(i).website);
                intent.putExtra("description", PlacesFromFile.get(i).description);
            }
        }

        startActivity(intent);

    }

    /*@Override
    protected void onRestart() {
        super.onRestart();

        // Reload again, to update when new place added
        AssetManager am = this.getAssets();
        // first time launch: check if there is a file in getFileDir, if not, open in assets
        // Dummy code for BookMark Act and ScrollingAct only
        try {
            PlacesFromFile = FileManager.LoadPlacesFromFile(openFileInput("places.txt")); // Load from file and store in ArrayList<Place>
        } catch (IOException e) {
            //e.printStackTrace();
            try {
                PlacesFromFile = FileManager.LoadPlacesFromFile(am.open("places.txt")); // if not found (not create) in fileDir, go on this file
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }*/
}
