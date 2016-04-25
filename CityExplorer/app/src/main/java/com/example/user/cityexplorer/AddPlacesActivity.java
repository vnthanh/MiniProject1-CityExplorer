package com.example.user.cityexplorer;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

public class AddPlacesActivity extends AppCompatActivity {

    ImageView iv;
    Bitmap bmp; // Store iamge to save
    EditText et_PlaceName, et_Latitude, et_Longitude, et_Phone, et_Website, et_Description;
    ArrayList<Place> PlacesFromFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_places);
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

        //
        iv = (ImageView) findViewById(R.id.iv_Photo);
        et_PlaceName = (EditText) findViewById(R.id.et_PlaceName) ;
        et_Latitude = (EditText) findViewById(R.id.et_Latitude) ;
        et_Longitude = (EditText) findViewById(R.id.et_Longitude) ;
        et_Phone = (EditText) findViewById(R.id.et_Phone) ;
        et_Website = (EditText) findViewById(R.id.et_Website) ;
        et_Description = (EditText) findViewById(R.id.et_Description) ;

        // Load from file to write again to
        // Load from internal, getFileDir

        // Code copy from Bookmark act
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

    private static final int REQ_CODE_TAKE_PICTURE = 456;

    public void bt_TakePhoto_Clicked(View view) {
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(picIntent, REQ_CODE_TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == REQ_CODE_TAKE_PICTURE
                && resultCode == RESULT_OK) {
            bmp = (Bitmap) intent.getExtras().get("data");
            iv.setImageBitmap(bmp);
        }
    }


    public void bt_Save_Clicked(View view) {
        String imageName = et_PlaceName.getText().toString();
        String imageFileName = PlaceNameStringProcess(imageName);

        /////////////////// Write bitmap to external sd card
        File sd = Environment.getExternalStorageDirectory();
        String fileName = imageFileName + ".png";
        File dest = new File(sd, fileName);
        try {
            FileOutputStream out;
            out = new FileOutputStream(dest);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ///////////////////


         //Work on data first, image later
        String name = et_PlaceName.getText().toString();
        String lat = et_Latitude.getText().toString();
        String longi = et_Longitude.getText().toString();
        String phone = et_Phone.getText().toString();
        String web = et_Website.getText().toString();
        String des = et_Description.getText().toString();

        LatLng ll = new LatLng(Double.parseDouble(lat),Double.parseDouble(longi));

        PlacesFromFile.add(new Place(ll,name,phone,web,des,false));

        // Bad code, re-write all to FileDir ? is it FileDir
        try {
            FileManager.SavePlacesToFile(PlacesFromFile,new PrintStream(openFileOutput("places.txt",MODE_PRIVATE)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Press save: restart maps act
        // Solve not update problem, by start all over again
        Intent intent = new Intent(AddPlacesActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    // Prpcess to get lower-case, space-clear string
    public String PlaceNameStringProcess(String before){
        String after = before.replaceAll(" ","");
        after = after.toLowerCase(); // Note: It doesn't work because strings are immutable. You need to reassign
        return after;
    }
}
