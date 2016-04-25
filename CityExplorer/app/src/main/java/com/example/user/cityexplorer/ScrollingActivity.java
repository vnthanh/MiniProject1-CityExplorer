package com.example.user.cityexplorer;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
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
    String phoneNumber, website;

    // Load from file again, entire places info
    ArrayList<Place> PlacesFromFile;

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
        // disable this line : clear top back button, press back (on device) now doesn't reload MapsAct anymore
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        // set activity name : to place name
        this.setTitle(name);

        // Set text: place decription
        String description = intent.getStringExtra("description");
        TextView tv = (TextView) findViewById(R.id.ScrollActText);
        tv.setText(description);

        // Declare outside
        phoneNumber = intent.getStringExtra("phone");
        website = intent.getStringExtra("website");

        bt_Call = (Button) findViewById(R.id.bt_Call);
        bt_GoWeb = (Button) findViewById(R.id.bt_GoWeb);
        bt_Bookmark = (Button) findViewById(R.id.bt_Bookmark);

        bt_Call.setText("Call: " + phoneNumber);
        bt_GoWeb.setText("Check out Website");


        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx   Load image to image view by place name - place image name
        iv_PlaceImage = (ImageView) findViewById(R.id.iv_PlaceImage);
        // Process name and get res id, set
        String placeNameToGetImage = PlaceNameStringProcess(name);
        int picId = getResources().getIdentifier(placeNameToGetImage, "drawable", getApplicationContext().getPackageName());

        // Update: process added place image, store in external file
        if(picId == 0){ // not found in internal
            //getResources().getIdentifier(placeNameToGetImage,getFilesDir().toString(),getApplicationContext().getPackageName());
            String photoPath = Environment.getExternalStorageDirectory() + "/" +placeNameToGetImage + ".png";
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);

            iv_PlaceImage.setImageBitmap(bitmap); // image in external
        }
        else {
            iv_PlaceImage.setImageResource(picId); // image inside (internal)
        }

        // ======================== for bookmark check-> dummy code : load all, write all
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


    // write file when after all
    @Override
    protected void onStop() {
        // Dummy code: write all each time, even when nothing happen (bookmark not clicked)
        try {

            PrintStream output = new PrintStream(openFileOutput("places.txt",MODE_PRIVATE));
            // solution: only bookmark load from app file dir, ScrollAct write to this dir too
            // FUCK YOU: CANT WRITE RES FILE xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

            /*output.println(PlacesFromFile.size());

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

            output.close();*/
            FileManager.SavePlacesToFile(PlacesFromFile,output);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        super.onStop();
    }

    public void bt_Call_Clicked(View view) {
        // Call right-away, not dial
        Uri number = Uri.parse("tel:"+phoneNumber);
        Intent callIntent = new Intent(Intent.ACTION_CALL, number);
        //callIntent.setData(Uri.parse("tel:"+phoneNumber));
        startActivity(callIntent); // should check permission
    }

    public void bt_GoWeb_Clicked(View view) {
        Uri webpage = Uri.parse("http://"+website);
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
        startActivity(webIntent);
    }
}
