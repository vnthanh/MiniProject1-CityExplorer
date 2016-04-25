package com.example.user.cityexplorer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
    ArrayList<com.example.user.cityexplorer.Place> PlacesFromFile = new ArrayList<>();
    EditText et_SearchKeyWord;

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        AssetManager am = this.getAssets();
        try {
            LoadPlacesFromFile(am.open("places.txt")); // Load from file and store in ArrayList<Place>
        } catch (IOException e) {
            e.printStackTrace();
        }

        et_SearchKeyWord = (EditText) findViewById(R.id.et_SearchKeyWord);
        //et_dest = (EditText) findViewById(R.id.et_destPos);


        mDrawerList = (ListView) findViewById(R.id.navList);
        addDrawerItems();
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

            }
        });
    }
    // Grab back result from PlacesAcvtivity and BookmarkActivity
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Close drawer (left-side drawer -> close visually)
        DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
        dl.closeDrawers();

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

                String name = marker.getTitle().toString();
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

                return true;
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
                return;
            }
        }
        // not found
        Toast.makeText(MapsActivity.this, "Places not found!", Toast.LENGTH_SHORT).show();
    }

    public void LoadPlacesFromFile(InputStream inputStream) {
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

        /*String n = scan.nextLine();
        int nPlaces = Integer.parseInt(n);*/
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
    }

}
