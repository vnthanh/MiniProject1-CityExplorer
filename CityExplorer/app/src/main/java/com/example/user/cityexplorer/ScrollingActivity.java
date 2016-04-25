package com.example.user.cityexplorer;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Scanner;

public class ScrollingActivity extends AppCompatActivity {

    Button bt_Call,bt_GoWeb,bt_Bookmark;
    ImageView iv_PlaceImage;

    // Load from file again, entire places info
    ArrayList<Place> PlacesFromFile = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        // set activity name : to place name
        this.setTitle(name);

        // Set text: place decription
        String description = intent.getStringExtra("description");
        TextView tv = (TextView) findViewById(R.id.ScrollActText);
        tv.setText(description);

        String phone = intent.getStringExtra("phone");
        String website = intent.getStringExtra("website");

        bt_Call = (Button) findViewById(R.id.bt_Call);
        bt_GoWeb = (Button) findViewById(R.id.bt_GoWeb);
        bt_Bookmark = (Button) findViewById(R.id.bt_Bookmark);

        bt_Call.setText("Call: " + phone);
        bt_GoWeb.setText("Go to Web: " + website);


        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx   Load image to image view by place name - place image name
        iv_PlaceImage = (ImageView) findViewById(R.id.iv_PlaceImage);
        // Process name and get res id, set
        String placeNameToGetImage = PlaceNameStringProcess(name);
        int picId = getResources().getIdentifier(placeNameToGetImage, "drawable", getApplicationContext().getPackageName());
        iv_PlaceImage.setImageResource(picId);


        // ======================== for bookmark check-> dummy code : load all, write all
        AssetManager am = this.getAssets();
        try {
            LoadPlacesFromFile(am.open("places.txt")); // Load from file and store in ArrayList<Place>
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Process: clear space, toLowercase to get place image from res/drawable
    public String PlaceNameStringProcess(String before){
        String after = before.replaceAll(" ","");
        after = after.toLowerCase(); // Note: It doesn't work because strings are immutable. You need to reassign
        return after;
    }

    // Transform string to res id
    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Save file again with bookmark update
    public void bt_Bookmark_Clicked(View view) {
        // Get which place selected from parent Activity
        Intent intent = getIntent();
        // Place name? should i use an id?
        String name = intent.getStringExtra("name");

        // Re-write file at the specific place : isBookmark line
        for(int i=0;i<PlacesFromFile.size();i++){
            if(name.equals(PlacesFromFile.get(i).name) && PlacesFromFile.get(i).isBookmark==false){
                // reset its bookmark (to true //dummy)
                PlacesFromFile.get(i).isBookmark = true;
            }
        }
    }

    public void LoadPlacesFromFile(InputStream inputStream)
    {
        com.example.user.cityexplorer.Place tempPlace;
        LatLng tempPosition;
        double tempLat, tempLng; // Lat and Lng
        String tempName;
        String tempPhone;
        String tempWebsite;
        String tempDes;
        boolean tempBookmark;

        Scanner scan  = new Scanner(inputStream);

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
            tempBookmark = scan.nextBoolean();

            tempPlace = new com.example.user.cityexplorer.Place(tempPosition,tempName,tempPhone,tempWebsite,tempDes,tempBookmark);
            PlacesFromFile.add(tempPlace);
        }

        scan.close();
    }

    // write file when after all
    @Override
    protected void onStop() {
        // Dummy code: write all each time, even when nothing happen (bookmark not clicked)
        try {

            PrintStream output = new PrintStream(openFileOutput("places.txt",MODE_PRIVATE));
            // solution: only bookmark load from app file dir, ScrollAct write to this dir too
            // FUCK YOU: CANT WRITE RES FILE xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

            output.println(PlacesFromFile.size());

            for(int i=0;i<PlacesFromFile.size();i++)
            {
                output.println(PlacesFromFile.get(i).postion.latitude);
                output.println(PlacesFromFile.get(i).postion.latitude);
                output.println(PlacesFromFile.get(i).name);
                output.println(PlacesFromFile.get(i).phone);
                output.println(PlacesFromFile.get(i).website);
                output.println(PlacesFromFile.get(i).description);
                output.println(PlacesFromFile.get(i).isBookmark);
            }

            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        super.onStop();
    }
}
